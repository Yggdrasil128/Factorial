package de.yggdrasil128.factorial.model.resource;

public class ResourceRemovedEvent {

    private final int saveId;
    private final int factoryId;
    private final int resourceId;

    public ResourceRemovedEvent(int saveId, int factoryId, int resourceId) {
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
