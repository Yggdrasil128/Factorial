package de.yggdrasil128.factorial.model.resource.local;

import de.yggdrasil128.factorial.engine.ResourceContributions;

public class LocalResourceContributionsChangedEvent extends LocalResourceUpdatedEvent {

    private final ResourceContributions contributions;

    public LocalResourceContributionsChangedEvent(LocalResource resource, ResourceContributions contributions) {
        super(resource, true);
        this.contributions = contributions;
    }

    public ResourceContributions getContributions() {
        return contributions;
    }

}
