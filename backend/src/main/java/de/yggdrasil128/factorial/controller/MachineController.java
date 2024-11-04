package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameService;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.machine.Machine;
import de.yggdrasil128.factorial.model.machine.MachineService;
import de.yggdrasil128.factorial.model.machine.MachineStandalone;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierService;
import de.yggdrasil128.factorial.model.save.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MachineController {

    private final GameService gameService;
    private final IconService iconService;
    private final RecipeModifierService recipeModifierService;
    private final MachineService machineService;
    private final SaveService saveService;

    @Autowired
    public MachineController(GameService gameService, IconService iconService,
                             RecipeModifierService recipeModifierService, MachineService machineService,
                             SaveService saveService) {
        this.gameService = gameService;
        this.iconService = iconService;
        this.recipeModifierService = recipeModifierService;
        this.machineService = machineService;
        this.saveService = saveService;
    }

    @PostMapping("/game/machines")
    public MachineStandalone create(int gameId, @RequestBody MachineStandalone input) {
        Game game = gameService.get(gameId);
        Machine machine = new Machine(game, input);
        applyRelations(input, machine);
        machine = machineService.create(machine);
        gameService.addAttachedMachine(game, machine);
        return new MachineStandalone(machine);
    }

    @GetMapping("save/machines")
    public List<MachineStandalone> retrieveAllFromSave(int saveId) {
        return retrieveAll(saveService.get(saveId).getGame());
    }

    @GetMapping("game/machines")
    public List<MachineStandalone> retrieveAll(int gameId) {
        return retrieveAll(gameService.get(gameId));
    }

    private static List<MachineStandalone> retrieveAll(Game game) {
        return game.getMachines().stream().map(MachineStandalone::new).toList();
    }

    @GetMapping("machine")
    public MachineStandalone retrieve(int machineId) {
        return new MachineStandalone(machineService.get(machineId));
    }

    @PatchMapping("/machine")
    public MachineStandalone update(int machineId, @RequestBody MachineStandalone input) {
        Machine machine = machineService.get(machineId);
        machine.applyBaics(input);
        applyRelations(input, machine);
        return new MachineStandalone(machineService.update(machine));
    }

    private void applyRelations(MachineStandalone input, Machine machine) {
        OptionalInputField.ofId(input.iconId(), iconService::get).apply(machine::setIcon);
        OptionalInputField.ofIds(input.machineModifierIds(), recipeModifierService::get)
                .applyList(machine::setMachineModifiers);
    }

    @DeleteMapping("/machine")
    public void delete(int machineId) {
        machineService.delete(machineId);
    }

}
