package de.yggdrasil128.factorial.model.resource.local;

import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.resource.Resource;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class LocalResource extends Resource {

    @ManyToOne(optional = false)
    private Factory factory;
    @Column(nullable = false)
    private boolean importExport;

    public LocalResource() {
    }

    public LocalResource(Factory factory, LocalResourceStandalone standalone) {
        this.factory = factory;
        applyBasics(standalone);
    }

    public void applyBasics(LocalResourceStandalone standalone) {
        OptionalInputField.of(standalone.ordinal()).apply(this::setOrdinal);
        OptionalInputField.of(standalone.importExport()).apply(this::setImportExport);
    }

    public Factory getFactory() {
        return factory;
    }

    public void setFactory(Factory factory) {
        this.factory = factory;
    }

    public boolean isImportExport() {
        return importExport;
    }

    public void setImportExport(boolean importExport) {
        this.importExport = importExport;
    }

    @Override
    public String toString() {
        return super.toString() + ", import/export=" + importExport;
    }

}
