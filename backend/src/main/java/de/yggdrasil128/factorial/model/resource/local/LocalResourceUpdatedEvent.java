package de.yggdrasil128.factorial.model.resource.local;

public class LocalResourceUpdatedEvent {

    private final LocalResource resource;
    private final boolean complete;

    public LocalResourceUpdatedEvent(LocalResource resource, boolean complete) {
        this.resource = resource;
        this.complete = complete;
    }

    public LocalResource getResource() {
        return resource;
    }

    public boolean isComplete() {
        return complete;
    }

}
