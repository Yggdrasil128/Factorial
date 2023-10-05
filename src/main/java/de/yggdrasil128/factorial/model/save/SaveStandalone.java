package de.yggdrasil128.factorial.model.save;

import static java.util.Collections.emptyList;

import java.util.List;

public class SaveStandalone {

    private String name;
    private List<Integer> factoryIds = emptyList();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getFactoryIds() {
        return factoryIds;
    }

    public void setFactoryIds(List<Integer> factoryIds) {
        this.factoryIds = factoryIds;
    }

}
