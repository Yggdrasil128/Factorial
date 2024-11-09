package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.AsyncHelper;
import de.yggdrasil128.factorial.model.game.GameService;
import de.yggdrasil128.factorial.model.machine.MachineService;
import de.yggdrasil128.factorial.model.machine.MachineStandalone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class MachineController {

    private final AsyncHelper asyncHelper;
    private final GameService gameService;
    private final MachineService machineService;

    @Autowired
    public MachineController(AsyncHelper asyncHelper, GameService gameService, MachineService machineService) {
        this.asyncHelper = asyncHelper;
        this.gameService = gameService;
        this.machineService = machineService;
    }

    @PostMapping("/game/machines")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public CompletableFuture<Void> create(int gameId, @RequestBody MachineStandalone input) {
        return asyncHelper.submit(result -> machineService.create(gameId, input, result));
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
    public CompletableFuture<Void> update(int machineId, @RequestBody MachineStandalone input) {
        return asyncHelper.submit(result -> machineService.update(machineId, input, result));
    }

    @DeleteMapping("/machine")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public CompletableFuture<Void> delete(int machineId) {
        return asyncHelper.submit(result -> machineService.delete(machineId, result));
    }

}
