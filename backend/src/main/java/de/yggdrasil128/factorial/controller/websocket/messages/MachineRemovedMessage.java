package de.yggdrasil128.factorial.controller.websocket.messages;

public class MachineRemovedMessage extends GameRelatedModelChangedMessage {

    private final int machineId;

    public MachineRemovedMessage(String runtimeId, int messageId, int gameId, int machineId) {
        super(runtimeId, messageId, gameId);
        this.machineId = machineId;
    }

    public int getMachineId() {
        return machineId;
    }

}
