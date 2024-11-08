package de.yggdrasil128.factorial.model.changelist;

public class ChangelistUpdatedEvent {

    private final Changelist changelist;

    public ChangelistUpdatedEvent(Changelist changelist) {
        this.changelist = changelist;
    }

    public Changelist getChangelist() {
        return changelist;
    }

}
