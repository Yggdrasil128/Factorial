package de.yggdrasil128.factorial.model.machine;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class MachineService extends ModelService<Machine, MachineRepository> {

    private final ApplicationEventPublisher events;
    private final GameRepository games;

    public MachineService(MachineRepository repository, ApplicationEventPublisher events, GameRepository games) {
        super(repository);
        this.events = events;
        this.games = games;
    }

    @Override
    public Machine create(Machine entity) {
        Machine machine = super.create(entity);
        events.publishEvent(new MachineUpdatedEvent(machine));
        return machine;
    }

    @Override
    public Machine update(Machine entity) {
        Machine machine = super.update(entity);
        events.publishEvent(new MachineUpdatedEvent(machine));
        return machine;
    }

    @Override
    public void delete(int id) {
        Game game = games.findByMachinesId(id);
        super.delete(id);
        if (null != game) {
            events.publishEvent(new MachineRemovedEvent(game.getId(), id));
        }
    }

}
