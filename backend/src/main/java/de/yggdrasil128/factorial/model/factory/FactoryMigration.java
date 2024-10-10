package de.yggdrasil128.factorial.model.factory;

import de.yggdrasil128.factorial.model.productionstep.ProductionStepMigration;

import java.util.List;

import static java.util.Collections.emptyList;

public class FactoryMigration {

    private int ordinal;
    private String name;
    private String description;
    private String iconName;
    private List<ProductionStepMigration> productionSteps;
    private List<String> itemOrder;

    public FactoryMigration() {
        productionSteps = emptyList();
        itemOrder = emptyList();
    }

    public FactoryMigration(int ordinal, String name, String description, String iconName,
                            List<ProductionStepMigration> productionSteps, List<String> itemOrder) {
        this.ordinal = ordinal;
        this.name = name;
        this.description = description;
        this.iconName = iconName;
        this.productionSteps = productionSteps;
        this.itemOrder = itemOrder;
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

    public List<String> getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(List<String> itemOrder) {
        this.itemOrder = itemOrder;
    }

}
