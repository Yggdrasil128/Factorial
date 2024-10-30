package de.yggdrasil128.factorial.model;

import de.yggdrasil128.factorial.model.changelist.ChangelistStandalone;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryStandalone;
import de.yggdrasil128.factorial.model.factory.FactorySummary;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionStandalone;
import de.yggdrasil128.factorial.model.gameversion.GameVersionSummary;
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

    public static GameVersionSummary exportGameVersion(GameVersion gameVersion,
                                                       RelationRepresentation resolveStrategy) {
        GameVersionSummary summary = new GameVersionSummary();
        summary.setGameVersion(GameVersionStandalone.of(gameVersion, resolveStrategy));
        summary.setIcons(
                gameVersion.getIcons().stream().map(icon -> IconStandalone.of(icon, resolveStrategy)).toList());
        summary.setItems(
                gameVersion.getItems().stream().map(item -> ItemStandalone.of(item, resolveStrategy)).toList());
        summary.setRecipes(gameVersion.getRecipes().stream()
                .map(recipe -> new RecipeStandalone(recipe, resolveStrategy)).toList());
        summary.setRecipeModifiers(gameVersion.getRecipeModifiers().stream()
                .map(recipeModifier -> RecipeModifierStandalone.of(recipeModifier, resolveStrategy)).toList());
        summary.setMachines(gameVersion.getMachines().stream()
                .map(machine -> new MachineStandalone(machine, resolveStrategy)).toList());
        return summary;
    }

    public static SaveSummary exportSave(Save save, RelationRepresentation resolveStrategy) {
        SaveSummary summary = new SaveSummary();
        summary.setSave(SaveStandalone.of(save, resolveStrategy));
        summary.setFactories(
                save.getFactories().stream().map(factory -> exportFactory(factory, resolveStrategy)).toList());
        summary.setChangelists(save.getChangelists().stream()
                .map(changelist -> ChangelistStandalone.of(changelist, resolveStrategy)).toList());
        return summary;
    }

    private static FactorySummary exportFactory(Factory factory, RelationRepresentation resolveStrategy) {
        FactorySummary summary = new FactorySummary();
        summary.setFactory(FactoryStandalone.of(factory, resolveStrategy));
        summary.setProductionSteps(factory.getProductionSteps().stream()
                .map(productionStep -> ProductionStepStandalone.of(productionStep, resolveStrategy)).toList());
        summary.setResources(factory.getResources().stream()
                .map(resource -> ResourceStandalone.of(resource, resolveStrategy)).toList());
        return summary;
    }

}
