package de.yggdrasil128.factorial.model.resource;

import de.yggdrasil128.factorial.model.ModelChanged;

public class ResourceUpdated implements ModelChanged {

    private final Resource resource;

    public ResourceUpdated(Resource resource) {
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
