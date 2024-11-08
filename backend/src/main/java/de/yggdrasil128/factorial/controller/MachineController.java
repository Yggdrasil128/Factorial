package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.game.GameService;
import de.yggdrasil128.factorial.model.machine.MachineService;
import de.yggdrasil128.factorial.model.machine.MachineStandalone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MachineController {

    private final GameService gameService;
    private final MachineService machineService;

    @Autowired
    public MachineController(GameService gameService, MachineService machineService) {
        this.gameService = gameService;
        this.machineService = machineService;
    }

    @PostMapping("/game/machines")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void create(int gameId, @RequestBody MachineStandalone input) {
        machineService.create(gameId, input);
    }

    @GetMapping("game/machines")
    public List<MachineStandalone> retrieveAll(int gameId) {
        return gameService.get(gameId).getMachines().stream().map(MachineStandalone::of).toList();
    }

    @GetMapping("machine")
    public MachineStandalone retrieve(int machineId) {
        return MachineStandalone.of(machineService.get(machineId));
    }

    @PatchMapping("/machine")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void update(int machineId, @RequestBody MachineStandalone input) {
        machineService.update(machineId, input);
    }

    @DeleteMapping("/machine")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void delete(int machineId) {
        machineService.delete(machineId);
    }

}
