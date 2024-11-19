package de.yggdrasil128.factorial.model.resource.local;

import de.yggdrasil128.factorial.engine.ResourceContributions;

public class LocalResourceContributionsChangedEvent extends LocalResourceUpdatedEvent {

    private final ResourceContributions contributions;

    public LocalResourceContributionsChangedEvent(LocalResource resource, boolean importExportChanged,
                                                  ResourceContributions contributions) {
        super(resource, importExportChanged);
        this.contributions = contributions;
    }

    public ResourceContributions getContributions() {
        return contributions;
    }

}
