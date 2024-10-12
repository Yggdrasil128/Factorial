package de.yggdrasil128.factorial.api.migration;

import de.yggdrasil128.factorial.model.RelationRepresentation;
import de.yggdrasil128.factorial.model.changelist.ChangelistStandalone;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryStandalone;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionStandalone;
import de.yggdrasil128.factorial.model.gameversion.GameVersionSummary;
import de.yggdrasil128.factorial.model.icon.IconStandalone;
import de.yggdrasil128.factorial.model.item.ItemStandalone;
import de.yggdrasil128.factorial.model.machine.MachineStandalone;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepStandalone;
import de.yggdrasil128.factorial.model.recipe.RecipeStandalone;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierStandalone;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveStandalone;
import de.yggdrasil128.factorial.model.save.SaveSummary;

import java.util.ArrayList;
import java.util.List;

public class Exporter {

    public static GameVersionSummary exportGameVersion(GameVersion gameVersion,
                                                       RelationRepresentation resolveStrategy) {
        GameVersionSummary summary = new GameVersionSummary();
        summary.setGameVersion(new GameVersionStandalone(gameVersion, resolveStrategy));
        summary.setIcons(
                gameVersion.getIcons().stream().map(icon -> new IconStandalone(icon, resolveStrategy)).toList());
        summary.setItems(
                gameVersion.getItems().stream().map(item -> new ItemStandalone(item, resolveStrategy)).toList());
        summary.setRecipes(gameVersion.getRecipes().stream()
                .map(recipe -> new RecipeStandalone(recipe, resolveStrategy)).toList());
        summary.setRecipeModifiers(gameVersion.getRecipeModifiers().stream()
                .map(recipeModifier -> new RecipeModifierStandalone(recipeModifier, resolveStrategy)).toList());
        summary.setMachines(gameVersion.getMachines().stream()
                .map(machine -> new MachineStandalone(machine, resolveStrategy)).toList());
        return summary;
    }

    public static SaveSummary exportSave(Save save, RelationRepresentation resolveStrategy) {
        SaveSummary summary = new SaveSummary();
        summary.setSave(new SaveStandalone(save, resolveStrategy));
        List<FactoryStandalone> factories = new ArrayList<>();
        List<List<ProductionStepStandalone>> productionSteps = new ArrayList<>();
        for (Factory factory : save.getFactories()) {
            factories.add(new FactoryStandalone(factory, resolveStrategy));
            productionSteps.add(factory.getProductionSteps().stream()
                    .map(productionStep -> new ProductionStepStandalone(productionStep, resolveStrategy)).toList());
        }
        summary.setFactories(factories);
        summary.setProductionSteps(productionSteps);
        summary.setChangelists(save.getChangelists().stream()
                .map(changelist -> new ChangelistStandalone(changelist, resolveStrategy)).toList());
        return summary;
    }

}
