package de.yggdrasil128.factorial.api.migration;

import de.yggdrasil128.factorial.model.gameversion.GameVersionMigration;
import de.yggdrasil128.factorial.model.gameversion.GameVersionService;
import de.yggdrasil128.factorial.model.save.SaveMigration;
import de.yggdrasil128.factorial.model.save.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/migration")
public class MigrationController {

    private final MigrationService migrationService;
    private final GameVersionService gameVersionService;
    private final SaveService saveService;

    @Autowired
    public MigrationController(MigrationService migrationService, GameVersionService gameVersionService,
                               SaveService saveService) {
        this.migrationService = migrationService;
        this.gameVersionService = gameVersionService;
        this.saveService = saveService;
    }

    @PostMapping("/gameVersion")
    public void importGameVersion(@RequestBody GameVersionMigration input) {
        migrationService.importGameVersion(input);
    }

    @PostMapping("/save")
    public void importSave(@RequestBody SaveMigration input) {
        migrationService.importSave(input);
    }

    @GetMapping("/gameVersion")
    public GameVersionMigration exportGameVersion(int gameVersionId) {
        return migrationService.exportGameVersion(gameVersionService.get(gameVersionId));
    }

    @GetMapping("/save")
    public SaveMigration erxportSave(int saveId) {
        return migrationService.exportSave(saveService.get(saveId));
    }

}
