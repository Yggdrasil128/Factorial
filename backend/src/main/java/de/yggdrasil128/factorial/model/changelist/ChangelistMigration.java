package de.yggdrasil128.factorial.model.changelist;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;

public class ChangelistMigration {

    private int ordinal;
    private String name;
    private boolean primary;
    private boolean active;
    private String iconName;
    private Map<String, List<ProductionStepChangeMigration>> productionStepChanges;

    public ChangelistMigration() {
        productionStepChanges = emptyMap();
    }

    public ChangelistMigration(int ordinal, String name, boolean primary, boolean active, String iconName,
                               Map<String, List<ProductionStepChangeMigration>> productionStepChanges) {
        this.ordinal = ordinal;
        this.name = name;
        this.primary = primary;
        this.active = active;
        this.iconName = iconName;
        this.productionStepChanges = productionStepChanges;
    }

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

    public Map<String, List<ProductionStepChangeMigration>> getProductionStepChanges() {
        return productionStepChanges;
    }

    public void setProductionStepChanges(Map<String, List<ProductionStepChangeMigration>> productionStepChanges) {
        this.productionStepChanges = productionStepChanges;
    }

}
