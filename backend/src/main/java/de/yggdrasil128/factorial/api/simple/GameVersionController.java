package de.yggdrasil128.factorial.api.simple;

import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameService;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionInput;
import de.yggdrasil128.factorial.model.gameversion.GameVersionOutput;
import de.yggdrasil128.factorial.model.gameversion.GameVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GameVersionController {

    private final GameService gameService;
    private final GameVersionService gameVersionService;

    @Autowired
    public GameVersionController(GameService gameService, GameVersionService gameVersionService) {
        this.gameService = gameService;
        this.gameVersionService = gameVersionService;
    }

    @PostMapping("/game/gameVersions")
    public GameVersionOutput create(int gameId, @RequestBody GameVersionInput input) {
        Game game = gameService.get(gameId);
        GameVersion gameVersion = gameVersionService.create(game, input);
        gameService.addAttachedGameVersion(game, gameVersion);
        return new GameVersionOutput(gameVersion);
    }

    @GetMapping("/game/gameVersions")
    public List<GameVersionOutput> retrieveAll(int gameId) {
        return gameService.get(gameId).getGameVersions().stream().map(GameVersionOutput::new).toList();
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
