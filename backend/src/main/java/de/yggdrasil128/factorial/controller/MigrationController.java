package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.Exporter;
import de.yggdrasil128.factorial.model.Importer;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.RelationRepresentation;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionService;
import de.yggdrasil128.factorial.model.gameversion.GameVersionSummary;
import de.yggdrasil128.factorial.model.save.SaveService;
import de.yggdrasil128.factorial.model.save.SaveSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/migration")
public class MigrationController {

    private final GameVersionService gameVersionService;
    private final SaveService saveService;

    @Autowired
    public MigrationController(GameVersionService gameVersionService, SaveService saveService) {
        this.gameVersionService = gameVersionService;
        this.saveService = saveService;
    }

    @PostMapping("/gameVersion")
    public void importGameVersion(@RequestBody GameVersionSummary input) {
        gameVersionService.create(Importer.importGameVersion(input));
    }

    @PostMapping("/save")
    public void importSave(@RequestBody SaveSummary input) {
        String gameVersionName = (String) input.getSave().getGameVersion();
        GameVersion gameVersion = gameVersionService.get(gameVersionName)
                .orElseThrow(() -> ModelService.report(HttpStatus.CONFLICT,
                        "save requires the game version '" + gameVersionName + "' to be installed"));
        saveService.create(Importer.importSave(input, gameVersion));
    }

    @GetMapping("/gameVersion")
    public GameVersionSummary exportGameVersion(int gameVersionId) {
        return Exporter.exportGameVersion(gameVersionService.get(gameVersionId), RelationRepresentation.NAME);
    }

    @GetMapping("/save")
    public SaveSummary erxportSave(int saveId) {
        return Exporter.exportSave(saveService.get(saveId), RelationRepresentation.NAME);
    }

}
