package de.yggdrasil128.factorial.model.factory;

import de.yggdrasil128.factorial.model.productionstep.ProductionStepMigration;
import de.yggdrasil128.factorial.model.xgress.XgressMigration;

import java.util.List;

import static java.util.Collections.emptyList;

public class FactoryMigration {

    private int ordinal;
    private String name;
    private String description;
    private String iconName;
    private List<ProductionStepMigration> productionSteps = emptyList();
    private List<String> itemOrder = emptyList();
    private List<XgressMigration> ingresses = emptyList();
    private List<XgressMigration> egresses = emptyList();

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

    public List<XgressMigration> getIngresses() {
        return ingresses;
    }

    public void setIngresses(List<XgressMigration> ingresses) {
        this.ingresses = ingresses;
    }

    public List<XgressMigration> getEgresses() {
        return egresses;
    }

    public void setEgresses(List<XgressMigration> egresses) {
        this.egresses = egresses;
    }

    public List<String> getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(List<String> itemOrder) {
        this.itemOrder = itemOrder;
    }

}
