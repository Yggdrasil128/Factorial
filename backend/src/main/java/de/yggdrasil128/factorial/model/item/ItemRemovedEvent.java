package de.yggdrasil128.factorial.model.item;

public class ItemRemovedEvent {

    private final int gameId;
    private final int itemId;

    public ItemRemovedEvent(int gameId, int itemId) {
        this.gameId = gameId;
        this.itemId = itemId;
    }

    public int getGameId() {
        return gameId;
    }

    public int getItemId() {
        return itemId;
    }

}
