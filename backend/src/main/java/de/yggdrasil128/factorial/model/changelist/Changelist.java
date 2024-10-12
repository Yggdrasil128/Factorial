package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.save.Save;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

@Entity
public class Changelist implements NamedModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne(optional = false)
    private Save save;
    @Column(nullable = false)
    private int ordinal;
    private String name;
    @Column(name = "is_primary")
    private boolean primary;
    private boolean active;
    @ManyToOne
    private Icon icon;
    @ElementCollection
    private Map<ProductionStep, Fraction> productionStepChanges;

    public Changelist() {
    }

    public Changelist(Save save, ChangelistStandalone standalone) {
        this.save = save;
        ordinal = standalone.getOrdinal();
        name = standalone.getName();
        primary = standalone.isPrimary();
        active = standalone.isActive();
        icon = null;
        productionStepChanges = new HashMap<>();
    }

    public Changelist(Save save, int ordinal, String name, boolean primary, boolean active, Icon icon,
                      Map<ProductionStep, Fraction> productionStepChanges) {
        this.save = save;
        this.ordinal = ordinal;
        this.name = name;
        this.primary = primary;
        this.active = active;
        this.icon = icon;
        this.productionStepChanges = productionStepChanges;
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

    public int getOrdinal() {
        return ordinal;
    }

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
        return name;
    }

}
