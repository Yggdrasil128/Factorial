package de.yggdrasil128.factorial.model.item;

public class ItemUpdatedEvent {

    private final Item item;

    public ItemUpdatedEvent(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

}
