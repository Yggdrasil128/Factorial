package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.model.ModelChangedEvent;

public class ChangelistRemovedEvent implements ModelChangedEvent {

    private final int saveId;
    private final int changelistId;

    public ChangelistRemovedEvent(int saveId, int changelistId) {
        this.saveId = saveId;
        this.changelistId = changelistId;
    }

    @Override
    public int getSaveId() {
        return saveId;
    }

    public int getChangelistId() {
        return changelistId;
    }

}
