package de.yggdrasil128.factorial.controller.websocket.messages;

import de.yggdrasil128.factorial.model.resource.local.LocalResourceStandalone;

public class LocalResourceUpdatedMessage extends SaveRelatedModelChangedMessage {

    private final LocalResourceStandalone localResource;

    public LocalResourceUpdatedMessage(String runtimeId, int messageId, int saveId,
                                       LocalResourceStandalone localResource) {
        super(runtimeId, messageId, saveId);
        this.localResource = localResource;
    }

    public LocalResourceStandalone getLocalResource() {
        return localResource;
    }
}
