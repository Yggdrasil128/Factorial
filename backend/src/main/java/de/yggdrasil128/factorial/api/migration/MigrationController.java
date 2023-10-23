package de.yggdrasil128.factorial.api.migration;

import de.yggdrasil128.factorial.model.gameversion.GameVersionMigration;
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

    @PostMapping("/gameVersion")
    public void importGameVersion(@RequestBody GameVersionMigration input) {
        service.importGameVersion(input);
    }

    @PostMapping("/save")
    public void importSave(@RequestBody SaveMigration input) {
        service.importSave(input);
    }

}
