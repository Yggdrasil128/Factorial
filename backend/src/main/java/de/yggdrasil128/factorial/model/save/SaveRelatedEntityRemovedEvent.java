package de.yggdrasil128.factorial.model.save;

public abstract class SaveRelatedEntityRemovedEvent implements SaveRelatedEvent {

    private final int saveId;

    protected SaveRelatedEntityRemovedEvent(int saveId) {
        this.saveId = saveId;
    }

    @Override
    public int getSaveId() {
        return saveId;
    }

}
