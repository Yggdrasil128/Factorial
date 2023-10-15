package de.yggdrasil128.factorial.model.factory;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepMigration;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

public class FactoryMigration {

    private int ordinal;
    private String name;
    private String description;
    private String iconName;
    private List<ProductionStepMigration> productionSteps = emptyList();
    private List<String> itemOrder = emptyList();
    private Map<String, Map<String, Fraction>> ingresses = emptyMap();
    private Map<String, Map<String, Fraction>> egresses = emptyMap();

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

    public Map<String, Map<String, Fraction>> getIngresses() {
        return ingresses;
    }

    public void setIngresses(Map<String, Map<String, Fraction>> ingresses) {
        this.ingresses = ingresses;
    }

    public Map<String, Map<String, Fraction>> getEgresses() {
        return egresses;
    }

    public void setEgresses(Map<String, Map<String, Fraction>> egresses) {
        this.egresses = egresses;
    }

    public List<String> getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(List<String> itemOrder) {
        this.itemOrder = itemOrder;
    }

}
