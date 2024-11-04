package de.yggdrasil128.factorial.model;

import de.yggdrasil128.factorial.model.changelist.ChangelistStandalone;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryStandalone;
import de.yggdrasil128.factorial.model.factory.FactorySummary;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameStandalone;
import de.yggdrasil128.factorial.model.game.GameSummary;
import de.yggdrasil128.factorial.model.icon.IconStandalone;
import de.yggdrasil128.factorial.model.item.ItemStandalone;
import de.yggdrasil128.factorial.model.machine.MachineStandalone;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepStandalone;
import de.yggdrasil128.factorial.model.recipe.RecipeStandalone;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierStandalone;
import de.yggdrasil128.factorial.model.resource.ResourceStandalone;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveStandalone;
import de.yggdrasil128.factorial.model.save.SaveSummary;

public class Exporter {

    public static GameSummary exportGame(Game game,
                                         External resolveStrategy) {
        GameSummary summary = new GameSummary();
        summary.setGame(GameStandalone.of(game, resolveStrategy));
        summary.setIcons(
                game.getIcons().stream().map(icon -> IconStandalone.of(icon, resolveStrategy)).toList());
        summary.setItems(
                game.getItems().stream().map(item -> ItemStandalone.of(item, resolveStrategy)).toList());
        summary.setRecipes(game.getRecipes().stream()
                .map(recipe -> new RecipeStandalone(recipe, resolveStrategy)).toList());
        summary.setRecipeModifiers(game.getRecipeModifiers().stream()
                .map(recipeModifier -> RecipeModifierStandalone.of(recipeModifier, resolveStrategy)).toList());
        summary.setMachines(game.getMachines().stream()
                .map(machine -> new MachineStandalone(machine, resolveStrategy)).toList());
        return summary;
    }

    public static SaveSummary exportSave(Save save, External resolveStrategy) {
        SaveSummary summary = new SaveSummary();
        summary.setSave(SaveStandalone.of(save, resolveStrategy));
        summary.setFactories(
                save.getFactories().stream().map(factory -> exportFactory(factory, resolveStrategy)).toList());
        summary.setChangelists(save.getChangelists().stream()
                .map(changelist -> ChangelistStandalone.of(changelist, resolveStrategy)).toList());
        return summary;
    }

    private static FactorySummary exportFactory(Factory factory, External resolveStrategy) {
        FactorySummary summary = new FactorySummary();
        summary.setFactory(FactoryStandalone.of(factory, resolveStrategy));
        summary.setProductionSteps(factory.getProductionSteps().stream()
                .map(productionStep -> ProductionStepStandalone.of(productionStep, resolveStrategy)).toList());
        summary.setResources(factory.getResources().stream()
                .map(resource -> ResourceStandalone.of(resource, resolveStrategy)).toList());
        return summary;
    }

}
