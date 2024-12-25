package de.yggdrasil128.factorial.model.resource.local;

import de.yggdrasil128.factorial.model.save.SaveRelatedEntityRemovedEvent;

public class LocalResourceRemovedEvent extends SaveRelatedEntityRemovedEvent {

    private final int factoryId;
    private final int resourceId;

    public LocalResourceRemovedEvent(int saveId, int factoryId, int resourceId) {
        super(saveId);
        this.factoryId = factoryId;
        this.resourceId = resourceId;
    }

    public int getFactoryId() {
        return factoryId;
    }

    public int getResourceId() {
        return resourceId;
    }

}
