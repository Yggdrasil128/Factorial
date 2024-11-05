package de.yggdrasil128.factorial.model.item;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class ItemService extends ModelService<Item, ItemRepository> {

    private final ApplicationEventPublisher events;
    private final GameRepository games;

    public ItemService(ItemRepository repository, ApplicationEventPublisher events, GameRepository games) {
        super(repository);
        this.events = events;
        this.games = games;
    }

    @Override
    public Item create(Item entity) {
        Item item = super.create(entity);
        events.publishEvent(new ItemUpdatedEvent(item));
        return item;
    }

    @Override
    public Item update(Item entity) {
        Item item = super.update(entity);
        events.publishEvent(new ItemUpdatedEvent(item));
        return item;
    }

    @Override
    public void delete(int id) {
        Game game = games.findByItemsId(id);
        super.delete(id);
        if (null != game) {
            events.publishEvent(new ItemRemovedEvent(game.getId(), id));
        }
    }

}
