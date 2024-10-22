package de.yggdrasil128.factorial.model.resource;

import de.yggdrasil128.factorial.engine.ResourceContributions;

public class ResourceContributionsChanged extends ResourceUpdated {

    private final ResourceContributions contributions;

    public ResourceContributionsChanged(Resource resource, ResourceContributions contributions) {
        super(resource);
        this.contributions = contributions;
    }

    public ResourceContributions getContributions() {
        return contributions;
    }

}
