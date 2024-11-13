package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.AsyncHelper;
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

    private final AsyncHelper asyncHelper;
    private final GameService gameService;

    @Autowired
    public GameController(AsyncHelper asyncHelper, GameService gameService) {
        this.asyncHelper = asyncHelper;
        this.gameService = gameService;
    }

    @PostMapping("/games")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public CompletableFuture<Void> create(@RequestBody List<GameStandalone> input) {
        return asyncHelper.submit(result -> gameService.create(input, result));
    }

    @GetMapping("/games")
    public CompletableFuture<List<GameStandalone>> retrieveAll() {
        return asyncHelper.submit(() -> gameService.getAll());
    }

    @GetMapping("/game")
    public GameStandalone retrieve(int gameId) {
        return GameStandalone.of(gameService.get(gameId));
    }

    @PatchMapping("/games/order")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public CompletableFuture<Void> order(@RequestBody List<EntityPosition> input) {
        return asyncHelper.submit(result -> gameService.reorder(input, result));
    }

    @GetMapping("/game/summary")
    public CompletableFuture<GameSummary> retrieveSummary(int gameId) {
        return asyncHelper.submit(() -> gameService.getSummary(gameId, External.FRONTEND));
    }

    @PatchMapping("/games")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public CompletableFuture<Void> update(@RequestBody List<GameStandalone> input) {
        return asyncHelper.submit(result -> gameService.update(input, result));
    }

    @DeleteMapping("/games")
    public CompletableFuture<Void> delete(List<Integer> gameIds) {
        return asyncHelper.submit(result -> gameService.delete(gameIds, result));
    }

}
