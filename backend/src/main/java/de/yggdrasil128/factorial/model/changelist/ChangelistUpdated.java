package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.model.ModelChanged;

public class ChangelistUpdated implements ModelChanged {

    private final Changelist changelist;
    private final boolean primaryActiveChanged;

    public ChangelistUpdated(Changelist changelist, boolean primaryActiveChanged) {
        this.changelist = changelist;
        this.primaryActiveChanged = primaryActiveChanged;
    }

    @Override
    public int getSaveId() {
        return changelist.getSave().getId();
    }

    public Changelist getChangelist() {
        return changelist;
    }

    public boolean isPrimaryActiveChanged() {
        return primaryActiveChanged;
    }

}
