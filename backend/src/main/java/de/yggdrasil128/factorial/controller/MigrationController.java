package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.Exporter;
import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.Importer;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameService;
import de.yggdrasil128.factorial.model.game.GameSummary;
import de.yggdrasil128.factorial.model.save.SaveService;
import de.yggdrasil128.factorial.model.save.SaveSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    public void importGame(@RequestBody GameSummary input) {
        gameService.create(Importer.importGame(input));
    }

    @PostMapping("/save")
    public void importSave(@RequestBody SaveSummary input) {
        String gameName = (String) input.getSave().gameId();
        Game game = gameService.get(gameName)
                .orElseThrow(() -> ModelService.report(HttpStatus.CONFLICT,
                        "save requires the game '" + gameName + "' to be installed"));
        saveService.create(Importer.importSave(input, game));
    }

    @GetMapping("/game")
    public GameSummary exportGame(int gameId) {
        return Exporter.exportGame(gameService.get(gameId), External.SAVE_FILE);
    }

    @GetMapping("/save")
    public SaveSummary exportSave(int saveId) {
        return Exporter.exportSave(saveService.get(saveId), External.SAVE_FILE);
    }

}
