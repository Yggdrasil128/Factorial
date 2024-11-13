package de.yggdrasil128.factorial.model.item;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.ParentedModelService;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameRepository;
import de.yggdrasil128.factorial.model.icon.IconService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ItemService extends ParentedModelService<Item, ItemStandalone, Game, ItemRepository> {

    private final ApplicationEventPublisher events;
    private final GameRepository gameRepository;
    private final IconService iconService;

    public ItemService(ItemRepository repository, ApplicationEventPublisher events, GameRepository gameRepository,
                       IconService iconService) {
        super(repository);
        this.events = events;
        this.gameRepository = gameRepository;
        this.iconService = iconService;
    }

    @Override
    protected int getEntityId(Item item) {
        return item.getId();
    }

    @Override
    protected int getStandaloneId(ItemStandalone standalone) {
        return standalone.id();
    }

    @Override
    protected Game getParentEntity(int parentId) {
        return gameRepository.findById(parentId).orElseThrow(ModelService::reportNotFound);
    }

    @Override
    protected Item prepareCreate(Game game, ItemStandalone standalone) {
        Item item = new Item(game, standalone);
        applyRelations(item, standalone);
        return item;
    }

    @Override
    protected void handleBulkCreate(Game game, Iterable<Item> items) {
        for (Item item : items) {
            game.getItems().add(item);
            events.publishEvent(new ItemUpdatedEvent(item));
        }
        gameRepository.save(game);
    }

    @Override
    protected void prepareUpdate(Item item, ItemStandalone standalone) {
        item.applyBasics(standalone);
        applyRelations(item, standalone);
    }

    private void applyRelations(Item item, ItemStandalone standalone) {
        OptionalInputField.ofId(standalone.iconId(), iconService::get).apply(item::setIcon);
    }

    @Override
    protected void handleUpdate(Item item) {
        events.publishEvent(new ItemUpdatedEvent(item));
    }

    @Override
    protected Game findParentEntity(int id) {
        Game game = gameRepository.findByItemsId(id);
        if (null == game) {
            throw report(HttpStatus.CONFLICT, "item does not belong to a game");
        }
        return game;
    }

    @Override
    protected void handleDelete(Game game, int id) {
        events.publishEvent(new ItemRemovedEvent(game.getId(), id));
    }

}
