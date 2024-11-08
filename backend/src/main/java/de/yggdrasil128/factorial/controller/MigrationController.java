package de.yggdrasil128.factorial.controller;

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

    private final GameService gameService;
    private final SaveService saveService;

    @Autowired
    public MigrationController(GameService gameService, SaveService saveService) {
        this.gameService = gameService;
        this.saveService = saveService;
    }

    @PostMapping("/game")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void importGame(@RequestBody GameSummary input) {
        gameService.doImport(input);
    }

    @PostMapping("/save")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void importSave(@RequestBody SaveSummary input) {
        saveService.doImport(input);
    }

    @GetMapping("/game")
    public CompletableFuture<GameSummary> exportGame(int gameId) {
        return gameService.getSummary(gameId, External.SAVE_FILE);
    }

    @GetMapping("/save")
    public CompletableFuture<SaveSummary> exportSave(int saveId) {
        return saveService.getSummary(saveId, External.SAVE_FILE);
    }

}
