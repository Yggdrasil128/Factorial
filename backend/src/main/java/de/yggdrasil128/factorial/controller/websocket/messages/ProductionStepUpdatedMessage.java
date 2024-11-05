package de.yggdrasil128.factorial.controller.websocket.messages;

import de.yggdrasil128.factorial.model.productionstep.ProductionStepStandalone;

public class ProductionStepUpdatedMessage extends SaveRelatedModelChangedMessage {

    private final ProductionStepStandalone productionStep;

    public ProductionStepUpdatedMessage(String runtimeId, int messageId, int saveId, ProductionStepStandalone productionStep) {
        super(runtimeId, messageId, saveId);
        this.productionStep = productionStep;
    }

    public ProductionStepStandalone getProductionStep() {
        return productionStep;
    }
}
