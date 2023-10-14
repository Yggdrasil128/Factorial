package de.yggdrasil128.factorial.model.factory;

import de.yggdrasil128.factorial.model.productionstep.ProductionStepMigration;

import java.util.List;

import static java.util.Collections.emptyList;

public class FactoryMigration {

    private String name;
    private String description;
    private String iconName;
    private List<ProductionStepMigration> productionSteps = emptyList();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public List<ProductionStepMigration> getProductionSteps() {
        return productionSteps;
    }

    public void setProductionSteps(List<ProductionStepMigration> productionSteps) {
        this.productionSteps = productionSteps;
    }

}
