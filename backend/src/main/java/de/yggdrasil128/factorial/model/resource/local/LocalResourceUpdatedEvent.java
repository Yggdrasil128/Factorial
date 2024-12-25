package de.yggdrasil128.factorial.model.resource.local;

import de.yggdrasil128.factorial.model.save.SaveRelatedEvent;

public class LocalResourceUpdatedEvent implements SaveRelatedEvent {

    private final LocalResource resource;
    private final boolean importExportChanged;

    public LocalResourceUpdatedEvent(LocalResource resource, boolean importExportChanged) {
        this.resource = resource;
        this.importExportChanged = importExportChanged;
    }

    @Override
    public int getSaveId() {
        return resource.getFactory().getSave().getId();
    }

    public LocalResource getResource() {
        return resource;
    }

    public boolean isImportExportChanged() {
        return importExportChanged;
    }

}
