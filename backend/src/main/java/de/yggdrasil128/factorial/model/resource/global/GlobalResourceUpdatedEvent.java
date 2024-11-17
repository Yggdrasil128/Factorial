package de.yggdrasil128.factorial.model.resource.global;

public class GlobalResourceUpdatedEvent {

    private final GlobalResource resource;
    private final boolean complete;

    public GlobalResourceUpdatedEvent(GlobalResource resource, boolean complete) {
        this.resource = resource;
        this.complete = complete;
    }

    public GlobalResource getResource() {
        return resource;
    }

    public boolean isComplete() {
        return complete;
    }

}
