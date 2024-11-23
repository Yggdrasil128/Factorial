package de.yggdrasil128.factorial.model;

import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.changelist.ChangelistStandalone;
import de.yggdrasil128.factorial.model.changelist.ProductionStepChangeStandalone;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryStandalone;
import de.yggdrasil128.factorial.model.factory.FactorySummary;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameStandalone;
import de.yggdrasil128.factorial.model.game.GameSummary;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconDownloader;
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
import de.yggdrasil128.factorial.model.resource.local.LocalResource;
import de.yggdrasil128.factorial.model.resource.local.LocalResourceStandalone;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveStandalone;
import de.yggdrasil128.factorial.model.save.SaveSummary;
import org.springframework.http.HttpStatus;

import java.util.*;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;

public class Importer {

    public static Game importGame(GameSummary summary, IconDownloader iconDownloader) {
        Importer importer = new Importer();
        GameStandalone input = summary.getGame();
        Game game = new Game(input);
        /*
         * Caveat: The order of these loops is meaningful, because the 'importer.import...' calls also populate the
         * 'importer' instance's mappings, which are then used in the later loops.
         */
        for (IconStandalone icon : summary.getIcons()) {
            game.getIcons().add(importer.importIcon(game, icon, iconDownloader));
        }
        for (ItemStandalone item : summary.getItems()) {
            game.getItems().add(importer.importItem(game, item));
        }
        for (RecipeModifierStandalone recipeModifier : summary.getRecipeModifiers()) {
            game.getRecipeModifiers().add(importer.importRecipeModifier(game, recipeModifier));
        }
        for (MachineStandalone machine : summary.getMachines()) {
            game.getMachines().add(importer.importMachine(game, machine));
        }
        for (RecipeStandalone recipe : summary.getRecipes()) {
            game.getRecipes().add(importer.importRecipe(game, recipe));
        }
        game.setIcon(importer.findIcon(input.iconId()));
        return game;
    }

    public static Save importSave(SaveSummary summary, Game game) {
        Importer importer = new Importer(game);
        SaveStandalone input = summary.getSave();
        Save save = new Save(game, input);
        /**
         * Caveat: The order of invocations is meaningful, because the latter one uses the objects that were imported in
         * the first one.
         */
        importer.importFactories(summary, save);
        importer.importChangelists(summary, save);
        return save;
    }

    private final Map<String, Icon> icons = new HashMap<>();
    private final Map<String, Item> items = new HashMap<>();
    private final Map<String, Recipe> recipes = new HashMap<>();
    private final Map<String, RecipeModifier> recipeModifiers = new HashMap<>();
    private final Map<String, Machine> machines = new HashMap<>();

    private Importer() {
    }

    private Importer(Game context) {
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

    private Icon importIcon(Game game, IconStandalone input, IconDownloader iconDownloader) {
        Icon icon = new Icon(game, input);
        iconDownloader.downloadIcon(icon, input);
        icons.put(icon.getName(), icon);
        return icon;
    }

    private Item importItem(Game game, ItemStandalone input) {
        Item item = new Item(game, input);
        item.setIcon(findIcon(input.iconId()));
        items.put(item.getName(), item);
        return item;
    }

    private RecipeModifier importRecipeModifier(Game game, RecipeModifierStandalone input) {
        RecipeModifier recipeModifier = new RecipeModifier(game, input);
        recipeModifier.setIcon(findIcon(input.iconId()));
        recipeModifiers.put(recipeModifier.getName(), recipeModifier);
        return recipeModifier;
    }

    private Machine importMachine(Game game, MachineStandalone input) {
        Machine machine = new Machine(game, input);
        machine.setIcon(findIcon(input.iconId()));
        machine.setMachineModifiers(findRecipeModifiers(input.machineModifierIds()));
        machines.put(machine.getName(), machine);
        return machine;
    }

    private Recipe importRecipe(Game game, RecipeStandalone input) {
        Recipe recipe = new Recipe(game, input);
        recipe.setIcon(findIcon(input.iconId()));
        recipe.setIngredients(findResources(input.ingredients()));
        recipe.setProducts(findResources(input.products()));
        recipe.setApplicableModifiers(findRecipeModifiers(input.applicableModifierIds()));
        recipe.setApplicableMachines(findMachines(input.applicableMachineIds()));
        return recipe;
    }

    private void importFactories(SaveSummary summary, Save save) {
        int ordinal = 0;
        for (FactorySummary input : summary.getFactories()) {
            Factory factory = importFactory(save, input.getFactory());
            ordinal = inferOrdinal(ordinal, factory, input.getFactory().ordinal());
            save.getFactories().add(factory);
            for (ProductionStepStandalone productionStep : input.getProductionSteps()) {
                factory.getProductionSteps().add(importProductionStep(factory, productionStep));
            }
            importResources(input, factory);
            save.getFactories().add(factory);
        }
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

    private void importResources(FactorySummary summary, Factory factory) {
        int ordinal = 0;
        for (LocalResourceStandalone input : summary.getResources()) {
            LocalResource resource = importResource(factory, input);
            ordinal = inferOrdinal(ordinal, resource, input.ordinal());
            factory.getResources().add(resource);
        }
    }

    private LocalResource importResource(Factory factory, LocalResourceStandalone input) {
        LocalResource resource = new LocalResource(factory, input);
        resource.setItem(findItem(input.itemId()));
        return resource;
    }

    private void importChangelists(SaveSummary summary, Save save) {
        int ordinal = 0;
        for (ChangelistStandalone input : summary.getChangelists()) {
            Changelist changelist = importChangelist(save, input);
            ordinal = inferOrdinal(ordinal, changelist, input.ordinal());
            save.getChangelists().add(changelist);
        }
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
                        .filter(Objects::nonNull).collect(toCollection(ArrayList::new));
    }

    private List<RecipeModifier> findRecipeModifiers(List<Object> names) {
        return null == names ? new ArrayList<>()
                : names.stream().map(this::findRecipeModifier).filter(Objects::nonNull)
                        .collect(toCollection(ArrayList::new));
    }

    private List<Machine> findMachines(List<Object> names) {
        return null == names ? new ArrayList<>()
                : names.stream().map(this::findMachine).filter(Objects::nonNull).collect(toCollection(ArrayList::new));
    }

    private static Map<ProductionStep, Fraction>
            findProductionStepChanges(Save save, List<ProductionStepChangeStandalone> standalones) {
        // During import, production steps do not have an id, so we cannot use a normal HashMap here.
        return null == standalones ? new HashMap<>() : standalones.stream().collect(toMap(standalone -> {
            // TODO error handling
            String[] parts = ((String) standalone.productionStepId()).split("\\.");
            return save.getFactories().get(Integer.parseInt(parts[0])).getProductionSteps()
                    .get(Integer.parseInt(parts[1]));
        }, ProductionStepChangeStandalone::change, (p1, p2) -> {
            throw new IllegalStateException("Duplicate key " + p1);
        }, IdentityHashMap::new));
    }

    private static int inferOrdinal(int max, OrderedModel entity, Integer input) {
        int ordinal = null == input ? max + 1 : input.intValue();
        entity.setOrdinal(ordinal);
        return Integer.max(max, ordinal);
    }

}
