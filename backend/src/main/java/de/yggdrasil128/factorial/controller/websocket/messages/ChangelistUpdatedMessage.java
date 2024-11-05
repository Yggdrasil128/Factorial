package de.yggdrasil128.factorial.controller.websocket.messages;

import de.yggdrasil128.factorial.model.changelist.ChangelistStandalone;

public class ChangelistUpdatedMessage extends SaveRelatedModelChangedMessage {

    private final ChangelistStandalone changelist;

    public ChangelistUpdatedMessage(String runtimeId, int messageId, int saveId, ChangelistStandalone changelist) {
        super(runtimeId, messageId, saveId);
        this.changelist = changelist;
    }

    public ChangelistStandalone getChangelist() {
        return changelist;
    }
}
