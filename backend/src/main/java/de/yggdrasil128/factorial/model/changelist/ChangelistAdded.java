package de.yggdrasil128.factorial.model.changelist;

public class ChangelistAdded extends ChangelistUpdated {

    public ChangelistAdded(Changelist changelist) {
        super(changelist, true);
    }

}
