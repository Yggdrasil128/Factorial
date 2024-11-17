package de.yggdrasil128.factorial.model.resource.global;

public class GlobalResourceRemovedEvent {

    private final int saveId;
    private final int resourceId;

    public GlobalResourceRemovedEvent(int saveId, int resourceId) {
        this.saveId = saveId;
        this.resourceId = resourceId;
    }

    public int getSaveId() {
        return saveId;
    }

    public int getResourceId() {
        return resourceId;
    }

}
