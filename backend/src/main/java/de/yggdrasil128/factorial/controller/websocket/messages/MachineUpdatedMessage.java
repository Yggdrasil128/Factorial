package de.yggdrasil128.factorial.controller.websocket.messages;

import de.yggdrasil128.factorial.model.machine.MachineStandalone;

public class MachineUpdatedMessage extends GameRelatedModelChangedMessage {

    private final MachineStandalone machine;

    public MachineUpdatedMessage(String runtimeId, int messageId, int gameId, MachineStandalone machine) {
        super(runtimeId, messageId, gameId);
        this.machine = machine;
    }

    public MachineStandalone getMachine() {
        return machine;
    }

}
