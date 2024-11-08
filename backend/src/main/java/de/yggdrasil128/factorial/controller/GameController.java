package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.EntityPosition;
import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.game.GameService;
import de.yggdrasil128.factorial.model.game.GameStandalone;
import de.yggdrasil128.factorial.model.game.GameSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/games")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void create(@RequestBody GameStandalone input) {
        gameService.create(input);
    }

    @GetMapping("/games")
    public List<GameStandalone> retrieveAll() {
        return gameService.stream().map(GameStandalone::of).toList();
    }

    @GetMapping("/game")
    public GameStandalone retrieve(int gameId) {
        return GameStandalone.of(gameService.get(gameId));
    }

    @PatchMapping("/games/order")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void order(@RequestBody List<EntityPosition> input) {
        gameService.reorder(input);
    }

    @GetMapping("/game/summary")
    public CompletableFuture<GameSummary> retrieveSummary(int gameId) {
        return gameService.getSummary(gameId, External.FRONTEND);
    }

    @PatchMapping("/game")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void update(int gameId, @RequestBody GameStandalone input) {
        gameService.update(gameId, input);
    }

    @DeleteMapping("/game")
    public void delete(int gameId) {
        gameService.delete(gameId);
    }

}
