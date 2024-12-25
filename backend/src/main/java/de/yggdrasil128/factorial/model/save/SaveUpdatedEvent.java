package de.yggdrasil128.factorial.model.save;

public class SaveUpdatedEvent implements SaveRelatedEvent {

    private final Save save;

    public SaveUpdatedEvent(Save save) {
        this.save = save;
    }

    @Override
    public int getSaveId() {
        return save.getId();
    }

    public Save getSave() {
        return save;
    }

}
