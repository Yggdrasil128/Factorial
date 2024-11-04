package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameService;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.item.ItemService;
import de.yggdrasil128.factorial.model.item.ItemStandalone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ItemController {

    private final GameService gameService;
    private final IconService iconService;
    private final ItemService itemService;

    @Autowired
    public ItemController(GameService gameService, IconService iconService, ItemService itemService) {
        this.gameService = gameService;
        this.iconService = iconService;
        this.itemService = itemService;
    }

    @PostMapping("/game/items")
    public ItemStandalone create(int gameId, @RequestBody ItemStandalone input) {
        Game game = gameService.get(gameId);
        Item item = new Item(game, input);
        applyRelations(input, item);
        item = itemService.create(item);
        gameService.addAttachedItem(game, item);
        return ItemStandalone.of(item);
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
    public ItemStandalone update(int itemId, @RequestBody ItemStandalone input) {
        Item item = itemService.get(itemId);
        item.applyBasics(input);
        applyRelations(input, item);
        return ItemStandalone.of(itemService.update(item));
    }

    private void applyRelations(ItemStandalone input, Item item) {
        OptionalInputField.ofId(input.iconId(), iconService::get).apply(item::setIcon);
    }

    @DeleteMapping("/item")
    public void delete(int itemId) {
        itemService.delete(itemId);
    }

}
