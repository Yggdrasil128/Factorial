package de.yggdrasil128.factorial.model.item;

import de.yggdrasil128.factorial.model.game.GameRelatedEntityRemovedEvent;

public class ItemRemovedEvent extends GameRelatedEntityRemovedEvent {

    private final int itemId;

    public ItemRemovedEvent(int gameId, int itemId) {
        super(gameId);
        this.itemId = itemId;
    }

    public int getItemId() {
        return itemId;
    }

}
