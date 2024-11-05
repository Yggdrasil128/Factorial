package de.yggdrasil128.factorial.model.machine;

public class MachineUpdatedEvent {

    private final Machine machine;

    public MachineUpdatedEvent(Machine machine) {
        this.machine = machine;
    }

    public Machine getMachine() {
        return machine;
    }

}
