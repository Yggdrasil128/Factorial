package de.yggdrasil128.factorial.api.migration;

import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameMigration;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveMigration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/migration")
public class MigrationController {

    private final MigrationService service;

    @Autowired
    public MigrationController(MigrationService service) {
        this.service = service;
    }

    @PostMapping("/game")
    public Game importGame(@RequestBody GameMigration input) {
        return service.importGame(input);
    }

    @PostMapping("/save")
    public Save importSave(@RequestBody SaveMigration input) {
        return service.importSave(input);
    }

}
