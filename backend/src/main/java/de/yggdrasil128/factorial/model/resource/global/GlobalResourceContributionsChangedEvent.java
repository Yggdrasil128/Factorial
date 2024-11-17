package de.yggdrasil128.factorial.model.resource.global;

import de.yggdrasil128.factorial.engine.ResourceContributions;

public class GlobalResourceContributionsChangedEvent extends GlobalResourceUpdatedEvent {

    private final ResourceContributions contributions;

    public GlobalResourceContributionsChangedEvent(GlobalResource resource, ResourceContributions contributions) {
        super(resource, true);
        this.contributions = contributions;
    }

    public ResourceContributions getContributions() {
        return contributions;
    }

}
