package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.game.GameService;
import de.yggdrasil128.factorial.model.item.ItemService;
import de.yggdrasil128.factorial.model.item.ItemStandalone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ItemController {

    private final GameService gameService;
    private final ItemService itemService;

    @Autowired
    public ItemController(GameService gameService, ItemService itemService) {
        this.gameService = gameService;
        this.itemService = itemService;
    }

    @PostMapping("/game/items")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void create(int gameId, @RequestBody ItemStandalone input) {
        itemService.create(gameId, input);
    }

    @GetMapping("/game/items")
    public List<ItemStandalone> retrieveAll(int gameId) {
        return gameService.get(gameId).getItems().stream().map(ItemStandalone::of).toList();
    }

    @GetMapping("/item")
    public ItemStandalone retrieve(int itemId) {
        return ItemStandalone.of(itemService.get(itemId));
    }

    @PatchMapping("/item")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void update(int itemId, @RequestBody ItemStandalone input) {
        itemService.update(itemId, input);
    }

    @DeleteMapping("/item")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void delete(int itemId) {
        itemService.delete(itemId);
    }

}
