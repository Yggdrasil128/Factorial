package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.model.save.SaveRelatedEntityRemovedEvent;

public class ChangelistRemovedEvent extends SaveRelatedEntityRemovedEvent {

    private final int changelistId;

    public ChangelistRemovedEvent(int saveId, int changelistId) {
        super(saveId);
        this.changelistId = changelistId;
    }

    public int getChangelistId() {
        return changelistId;
    }

}
