package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.engine.ProductionStepChanges;
import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;
import de.yggdrasil128.factorial.model.*;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryRepository;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.productionstep.*;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toMap;

@Service
public class ChangelistService
        extends ParentedModelService<Changelist, ChangelistStandalone, Save, ChangelistRepository> {

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

    @Override
    protected int getEntityId(Changelist changelist) {
        return changelist.getId();
    }

    @Override
    protected int getStandaloneId(ChangelistStandalone standalone) {
        return standalone.id();
    }

    @Override
    protected Save getParentEntity(int parentId) {
        return saveRepository.findById(parentId).orElseThrow(ModelService::reportNotFound);
    }

    @Override
    protected Changelist prepareCreate(Save save, ChangelistStandalone standalone) {
        Changelist changelist = new Changelist(save, standalone);
        applyRelations(changelist, standalone);
        inferOrdinal(save, changelist);
        return changelist;
    }

    private static void inferOrdinal(Save save, Changelist changelist) {
        if (0 >= changelist.getOrdinal()) {
            changelist.setOrdinal(save.getChangelists().stream().mapToInt(Changelist::getOrdinal).max().orElse(0) + 1);
        }
    }

    @Override
    protected void handleBulkCreate(Save save, Iterable<Changelist> changelists) {
        for (Changelist changelist : changelists) {
            if (changelist.isPrimary()) {
                handleNewPrimaryChangelist(changelist).forEach(this::publishProductionStepThroughputsChanged);
            }
            save.getChangelists().add(changelist);
            events.publishEvent(new ChangelistUpdatedEvent(changelist));
        }
        saveRepository.save(save);
    }

    @Override
    protected void prepareUpdate(Changelist changelist, ChangelistStandalone standalone) {
        if (changelist.isPrimary()) {
            if (Boolean.FALSE.equals(standalone.primary())) {
                throw report(HttpStatus.CONFLICT, "cannot set primary changelist to non-primary");
            }
            if (Boolean.FALSE.equals(standalone.active())) {
                throw report(HttpStatus.CONFLICT, "cannot set primary changelist to non-primary");
            }
        }
        changelist.applyBasics(standalone);
        applyRelations(changelist, standalone);
    }

    private void applyRelations(Changelist changelist, ChangelistStandalone standalone) {
        OptionalInputField.ofId(standalone.iconId(), iconService::get).apply(changelist::setIcon);
        OptionalInputField.of(standalone.productionStepChanges())
                .apply(list -> changelist.setProductionStepChanges(list.stream()
                        .collect(toMap(change -> productionStepService.get((int) change.productionStepId()),
                                ProductionStepChangeStandalone::change))));
    }

    @Override
    protected void handleUpdate(Changelist changelist) {
        Map<Integer, ProductionStepThroughputs> collectThroughputs = new HashMap<>();
        if (changelist.isPrimary()) {
            handleNewPrimaryChangelist(changelist)
                    .forEach(throughputs -> collectThroughputs.put(throughputs.getEntityId(), throughputs));
        }
        ProductionStepChanges changes = cache.remove(changelist.getId());
        if (null != changes) {
            changes.undo().forEach(throughputs -> collectThroughputs.put(throughputs.getEntityId(), throughputs));
        }
        computeProductionStepChanges(changelist, true)
                .forEach(throughputs -> collectThroughputs.put(throughputs.getEntityId(), throughputs));
        collectThroughputs.values().forEach(this::publishProductionStepThroughputsChanged);
        events.publishEvent(new ChangelistUpdatedEvent(changelist));
    }

    private Collection<ProductionStepThroughputs> handleNewPrimaryChangelist(Changelist changelist) {
        Changelist oldPrimary = repository.findBySaveIdAndIdNotAndPrimaryIsTrue(changelist.getSave().getId(),
                changelist.getId());
        if (null == oldPrimary) {
            return Collections.emptyList();
        }
        ProductionStepChanges changes = computeProductionStepChanges(oldPrimary);
        oldPrimary.setPrimary(false);
        repository.save(oldPrimary);
        events.publishEvent(new ChangelistUpdatedEvent(oldPrimary));
        return changes.deactivatePrimary();
    }

    @Override
    public void delete(List<Integer> ids, CompletableFuture<Void> result) {
        for (Integer id : ids) {
            if (repository.existsByIdAndPrimaryIsTrue(id)) {
                throw report(HttpStatus.CONFLICT, "cannot delete primary changelist");
            }
        }
        super.delete(ids, result);
    }

    @Override
    protected Save findParentEntity(int id) {
        Save save = saveRepository.findByChangelistsId(id);
        if (null == save) {
            throw report(HttpStatus.CONFLICT, "changelist does not belong to a save");
        }
        save.getChangelists().remove(get(id));
        return save;
    }

    @Override
    protected void handleDelete(Save save, int id) {
        ProductionStepChanges changes = cache.remove(id);
        if (null != changes) {
            changes.undo().forEach(this::publishProductionStepThroughputsChanged);
        }
        events.publishEvent(new ChangelistRemovedEvent(save.getId(), id));
    }

    public ProductionStepChanges computeProductionStepChanges(Changelist changelist) {
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
                    collectThroughputs.put(throughputs.getEntityId(), throughputs);
                }
            }
        }
        return collectThroughputs.values();
    }

    private ProductionStepChanges computeProductionStepChanges0(Changelist changelist) {
        return cache.computeIfAbsent(changelist.getId(), key -> new ProductionStepChanges(changelist));
    }

    public Changelist initDefaultPrimary(Save save) {
        Changelist changelist = new Changelist();
        changelist.setSave(save);
        changelist.setOrdinal(1);
        changelist.setName("default");
        changelist.setPrimary(true);
        changelist.setActive(true);
        save.getChangelists().add(changelist);
        return changelist;
    }

    @Transactional
    public void reorder(int saveId, List<EntityPosition> input, CompletableFuture<Void> result) {
        Save save = saveRepository.findById(saveId).orElseThrow(ModelService::reportNotFound);
        Map<Integer, Integer> order = input.stream().collect(toMap(EntityPosition::id, EntityPosition::ordinal));
        AsyncHelper.complete(result);
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

    private void publishProductionStepThroughputsChanged(ProductionStepThroughputs throughputs) {
        events.publishEvent(new ProductionStepThroughputsChangedEvent(
                productionStepService.get(throughputs.getEntityId()), throughputs, false));
    }

    public QuantityByChangelist getProductionStepChanges(ProductionStep productionStep) {
        Save save = productionStep.getFactory().getSave();
        return StreamSupport.stream(repository.findAllBySaveIdAndActiveIsTrue(save.getId()).spliterator(), false)
                .map(this::computeProductionStepChanges).map(changes -> changes.getChanges(productionStep))
                .reduce(QuantityByChangelist.ZERO, QuantityByChangelist::add);
    }

    @Transactional
    public void apply(int id) {
        Changelist changelist = get(id);
        if (!changelist.getProductionStepChanges().isEmpty()) {
            ProductionStepChanges changes = computeProductionStepChanges(changelist);
            for (ProductionStep productionStep : changelist.getProductionStepChanges().keySet()) {
                ProductionStepChanges.Link link = changes.getLink(productionStep.getId());
                if (null != link) {
                    Fraction change = link.getChange();
                    if (link.applyChange()) {
                        productionStepService.setMachineCount(productionStep, link.getThroughputs(), change);
                    }
                }
            }
            changelist.getProductionStepChanges().clear();
            repository.save(changelist);
            events.publishEvent(new ChangelistUpdatedEvent(changelist));
        }
    }

    @Transactional
    public void setPrimaryMachineCount(int productionStepId, Fraction machineCount, CompletableFuture<Void> result) {
        ProductionStep productionStep = productionStepService.get(productionStepId);
        AsyncHelper.complete(result);
        Changelist changelist = findPrimaryChangelist(productionStepId);
        Fraction change = machineCount.subtract(productionStep.getMachineCount());
        setChange(changelist, productionStep, change);
    }

    @Transactional
    public void applyPrimaryChangelist(int productionStepId, CompletableFuture<Void> result) {
        ProductionStep productionStep = productionStepService.get(productionStepId);
        AsyncHelper.complete(result);
        Changelist changelist = findPrimaryChangelist(productionStepId);
        applyChange(changelist, productionStep);
    }

    private Changelist findPrimaryChangelist(int productionStepId) {
        Factory factory = factoryRepository.findByProductionStepsId(productionStepId);
        Save save = saveRepository.findByFactoriesId(factory.getId());
        return repository.findBySaveIdAndPrimaryIsTrue(save.getId());
    }

    @Transactional
    public void setMachineCountChange(int id, int productionStepId, Fraction machineCountChange,
                                      CompletableFuture<Void> result) {
        Changelist changelist = get(id);
        ProductionStep productionStep = productionStepService.get(productionStepId);
        AsyncHelper.complete(result);
        setChange(changelist, productionStep, machineCountChange);
    }

    @Transactional
    public void applyChange(int id, int productionStepId, CompletableFuture<Void> result) {
        Changelist changelist = get(id);
        ProductionStep productionStep = productionStepService.get(productionStepId);
        AsyncHelper.complete(result);
        applyChange(changelist, productionStep);
    }

    private void setChange(Changelist changelist, ProductionStep productionStep, Fraction change) {
        ProductionStepThroughputs throughputs = productionStepService.computeThroughputs(productionStep,
                () -> getProductionStepChanges(productionStep));
        ProductionStepChanges changes = computeProductionStepChanges(changelist);
        if (changes.setChange(productionStep.getId(), throughputs, change)) {
            events.publishEvent(new ProductionStepThroughputsChangedEvent(productionStep, throughputs, false));
        }
        if (change.isZero()) {
            changelist.getProductionStepChanges().remove(productionStep);
        } else {
            changelist.getProductionStepChanges().put(productionStep, change);
        }
        repository.save(changelist);
        events.publishEvent(new ChangelistUpdatedEvent(changelist));
    }

    private void applyChange(Changelist changelist, ProductionStep productionStep) {
        ProductionStepChanges.Link link = computeProductionStepChanges(changelist).getLink(productionStep.getId());
        if (null != link) {
            Fraction change = link.getChange();
            if (link.applyChange()) {
                productionStepService.setMachineCount(productionStep, link.getThroughputs(), change);
                events.publishEvent(new ChangelistUpdatedEvent(changelist));
            }
        }
        changelist.getProductionStepChanges().remove(productionStep);
        repository.save(changelist);
        events.publishEvent(new ChangelistUpdatedEvent(changelist));
    }

    /**
     * Establishes the link for the {@link ProductionStepChanges} engine objects for all {@link Changelist Changelists}
     * that have an entry for the given {@link ProductionStep}. If the respective engine objects do not exist yet, they
     * are computed on demand.
     * 
     * @param event the event that signals the initialization of a new {@link ProductionStepThroughputs} engine object
     * @return an event signalling that the {@link ProductionStepThroughputs} have been updated
     */
    @EventListener
    public ProductionStepThroughputsChangedEvent on(ProductionStepThroughputsInitalizedEvent event) {
        for (Changelist changelist : repository
                .findAllByProductionStepChangesProductionStep(event.getProductionStep())) {
            computeProductionStepChanges(changelist).establishLink(event.getThroughputs(), true);
        }
        /*
         * Currently, we need to signal that we want the factory production line to recalculate its resources, hence we
         * arrive at causing an invocation to ProductionLine.updateContribution, although we should really try to arrive
         * at ProductionLine.addContributor.
         */
        return new ProductionStepThroughputsChangedEvent(event.getProductionStep(), event.getThroughputs(), true);
    }

    @EventListener
    public List<ChangelistUpdatedEvent> on(ProductionStepRemovedEvent event) {
        List<ChangelistUpdatedEvent> downstream = new ArrayList<>();
        for (Changelist changelist : repository.findAllBySaveIdAndActiveIsTrue(event.getSaveId())) {
            ProductionStepChanges changes = cache.get(changelist.getId());
            if (null != changes) {
                if (changes.drop(event.getProductionStepId())) {
                    downstream.add(new ChangelistUpdatedEvent(changelist));
                }
            }
        }
        return downstream;
    }

}
