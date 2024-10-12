package de.yggdrasil128.factorial.api.simple;

import de.yggdrasil128.factorial.model.gameversion.GameVersionInput;
import de.yggdrasil128.factorial.model.gameversion.GameVersionOutput;
import de.yggdrasil128.factorial.model.gameversion.GameVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GameVersionController {

    private final GameVersionService gameVersionService;

    @Autowired
    public GameVersionController(GameVersionService gameVersionService) {
        this.gameVersionService = gameVersionService;
    }

    @PostMapping("/gameVersions")
    public GameVersionOutput create(@RequestBody GameVersionInput input) {
        return new GameVersionOutput(gameVersionService.create(input));
    }

    @GetMapping("/gameVersions")
    public List<GameVersionOutput> retrieveAll() {
        return gameVersionService.stream().map(GameVersionOutput::new).toList();
    }

    @GetMapping("/gameVersion")
    public GameVersionOutput retrieve(int gameVersionId) {
        return new GameVersionOutput(gameVersionService.get(gameVersionId));
    }

    @PatchMapping("/gameVersion")
    public GameVersionOutput update(int gameVersionId, @RequestBody GameVersionInput input) {
        return new GameVersionOutput(gameVersionService.update(gameVersionId, input));
    }

    @DeleteMapping("/gameVersion")
    public void delete(int gameVersionId) {
        gameVersionService.delete(gameVersionId);
    }

}
