package de.yggdrasil128.factorial.model.resource;

import de.yggdrasil128.factorial.model.ModelChangedEvent;

public class ResourceUpdatedEvent implements ModelChangedEvent {

    private final Resource resource;

    public ResourceUpdatedEvent(Resource resource) {
        this.resource = resource;
    }

    @Override
    public int getSaveId() {
        return resource.getFactory().getSave().getId();
    }

    public Resource getResource() {
        return resource;
    }

}
