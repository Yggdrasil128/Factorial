package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.engine.ProductionStepChanges;
import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;
import de.yggdrasil128.factorial.engine.QuantityByChangelist;
import de.yggdrasil128.factorial.model.EntityPosition;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryRepository;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepService;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepThroughputsChangedEvent;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepThroughputsInitalizedEvent;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toMap;

@Service
public class ChangelistService extends ModelService<Changelist, ChangelistRepository> {

    private final ApplicationEventPublisher events;
    private final SaveRepository saveRepository;
    private final FactoryRepository factoryRepository;
    private final IconService iconService;
    private final ProductionStepService productionStepService;
    private final Map<Integer, ProductionStepChanges> cache = new HashMap<>();

    public ChangelistService(ChangelistRepository repository, ApplicationEventPublisher events,
                             SaveRepository saveRepository, FactoryRepository factoryRepository,
                             IconService iconService, ProductionStepService productionStepService) {
        super(repository);
        this.events = events;
        this.saveRepository = saveRepository;
        this.factoryRepository = factoryRepository;
        this.iconService = iconService;
        this.productionStepService = productionStepService;
    }

    public void create(int saveId, ChangelistStandalone standalone) {
        Save save = saveRepository.findById(saveId).orElseThrow(ModelService::reportNotFound);
        Changelist changelist = new Changelist(save, standalone);
        applyRelations(changelist, standalone);
        changelist = create(changelist);
        if (changelist.isPrimary()) {
            handleNewPrimaryChangelist(changelist).forEach(this::publishProductionStepThroughputsChanged);
        }
        save.getChangelists().add(changelist);
        saveRepository.save(save);
        events.publishEvent(new ChangelistUpdatedEvent(changelist));
    }

    @Override
    public Changelist create(Changelist changelist) {
        Save save = changelist.getSave();
        if (0 >= changelist.getOrdinal()) {
            changelist.setOrdinal(save.getChangelists().stream().mapToInt(Changelist::getOrdinal).max().orElse(0) + 1);
        }
        return super.create(changelist);
    }

    private ProductionStepChanges computeProductionStepChanges(Changelist changelist) {
        ProductionStepChanges cached = cache.get(changelist.getId());
        if (null != cached) {
            return cached;
        }
        if (changelist.getProductionStepChanges().isEmpty()) {
            return computeProductionStepChanges0(changelist);
        }
        computeProductionStepChanges(changelist, false);
        return cache.get(changelist.getId());
    }

    private Collection<ProductionStepThroughputs> computeProductionStepChanges(Changelist changelist,
                                                                               boolean applyChanges) {
        /*
         * We do not want to change production step throughputs unnecessarily, so we have to first collect all changes
         * and only then start computing the production step throughputs and finally link them with our engine objects.
         * Since production step throughputs are affected by all changelists in a save, we have to do this for all
         * changelists in the enclosing save.
         */
        Save save = changelist.getSave();
        Map<Integer, QuantityByChangelist> productionStepChanges = new HashMap<>();
        Collection<Changelist> unlinked = new ArrayList<>();
        for (Changelist c : save.getChangelists()) {
            ProductionStepChanges changes = computeProductionStepChanges0(c);
            unlinked.add(c);
            for (ProductionStep productionStep : c.getProductionStepChanges().keySet()) {
                productionStepChanges.put(productionStep.getId(), changes.getChanges(productionStep));
            }
        }
        Map<Integer, ProductionStepThroughputs> collectThroughputs = new HashMap<>();
        for (Changelist c : unlinked) {
            ProductionStepChanges changes = cache.get(c.getId());
            for (Map.Entry<ProductionStep, Fraction> entry : c.getProductionStepChanges().entrySet()) {
                ProductionStepThroughputs throughputs = productionStepService.computeThroughputs(entry.getKey(),
                        () -> productionStepChanges.get(entry.getKey().getId()));
                if (changes.establishLink(throughputs, applyChanges)) {
                    collectThroughputs.put(throughputs.getProductionStepId(), throughputs);
                }
            }
        }
        return collectThroughputs.values();
    }

    private ProductionStepChanges computeProductionStepChanges0(Changelist changelist) {
        return cache.computeIfAbsent(changelist.getId(), key -> new ProductionStepChanges(changelist));
    }

    public void reorder(int saveId, List<EntityPosition> input) {
        Save save = saveRepository.findById(saveId).orElseThrow(ModelService::reportNotFound);
        Map<Integer, Integer> order = input.stream().collect(toMap(EntityPosition::id, EntityPosition::ordinal));
        Collection<Changelist> changelists = new ArrayList<>();
        for (Changelist changelist : save.getChangelists()) {
            Integer ordinal = order.get(changelist.getId());
            if (null != ordinal) {
                changelist.setOrdinal(ordinal.intValue());
                changelists.add(changelist);
                repository.save(changelist);
            }
        }
        events.publishEvent(new ChangelistsReorderedEvent(save.getId(), changelists));
    }

    public Changelist getPrimaryChangelistExcept(Changelist not) {
        return repository.findBySaveIdAndIdNotAndPrimaryIsTrue(not.getSave().getId(), not.getId());
    }

    public void update(int id, ChangelistStandalone standalone) {
        Changelist changelist = get(id);
        if (changelist.isPrimary()) {
            if (Boolean.FALSE.equals(standalone.primary())) {
                // cannot set primary to false
                return;
            }
            if (Boolean.FALSE.equals(standalone.active())) {
                // cannot set primary to inactive
                return;
            }
        }
        changelist.applyBasics(standalone);
        applyRelations(changelist, standalone);
        update(changelist);
    }

    private void applyRelations(Changelist changelist, ChangelistStandalone standalone) {
        OptionalInputField.ofId(standalone.iconId(), iconService::get).apply(changelist::setIcon);
        OptionalInputField.of(standalone.productionStepChanges())
                .apply(list -> changelist.setProductionStepChanges(list.stream()
                        .collect(toMap(change -> productionStepService.get((int) change.productionStepId()),
                                ProductionStepChangeStandalone::change))));
    }

    @Override
    public Changelist update(Changelist entity) {
        Changelist changelist = super.update(entity);
        Map<Integer, ProductionStepThroughputs> collectThroughputs = new HashMap<>();
        if (changelist.isPrimary()) {
            handleNewPrimaryChangelist(changelist)
                    .forEach(throughputs -> collectThroughputs.put(throughputs.getProductionStepId(), throughputs));
        }
        ProductionStepChanges changes = cache.remove(changelist.getId());
        if (null != changes) {
            changes.undo()
                    .forEach(throughputs -> collectThroughputs.put(throughputs.getProductionStepId(), throughputs));
        }
        computeProductionStepChanges(changelist, true)
                .forEach(throughputs -> collectThroughputs.put(throughputs.getProductionStepId(), throughputs));
        collectThroughputs.values().forEach(this::publishProductionStepThroughputsChanged);
        events.publishEvent(new ChangelistUpdatedEvent(changelist));
        return changelist;
    }

    private Stream<ProductionStepThroughputs> handleNewPrimaryChangelist(Changelist changelist) {
        Changelist oldPrimary = repository.findBySaveIdAndIdNotAndPrimaryIsTrue(changelist.getSave().getId(),
                changelist.getId());
        if (null == oldPrimary) {
            return Stream.empty();
        }
        ProductionStepChanges changes = computeProductionStepChanges(oldPrimary);
        oldPrimary.setPrimary(false);
        repository.save(oldPrimary);
        events.publishEvent(new ChangelistUpdatedEvent(oldPrimary));
        return changes.deactivatePrimary();
    }

    public void reportMachineCount(int changelistId, ProductionStep productionStep, Fraction change) {
        Changelist changelist = get(changelistId);
        if (Fraction.ZERO.equals(change)) {
            changelist.getProductionStepChanges().remove(productionStep);
        } else {
            changelist.getProductionStepChanges().put(productionStep, change);
        }
        repository.save(changelist);
        events.publishEvent(new ChangelistUpdatedEvent(changelist));
    }

    @Override
    public void delete(int id) {
        if (repository.existsByIdAndPrimaryIsTrue(id)) {
            throw report(HttpStatus.CONFLICT, "cannot delete primary changelist");
        }
        Save save = saveRepository.findByChangelistsId(id);
        super.delete(id);
        ProductionStepChanges changes = cache.remove(id);
        if (null != changes) {
            changes.undo().forEach(this::publishProductionStepThroughputsChanged);
        }
        events.publishEvent(new ChangelistRemovedEvent(save.getId(), id));
    }

    private void publishProductionStepThroughputsChanged(ProductionStepThroughputs throughputs) {
        events.publishEvent(new ProductionStepThroughputsChangedEvent(
                productionStepService.get(throughputs.getProductionStepId()), throughputs, false));
    }

    public QuantityByChangelist getProductionStepChanges(ProductionStep productionStep) {
        Save save = productionStep.getFactory().getSave();
        return StreamSupport.stream(repository.findAllBySaveIdAndActiveIsTrue(save.getId()).spliterator(), false)
                .map(this::computeProductionStepChanges).map(changes -> changes.getChanges(productionStep))
                .reduce(QuantityByChangelist.ZERO, QuantityByChangelist::add);
    }

    public void apply(int id) {
        Changelist changelist = get(id);
        if (!changelist.getProductionStepChanges().isEmpty()) {
            ProductionStepChanges changes = computeProductionStepChanges(changelist);
            for (ProductionStep productionStep : changelist.getProductionStepChanges().keySet()) {
                ProductionStepChanges.Link link = changes.getLink(productionStep.getId());
                if (null != link) {
                    Fraction change = link.getChange();
                    if (link.applyChange()) {
                        setProductionStepMachineCount(productionStep, link.getThroughputs(), change);
                    }
                }
            }
            changelist.getProductionStepChanges().clear();
            repository.save(changelist);
            events.publishEvent(new ChangelistUpdatedEvent(changelist));
        }
    }

    public void setPrimaryMachineCount(int productionStepId, Fraction machineCount) {
        ProductionStep productionStep = productionStepService.get(productionStepId);
        Changelist changelist = findPrimaryChangelist(productionStepId);
        Fraction change = machineCount.subtract(productionStep.getMachineCount());
        ProductionStepThroughputs throughputs = productionStepService.computeThroughputs(productionStep,
                () -> getProductionStepChanges(productionStep));
        ProductionStepChanges changes = computeProductionStepChanges(changelist);
        if (changes.setChange(productionStepId, throughputs, change)) {
            publishProductionStepThroughputsChanged(productionStep, throughputs);
        }
        changelist.getProductionStepChanges().put(productionStep, change);
        repository.save(changelist);
        events.publishEvent(new ChangelistUpdatedEvent(changelist));
    }

    public void applyPrimaryChangelist(int productionStepId) {
        ProductionStep productionStep = productionStepService.get(productionStepId);
        Changelist changelist = findPrimaryChangelist(productionStepId);
        ProductionStepChanges.Link link = computeProductionStepChanges(changelist).getLink(productionStepId);
        if (null != link) {
            Fraction change = link.getChange();
            if (link.applyChange()) {
                setProductionStepMachineCount(productionStep, link.getThroughputs(), change);
                events.publishEvent(new ChangelistUpdatedEvent(changelist));
            }
        }
        changelist.getProductionStepChanges().remove(productionStep);
        repository.save(changelist);
        events.publishEvent(new ChangelistUpdatedEvent(changelist));
    }

    private Changelist findPrimaryChangelist(int productionStepId) {
        Factory factory = factoryRepository.findByProductionStepsId(productionStepId);
        Save save = saveRepository.findByFactoriesId(factory.getId());
        return repository.findBySaveIdAndPrimaryIsTrue(save.getId());
    }

    private void setProductionStepMachineCount(ProductionStep productionStep, ProductionStepThroughputs throughputs,
                                               Fraction change) {
        productionStep.setMachineCount(productionStep.getMachineCount().add(change));
        productionStep = productionStepService.update(productionStep);
        publishProductionStepThroughputsChanged(productionStep, throughputs);
    }

    private void publishProductionStepThroughputsChanged(ProductionStep productionStep,
                                                         ProductionStepThroughputs throughputs) {
        events.publishEvent(new ProductionStepThroughputsChangedEvent(productionStep, throughputs, false));
    }

    /**
     * Establishes the link for the {@link ProductionStepChanges} engine objects for all {@link Changelist Changelists}
     * that have an entry for the given {@link ProductionStep}. If the respective engine objects do not exist yet, they
     * are computed on demand.
     * 
     * @param event the event that signals the initialization of a new {@link ProductionStepThroughputs} engine object
     * @return an event signalling that the {@link ProductionStepChanges} have been updated
     */
    @EventListener
    public ProductionStepThroughputsChangedEvent on(ProductionStepThroughputsInitalizedEvent event) {
        for (Changelist changelist : repository
                .findAllByProductionStepChangesProductionStep(event.getProductionStep())) {
            computeProductionStepChanges(changelist).establishLink(event.getThroughputs(), true);
        }
        /*
         * Currently, we need to signal that we want the factory production line to recalculate its resources, although
         * we should really try to arrive at causing an invocation to ProductionLine.addContributor to ensue.
         */
        return new ProductionStepThroughputsChangedEvent(event.getProductionStep(), event.getThroughputs(), true);
    }

}
