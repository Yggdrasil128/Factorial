package de.yggdrasil128.factorial.model.resource;

import java.util.Collection;

public class ResourcesReorderedEvent {

    private final int saveId;
    private final Collection<Resource> resources;

    public ResourcesReorderedEvent(int saveId, Collection<Resource> resources) {
        this.saveId = saveId;
        this.resources = resources;
    }

    public int getSaveId() {
        return saveId;
    }

    public Collection<Resource> getResources() {
        return resources;
    }

}
