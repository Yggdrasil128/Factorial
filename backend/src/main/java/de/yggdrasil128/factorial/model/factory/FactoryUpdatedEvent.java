package de.yggdrasil128.factorial.model.factory;

import de.yggdrasil128.factorial.model.save.SaveRelatedEvent;

public class FactoryUpdatedEvent implements SaveRelatedEvent {

    private final Factory factory;

    public FactoryUpdatedEvent(Factory factory) {
        this.factory = factory;
    }

    @Override
    public int getSaveId() {
        return factory.getSave().getId();
    }

    public Factory getFactory() {
        return factory;
    }

}
