package de.yggdrasil128.factorial.model.resource.local;

public class LocalResourceUpdatedEvent {

    private final LocalResource resource;
    private final boolean importExportChanged;

    public LocalResourceUpdatedEvent(LocalResource resource, boolean importExportChanged) {
        this.resource = resource;
        this.importExportChanged = importExportChanged;
    }

    public LocalResource getResource() {
        return resource;
    }

    public boolean isImportExportChanged() {
        return importExportChanged;
    }

}
