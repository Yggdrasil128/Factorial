package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.Exporter;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.RelationRepresentation;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionService;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveService;
import de.yggdrasil128.factorial.model.save.SaveStandalone;
import de.yggdrasil128.factorial.model.save.SaveSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SaveController {

    private final GameVersionService gameVersionService;
    private final SaveService saveService;

    @Autowired
    public SaveController(GameVersionService gameVersionService, SaveService saveService) {
        this.gameVersionService = gameVersionService;
        this.saveService = saveService;
    }

    @PostMapping("/saves")
    public SaveStandalone create(@RequestBody SaveStandalone input) {
        GameVersion gameVersion = gameVersionService.get((int) input.getGameVersion());
        Save save = new Save(gameVersion, input);
        return new SaveStandalone(saveService.create(save));
    }

    @GetMapping("/saves")
    public List<SaveStandalone> retrieveAll() {
        return saveService.stream().map(SaveStandalone::new).toList();
    }

    @GetMapping("/save")
    public SaveStandalone retrieve(int saveId) {
        return new SaveStandalone(saveService.get(saveId));
    }

    @GetMapping("/save/summary")
    public SaveSummary retrieveSummary(int saveId) {
        return Exporter.exportSave(saveService.get(saveId), RelationRepresentation.ID);
    }

    @PatchMapping("/save")
    public SaveStandalone update(int saveId, @RequestBody SaveStandalone input) {
        Save save = saveService.get(saveId);
        OptionalInputField.of(input.getName()).apply(save::setName);
        if (0 != (int) input.getGameVersion()) {
            throw ModelService.report(HttpStatus.NOT_IMPLEMENTED, "cannot update game version");
        }
        return new SaveStandalone(saveService.update(save));
    }

    @DeleteMapping("/save")
    public void delete(int saveId) {
        saveService.delete(saveId);
    }

}
