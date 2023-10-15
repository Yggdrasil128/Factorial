package de.yggdrasil128.factorial.api.simple;

import de.yggdrasil128.factorial.model.game.GameInput;
import de.yggdrasil128.factorial.model.game.GameOutput;
import de.yggdrasil128.factorial.model.game.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/games")
    public GameOutput create(@RequestBody GameInput input) {
        return new GameOutput(gameService.create(input));
    }

    @GetMapping("/games")
    public List<GameOutput> retrieveAll() {
        return gameService.stream().map(GameOutput::new).toList();
    }

    @GetMapping("/game")
    public GameOutput retrieve(int gameId) {
        return new GameOutput(gameService.get(gameId));
    }

    @PatchMapping("/game")
    public GameOutput update(int gameId, @RequestBody GameInput input) {
        return new GameOutput(gameService.update(gameId, input));
    }

    @DeleteMapping("/game")
    public void delete(int gameId) {
        gameService.delete(gameId);
    }

}
