package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.AsyncHelper;
import de.yggdrasil128.factorial.model.game.GameService;
import de.yggdrasil128.factorial.model.item.ItemService;
import de.yggdrasil128.factorial.model.item.ItemStandalone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class ItemController {

    private final AsyncHelper asyncHelper;
    private final GameService gameService;
    private final ItemService itemService;

    @Autowired
    public ItemController(AsyncHelper asyncHelper, GameService gameService, ItemService itemService) {
        this.asyncHelper = asyncHelper;
        this.gameService = gameService;
        this.itemService = itemService;
    }

    @PostMapping("/game/items")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public CompletableFuture<Void> create(int gameId, @RequestBody List<ItemStandalone> input) {
        return asyncHelper.submit(result -> itemService.create(gameId, input, result));
    }

    @GetMapping("/game/items")
    public List<ItemStandalone> retrieveAll(int gameId) {
        return gameService.get(gameId).getItems().stream().map(ItemStandalone::of).toList();
    }

    @GetMapping("/item")
    public ItemStandalone retrieve(int itemId) {
        return ItemStandalone.of(itemService.get(itemId));
    }

    @PatchMapping("/items")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public CompletableFuture<Void> update(@RequestBody List<ItemStandalone> input) {
        return asyncHelper.submit(result -> itemService.update(input, result));
    }

    @DeleteMapping("/items")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public CompletableFuture<Void> delete(String itemIds) {
        List<Integer> itemIdsList = Arrays.stream(itemIds.split(",")).map(Integer::parseInt).toList();
        return asyncHelper.submit(result -> itemService.delete(itemIdsList, result));
    }

}
