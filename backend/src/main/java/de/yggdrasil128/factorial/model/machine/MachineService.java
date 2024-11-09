package de.yggdrasil128.factorial.model.machine;

import de.yggdrasil128.factorial.model.AsyncHelper;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameRepository;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
public class MachineService extends ModelService<Machine, MachineRepository> {

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

    @Transactional
    public void create(int gameId, MachineStandalone standalone, CompletableFuture<Void> result) {
        Game game = gameRepository.findById(gameId).orElseThrow(ModelService::reportNotFound);
        Machine machine = new Machine(game, standalone);
        applyRelations(machine, standalone);
        AsyncHelper.complete(result);
        machine = create(machine);
        game.getMachines().add(machine);
        events.publishEvent(new MachineUpdatedEvent(machine));
    }

    @Transactional
    public void update(int id, MachineStandalone standalone, CompletableFuture<Void> result) {
        Machine machine = get(id);
        machine.applyBasics(standalone);
        applyRelations(machine, standalone);
        AsyncHelper.complete(result);
        machine = update(machine);
        events.publishEvent(new MachineUpdatedEvent(machine));
    }

    private void applyRelations(Machine machine, MachineStandalone standalone) {
        OptionalInputField.ofId(standalone.iconId(), iconService::get).apply(machine::setIcon);
        OptionalInputField.ofIds(standalone.machineModifierIds(), recipeModifierService::get)
                .applyList(machine::setMachineModifiers);
    }

    @Transactional
    public void delete(int id, CompletableFuture<Void> result) {
        Game game = gameRepository.findByMachinesId(id);
        if (null == game) {
            throw report(HttpStatus.CONFLICT, "machine does not belong to a game");
        }
        AsyncHelper.complete(result);
        delete(id);
        events.publishEvent(new MachineRemovedEvent(game.getId(), id));
    }

}
