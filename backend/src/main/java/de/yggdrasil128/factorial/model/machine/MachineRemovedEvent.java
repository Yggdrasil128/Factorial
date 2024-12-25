package de.yggdrasil128.factorial.model.machine;

import de.yggdrasil128.factorial.model.game.GameRelatedEntityRemovedEvent;

public class MachineRemovedEvent extends GameRelatedEntityRemovedEvent {

    private final int machineId;

    public MachineRemovedEvent(int gameId, int machineId) {
        super(gameId);
        this.machineId = machineId;
    }

    public int getMachineId() {
        return machineId;
    }

}
