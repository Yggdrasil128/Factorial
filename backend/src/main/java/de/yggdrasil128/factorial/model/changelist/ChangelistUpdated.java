package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.model.ModelChanged;

public class ChangelistUpdated implements ModelChanged {

    private final Changelist changelist;
    private final boolean updateProductionSteps;

    public ChangelistUpdated(Changelist changelist, boolean updateProductionSteps) {
        this.changelist = changelist;
        this.updateProductionSteps = updateProductionSteps;
    }

    @Override
    public int getSaveId() {
        return changelist.getSave().getId();
    }

    public Changelist getChangelist() {
        return changelist;
    }

    public boolean isUpdateProductionSteps() {
        return updateProductionSteps;
    }

}
