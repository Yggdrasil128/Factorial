package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.AsyncHelper;
import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.game.GameService;
import de.yggdrasil128.factorial.model.game.GameSummary;
import de.yggdrasil128.factorial.model.save.SaveService;
import de.yggdrasil128.factorial.model.save.SaveSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/migration")
public class MigrationController {

    private final AsyncHelper asyncHelper;
    private final GameService gameService;
    private final SaveService saveService;

    @Autowired
    public MigrationController(AsyncHelper asyncHelper, GameService gameService, SaveService saveService) {
        this.asyncHelper = asyncHelper;
        this.gameService = gameService;
        this.saveService = saveService;
    }

    @PostMapping("/game")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public CompletableFuture<Void> importGame(@RequestBody GameSummary input) {
        return asyncHelper.submit(result -> gameService.doImport(input, result));
    }

    @PostMapping("/game/clone")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public CompletableFuture<Void> cloneGame(int gameId, String newName) {
        return asyncHelper.submit(result -> gameService.doClone(gameId, newName, result));
    }

    @GetMapping("/game")
    public CompletableFuture<GameSummary> exportGame(int gameId) {
        return asyncHelper.submit(() -> gameService.getSummary(gameId, External.SAVE_FILE));
    }

    @PostMapping("/save")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public CompletableFuture<Void> importSave(@RequestBody SaveSummary input) {
        return asyncHelper.submit(result -> saveService.doImport(input, result));
    }

    @PostMapping("/save/clone")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public CompletableFuture<Void> cloneSave(int saveId, String newName) {
        return asyncHelper.submit(result -> saveService.doClone(saveId, newName, result));
    }

    @PostMapping("/save/migrate")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public CompletableFuture<Void> migrateSave(int saveId, String newName, int gameId) {
        return asyncHelper.submit(result -> saveService.migrate(saveId, newName, gameId, result));
    }

    @GetMapping("/save")
    public CompletableFuture<SaveSummary> exportSave(int saveId) {
        return asyncHelper.submit(() -> saveService.getSummary(saveId, External.SAVE_FILE));
    }

}
