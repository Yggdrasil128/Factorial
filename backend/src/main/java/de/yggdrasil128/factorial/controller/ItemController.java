package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionService;
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

    private final GameVersionService gameVersionService;
    private final IconService iconService;
    private final ItemService itemService;

    @Autowired
    public ItemController(GameVersionService gameVersionService, IconService iconService, ItemService itemService) {
        this.gameVersionService = gameVersionService;
        this.iconService = iconService;
        this.itemService = itemService;
    }

    @PostMapping("/gameVersion/items")
    public ItemStandalone create(int gameVersionId, @RequestBody ItemStandalone input) {
        GameVersion gameVersion = gameVersionService.get(gameVersionId);
        Item item = new Item(gameVersion, input);
        applyRelations(input, item);
        item = itemService.create(item);
        gameVersionService.addAttachedItem(gameVersion, item);
        return ItemStandalone.of(item);
    }

    @GetMapping("/gameVersion/items")
    public List<ItemStandalone> retrieveAll(int gameVersionId) {
        return gameVersionService.get(gameVersionId).getItems().stream().map(ItemStandalone::of).toList();
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
