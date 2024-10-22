package de.yggdrasil128.factorial.model.resource;

import de.yggdrasil128.factorial.model.ModelChanged;

public class ResourceRemoved implements ModelChanged {

    private final int saveId;
    private final int factoryId;
    private final int resourceId;

    public ResourceRemoved(int saveId, int factoryId, int resourceId) {
        this.saveId = saveId;
        this.factoryId = factoryId;
        this.resourceId = resourceId;
    }

    @Override
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
