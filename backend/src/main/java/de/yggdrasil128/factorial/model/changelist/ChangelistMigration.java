package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.model.Fraction;

import java.util.Map;

import static java.util.Collections.emptyMap;

public class ChangelistMigration {

    private int ordinal;
    private String name;
    private boolean primary;
    private boolean active;
    private String iconName;
    private Map<String, Fraction> productionStepChanges = emptyMap();

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

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

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public Map<String, Fraction> getProductionStepChanges() {
        return productionStepChanges;
    }

    public void setProductionStepChanges(Map<String, Fraction> productionStepChanges) {
        this.productionStepChanges = productionStepChanges;
    }

}
