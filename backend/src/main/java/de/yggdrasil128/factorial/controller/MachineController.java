package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionService;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.machine.*;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierService;
import de.yggdrasil128.factorial.model.save.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MachineController {

    private final GameVersionService gameVersionService;
    private final IconService iconService;
    private final RecipeModifierService recipeModifierService;
    private final MachineService machineService;
    private final SaveService saveService;

    @Autowired
    public MachineController(GameVersionService gameVersionService, IconService iconService,
                             RecipeModifierService recipeModifierService, MachineService machineService,
                             SaveService saveService) {
        this.gameVersionService = gameVersionService;
        this.iconService = iconService;
        this.recipeModifierService = recipeModifierService;
        this.machineService = machineService;
        this.saveService = saveService;
    }

    @PostMapping("/gameVersion/machines")
    public MachineStandalone create(int gameVersionId, @RequestBody MachineStandalone input) {
        GameVersion gameVersion = gameVersionService.get(gameVersionId);
        Machine machine = new Machine(gameVersion, input);
        applyRelations(input, machine);
        machine = machineService.create(machine);
        gameVersionService.addAttachedMachine(gameVersion, machine);
        return new MachineStandalone(machine);
    }

    @GetMapping("save/machines")
    public List<MachineStandalone> retrieveAllFromSave(int saveId) {
        return retrieveAll(saveService.get(saveId).getGameVersion());
    }

    @GetMapping("gameVersion/machines")
    public List<MachineStandalone> retrieveAll(int gameVersionId) {
        return retrieveAll(gameVersionService.get(gameVersionId));
    }

    private static List<MachineStandalone> retrieveAll(GameVersion gameVersion) {
        return gameVersion.getMachines().stream().map(MachineStandalone::new).toList();
    }

    @GetMapping("machine")
    public MachineStandalone retrieve(int machineId) {
        return new MachineStandalone(machineService.get(machineId));
    }

    @PatchMapping("/machine")
    public MachineStandalone update(int machineId, @RequestBody MachineStandalone input) {
        Machine machine = machineService.get(machineId);
        applyBasics(input, machine);
        applyRelations(input, machine);
        return new MachineStandalone(machineService.update(machine));
    }

    private static void applyBasics(MachineStandalone input, Machine machine) {
        OptionalInputField.of(input.name()).apply(machine::setName);
        OptionalInputField.of(input.category()).apply(machine::setCategory);
    }

    private void applyRelations(MachineStandalone input, Machine machine) {
        OptionalInputField.ofId((int) input.iconId(), iconService::get).apply(machine::setIcon);
        OptionalInputField.ofIds(input.machineModifierIds(), recipeModifierService::get)
                .applyList(machine::setMachineModifiers);
    }

    @DeleteMapping("/machine")
    public void delete(int machineId) {
        machineService.delete(machineId);
    }

}
