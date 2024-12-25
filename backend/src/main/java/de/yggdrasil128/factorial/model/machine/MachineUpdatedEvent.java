package de.yggdrasil128.factorial.model.machine;

import de.yggdrasil128.factorial.model.game.GameRelatedEvent;

public class MachineUpdatedEvent implements GameRelatedEvent {

    private final Machine machine;

    public MachineUpdatedEvent(Machine machine) {
        this.machine = machine;
    }

    @Override
    public int getGameId() {
        return machine.getGame().getId();
    }

    public Machine getMachine() {
        return machine;
    }

}
