package de.yggdrasil128.factorial.api.simple;

import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionService;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.item.ItemInput;
import de.yggdrasil128.factorial.model.item.ItemOutput;
import de.yggdrasil128.factorial.model.item.ItemService;
import de.yggdrasil128.factorial.model.save.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ItemController {

    private final GameVersionService gameVersionService;
    private final ItemService itemService;
    private final SaveService saveService;

    @Autowired
    public ItemController(GameVersionService gameVersionService, ItemService itemService, SaveService saveService) {
        this.gameVersionService = gameVersionService;
        this.itemService = itemService;
        this.saveService = saveService;
    }

    @PostMapping("/save/items")
    public ItemOutput createFromSave(int saveId, @RequestBody ItemInput input) {
        return create(saveService.get(saveId).getGameVersion(), input);
    }

    @PostMapping("/gameVersion/items")
    public ItemOutput create(int gameVersionId, @RequestBody ItemInput input) {
        return create(gameVersionService.get(gameVersionId), input);
    }

    private ItemOutput create(GameVersion gameVersion, ItemInput input) {
        Item item = itemService.create(gameVersion, input);
        gameVersionService.addAttachedItem(gameVersion, item);
        return new ItemOutput(item);
    }

    @GetMapping("/save/items")
    public List<ItemOutput> retrieveAllFromSave(int saveId) {
        return retrieveAll(saveService.get(saveId).getGameVersion());
    }

    @GetMapping("/gameVersion/items")
    public List<ItemOutput> retrieveAll(int gameVersionId) {
        return retrieveAll(gameVersionService.get(gameVersionId));
    }

    private static List<ItemOutput> retrieveAll(GameVersion gameVersion) {
        return gameVersion.getItems().stream().map(ItemOutput::new).toList();
    }

    @GetMapping("/item")
    public ItemOutput retrieve(int itemId) {
        return new ItemOutput(itemService.get(itemId));
    }

    @PatchMapping("/item")
    public ItemOutput update(int itemId, @RequestBody ItemInput input) {
        return new ItemOutput(itemService.update(itemId, input));
    }

    @DeleteMapping("/item")
    public void delete(int itemId) {
        itemService.delete(itemId);
    }

}
