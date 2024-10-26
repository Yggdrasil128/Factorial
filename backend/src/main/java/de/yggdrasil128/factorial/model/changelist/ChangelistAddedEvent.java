package de.yggdrasil128.factorial.model.changelist;

public class ChangelistAddedEvent extends ChangelistUpdatedEvent {

    public ChangelistAddedEvent(Changelist changelist) {
        super(changelist, true);
    }

}
