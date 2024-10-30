package de.yggdrasil128.factorial.model.changelist;

public class ChangelistRemovedEvent {

    private final int saveId;
    private final int changelistId;

    public ChangelistRemovedEvent(int saveId, int changelistId) {
        this.saveId = saveId;
        this.changelistId = changelistId;
    }

    public int getSaveId() {
        return saveId;
    }

    public int getChangelistId() {
        return changelistId;
    }

}
