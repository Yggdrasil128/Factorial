package de.yggdrasil128.factorial.model.resource.global;

import de.yggdrasil128.factorial.model.save.SaveRelatedEvent;

public class GlobalResourceUpdatedEvent implements SaveRelatedEvent {

    private final GlobalResource resource;
    private final boolean complete;

    public GlobalResourceUpdatedEvent(GlobalResource resource, boolean complete) {
        this.resource = resource;
        this.complete = complete;
    }

    @Override
    public int getSaveId() {
        return resource.getSave().getId();
    }

    public GlobalResource getResource() {
        return resource;
    }

    public boolean isComplete() {
        return complete;
    }

}
