package de.yggdrasil128.factorial.controller.websocket.messages;

public class FactoryRemovedMessage extends AbstractModelChangedMessage {

    private final int factoryId;

    public FactoryRemovedMessage(String runtimeId, int messageId, int saveId, int factoryId) {
        super(runtimeId, messageId, saveId);
        this.factoryId = factoryId;
    }

    public int getFactoryId() {
        return factoryId;
    }

}
