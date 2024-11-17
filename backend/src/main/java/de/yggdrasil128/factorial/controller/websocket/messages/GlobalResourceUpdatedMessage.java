package de.yggdrasil128.factorial.controller.websocket.messages;

import de.yggdrasil128.factorial.model.resource.global.GlobalResourceStandalone;

public class GlobalResourceUpdatedMessage extends SaveRelatedModelChangedMessage {

    private final GlobalResourceStandalone globalResource;

    public GlobalResourceUpdatedMessage(String runtimeId, int messageId, int saveId,
                                        GlobalResourceStandalone globalResource) {
        super(runtimeId, messageId, saveId);
        this.globalResource = globalResource;
    }

    public GlobalResourceStandalone getGlobalResource() {
        return globalResource;
    }
}
