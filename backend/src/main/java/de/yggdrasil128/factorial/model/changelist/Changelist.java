package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.model.*;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.save.Save;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"save_id", "name"}))
public class Changelist implements NamedModel, OrderedModel {

    @Id
    @GeneratedValue
    private int id;
    @ManyToOne(optional = false)
    private Save save;
    @Column(nullable = false)
    private int ordinal;
    private String name = "";
    @Column(name = "is_primary")
    private boolean primary;
    private boolean active;
    @ManyToOne
    private Icon icon;
    // TODO this currently prevents us from deleting a production step that has a change associated with it
    @ElementCollection
    @Convert(converter = FractionConverter.class, attributeName = "value")
    private Map<ProductionStep, Fraction> productionStepChanges;

    public Changelist() {
    }

    public Changelist(Save save, ChangelistStandalone standalone) {
        this.save = save;
        productionStepChanges = new HashMap<>();
        applyBasics(standalone);
    }

    public void applyBasics(ChangelistStandalone standalone) {
        OptionalInputField.of(standalone.ordinal()).apply(this::setOrdinal);
        OptionalInputField.of(standalone.name()).apply(this::setName);
        OptionalInputField.of(standalone.primary()).apply(this::setPrimary);
        OptionalInputField.of(standalone.active()).apply(this::setActive);
    }

    @Override
    public int getId() {
        return id;
    }

    public Save getSave() {
        return save;
    }

    public void setSave(Save save) {
        this.save = save;
    }

    @Override
    public int getOrdinal() {
        return ordinal;
    }

    @Override
    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public Map<ProductionStep, Fraction> getProductionStepChanges() {
        return productionStepChanges;
    }

    public void setProductionStepChanges(Map<ProductionStep, Fraction> productionStepChanges) {
        this.productionStepChanges = productionStepChanges;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Changelist && id == ((Changelist) obj).id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return name + ": " + productionStepChanges;
    }

}
