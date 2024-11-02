package de.yggdrasil128.factorial.controller.websocket.messages;

import de.yggdrasil128.factorial.model.factory.FactoryStandalone;

public class FactoryUpdatedMessage extends AbstractModelChangedMessage {

    private final FactoryStandalone factory;

    public FactoryUpdatedMessage(String runtimeId, int messageId, int saveId, FactoryStandalone factory) {
        super(runtimeId, messageId, saveId);
        this.factory = factory;
    }

    public FactoryStandalone getFactory() {
        return factory;
    }

}
