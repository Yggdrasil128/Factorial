package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.model.ModelChangedEvent;

public class ChangelistUpdatedEvent implements ModelChangedEvent {

    private final Changelist changelist;
    private final boolean updateProductionSteps;

    public ChangelistUpdatedEvent(Changelist changelist, boolean updateProductionSteps) {
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
