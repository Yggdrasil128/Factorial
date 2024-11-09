package de.yggdrasil128.factorial.model.save;

import de.yggdrasil128.factorial.model.*;
import de.yggdrasil128.factorial.model.changelist.ChangelistService;
import de.yggdrasil128.factorial.model.changelist.ChangelistStandalone;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameService;
import de.yggdrasil128.factorial.model.icon.IconService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toMap;

@Service
public class SaveService extends ModelService<Save, SaveRepository> {

    private final ApplicationEventPublisher events;
    private final GameService gameService;
    private final IconService iconService;
    private final FactoryService factoryService;
    private final ChangelistService changelistService;

    public SaveService(SaveRepository repository, ApplicationEventPublisher events, GameService gameService,
                       IconService iconService, FactoryService factoryService, ChangelistService changelistService) {
        super(repository);
        this.events = events;
        this.gameService = gameService;
        this.iconService = iconService;
        this.factoryService = factoryService;
        this.changelistService = changelistService;
    }

    @Transactional
    public void create(SaveStandalone standalone, CompletableFuture<Void> result) {
        Game game = gameService.get((int) standalone.gameId());
        Save save = new Save(game, standalone);
        applyRelations(save, standalone);
        AsyncHelper.complete(result);
        inferOrdinal(save);
        save = create(save);
        events.publishEvent(new SaveUpdatedEvent(save));
    }

    private void inferOrdinal(Save save) {
        if (0 >= save.getOrdinal()) {
            save.setOrdinal(stream().mapToInt(Save::getOrdinal).max().orElse(0) + 1);
        }
    }

    @Transactional
    public void doImport(SaveSummary summary, CompletableFuture<Void> result) {
        String gameName = (String) summary.getSave().gameId();
        Game game = gameService.get(gameName).orElseThrow(() -> ModelService.report(HttpStatus.CONFLICT,
                "save requires the game '" + gameName + "' to be installed"));
        create(Importer.importSave(summary, game));
    }

    @Transactional
    public SaveSummary getSummary(int id, External destination) {
        Save save = get(id);
        SaveSummary summary = new SaveSummary();
        summary.setSave(SaveStandalone.of(save, destination));
        summary.setChangelists(save.getChangelists().stream().map(changelist -> {
            if (External.FRONTEND == destination) {
                changelistService.computeProductionStepChanges(changelist);
            }
            return ChangelistStandalone.of(changelist, destination);
        }).toList());
        summary.setFactories(save.getFactories().stream().map(factory -> factoryService.getFactorySummary(factory,
                destination, changelistService::getProductionStepChanges)).toList());
        return summary;
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

    @Transactional
    public void update(int id, SaveStandalone standalone, CompletableFuture<Void> result) {
        Save save = get(id);
        save.applyBasics(standalone);
        applyRelations(save, standalone);
        AsyncHelper.complete(result);
        update(save);
        events.publishEvent(new SaveUpdatedEvent(save));
    }

    private void applyRelations(Save save, SaveStandalone standalone) {
        OptionalInputField.ofId(standalone.iconId(), iconService::get).apply(save::setIcon);
    }

    @Transactional
    public void delete(int id, CompletableFuture<Void> result) {
        AsyncHelper.complete(result);
        delete(id);
        events.publishEvent(new SaveRemovedEvent(id));
    }

}
