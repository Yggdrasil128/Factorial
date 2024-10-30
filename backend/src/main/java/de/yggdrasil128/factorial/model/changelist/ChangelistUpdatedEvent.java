package de.yggdrasil128.factorial.model.changelist;

public class ChangelistUpdatedEvent {

    private final Changelist changelist;
    private final boolean updateProductionSteps;

    public ChangelistUpdatedEvent(Changelist changelist, boolean updateProductionSteps) {
        this.changelist = changelist;
        this.updateProductionSteps = updateProductionSteps;
    }

    public Changelist getChangelist() {
        return changelist;
    }

    public boolean isUpdateProductionSteps() {
        return updateProductionSteps;
    }

}
