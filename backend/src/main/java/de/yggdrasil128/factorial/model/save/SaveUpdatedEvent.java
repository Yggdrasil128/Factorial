package de.yggdrasil128.factorial.model.save;

public class SaveUpdatedEvent {

    private final Save save;

    public SaveUpdatedEvent(Save save) {
        this.save = save;
    }

    public Save getSave() {
        return save;
    }

}
