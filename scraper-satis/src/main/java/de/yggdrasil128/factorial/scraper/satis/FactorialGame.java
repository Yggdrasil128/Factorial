package de.yggdrasil128.factorial.scraper.satis;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.game.GameStandalone;
import de.yggdrasil128.factorial.model.game.GameSummary;
import de.yggdrasil128.factorial.model.icon.IconStandalone;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierStandalone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class FactorialGame {

    private static final Logger LOG = LoggerFactory.getLogger(FactorialGame.class);

    public static FactorialGame from(SatisGame source) {

        FactorialGame result = new FactorialGame(source.getName());

        for (SatisItem item : source.getItems().values()) {
            result.getItems().put(item.className(), FactorialItem.from(item));
        }
        for (SatisResource resource : source.getResources().values()) {
            result.getItems().put(resource.className(), FactorialItem.from(resource));
        }

        Map<ResourceForm, List<SatisResourceExtractor>> resourceExtractorsPerForm = new EnumMap<>(ResourceForm.class);
        for (SatisResourceExtractor resourceExtractor : source.getResourceExtractors().values()) {
            Set<ResourceForm> allowedResourcesForms = EnumSet.noneOf(ResourceForm.class);
            for (StringStructs.Node node : resourceExtractor.allowedResourceForms().root()) {
                allowedResourcesForms.add(ResourceForm.valueOf(node.asText()));
            }
            for (ResourceForm form : allowedResourcesForms) {
                resourceExtractorsPerForm.computeIfAbsent(form, key -> new ArrayList<>()).add(resourceExtractor);
            }
            FactorialMachine machine = FactorialMachine.from(resourceExtractor, allowedResourcesForms);
            for (FactorialRecipeModifier recipeModifier : machine.machineModifiers()) {
                result.getRecipeModifiers().put(recipeModifier.name(), recipeModifier);
            }
            result.getMachines().put(resourceExtractor.className(), machine);
        }
        for (SatisManufacturer manufacturer : source.getManufacturers().values()) {
            result.getMachines().put(manufacturer.className(), FactorialMachine.from(manufacturer));
        }
        for (SatisFuelGenerator fuelGenerator : source.getFuelGenerators().values()) {
            result.getMachines().put(fuelGenerator.className(), FactorialMachine.from(fuelGenerator));
        }

        for (SatisRecipe recipe : source.getRecipes().values()) {
            FactorialRecipe.from(result, recipe).ifPresent(r -> result.getRecipes().put(recipe.className(), r));
        }
        for (SatisResource resource : source.getResources().values()) {
            FactorialRecipe.from(result, resource, resourceExtractorsPerForm.get(resource.form()))
                    .ifPresent(r -> result.getRecipes().put(resource.className(), r));
        }
        for (SatisFuelGenerator fuelGenerator : source.getFuelGenerators().values()) {
            for (SatisFuelGenerator.Fuel fuel : fuelGenerator.fuels()) {
                FactorialRecipe.from(result, fuelGenerator, fuel)
                        .ifPresent(r -> result.getRecipes().put("Burn_" + fuel.fuelClass(), r));
            }
        }

        return result;
    }

    private final String name;
    private final Map<String, FactorialItem> items = new HashMap<>();
    private final Map<String, FactorialRecipe> recipes = new HashMap<>();
    private final Map<String, FactorialRecipeModifier> recipeModifiers = new HashMap<>();
    private final Map<String, FactorialMachine> machines = new HashMap<>();

    public FactorialGame(String name) {
        this.name = name;
    }

    public Map<String, FactorialItem> getItems() {
        return items;
    }

    public Map<String, FactorialRecipe> getRecipes() {
        return recipes;
    }

    public Map<String, FactorialRecipeModifier> getRecipeModifiers() {
        return recipeModifiers;
    }

    public Map<String, FactorialMachine> getMachines() {
        return machines;
    }

    private static RecipeModifierStandalone IMPURE_NODE = new RecipeModifierStandalone(0, 0, "Impure Node", "", null,
            Fraction.ONE, Fraction.ONE, Fraction.of(1, 2), emptyList());
    private static RecipeModifierStandalone NORMAL_NODE = new RecipeModifierStandalone(0, 0, "Normal Node", "", null,
            Fraction.ONE, Fraction.ONE, Fraction.ONE, emptyList());
    private static RecipeModifierStandalone PURE_NODE = new RecipeModifierStandalone(0, 0, "Pure Node", "", null,
            Fraction.ONE, Fraction.ONE, Fraction.of(2), emptyList());

    public GameSummary toSummary() throws IOException {
        try {
            LOG.debug("Scraping wiki for icons");
            Map<String, String> wikiIconPages = WikiIconScraper.findIconPages();
            Map<String, IconStandalone> icons = new HashMap<>();
            GameSummary summary = new GameSummary();
            summary.setGame(new GameStandalone(0, null, name, "", null));
            summary.setItems(items.values().stream()
                    .map(item -> item.toStandalone(loadIcon(wikiIconPages, icons, item.name(), "items"))).toList());
            summary.setRecipes(recipes.values().stream().map(FactorialRecipe::toStandalone).toList());
            summary.setRecipeModifiers(Stream.concat(Stream.of(IMPURE_NODE, NORMAL_NODE, PURE_NODE),
                    recipeModifiers.values().stream().map(FactorialRecipeModifier::toStandalone)).toList());
            summary.setMachines(machines.values().stream()
                    .map(machine -> machine.toStandalone(loadIcon(wikiIconPages, icons, machine.name(), "machines")))
                    .toList());
            summary.setIcons(icons.values().stream().toList());
            return summary;
        } catch (UncheckedIOException exc) {
            throw exc.getCause();
        }
    }

    private static IconStandalone loadIcon(Map<String, String> wikiIconPages, Map<String, IconStandalone> icons,
                                           String name, String category) {
        IconStandalone icon = icons.get(name);
        if (null != icon) {
            return icon;
        }
        String url = wikiIconPages.get(name);
        if (null == url) {
            LOG.info("No icon found for {}", name);
            return null;
        }
        try {
            icon = new IconStandalone(0, 0, name, null, WikiIconScraper.readIconPage(url), null, 0,
                    singletonList(category));
            icons.put(icon.name(), icon);
            return icon;

        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }
    }

}