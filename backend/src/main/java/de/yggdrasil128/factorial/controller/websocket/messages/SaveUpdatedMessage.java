package de.yggdrasil128.factorial.controller.websocket.messages;

import de.yggdrasil128.factorial.model.save.SaveStandalone;

public class SaveUpdatedMessage extends AbstractModelChangedMessage {

    private final SaveStandalone save;

    public SaveUpdatedMessage(String runtimeId, int messageId, int saveId, SaveStandalone save) {
        super(runtimeId, messageId, saveId);
        this.save = save;
    }

    public SaveStandalone getSave() {
        return save;
    }

}
