package de.yggdrasil128.factorial.controller.websocket.messages;

public class ProductionStepRemovedMessage extends AbstractModelChangedMessage {

    private final int productionStepId;

    public ProductionStepRemovedMessage(String runtimeId, int messageId, int saveId, int productionStepId) {
        super(runtimeId, messageId, saveId);
        this.productionStepId = productionStepId;
    }

    public int getProductionStepId() {
        return productionStepId;
    }
}
