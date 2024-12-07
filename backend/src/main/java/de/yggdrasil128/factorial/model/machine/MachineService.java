package de.yggdrasil128.factorial.model.machine;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.ParentedModelService;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameRepository;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class MachineService extends ParentedModelService<Machine, MachineStandalone, Game, MachineRepository> {

    private final ApplicationEventPublisher events;
    private final GameRepository gameRepository;
    private final IconService iconService;
    private final RecipeModifierService recipeModifierService;

    public MachineService(MachineRepository repository, ApplicationEventPublisher events, GameRepository gameRepository,
                          IconService iconService, RecipeModifierService recipeModifierService) {
        super(repository);
        this.events = events;
        this.gameRepository = gameRepository;
        this.iconService = iconService;
        this.recipeModifierService = recipeModifierService;
    }

    @Override
    protected int getEntityId(Machine machine) {
        return machine.getId();
    }

    @Override
    protected int getStandaloneId(MachineStandalone standalone) {
        return standalone.id();
    }

    @Override
    protected Game getParentEntity(int parentId) {
        return gameRepository.findById(parentId).orElseThrow(ModelService::reportNotFound);
    }

    @Override
    protected Machine prepareCreate(Game game, MachineStandalone standalone) {
        if (null == standalone.name()) {
            throw report(HttpStatus.BAD_REQUEST, "'name' is required");
        }
        ensureUniqueName(game, standalone);
        Machine machine = new Machine(game, standalone);
        applyRelations(machine, standalone);
        return machine;
    }

    @Override
    protected void handleBulkCreate(Game game, Iterable<Machine> machines) {
        for (Machine machine : machines) {
            game.getMachines().add(machine);
            events.publishEvent(new MachineUpdatedEvent(machine));
        }
        gameRepository.save(game);
    }

    @Override
    protected void prepareUpdate(Machine machine, MachineStandalone standalone) {
        if (!machine.getName().equals(standalone.name())) {
            ensureUniqueName(machine.getGame(), standalone);
        }
        machine.applyBasics(standalone);
        applyRelations(machine, standalone);
    }

    private void ensureUniqueName(Game game, MachineStandalone standalone) {
        if (repository.existsByGameIdAndName(game.getId(), standalone.name())) {
            throw report(HttpStatus.CONFLICT, "A MAchine with that name already exists");
        }
    }

    private void applyRelations(Machine machine, MachineStandalone standalone) {
        OptionalInputField.ofId(standalone.iconId(), iconService::get).apply(machine::setIcon);
        OptionalInputField.ofIds(standalone.machineModifierIds(), recipeModifierService::get)
                .applyList(machine::setMachineModifiers);
    }

    @Override
    protected void handleUpdate(Machine machine) {
        events.publishEvent(new MachineUpdatedEvent(machine));
    }

    @Override
    protected Game findParentEntity(int id) {
        Game game = gameRepository.findByMachinesId(id);
        if (null == game) {
            throw report(HttpStatus.CONFLICT, "machine does not belong to a game");
        }
        game.getMachines().remove(get(id));
        return game;
    }

    @Override
    protected void handleDelete(Game game, int id) {
        events.publishEvent(new MachineRemovedEvent(game.getId(), id));
    }

}
