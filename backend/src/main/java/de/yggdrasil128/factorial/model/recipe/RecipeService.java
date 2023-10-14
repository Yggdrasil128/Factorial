package de.yggdrasil128.factorial.model.recipe;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionService;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.item.ItemService;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.resource.ResourceStandalone;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

@Service
public class RecipeService extends ModelService<Recipe, RecipeRepository> {

    private final IconService icons;
    private final ItemService items;

    public RecipeService(RecipeRepository repository, IconService icons, ItemService items) {
        super(repository);
        this.icons = icons;
        this.items = items;
    }

    public Recipe get(GameVersion gameVersion, String name) {
        return repository.findByGameVersionIdAndName(gameVersion.getId(), name)
                .orElseThrow(ModelService::reportNotFound);
    }

    public Recipe create(GameVersion gameVersion, RecipeStandalone input) {
        Icon icon = 0 == input.getIconId() ? null : icons.get(input.getIconId());
        List<Resource> inputs = fromStandalones(input.getInput());
        List<Resource> outputs = fromStandalones(input.getOutput());
        return repository.save(new Recipe(gameVersion, input.getName(), icon, inputs, outputs, input.getDuration(),
                emptyList(), emptyList()));
    }

    private List<Resource> fromStandalones(List<ResourceStandalone> resources) {
        return resources.stream().map(resource -> new Resource(items.get(resource.getItemId()), resource.getQuantity()))
                .toList();
    }

    public Recipe doImport(GameVersion gameVersion, String name, RecipeMigration input) {
        Icon icon = null == input.getIconName() ? null
                : GameVersionService.getDetachedIcon(gameVersion, input.getIconName());
        List<Resource> inputs = fromMigrations(gameVersion, input.getInput());
        List<Resource> outputs = fromMigrations(gameVersion, input.getOutput());
        return new Recipe(gameVersion, name, icon, inputs, outputs, input.getDuration(), new ArrayList<>(),
                new ArrayList<>());
    }

    private static List<Resource> fromMigrations(GameVersion version, Map<String, Fraction> resources) {
        return resources.entrySet().stream().map(
                entry -> new Resource(GameVersionService.getDetachedItem(version, entry.getKey()), entry.getValue()))
                .toList();
    }

}
