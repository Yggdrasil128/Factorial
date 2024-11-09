package de.yggdrasil128.factorial.model.item;

import de.yggdrasil128.factorial.model.AsyncHelper;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameRepository;
import de.yggdrasil128.factorial.model.icon.IconService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
public class ItemService extends ModelService<Item, ItemRepository> {

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

    @Transactional
    public void create(int gameId, ItemStandalone standalone, CompletableFuture<Void> result) {
        Game game = gameRepository.findById(gameId).orElseThrow(ModelService::reportNotFound);
        Item item = new Item(game, standalone);
        applyRelations(item, standalone);
        AsyncHelper.complete(result);
        item = create(item);
        game.getItems().add(item);
        gameRepository.save(game);
        events.publishEvent(new ItemUpdatedEvent(item));
    }

    @Transactional
    public void update(int id, ItemStandalone standalone, CompletableFuture<Void> result) {
        Item item = get(id);
        item.applyBasics(standalone);
        applyRelations(item, standalone);
        AsyncHelper.complete(result);
        item = update(item);
        events.publishEvent(new ItemUpdatedEvent(item));
    }

    private void applyRelations(Item item, ItemStandalone standalone) {
        OptionalInputField.ofId(standalone.iconId(), iconService::get).apply(item::setIcon);
    }

    @Transactional
    public void delete(int id, CompletableFuture<Void> result) {
        Game game = gameRepository.findByItemsId(id);
        if (null == game) {
            throw report(HttpStatus.CONFLICT, "item does not belong to a game");
        }
        AsyncHelper.complete(result);
        delete(id);
        events.publishEvent(new ItemRemovedEvent(game.getId(), id));
    }

}
