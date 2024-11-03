package de.yggdrasil128.factorial.model.factory;

import java.util.Collection;

public class FactoriesReorderedEvent {

    private final int saveId;
    private final Collection<Factory> factories;

    public FactoriesReorderedEvent(int saveId, Collection<Factory> factories) {
        this.saveId = saveId;
        this.factories = factories;
    }

    public int getSaveId() {
        return saveId;
    }

    public Collection<Factory> getFactories() {
        return factories;
    }

}
