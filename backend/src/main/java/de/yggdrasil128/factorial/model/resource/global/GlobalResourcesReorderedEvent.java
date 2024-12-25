package de.yggdrasil128.factorial.model.resource.global;

import de.yggdrasil128.factorial.model.save.SaveRelatedEvent;

import java.util.Collection;

public class GlobalResourcesReorderedEvent implements SaveRelatedEvent {

    private final int saveId;
    private final Collection<GlobalResource> resources;

    public GlobalResourcesReorderedEvent(int saveId, Collection<GlobalResource> resources) {
        this.saveId = saveId;
        this.resources = resources;
    }

    @Override
    public int getSaveId() {
        return saveId;
    }

    public Collection<GlobalResource> getResources() {
        return resources;
    }

}
