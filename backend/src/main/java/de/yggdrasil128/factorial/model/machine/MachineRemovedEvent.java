package de.yggdrasil128.factorial.model.machine;

public class MachineRemovedEvent {

    private final int gameId;
    private final int machineId;

    public MachineRemovedEvent(int gameId, int machineId) {
        this.gameId = gameId;
        this.machineId = machineId;
    }

    public int getGameId() {
        return gameId;
    }

    public int getMachineId() {
        return machineId;
    }

}
