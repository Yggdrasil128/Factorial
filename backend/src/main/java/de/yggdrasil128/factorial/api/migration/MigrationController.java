package de.yggdrasil128.factorial.api.migration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameMigration;
import de.yggdrasil128.factorial.model.game.GameService;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveMigration;
import de.yggdrasil128.factorial.model.save.SaveService;

@RestController
@RequestMapping("/api/migration")
public class MigrationController {

    private final GameService games;
    private final SaveService saves;

    @Autowired
    public MigrationController(GameService games, SaveService saves) {
        this.games = games;
        this.saves = saves;
    }

    @PostMapping("/game")
    public Game importGame(@RequestBody GameMigration gameInput) {
        return games.doImport(gameInput);
    }

    @PostMapping("/save")
    public Save importSave(@RequestBody SaveMigration input) {
        return saves.doImport(input);
    }

}
