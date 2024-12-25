package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.model.save.SaveRelatedEvent;

public class ChangelistUpdatedEvent implements SaveRelatedEvent {

    private final Changelist changelist;

    public ChangelistUpdatedEvent(Changelist changelist) {
        this.changelist = changelist;
    }

    @Override
    public int getSaveId() {
        return changelist.getSave().getId();
    }

    public Changelist getChangelist() {
        return changelist;
    }

}
