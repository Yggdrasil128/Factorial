package de.yggdrasil128.factorial.model.save;

import de.yggdrasil128.factorial.engine.ProductionLine;
import de.yggdrasil128.factorial.engine.ResourceContributions;
import de.yggdrasil128.factorial.model.*;
import de.yggdrasil128.factorial.model.changelist.ChangelistService;
import de.yggdrasil128.factorial.model.changelist.ChangelistStandalone;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryProductionLineChangedEvent;
import de.yggdrasil128.factorial.model.factory.FactoryRemovedEvent;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameService;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.resource.global.GlobalResource;
import de.yggdrasil128.factorial.model.resource.global.GlobalResourceService;
import de.yggdrasil128.factorial.model.resource.global.GlobalResourceStandalone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Service
public class SaveService extends OrphanModelService<Save, SaveStandalone, SaveRepository>
        implements ProductionLineService {

    private static final Logger LOG = LoggerFactory.getLogger(SaveService.class);

    private final ApplicationEventPublisher events;
    private final GameService gameService;
    private final IconService iconService;
    private final FactoryService factoryService;
    private final ChangelistService changelistService;
    private final GlobalResourceService resourceService;
    private final Map<Integer, ProductionLine> cache = new HashMap<>();

    public SaveService(SaveRepository repository, ApplicationEventPublisher events, GameService gameService,
                       IconService iconService, FactoryService factoryService, ChangelistService changelistService,
                       GlobalResourceService saveResourceService) {
        super(repository);
        this.events = events;
        this.gameService = gameService;
        this.iconService = iconService;
        this.factoryService = factoryService;
        this.changelistService = changelistService;
        this.resourceService = saveResourceService;
    }

    @Override
    protected int getEntityId(Save save) {
        return save.getId();
    }

    @Override
    protected int getStandaloneId(SaveStandalone standalone) {
        return standalone.id();
    }

    @Override
    protected Save prepareCreate(SaveStandalone standalone) {
        Game game = gameService.get((int) standalone.gameId());
        Save save = new Save(game, standalone);
        applyRelations(save, standalone);
        inferOrdinal(save);
        changelistService.initDefaultPrimary(save);
        return save;
    }

    private void inferOrdinal(Save save) {
        if (0 >= save.getOrdinal()) {
            save.setOrdinal(stream().mapToInt(Save::getOrdinal).max().orElse(0) + 1);
        }
    }

    @Override
    protected void handleBulkCreate(Iterable<Save> saves) {
        for (Save save : saves) {
            events.publishEvent(new SaveProductionLineChangedEvent(save, initEmptyProductionLine(save), false));
        }
    }

    private ProductionLine initEmptyProductionLine(Save save) {
        ProductionLine productionLine = new ProductionLine(save.getId(), this);
        cache.put(save.getId(), productionLine);
        return productionLine;
    }

    @Override
    protected void prepareUpdate(Save game, SaveStandalone standalone) {
        game.applyBasics(standalone);
        applyRelations(game, standalone);
    }

    private void applyRelations(Save save, SaveStandalone standalone) {
        OptionalInputField.ofId(standalone.iconId(), iconService::get).apply(save::setIcon);
    }

    @Override
    protected void handleUpdate(Save save) {
        events.publishEvent(new SaveUpdatedEvent(save));
    }

    @Override
    protected void handleDelete(int id) {
        cache.remove(id);
        events.publishEvent(new SaveRemovedEvent(id));
    }

    public ProductionLine
            computeProductionLine(Save save, Function<? super ProductionStep, ? extends QuantityByChangelist> changes) {
        return cache.computeIfAbsent(save.getId(), key -> initProduictionLine(save, changes));
    }

    private ProductionLine
            initProduictionLine(Save save, Function<? super ProductionStep, ? extends QuantityByChangelist> changes) {
        ProductionLine productionLine = new ProductionLine(save.getId(), this);
        for (GlobalResource resource : save.getResources()) {
            productionLine.addResource(resource, () -> resourceService.computeContributions(resource));
        }
        for (Factory factory : save.getFactories()) {
            productionLine.addContributor(factoryService.computeProductionLine(factory, changes));
        }
        if (productionLine.hasAlteredResources()) {
            repository.save(save);
        }
        return productionLine;
    }

    @Override
    public ResourceContributions spawnResource(int id, int itemId) {
        return resourceService.spawn(id, itemId);
    }

    @Override
    public void notifyResourceUpdate(int id, ResourceContributions contributions) {
        resourceService.updateContributions(contributions);
    }

    @Override
    public void destroyResource(int id, int resourceId) {
        resourceService.destroy(get(id), resourceId);
    }

    @Transactional
    public void doImport(SaveSummary summary, CompletableFuture<Void> result) {
        String gameName = (String) summary.getSave().gameId();
        Game game = gameService.get(gameName).orElseThrow(() -> ModelService.report(HttpStatus.CONFLICT,
                "save requires the game '" + gameName + "' to be installed"));
        Save save = Importer.importSave(summary, game);
        save.setOrdinal(0);
        inferOrdinal(save);
        AsyncHelper.complete(result);
        repository.save(save);
        events.publishEvent(new SaveUpdatedEvent(save));
    }

    @Transactional
    public void doClone(int id, String newName, CompletableFuture<Void> result) {
        Save save = get(id);
        Game game = save.getGame();
        copy(save, newName, game, result);
    }

    @Transactional
    public void migrate(int id, String newName, int gameId, CompletableFuture<Void> result) {
        Save save = get(id);
        Game game = gameService.get(gameId);
        copy(save, newName, game, result);
    }

    private void copy(Save save, String newName, Game game, CompletableFuture<Void> result) {
        SaveSummary temp = Exporter.exportSave(save, External.SAVE_FILE);
        Save clone = Importer.importSave(temp, game);
        clone.setName(newName);
        clone.setOrdinal(0);
        inferOrdinal(clone);
        AsyncHelper.complete(result);
        repository.save(clone);
        events.publishEvent(new SaveUpdatedEvent(clone));
    }

    @Transactional
    public SaveSummary getSummary(int id, External destination) {
        Save save = get(id);
        switch (destination) {
        case FRONTEND:
            SaveSummary summary = new SaveSummary();
            summary.setSave(SaveStandalone.of(save, destination));
            summary.setChangelists(save.getChangelists().stream().map(changelist -> {
                changelistService.computeProductionStepChanges(changelist);
                return ChangelistStandalone.of(changelist, destination);
            }).toList());
            summary.setFactories(
                    save.getFactories().stream().map(factory -> factoryService.getFrontendFactorySummary(factory,
                            destination, changelistService::getProductionStepChanges)).toList());
            ProductionLine productionLine = computeProductionLine(save, changelistService::getProductionStepChanges);
            summary.setResources(save.getResources().stream()
                    .map(resource -> GlobalResourceStandalone.of(resource, productionLine.getContributions(resource)))
                    .toList());
            return summary;
        case SAVE_FILE:
            return Exporter.exportSave(save, destination);
        default:
            throw new AssertionError(
                    "unexpected enum constant: " + External.class.getCanonicalName() + '.' + destination.name());
        }
    }

    @Transactional
    public List<SaveStandalone> getAll() {
        return stream().map(SaveStandalone::of).toList();
    }

    @Transactional
    public void reorder(List<EntityPosition> input, CompletableFuture<Void> result) {
        Map<Integer, Integer> order = input.stream().collect(toMap(EntityPosition::id, EntityPosition::ordinal));
        AsyncHelper.complete(result);
        Collection<Save> saves = new ArrayList<>();
        // we want to have all saves in memory so that we are immune to circles in the given order
        for (Save save : stream().toList()) {
            Integer ordinal = order.get(save.getId());
            if (null != ordinal) {
                save.setOrdinal(ordinal.intValue());
                saves.add(save);
                repository.save(save);
            }
        }
        events.publishEvent(new SavesReorderedEvent(saves));
    }

    @EventListener
    public SaveProductionLineChangedEvent on(FactoryProductionLineChangedEvent event) {
        Save save = event.getFactory().getSave();
        ProductionLine productionLine = cache.get(save.getId());
        if (null == productionLine) {
            // TODO fix this
            LOG.error(
                    "Some Factory import/export was changed, but the Production Line for Save {} is not computed, aborting event sequence",
                    save);
            return null;
        }
        boolean itemsChanged = false;
        if (event.isItemsChanged()) {
            productionLine.updateContribution(event.getProductionLine());
            itemsChanged = productionLine.hasAlteredResources();
            if (itemsChanged) {
                repository.save(save);
            }
        } else {
            productionLine.updateContributor(event.getProductionLine());
        }
        return new SaveProductionLineChangedEvent(save, productionLine, itemsChanged);
    }

    @EventListener
    public SaveProductionLineChangedEvent on(FactoryRemovedEvent event) {
        ProductionLine productionLine = cache.get(event.getSaveId());
        if (null == productionLine) {
            return null;
        }
        if (null != event.getProductionLine()) {
            productionLine.removeContributor(event.getProductionLine());
            return new SaveProductionLineChangedEvent(get(event.getSaveId()), productionLine, true);
        }
        // TODO find a better solution
        // we don't have access to the old production line here, so we can only do a full invalidate
        LOG.warn(
                "Removed a factory that had no computed production line, doing a full invalidate for Production Line of Save{}",
                event.getSaveId());
        cache.remove(event.getSaveId());
        return null;
    }

    /*
     * There is no meaningful update on a SaveResource that would require us to handle it here. Therefore, we do not
     * have an EventListener on GlobalResourceUpdatedEvent, in contrast to FactoryService.
     */

}
