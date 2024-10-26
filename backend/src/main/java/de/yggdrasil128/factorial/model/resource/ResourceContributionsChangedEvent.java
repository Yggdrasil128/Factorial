package de.yggdrasil128.factorial.model.resource;

import de.yggdrasil128.factorial.engine.ResourceContributions;

public class ResourceContributionsChangedEvent extends ResourceUpdatedEvent {

    private final ResourceContributions contributions;

    public ResourceContributionsChangedEvent(Resource resource, ResourceContributions contributions) {
        super(resource);
        this.contributions = contributions;
    }

    public ResourceContributions getContributions() {
        return contributions;
    }

}
