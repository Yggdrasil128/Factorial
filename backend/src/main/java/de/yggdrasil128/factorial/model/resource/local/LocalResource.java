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
    private boolean imported;
    @Column(nullable = false)
    private boolean exported;

    public LocalResource() {
    }

    public LocalResource(Factory factory, LocalResourceStandalone standalone) {
        this.factory = factory;
        applyBasics(standalone);
    }

    public void applyBasics(LocalResourceStandalone standalone) {
        OptionalInputField.of(standalone.ordinal()).apply(this::setOrdinal);
        OptionalInputField.of(standalone.imported()).apply(this::setImported);
        OptionalInputField.of(standalone.exported()).apply(this::setExported);
    }

    public Factory getFactory() {
        return factory;
    }

    public void setFactory(Factory factory) {
        this.factory = factory;
    }

    public boolean isImported() {
        return imported;
    }

    public void setImported(boolean imported) {
        this.imported = imported;
    }

    public boolean isExported() {
        return exported;
    }

    public void setExported(boolean exported) {
        this.exported = exported;
    }

    @Override
    public String toString() {
        return super.toString() + ", imported=" + imported + ", exported=" + exported;
    }

}
