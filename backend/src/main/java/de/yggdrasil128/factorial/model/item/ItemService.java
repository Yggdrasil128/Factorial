package de.yggdrasil128.factorial.model.item;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconService;
import org.springframework.stereotype.Service;

@Service
public class ItemService extends ModelService<Item, ItemRepository> {

    private final IconService icons;

    public ItemService(ItemRepository repository, IconService icons) {
        super(repository);
        this.icons = icons;
    }

    public Item create(GameVersion gameVersion, ItemStandalone input) {
        Icon icon = 0 == input.getIconId() ? null : icons.get(input.getIconId());
        return repository.save(new Item(gameVersion, input.getName(), input.getDescription(), icon));
    }

    public Item doImport(GameVersion gameVersion, String name, ItemMigration input) {
        return new Item(gameVersion, name, input.getDescription(), null);
    }

}
