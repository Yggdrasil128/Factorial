package de.yggdrasil128.factorial.model.resource;

public class ResourceUpdatedEvent {

    private final Resource resource;

    public ResourceUpdatedEvent(Resource resource) {
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }

}
