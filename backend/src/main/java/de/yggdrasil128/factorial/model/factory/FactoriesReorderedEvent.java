package de.yggdrasil128.factorial.model.factory;

import de.yggdrasil128.factorial.model.save.SaveRelatedEvent;

import java.util.Collection;

public class FactoriesReorderedEvent implements SaveRelatedEvent {

    private final int saveId;
    private final Collection<Factory> factories;

    public FactoriesReorderedEvent(int saveId, Collection<Factory> factories) {
        this.saveId = saveId;
        this.factories = factories;
    }

    @Override
    public int getSaveId() {
        return saveId;
    }

    public Collection<Factory> getFactories() {
        return factories;
    }

}
