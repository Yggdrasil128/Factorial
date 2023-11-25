package de.yggdrasil128.factorial.api.simple;

import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionService;
import de.yggdrasil128.factorial.model.save.SaveInput;
import de.yggdrasil128.factorial.model.save.SaveOutput;
import de.yggdrasil128.factorial.model.save.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public SaveOutput create(@RequestBody SaveInput input) {
        GameVersion gameVersion = gameVersionService.get(input.getGameVersionId());
        return new SaveOutput(saveService.create(gameVersion, input));
    }

    @GetMapping("/saves")
    public List<SaveOutput> retrieveAll() {
        return saveService.stream().map(SaveOutput::new).toList();
    }

    @GetMapping("/save")
    public SaveOutput retrieve(int saveId) {
        return new SaveOutput(saveService.get(saveId));
    }

    @PatchMapping("/save")
    public SaveOutput update(int saveId, @RequestBody SaveInput input) {
        return new SaveOutput(saveService.update(saveId, input));
    }

    @DeleteMapping("/save")
    public void delete(int saveId) {
        saveService.delete(saveId);
    }

}
