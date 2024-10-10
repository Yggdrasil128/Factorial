package de.yggdrasil128.factorial.api.migration;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.changelist.ChangelistMigration;
import de.yggdrasil128.factorial.model.changelist.ProductionStepChangeMigration;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryMigration;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.stream.Collectors.*;

@Service
public class MigrationService {

    private final GameVersionRepository gameVersionRepository;
    private final ItemRepository itemRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeModifierRepository recipeModifierRepository;
    private final MachineRepository machineRepository;
    private final SaveRepository saveRepository;
    private final IconRepository iconRepository;

    @Autowired
    public MigrationService(GameVersionRepository gameVersionRepository, ItemRepository itemRepository,
                            RecipeRepository recipeRepository, RecipeModifierRepository recipeModifierRepository,
                            MachineRepository machineRepository, SaveRepository saveRepository,
                            IconRepository iconRepository) {
        this.gameVersionRepository = gameVersionRepository;
        this.itemRepository = itemRepository;
        this.recipeRepository = recipeRepository;
        this.recipeModifierRepository = recipeModifierRepository;
        this.machineRepository = machineRepository;
        this.saveRepository = saveRepository;
        this.iconRepository = iconRepository;
    }

    public GameVersion importGameVersion(GameVersionMigration input) {
        GameVersion gameVersion;
        gameVersion = importGameVersion0(input);
        gameVersionRepository.save(gameVersion);
        return gameVersion;
    }

    private static GameVersion importGameVersion0(GameVersionMigration input) {
        GameVersion gameVersion = new GameVersion(input.getName(), null, nl(), nl(), nl(), nl(), nl());
        input.getIcons().entrySet().stream().map(entry -> importIcon(gameVersion, entry.getKey(), entry.getValue()))
                .forEach(gameVersion.getIcons()::add);
        input.getItems().entrySet().stream().map(entry -> importItem(gameVersion, entry.getKey(), entry.getValue()))
                .forEach(gameVersion.getItems()::add);
        input.getRecipes().entrySet().stream().map(entry -> importRecipe(gameVersion, entry.getKey(), entry.getValue()))
                .forEach(gameVersion.getRecipes()::add);
        input.getRecipeModifiers().entrySet().stream()
                .map(entry -> importRecipeModifier(gameVersion, entry.getKey(), entry.getValue()))
                .forEach(gameVersion.getRecipeModifiers()::add);
        input.getMachines().entrySet().stream()
                .map(entry -> importMachine(gameVersion, entry.getKey(), entry.getValue()))
                .forEach(gameVersion.getMachines()::add);
        if (null != input.getIconName()) {
            gameVersion.setIcon(getTransientIcon(gameVersion, input.getIconName()));
        }
        return gameVersion;
    }

    private static Icon importIcon(GameVersion gameVersion, String name, IconMigration input) {
        return new Icon(gameVersion, name, input.getImageData(), input.getMimeType(), input.getCategory());
    }

    private static Item importItem(GameVersion gameVersion, String name, ItemMigration input) {
        return new Item(gameVersion, name, input.getDescription(), getTransientIcon(gameVersion, input.getIconName()),
                input.getCategory());
    }

    private static Recipe importRecipe(GameVersion gameVersion, String name, RecipeMigration input) {
        Icon icon = getTransientIcon(gameVersion, input.getIconName());
        List<Resource> inputs = importResources(gameVersion, input.getInput());
        List<Resource> outputs = importResources(gameVersion, input.getOutput());
        return new Recipe(gameVersion, name, icon, inputs, outputs, input.getDuration(), nl(), nl(),
                input.getCategory());
    }

    private static List<Resource> importResources(GameVersion gameVersion, Map<String, Fraction> input) {
        return input.entrySet().stream()
                .map(entry -> new Resource(getTransientItem(gameVersion, entry.getKey()), entry.getValue())).toList();
    }

    private static RecipeModifier importRecipeModifier(GameVersion gameVersion, String name,
                                                       RecipeModifierMigration input) {
        return new RecipeModifier(gameVersion, name, input.getDescription(),
                getTransientIcon(gameVersion, input.getIconName()), input.getDurationMultiplier(),
                input.getInputQuantityMultiplier(), input.getOutputQuantityMultiplier());
    }

    private static Machine importMachine(GameVersion gameVersion, String name, MachineMigration input) {
        List<RecipeModifier> machineModifiers = input.getMachineModifierNames().stream()
                .map(machineModifierName -> getTransientRecipeModifier(gameVersion, machineModifierName)).toList();
        return new Machine(gameVersion, name, getTransientIcon(gameVersion, input.getIconName()), machineModifiers,
                input.getCategory());
    }

    private static Item getTransientItem(GameVersion gameVersion, String name) {
        return gameVersion.getItems().stream().filter(item -> item.getName().equals(name)).findAny()
                .orElseThrow(() -> ModelService.report(HttpStatus.BAD_REQUEST,
                        "nested entity refers to non existent item '" + name + "'"));
    }

    private static RecipeModifier getTransientRecipeModifier(GameVersion gameVersion, String name) {
        return gameVersion.getRecipeModifiers().stream().filter(recipeModifier -> recipeModifier.getName().equals(name))
                .findAny().orElseThrow(() -> ModelService.report(HttpStatus.BAD_REQUEST,
                        "nested entity refers to non-existent recipe modifier '" + name + "'"));
    }

    private static Icon getTransientIcon(GameVersion gameVersion, String name) {
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
        GameVersion gameVersion = gameVersionRepository.findByName(input.getGameVersion())
                .orElseThrow(() -> ModelService.report(HttpStatus.CONFLICT,
                        "save requires the game version '" + input.getGameVersion() + "' to be installed"));
        Save save = new Save(gameVersion, input.getName(), nl(), nl());
        input.getFactories().stream().map(entry -> importFactory(save, entry)).forEach(save.getFactories()::add);
        input.getChangelists().stream().map(entry -> importChangelist(save, entry)).forEach(save.getChangelists()::add);
        saveRepository.save(save);
        return save;
    }

    private Factory importFactory(Save save, FactoryMigration input) {
        Map<Item, Integer> itemOrder = new HashMap<>();
        for (int i = 0; i < input.getItemOrder().size(); i++) {
            itemOrder.put(getAttachedItem(save.getGameVersion(), input.getItemOrder().get(i)), i + 1);
        }
        Factory factory = new Factory(save, input.getOrdinal(), input.getName(), input.getDescription(),
                getAttachedIcon(save.getGameVersion(), input.getIconName()), nl(), itemOrder);
        for (ProductionStepMigration entry : input.getProductionSteps()) {
            ProductionStep productionStep = importProductionStep(factory, entry);
            factory.getProductionSteps().add(productionStep);
            productionStep.getRecipe().getInput().forEach(resource -> factory.getItemOrder()
                    .computeIfAbsent(resource.getItem(), item -> itemOrder.get(item)));
            productionStep.getRecipe().getOutput().forEach(resource -> factory.getItemOrder()
                    .computeIfAbsent(resource.getItem(), item -> itemOrder.get(item)));
        }
        return factory;
    }

    private ProductionStep importProductionStep(Factory factory, ProductionStepMigration input) {
        int gameVersionId = factory.getSave().getGameVersion().getId();
        Machine machine = machineRepository.findByGameVersionIdAndName(gameVersionId, input.getMachineName())
                .orElseThrow(() -> ModelService.report(HttpStatus.CONFLICT,
                        "entity in save refers to non-existent machine '" + input.getMachineName() + "'"));
        Recipe recipe = recipeRepository.findByGameVersionIdAndName(gameVersionId, input.getRecipeName())
                .orElseThrow(() -> ModelService.report(HttpStatus.CONFLICT,
                        "entity in save refers to non-existent recipe '" + input.getRecipeName() + "'"));
        List<RecipeModifier> modifiers = recipeModifierRepository.findAllByGameVersionIdAndNameIn(gameVersionId,
                input.getModifierNames());
        if (modifiers.size() != input.getModifierNames().size()) {
            throw ModelService.report(HttpStatus.CONFLICT,
                    "entity in save refers to at least one non-existent recipe modifier");
        }
        return new ProductionStep(factory, machine, recipe, modifiers, input.getMachineCount());
    }

    private Changelist importChangelist(Save save, ChangelistMigration input) {
        Map<ProductionStep, Fraction> productionStepChanges = new HashMap<>();
        for (Map.Entry<String, List<ProductionStepChangeMigration>> factoryEntry : input.getProductionStepChanges()
                .entrySet()) {
            Factory factory = getTransientFactory(save, factoryEntry.getKey());
            for (ProductionStepChangeMigration productionStepEntry : factoryEntry.getValue()) {
                productionStepChanges.put(
                        factory.getProductionSteps().get(productionStepEntry.getProductionStepIndex()),
                        productionStepEntry.getChange());
            }
        }
        return new Changelist(save, input.getOrdinal(), input.getName(), input.isPrimary(), input.isActive(),
                getAttachedIcon(save.getGameVersion(), input.getIconName()), productionStepChanges);
    }

    private Item getAttachedItem(GameVersion gameVersion, String name) {
        return itemRepository.findByGameVersionIdAndName(gameVersion.getId(), name).orElseThrow(() -> ModelService
                .report(HttpStatus.CONFLICT, "entity in save refers to non-existent item '" + name + "'"));
    }

    private Icon getAttachedIcon(GameVersion gameVersion, String name) {
        if (null == name) {
            return null;
        }
        return iconRepository.findByGameVersionIdAndName(gameVersion.getId(), name).orElseThrow(() -> ModelService
                .report(HttpStatus.CONFLICT, "entity in save refers to non-existent icon '" + name + "'"));
    }

    private static Factory getTransientFactory(Save save, String name) {
        return save.getFactories().stream().filter(factory -> factory.getName().equals(name)).findAny()
                .orElseThrow(() -> ModelService.report(HttpStatus.CONFLICT,
                        "entity in save refers to non-existent factory '" + name + "'"));
    }

    // since we need these so often
    private static <E> List<E> nl() {
        return new ArrayList<>();
    }

    public GameVersionMigration exportGameVersion(GameVersion gameVersion) {
        String iconName = null == gameVersion.getIcon() ? null : gameVersion.getIcon().getName();
        Map<String, IconMigration> icons = gameVersion.getIcons().stream().collect(toMap(Icon::getName,
                MigrationService::exportIcon, MigrationService::reportDuplicate, LinkedHashMap::new));
        Map<String, ItemMigration> items = gameVersion.getItems().stream().collect(toMap(Item::getName,
                MigrationService::exportItem, MigrationService::reportDuplicate, LinkedHashMap::new));
        Map<String, RecipeMigration> recipes = gameVersion.getRecipes().stream().collect(toMap(Recipe::getName,
                MigrationService::exportRecipe, MigrationService::reportDuplicate, LinkedHashMap::new));
        Map<String, RecipeModifierMigration> recipeModifiers = gameVersion.getRecipeModifiers().stream()
                .collect(toMap(RecipeModifier::getName, MigrationService::exportRecipeModifier,
                        MigrationService::reportDuplicate, LinkedHashMap::new));
        Map<String, MachineMigration> machines = gameVersion.getMachines().stream().collect(toMap(Machine::getName,
                MigrationService::exportMachine, MigrationService::reportDuplicate, LinkedHashMap::new));
        return new GameVersionMigration(gameVersion.getName(), iconName, icons, items, recipes, recipeModifiers,
                machines);
    }

    private static IconMigration exportIcon(Icon icon) {
        return new IconMigration(icon.getImageData(), icon.getMimeType(), icon.getCategory());
    }

    private static ItemMigration exportItem(Item item) {
        String iconName = null == item.getIcon() ? null : item.getIcon().getName();
        return new ItemMigration(item.getDescription(), iconName, item.getCategory());
    }

    private static RecipeMigration exportRecipe(Recipe recipe) {
        String iconName = null == recipe.getIcon() ? null : recipe.getIcon().getName();
        Map<String, Fraction> input = recipe.getInput().stream().collect(toMap(resource -> resource.getItem().getName(),
                Resource::getQuantity, MigrationService::reportDuplicate, LinkedHashMap::new));
        Map<String, Fraction> output = recipe.getOutput().stream()
                .collect(toMap(resource -> resource.getItem().getName(), Resource::getQuantity,
                        MigrationService::reportDuplicate, LinkedHashMap::new));
        return new RecipeMigration(iconName, input, output, recipe.getDuration(), recipe.getCategory());
    }

    private static RecipeModifierMigration exportRecipeModifier(RecipeModifier recipeModifier) {
        String iconName = null == recipeModifier.getIcon() ? null : recipeModifier.getIcon().getName();
        return new RecipeModifierMigration(recipeModifier.getDescription(), iconName,
                recipeModifier.getDurationMultiplier(), recipeModifier.getInputQuantityMultiplier(),
                recipeModifier.getOutputQuantityMultiplier());
    }

    private static MachineMigration exportMachine(Machine machine) {
        String iconName = null == machine.getIcon() ? null : machine.getIcon().getName();
        List<String> machineModifierNames = machine.getMachineModifiers().stream().map(RecipeModifier::getName)
                .toList();
        return new MachineMigration(iconName, machineModifierNames, machine.getCategory());
    }

    public SaveMigration exportSave(Save save) {
        List<FactoryMigration> factories = save.getFactories().stream().map(MigrationService::exportFactory).toList();
        List<ChangelistMigration> changelists = save.getChangelists().stream().map(MigrationService::exportChangelist)
                .toList();
        return new SaveMigration(save.getGameVersion().getName(), save.getName(), factories, changelists);
    }

    private static FactoryMigration exportFactory(Factory factory) {
        String iconName = null == factory.getIcon() ? null : factory.getIcon().getName();
        List<ProductionStepMigration> productionSteps = factory.getProductionSteps().stream()
                .map(MigrationService::exportProductionStep).toList();
        List<String> itemOrder = factory.getItemOrder().entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue)).map(entry -> entry.getKey().getName()).toList();
        return new FactoryMigration(factory.getOrdinal(), factory.getName(), factory.getDescription(), iconName,
                productionSteps, itemOrder);
    }

    private static ProductionStepMigration exportProductionStep(ProductionStep productionStep) {
        List<String> modifierNames = productionStep.getModifiers().stream().map(RecipeModifier::getName).toList();
        return new ProductionStepMigration(productionStep.getMachine().getName(), productionStep.getRecipe().getName(),
                modifierNames, productionStep.getMachineCount());
    }

    private static ChangelistMigration exportChangelist(Changelist changelist) {
        String iconName = null == changelist.getIcon() ? null : changelist.getIcon().getName();
        Map<String, List<ProductionStepChangeMigration>> productionStepChanges = changelist.getProductionStepChanges()
                .entrySet().stream().collect(
                        groupingBy(
                                entry -> entry.getKey().getFactory().getName(), mapping(
                                        entry -> new ProductionStepChangeMigration(entry.getKey().getFactory()
                                                .getProductionSteps().indexOf(entry.getKey()), entry.getValue()),
                                        toList())));
        return new ChangelistMigration(changelist.getOrdinal(), changelist.getName(), changelist.isPrimary(),
                changelist.isActive(), iconName, productionStepChanges);
    }

    private static <T> T reportDuplicate(T o1, T o2) {
        throw ModelService.report(HttpStatus.CONFLICT, "duplicate identifier for values " + o1 + " and " + o2);
    }

}
