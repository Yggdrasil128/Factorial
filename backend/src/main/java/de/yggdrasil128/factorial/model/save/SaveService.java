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

    public void create(SaveStandalone standalone) {
        Game game = gameService.get((int) standalone.gameId());
        Save save = new Save(game, standalone);
        applyRelations(save, standalone);
        inferOrdinal(save);
        save = create(save);
        events.publishEvent(new SaveUpdatedEvent(save));
    }

    private void inferOrdinal(Save save) {
        if (0 >= save.getOrdinal()) {
            save.setOrdinal(stream().mapToInt(Save::getOrdinal).max().orElse(0) + 1);
        }
    }

    public void doImport(SaveSummary summary) {
        String gameName = (String) summary.getSave().gameId();
        Game game = gameService.get(gameName).orElseThrow(() -> ModelService.report(HttpStatus.CONFLICT,
                "save requires the game '" + gameName + "' to be installed"));
        create(Importer.importSave(summary, game));
    }

    public CompletableFuture<SaveSummary> getSummary(int id, External destination) {
        Save save = get(id);
        SaveSummary summary = new SaveSummary();
        summary.setSave(SaveStandalone.of(save, destination));
        summary.setFactories(save.getFactories().stream().map(factory -> factoryService.getFactorySummary(factory,
                destination, changelistService::getProductionStepChanges)).toList());
        summary.setChangelists(save.getChangelists().stream()
                .map(changelist -> ChangelistStandalone.of(changelist, destination)).toList());
        return CompletableFuture.completedFuture(summary);
    }

    public void reorder(List<EntityPosition> input) {
        Map<Integer, Integer> order = input.stream().collect(toMap(EntityPosition::id, EntityPosition::ordinal));
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

    public void update(int id, SaveStandalone standalone) {
        Save save = get(id);
        save.applyBasics(standalone);
        applyRelations(save, standalone);
        update(save);
        events.publishEvent(new SaveUpdatedEvent(save));
    }

    private void applyRelations(Save save, SaveStandalone standalone) {
        OptionalInputField.ofId(standalone.iconId(), iconService::get).apply(save::setIcon);
    }

    @Override
    public void delete(int id) {
        super.delete(id);
        events.publishEvent(new SaveRemovedEvent(id));
    }

}
