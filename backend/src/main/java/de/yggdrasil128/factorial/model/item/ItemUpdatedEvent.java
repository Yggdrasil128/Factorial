package de.yggdrasil128.factorial.model.item;

import de.yggdrasil128.factorial.model.game.GameRelatedEvent;

public class ItemUpdatedEvent implements GameRelatedEvent {

    private final Item item;

    public ItemUpdatedEvent(Item item) {
        this.item = item;
    }

    @Override
    public int getGameId() {
        return item.getGame().getId();
    }

    public Item getItem() {
        return item;
    }

}
