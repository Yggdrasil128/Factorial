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

    public static GameSummary exportGame(Game game, External destination) {
        GameSummary summary = new GameSummary();
        summary.setGame(GameStandalone.of(game, destination));
        summary.setIcons(game.getIcons().stream().map(icon -> IconStandalone.of(icon, destination)).toList());
        summary.setItems(game.getItems().stream().map(item -> ItemStandalone.of(item, destination)).toList());
        summary.setRecipes(game.getRecipes().stream().map(recipe -> RecipeStandalone.of(recipe, destination)).toList());
        summary.setRecipeModifiers(game.getRecipeModifiers().stream()
                .map(recipeModifier -> RecipeModifierStandalone.of(recipeModifier, destination)).toList());
        summary.setMachines(
                game.getMachines().stream().map(machine -> MachineStandalone.of(machine, destination)).toList());
        return summary;
    }

    public static SaveSummary exportSave(Save save, External destination) {
        SaveSummary summary = new SaveSummary();
        summary.setSave(SaveStandalone.of(save, destination));
        summary.setFactories(save.getFactories().stream().map(factory -> exportFactory(factory, destination)).toList());
        summary.setChangelists(save.getChangelists().stream()
                .map(changelist -> ChangelistStandalone.of(changelist, destination)).toList());
        return summary;
    }

    private static FactorySummary exportFactory(Factory factory, External destination) {
        FactorySummary summary = new FactorySummary();
        summary.setFactory(FactoryStandalone.of(factory, destination));
        summary.setProductionSteps(factory.getProductionSteps().stream()
                .map(productionStep -> ProductionStepStandalone.of(productionStep, destination)).toList());
        summary.setResources(
                factory.getResources().stream().map(resource -> ResourceStandalone.of(resource, destination)).toList());
        return summary;
    }

}
