package de.yggdrasil128.factorial.model.changelist;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.productionstepchange.ProductionStepChange;
import de.yggdrasil128.factorial.model.save.Save;
import jakarta.persistence.*;

import java.util.List;

import static java.util.Collections.emptyList;

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
    private List<ProductionStepChange> productionStepChanges = emptyList();

    public Changelist() {
    }

    public Changelist(Save save, String name, boolean primary, boolean active, Icon icon,
        List<ProductionStepChange> productionStepChanges) {
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

    public List<ProductionStepChange> getProductionStepChanges() {
        return productionStepChanges;
    }

    public void setProductionStepChanges(List<ProductionStepChange> productionStepChanges) {
        this.productionStepChanges = productionStepChanges;
    }

}
