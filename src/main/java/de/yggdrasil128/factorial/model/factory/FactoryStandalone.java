package de.yggdrasil128.factorial.model.factory;

import static java.util.Collections.emptyList;

import java.util.List;

import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.save.Save;

public class FactoryStandalone {

    private String name;
    private String description;
    private int iconId;
    private List<Integer> productionStepIds = emptyList();

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

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public List<Integer> getProductionStepIds() {
        return productionStepIds;
    }

    public void setProductionStepIds(List<Integer> productionStepIds) {
        this.productionStepIds = productionStepIds;
    }

    Factory with(Save save, Icon icon) {
        return new Factory(save, name, description, icon, emptyList());
    }

}
