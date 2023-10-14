package de.yggdrasil128.factorial.model.changelist;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.save.Save;
import jakarta.persistence.*;

import java.util.Map;

@Entity
public class Changelist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne(optional = false)
    @JsonBackReference
    private Save save;
    private String name;
    @ManyToOne
    private Icon icon;
    @Column(name = "is_primary")
    private boolean primary;
    private boolean active;
    @ElementCollection
    private Map<ProductionStep, Fraction> productionStepChanges;

    public Changelist() {
    }

    public Changelist(Save save, String name, boolean primary, boolean active, Icon icon,
                      Map<ProductionStep, Fraction> productionStepChanges) {
        this.save = save;
        this.name = name;
        this.primary = primary;
        this.active = active;
        this.icon = icon;
        this.productionStepChanges = productionStepChanges;
    }

    public int getId() {
        return id;
    }

    public Save getSave() {
        return save;
    }

    public void setSave(Save save) {
        this.save = save;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
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

    public Map<ProductionStep, Fraction> getProductionStepChanges() {
        return productionStepChanges;
    }

    public void setProductionStepChanges(Map<ProductionStep, Fraction> productionStepChanges) {
        this.productionStepChanges = productionStepChanges;
    }

}
