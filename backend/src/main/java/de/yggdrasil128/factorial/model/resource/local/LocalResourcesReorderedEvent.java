package de.yggdrasil128.factorial.model.resource.local;

import java.util.Collection;

public class LocalResourcesReorderedEvent {

    private final int saveId;
    private final Collection<LocalResource> resources;

    public LocalResourcesReorderedEvent(int saveId, Collection<LocalResource> resources) {
        this.saveId = saveId;
        this.resources = resources;
    }

    public int getSaveId() {
        return saveId;
    }

    public Collection<LocalResource> getResources() {
        return resources;
    }

}
