package de.yggdrasil128.factorial.controller.websocket.messages;

import de.yggdrasil128.factorial.model.resource.ResourceStandalone;

public class ResourceUpdatedMessage extends AbstractModelChangedMessage {

    private final ResourceStandalone resource;

    public ResourceUpdatedMessage(String runtimeId, int messageId, int saveId, ResourceStandalone resource) {
        super(runtimeId, messageId, saveId);
        this.resource = resource;
    }

    public ResourceStandalone getResource() {
        return resource;
    }
}
