package de.yggdrasil128.factorial.model.resource;

public class ResourceUpdatedEvent {

    private final Resource resource;
    private final boolean complete;

    public ResourceUpdatedEvent(Resource resource, boolean complete) {
        this.resource = resource;
        this.complete = complete;
    }

    public Resource getResource() {
        return resource;
    }

    public boolean isComplete() {
        return complete;
    }

}
