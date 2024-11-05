package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.EntityPosition;
import de.yggdrasil128.factorial.model.Exporter;
import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameService;
import de.yggdrasil128.factorial.model.game.GameStandalone;
import de.yggdrasil128.factorial.model.game.GameSummary;
import de.yggdrasil128.factorial.model.icon.IconService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GameController {

    private final GameService gameService;
    private final IconService iconService;

    @Autowired
    public GameController(GameService gameService, IconService iconService) {
        this.gameService = gameService;
        this.iconService = iconService;
    }

    @PostMapping("/games")
    public GameStandalone create(@RequestBody GameStandalone input) {
        Game game = new Game(input);
        applyRelations(input, game);
        return GameStandalone.of(gameService.create(game));
    }

    @GetMapping("/games")
    public List<GameStandalone> retrieveAll() {
        return gameService.stream().map(GameStandalone::of).toList();
    }

    @PatchMapping("/games/order")
    public void order(@RequestBody List<EntityPosition> input) {
        gameService.reorder(input);
    }

    @GetMapping("/game")
    public GameStandalone retrieve(int gameId) {
        return GameStandalone.of(gameService.get(gameId));
    }

    @GetMapping("/game/summary")
    public GameSummary retrieveSummary(int gameId) {
        return Exporter.exportGame(gameService.get(gameId), External.FRONTEND);
    }

    @PatchMapping("/game")
    public GameStandalone update(int gameId, @RequestBody GameStandalone input) {
        Game game = gameService.get(gameId);
        game.applyBasics(input);
        applyRelations(input, game);
        return GameStandalone.of(gameService.update(game));
    }

    private void applyRelations(GameStandalone input, Game game) {
        OptionalInputField.ofId(input.iconId(), iconService::get).apply(game::setIcon);
    }

    @DeleteMapping("/game")
    public void delete(int gameId) {
        gameService.delete(gameId);
    }

}
