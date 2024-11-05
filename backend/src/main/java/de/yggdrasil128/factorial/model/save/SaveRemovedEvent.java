package de.yggdrasil128.factorial.model.save;

public class SaveRemovedEvent {

    private final int saveId;

    public SaveRemovedEvent(int saveId) {
        this.saveId = saveId;
    }

    public int getSaveId() {
        return saveId;
    }

}
