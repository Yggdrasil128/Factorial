package de.yggdrasil128.factorial.model.save;

import java.util.List;

import static java.util.Collections.emptyList;

public class SaveStandalone {

    private String name;
    private List<Integer> factoryIds = emptyList();
    private List<Integer> changeListIds = emptyList();

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

    public List<Integer> getChangeListIds() {
        return changeListIds;
    }

    public void setChangeListIds(List<Integer> changeListIds) {
        this.changeListIds = changeListIds;
    }

}
