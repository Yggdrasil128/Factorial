package de.yggdrasil128.factorial.model.factory;

public class FactoryUpdatedEvent {

    private final Factory factory;

    public FactoryUpdatedEvent(Factory factory) {
        this.factory = factory;
    }

    public Factory getFactory() {
        return factory;
    }

}
