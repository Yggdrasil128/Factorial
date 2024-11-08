package de.yggdrasil128.factorial.model.save;

import de.yggdrasil128.factorial.model.EntityPosition;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.changelist.ChangelistService;
import de.yggdrasil128.factorial.model.changelist.ChangelistStandalone;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameService;
import de.yggdrasil128.factorial.model.icon.IconService;
import org.springframework.context.ApplicationEventPublisher;
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
        create(save);
    }

    @Override
    public Save create(Save entity) {
        if (0 >= entity.getOrdinal()) {
            entity.setOrdinal(stream().mapToInt(Save::getOrdinal).max().orElse(0) + 1);
        }
        Save save = super.create(entity);
        events.publishEvent(new SaveUpdatedEvent(save));
        return save;
    }

    public CompletableFuture<SaveSummary> getSummary(int id) {
        Save save = get(id);
        SaveSummary summary = new SaveSummary();
        summary.setSave(SaveStandalone.of(save));
        summary.setFactories(save.getFactories().stream()
                .map(factory -> factoryService.getFactorySummary(factory, changelistService::getProductionStepChanges))
                .toList());
        summary.setChangelists(save.getChangelists().stream().map(ChangelistStandalone::of).toList());
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
    }

    @Override
    public Save update(Save entity) {
        // no need to invalidate resources, since we don't change anything related
        Save save = super.update(entity);
        events.publishEvent(new SaveUpdatedEvent(save));
        return save;
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
