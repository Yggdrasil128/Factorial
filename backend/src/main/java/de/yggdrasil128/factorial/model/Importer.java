package de.yggdrasil128.factorial.model;

import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.changelist.ChangelistStandalone;
import de.yggdrasil128.factorial.model.changelist.ProductionStepChangeStandalone;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryStandalone;
import de.yggdrasil128.factorial.model.factory.FactorySummary;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionStandalone;
import de.yggdrasil128.factorial.model.gameversion.GameVersionSummary;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconStandalone;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.item.ItemStandalone;
import de.yggdrasil128.factorial.model.machine.Machine;
import de.yggdrasil128.factorial.model.machine.MachineStandalone;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepStandalone;
import de.yggdrasil128.factorial.model.recipe.ItemQuantity;
import de.yggdrasil128.factorial.model.recipe.ItemQuantityStandalone;
import de.yggdrasil128.factorial.model.recipe.Recipe;
import de.yggdrasil128.factorial.model.recipe.RecipeStandalone;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierStandalone;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.resource.ResourceStandalone;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveStandalone;
import de.yggdrasil128.factorial.model.save.SaveSummary;
import org.springframework.http.HttpStatus;

import java.util.*;
import java.util.stream.Collectors;

public class Importer {

    public static GameVersion importGameVersion(GameVersionSummary summary) {
        Importer importer = new Importer();
        GameVersionStandalone input = summary.getGameVersion();
        GameVersion gameVersion = new GameVersion(input);
        /*
         * Caveat: The order of these loops is meaningful, because the 'importer.import...' calls also populate the
         * 'importer' instance's mappings, which are then used in the later loops.
         */
        for (IconStandalone icon : summary.getIcons()) {
            gameVersion.getIcons().add(importer.importIcon(gameVersion, icon));
        }
        for (ItemStandalone item : summary.getItems()) {
            gameVersion.getItems().add(importer.importItem(gameVersion, item));
        }
        for (RecipeModifierStandalone recipeModifier : summary.getRecipeModifiers()) {
            gameVersion.getRecipeModifiers().add(importer.importRecipeModifier(gameVersion, recipeModifier));
        }
        for (MachineStandalone machine : summary.getMachines()) {
            gameVersion.getMachines().add(importer.importMachine(gameVersion, machine));
        }
        for (RecipeStandalone recipe : summary.getRecipes()) {
            gameVersion.getRecipes().add(importer.importRecipe(gameVersion, recipe));
        }
        gameVersion.setIcon(importer.findIcon(input.iconId()));
        return gameVersion;
    }

    public static Save importSave(SaveSummary summary, GameVersion gameVersion) {
        Importer importer = new Importer(gameVersion);
        SaveStandalone input = summary.getSave();
        Save save = new Save(gameVersion, input);
        /**
         * Caveat: The order of these loops is meaningful, because each the latter one one uses the objects that were
         * imported in the first one.
         */
        for (FactorySummary factory : summary.getFactories()) {
            save.getFactories().add(importFactory(save, factory, gameVersion, importer));
        }
        for (ChangelistStandalone changelist : summary.getChangelists()) {
            save.getChangelists().add(importer.importChangelist(save, changelist));
        }
        return save;
    }

    private static Factory importFactory(Save save, FactorySummary summary, GameVersion gameVersion,
                                         Importer importer) {
        Factory factory = importer.importFactory(save, summary.getFactory());
        save.getFactories().add(factory);
        for (ProductionStepStandalone productionStep : summary.getProductionSteps()) {
            factory.getProductionSteps().add(importer.importProductionStep(factory, productionStep));
        }
        for (ResourceStandalone resource : summary.getResources()) {
            factory.getResources().add(importer.importResource(factory, resource));
        }
        return factory;
    }

    private final Map<String, Icon> icons = new HashMap<>();
    private final Map<String, Item> items = new HashMap<>();
    private final Map<String, Recipe> recipes = new HashMap<>();
    private final Map<String, RecipeModifier> recipeModifiers = new HashMap<>();
    private final Map<String, Machine> machines = new HashMap<>();

    private Importer() {
    }

    private Importer(GameVersion context) {
        for (Icon icon : context.getIcons()) {
            icons.put(icon.getName(), icon);
        }
        for (Item item : context.getItems()) {
            items.put(item.getName(), item);
        }
        for (Recipe recipe : context.getRecipes()) {
            recipes.put(recipe.getName(), recipe);
        }
        for (RecipeModifier recipeModifier : context.getRecipeModifiers()) {
            recipeModifiers.put(recipeModifier.getName(), recipeModifier);
        }
        for (Machine machine : context.getMachines()) {
            machines.put(machine.getName(), machine);
        }
    }

    private Icon importIcon(GameVersion gameVersion, IconStandalone input) {
        Icon icon = new Icon(gameVersion, input);
        icons.put(icon.getName(), icon);
        return icon;
    }

    private Item importItem(GameVersion gameVersion, ItemStandalone input) {
        Item item = new Item(gameVersion, input);
        item.setIcon(findIcon(input.iconId()));
        items.put(item.getName(), item);
        return item;
    }

    private RecipeModifier importRecipeModifier(GameVersion gameVersion, RecipeModifierStandalone input) {
        RecipeModifier recipeModifier = new RecipeModifier(gameVersion, input);
        recipeModifier.setIcon(findIcon(input.iconId()));
        recipeModifiers.put(recipeModifier.getName(), recipeModifier);
        return recipeModifier;
    }

    private Machine importMachine(GameVersion gameVersion, MachineStandalone input) {
        Machine machine = new Machine(gameVersion, input);
        machine.setIcon(findIcon(input.iconId()));
        machine.setMachineModifiers(findRecipeModifiers(input.machineModifierIds()));
        machines.put(machine.getName(), machine);
        return machine;
    }

    private Recipe importRecipe(GameVersion gameVersion, RecipeStandalone input) {
        Recipe recipe = new Recipe(gameVersion, input);
        recipe.setIcon(findIcon(input.iconId()));
        recipe.setIngredients(findResources(input.ingredients()));
        recipe.setProducts(findResources(input.products()));
        recipe.setApplicableModifiers(findRecipeModifiers(input.applicableModifierIds()));
        recipe.setApplicableMachines(findMachines(input.applicableMachineIds()));
        return recipe;
    }

    private Factory importFactory(Save save, FactoryStandalone input) {
        Factory factory = new Factory(save, input);
        factory.setIcon(findIcon(input.iconId()));
        return factory;
    }

    private ProductionStep importProductionStep(Factory factory, ProductionStepStandalone input) {
        ProductionStep productionStep = new ProductionStep(factory, input);
        productionStep.setMachine(findMachine(input.machineId()));
        productionStep.setRecipe(findRecipe(input.recipeId()));
        productionStep.setModifiers(findRecipeModifiers(input.modifierIds()));
        return productionStep;
    }

    private Resource importResource(Factory factory, ResourceStandalone input) {
        Resource resource = new Resource(factory, input);
        resource.setItem(findItem(input.itemId()));
        return resource;
    }

    private Changelist importChangelist(Save save, ChangelistStandalone input) {
        Changelist changelist = new Changelist(save, input);
        changelist.setIcon(findIcon(input.iconId()));
        changelist.setProductionStepChanges(findProductionStepChanges(save, input.productionStepChanges()));
        return changelist;
    }

    private Icon findIcon(Object name) {
        return findEntity(name, icons, "icon");
    }

    private Item findItem(Object name) {
        return findEntity(name, items, "item");
    }

    private Recipe findRecipe(Object name) {
        return findEntity(name, recipes, "recipe");
    }

    private RecipeModifier findRecipeModifier(Object name) {
        return findEntity(name, recipeModifiers, "recipe modifier");
    }

    private Machine findMachine(Object name) {
        return findEntity(name, machines, "machine");
    }

    private static <T> T findEntity(Object name, Map<String, T> context, String type) {
        if (null == name) {
            return null;
        }
        T entity = context.get(name);
        if (null == entity) {
            throw ModelService.report(HttpStatus.BAD_REQUEST,
                    "nested entity refers to non-existent " + type + " '" + name + "'");
        }
        return entity;
    }

    private List<ItemQuantity> findResources(List<ItemQuantityStandalone> standalones) {
        return null == standalones ? new ArrayList<>()
                : standalones.stream()
                        .map(standalone -> new ItemQuantity(findItem(standalone.itemId()), standalone.quantity()))
                        .filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new));
    }

    private List<RecipeModifier> findRecipeModifiers(List<Object> names) {
        return null == names ? new ArrayList<>()
                : names.stream().map(this::findRecipeModifier).filter(Objects::nonNull)
                        .collect(Collectors.toCollection(ArrayList::new));
    }

    private List<Machine> findMachines(List<Object> names) {
        return null == names ? new ArrayList<>()
                : names.stream().map(this::findMachine).filter(Objects::nonNull)
                        .collect(Collectors.toCollection(ArrayList::new));
    }

    private static Map<ProductionStep, Fraction>
            findProductionStepChanges(Save save, List<ProductionStepChangeStandalone> standalones) {
        return null == standalones ? new HashMap<>() : standalones.stream().collect(Collectors.toMap(standalone -> {
            // TODO error handling
            String[] parts = ((String) standalone.productionStepId()).split("\\.");
            return save.getFactories().get(Integer.parseInt(parts[0])).getProductionSteps()
                    .get(Integer.parseInt(parts[1]));
        }, ProductionStepChangeStandalone::change));
    }

}
