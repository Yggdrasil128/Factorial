package de.yggdrasil128.factorial.model.item;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconService;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Collections.emptyList;

@Service
public class ItemService extends ModelService<Item, ItemRepository> {

    private final IconService icons;

    public ItemService(ItemRepository repository, IconService icons) {
        super(repository);
        this.icons = icons;
    }

    public Item create(GameVersion gameVersion, ItemInput input) {
        Icon icon = 0 == input.getIconId() ? null : icons.get(input.getIconId());
        List<String> category = null == input.getCategory() ? emptyList() : input.getCategory();
        return repository.save(new Item(gameVersion, input.getName(), input.getDescription(), icon, category));
    }

    public Item update(int id, ItemInput input) {
        Item item = get(id);
        if (null != input.getName()) {
            item.setName(input.getName());
        }
        if (null != input.getDescription()) {
            item.setDescription(input.getDescription());
        }
        if (0 != input.getIconId()) {
            item.setIcon(icons.get(input.getIconId()));
        }
        if (null != input.getCategory()) {
            item.setCategory(input.getCategory());
        }
        return repository.save(item);
    }

}
