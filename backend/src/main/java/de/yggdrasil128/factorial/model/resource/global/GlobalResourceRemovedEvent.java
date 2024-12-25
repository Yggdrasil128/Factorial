package de.yggdrasil128.factorial.model.resource.global;

import de.yggdrasil128.factorial.model.save.SaveRelatedEntityRemovedEvent;

public class GlobalResourceRemovedEvent extends SaveRelatedEntityRemovedEvent {

    private final int resourceId;

    public GlobalResourceRemovedEvent(int saveId, int resourceId) {
        super(saveId);
        this.resourceId = resourceId;
    }

    public int getResourceId() {
        return resourceId;
    }

}
