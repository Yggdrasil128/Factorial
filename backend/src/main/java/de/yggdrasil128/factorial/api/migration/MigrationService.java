package de.yggdrasil128.factorial.api.migration;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.changelist.ChangelistMigration;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryMigration;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameMigration;
import de.yggdrasil128.factorial.model.game.GameRepository;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionMigration;
import de.yggdrasil128.factorial.model.gameversion.GameVersionRepository;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconMigration;
import de.yggdrasil128.factorial.model.icon.IconRepository;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.item.ItemMigration;
import de.yggdrasil128.factorial.model.item.ItemRepository;
import de.yggdrasil128.factorial.model.machine.Machine;
import de.yggdrasil128.factorial.model.machine.MachineMigration;
import de.yggdrasil128.factorial.model.machine.MachineRepository;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepMigration;
import de.yggdrasil128.factorial.model.recipe.Recipe;
import de.yggdrasil128.factorial.model.recipe.RecipeMigration;
import de.yggdrasil128.factorial.model.recipe.RecipeRepository;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierMigration;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierRepository;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveMigration;
import de.yggdrasil128.factorial.model.save.SaveRepository;
import de.yggdrasil128.factorial.model.transportlink.TransportLink;
import de.yggdrasil128.factorial.model.transportlink.TransportLinkMigration;
import de.yggdrasil128.factorial.model.xgress.Xgress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
public class MigrationService {

    private final GameRepository games;
    private final GameVersionRepository gameVersions;
    private final ItemRepository items;
    private final RecipeRepository recipes;
    private final RecipeModifierRepository recipeModifiers;
    private final MachineRepository machines;
    private final SaveRepository saves;
    private final IconRepository icons;

    @Autowired
    public MigrationService(GameRepository games, GameVersionRepository gameVersions, ItemRepository items,
                            RecipeRepository recipes, RecipeModifierRepository recipeModifiers,
                            MachineRepository machines, SaveRepository saves, IconRepository icons) {
        this.games = games;
        this.gameVersions = gameVersions;
        this.items = items;
        this.recipes = recipes;
        this.recipeModifiers = recipeModifiers;
        this.machines = machines;
        this.saves = saves;
        this.icons = icons;
    }

    public Game importGame(GameMigration input) {
        Game game = new Game(input.getName(), nl());
        input.getVersions().entrySet().stream().map(entry -> importGameVersion(game, entry.getKey(), entry.getValue()))
                .forEach(game.getGameVersions()::add);
        games.save(game);
        return game;
    }

    private static GameVersion importGameVersion(Game game, String name, GameVersionMigration input) {
        GameVersion gameVersion = new GameVersion(game, name, null, nl(), nl(), nl(), nl(), nl());
        input.getIcons().entrySet().stream().map(entry -> importIcon(gameVersion, entry.getKey(), entry.getValue()))
                .forEach(gameVersion.getIcons()::add);
        input.getItems().entrySet().stream().map(entry -> importItem(gameVersion, entry.getKey(), entry.getValue()))
                .forEach(gameVersion.getItems()::add);
        input.getRecipies().entrySet().stream()
                .map(entry -> importRecipe(gameVersion, entry.getKey(), entry.getValue()))
                .forEach(gameVersion.getRecipes()::add);
        input.getRecipeModifiers().entrySet().stream()
                .map(entry -> importRecipeModifier(gameVersion, entry.getKey(), entry.getValue()))
                .forEach(gameVersion.getRecipeModifiers()::add);
        input.getMachines().entrySet().stream()
                .map(entry -> importMachine(gameVersion, entry.getKey(), entry.getValue()))
                .forEach(gameVersion.getMachines()::add);
        if (null != input.getIconName()) {
            gameVersion.setIcon(getDetachedIcon(gameVersion, input.getIconName()));
        }
        return gameVersion;
    }

    private static Icon importIcon(GameVersion gameVersion, String name, IconMigration input) {
        return new Icon(gameVersion, name, input.getImageData(), input.getMimeType(), input.getCategory());
    }

    private static Item importItem(GameVersion gameVersion, String name, ItemMigration input) {
        return new Item(gameVersion, name, input.getDescription(), getDetachedIcon(gameVersion, input.getIconName()),
                input.getCategory());
    }

    private static Recipe importRecipe(GameVersion gameVersion, String name, RecipeMigration input) {
        Icon icon = getDetachedIcon(gameVersion, input.getIconName());
        List<Resource> inputs = importResources(gameVersion, input.getInput());
        List<Resource> outputs = importResources(gameVersion, input.getOutput());
        return new Recipe(gameVersion, name, icon, inputs, outputs, input.getDuration(), nl(), nl(),
                input.getCategory());
    }

    private static List<Resource> importResources(GameVersion gameVersion, Map<String, Fraction> input) {
        return input.entrySet().stream()
                .map(entry -> new Resource(getDetachedItem(gameVersion, entry.getKey()), entry.getValue())).toList();
    }

    private static Item getDetachedItem(GameVersion gameVersion, String name) {
        return gameVersion.getItems().stream().filter(item -> item.getName().equals(name)).findAny()
                .orElseThrow(() -> ModelService.report(HttpStatus.BAD_REQUEST,
                        "nested entity refers to non existent item '" + name + "'"));
    }

    private static RecipeModifier importRecipeModifier(GameVersion gameVersion, String name,
                                                       RecipeModifierMigration input) {
        return new RecipeModifier(gameVersion, name, input.getDescription(),
                getDetachedIcon(gameVersion, input.getIconName()), input.getDurationMultiplier(),
                input.getInputQuantityMultiplier(), input.getOutputQuantityMultiplier());
    }

    private static Machine importMachine(GameVersion gameVersion, String name, MachineMigration input) {
        List<RecipeModifier> machineModifiers = input.getMachineModifierNames().stream()
                .map(machineModifierName -> getDetachedRecipeModifier(gameVersion, machineModifierName)).toList();
        return new Machine(gameVersion, name, getDetachedIcon(gameVersion, input.getIconName()), machineModifiers,
                input.getCategory());
    }

    private static RecipeModifier getDetachedRecipeModifier(GameVersion gameVersion, String name) {
        return gameVersion.getRecipeModifiers().stream().filter(recipeModifier -> recipeModifier.getName().equals(name))
                .findAny().orElseThrow(() -> ModelService.report(HttpStatus.BAD_REQUEST,
                        "nested entity refers to non-existent recipe modifier '" + name + "'"));
    }

    private static Icon getDetachedIcon(GameVersion gameVersion, String name) {
        if (null == name) {
            return null;
        }
        return gameVersion.getIcons().stream().filter(icon -> icon.getName().equals(name)).findAny()
                .orElseThrow(() -> ModelService.report(HttpStatus.BAD_REQUEST,
                        "nested entity refers to non-existent icon '" + name + "'"));
    }

    public Save importSave(SaveMigration input) {
        if (1 != input.getChangelists().stream().filter(ChangelistMigration::isPrimary).count()) {
            throw ModelService.report(HttpStatus.BAD_REQUEST,
                    "save '" + input.getName() + "' must contain exactly one primary change list");
        }
        Game game = games.findByName(input.getGame()).orElseThrow(() -> ModelService.report(HttpStatus.CONFLICT,
                "save requires the game '" + input.getGame() + "' to be installed"));
        GameVersion gameVersion = gameVersions.findByGameIdAndName(game.getId(), input.getVersion())
                .orElseThrow(() -> ModelService.report(HttpStatus.CONFLICT,
                        "save requires the game version '" + input.getVersion() + "' to be installed"));
        Save save = new Save(gameVersion, input.getName(), nl(), nl(), nl());
        input.getFactories().stream().map(entry -> importFactory(save, entry)).forEach(save.getFactories()::add);
        input.getChangelists().stream().map(entry -> importChangelist(save, entry)).forEach(save.getChangelists()::add);
        input.getTransportLinks().stream().map(entry -> importTransportLink(save, entry))
                .forEach(save.getTransportLinks()::add);
        saves.save(save);
        return save;
    }

    private Factory importFactory(Save save, FactoryMigration input) {
        Factory factory = new Factory(save, input.getOrdinal(), input.getName(), input.getDescription(),
                getAttachedIcon(save.getGameVersion(), input.getIconName()), nl(), nl(), nl(), nm());
        Map<String, Integer> itemOrder = new HashMap<>();
        for (int i = 0; i < input.getItemOrder().size(); i++) {
            itemOrder.put(input.getItemOrder().get(i), i + 1);
        }
        for (ProductionStepMigration entry : input.getProductionSteps()) {
            ProductionStep productionStep = importProductionStep(factory, entry);
            factory.getProductionSteps().add(productionStep);
            productionStep.getRecipe().getInput().forEach(resource -> factory.getItemOrder()
                    .computeIfAbsent(resource.getItem(), item -> itemOrder.get(item.getName())));
            productionStep.getRecipe().getOutput().forEach(resource -> factory.getItemOrder()
                    .computeIfAbsent(resource.getItem(), item -> itemOrder.get(item.getName())));
        }
        input.getIngresses().entrySet().stream().map(entry -> importXgress(factory, entry.getKey(), entry.getValue()))
                .forEach(factory.getIngresses()::add);
        input.getEgresses().entrySet().stream().map(entry -> importXgress(factory, entry.getKey(), entry.getValue()))
                .forEach(factory.getEgresses()::add);
        return factory;
    }

    private ProductionStep importProductionStep(Factory factory, ProductionStepMigration input) {
        int gameVersionId = factory.getSave().getGameVersion().getId();
        Machine machine = machines.findByGameVersionIdAndName(gameVersionId, input.getMachineName())
                .orElseThrow(() -> ModelService.report(HttpStatus.CONFLICT,
                        "entity in save refers to non-existent machine '" + input.getMachineName() + "'"));
        Recipe recipe = recipes.findByGameVersionIdAndName(gameVersionId, input.getRecipeName())
                .orElseThrow(() -> ModelService.report(HttpStatus.CONFLICT,
                        "entity in save refers to non-existent recipe '" + input.getRecipeName() + "'"));
        List<RecipeModifier> modifiers = recipeModifiers.findAllByGameVersionIdAndNameIn(gameVersionId,
                input.getModifierNames());
        if (modifiers.size() != input.getModifierNames().size()) {
            throw ModelService.report(HttpStatus.CONFLICT,
                    "entity in save refers to at least one non-existent recipe modifier");
        }
        return new ProductionStep(factory, machine, recipe, modifiers, input.getMachineCount());
    }

    private Xgress importXgress(Factory factory, String name, Map<String, Fraction> resources) {
        return new Xgress(factory, name, importAttachedResources(factory.getSave().getGameVersion(), resources));
    }

    private Changelist importChangelist(Save save, ChangelistMigration input) {
        Map<ProductionStep, Fraction> productionStepChanges = input.getProductionStepChanges().entrySet().stream()
                .collect(toMap(entry -> getDetachedProductionStep(save, entry.getKey()), entry -> entry.getValue()));
        return new Changelist(save, input.getOrdinal(), input.getName(), input.isPrimary(), input.isActive(),
                getAttachedIcon(save.getGameVersion(), input.getIconName()), productionStepChanges);
    }

    private TransportLink importTransportLink(Save save, TransportLinkMigration input) {
        Icon icon = getAttachedIcon(save.getGameVersion(), input.getIconName());
        Factory sourceFactory = getDetachedFactory(save, input.getSourceFactoryName());
        Factory targetFactory = getDetachedFactory(save, input.getTargetFactoryName());
        List<Resource> resources = importAttachedResources(save.getGameVersion(), input.getResources());
        return new TransportLink(save, input.getName(), input.getDescription(), icon, sourceFactory, targetFactory,
                resources);
    }

    private List<Resource> importAttachedResources(GameVersion gameVersion, Map<String, Fraction> input) {
        return input.entrySet().stream()
                .map(entry -> new Resource(
                        items.findByGameVersionIdAndName(gameVersion.getId(), entry.getKey())
                                .orElseThrow(() -> ModelService.report(HttpStatus.CONFLICT,
                                        "entity in save refers to non-existent item '" + entry.getKey() + "'")),
                        entry.getValue()))
                .toList();
    }

    private static Factory getDetachedFactory(Save save, String name) {
        return save.getFactories().stream().filter(factory -> factory.getName().equals(name)).findAny()
                .orElseThrow(() -> ModelService.report(HttpStatus.CONFLICT,
                        "entity in save refers to non-existent factory '" + name + "'"));
    }

    private static ProductionStep getDetachedProductionStep(Save save, String name) {
        // TODO production steps need names now?
        return save.getFactories().stream().map(Factory::getProductionSteps).flatMap(List::stream)
                .filter(productionStep -> productionStep.getRecipe().getName().equals(name)).findAny()
                .orElseThrow(() -> ModelService.report(HttpStatus.BAD_REQUEST,
                        "entity in save refers to non-existent production step '" + name + "'"));
    }

    private Icon getAttachedIcon(GameVersion gameVersion, String name) {
        if (null == name) {
            return null;
        }
        return icons.findByGameVersionIdAndName(gameVersion.getId(), name).orElseThrow(() -> ModelService
                .report(HttpStatus.CONFLICT, "entity in save refers to non-existent icon '" + name + "'"));
    }

    // since we need empty modifiable collections so often
    private static <E> List<E> nl() {
        return new ArrayList<>();
    }

    private static <K, V> Map<K, V> nm() {
        return new HashMap<>();
    }

}
