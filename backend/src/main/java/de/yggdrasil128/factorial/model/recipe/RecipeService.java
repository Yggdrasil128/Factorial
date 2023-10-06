package de.yggdrasil128.factorial.model.recipe;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.item.Item;
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

    private final ItemService items;

    public RecipeService(RecipeRepository repository, ItemService items) {
        super(repository);
        this.items = items;
    }

    public Recipe get(GameVersion gameVersion, String name) {
        return repository.findByGameVersionIdAndName(gameVersion.getId(), name)
                .orElseThrow(ModelService::reportNotFound);
    }

    public Recipe create(GameVersion gameVersion, RecipeStandalone input) {
        List<Resource> inputs = fromStandalones(input.getInput());
        List<Resource> outputs = fromStandalones(input.getOutput());
        return repository.save(
                new Recipe(gameVersion, input.getName(), inputs, outputs, input.getDuration(), emptyList(), emptyList()));
    }

    private List<Resource> fromStandalones(List<ResourceStandalone> resources) {
        return resources.stream().map(resource -> new Resource(items.get(resource.getItemId()), resource.getQuantity()))
                .toList();
    }

    public Recipe doImport(GameVersion version, String name, RecipeMigration input) {
        List<Resource> inputs = fromMigrations(version, input.getInput());
        List<Resource> outputs = fromMigrations(version, input.getOutput());
        return new Recipe(version, name, inputs, outputs, input.getDuration(), new ArrayList<>(), new ArrayList<>());
    }

    private static List<Resource> fromMigrations(GameVersion version, Map<String, Fraction> resources) {
        return resources.entrySet().stream()
                .map(entry -> new Resource(getDetachedItem(version, entry.getKey()), entry.getValue())).toList();
    }

    private static Item getDetachedItem(GameVersion version, String itemName) {
        return version.getItems().stream().filter(item -> item.getName().equals(itemName)).findAny()
                .orElseThrow(ModelService::reportNotFound);
    }

}
