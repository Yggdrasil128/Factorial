package de.yggdrasil128.factorial.model.resource.local;

public class LocalResourceRemovedEvent {

    private final int saveId;
    private final int factoryId;
    private final int resourceId;

    public LocalResourceRemovedEvent(int saveId, int factoryId, int resourceId) {
        this.saveId = saveId;
        this.factoryId = factoryId;
        this.resourceId = resourceId;
    }

    public int getSaveId() {
        return saveId;
    }

    public int getFactoryId() {
        return factoryId;
    }

    public int getResourceId() {
        return resourceId;
    }

}
