package de.yggdrasil128.factorial.scraper.satis;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.recipe.RecipeStandalone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.StreamSupport;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public record FactorialRecipe(String name,
                              String description,
                              List<FactorialItemQuantity> ingredients,
                              List<FactorialItemQuantity> products,
                              Fraction duration,
                              Collection<FactorialRecipeModifier> applicableModifiers,
                              Collection<FactorialMachine> applicableMachines) {

    private static final Logger LOG = LoggerFactory.getLogger(FactorialRecipe.class);

    /**
     * Interprets a Factorial Recipe from a Satisfactory Recipe.
     * <p>
     * This is the "simple" case where we just have to find the correct Items for input and output and the applicable
     * Machines. We never have Recipe Modifiers, because those are all generated to represent the more interesting
     * Factorial Recipes.
     * 
     * @param context the enclosing Game, used for locating Items and Machines
     * @param source the source Satisfactory Recipe
     * @return the Factorial Recipe
     */
    public static Optional<FactorialRecipe> from(FactorialGame context, SatisRecipe source) {
        List<FactorialMachine> applicableMachines = new ArrayList<>();
        for (StringStructs.Node node : source.producedIn().root()) {
            QualifiedName producedIn = QualifiedName.of(node.asText());
            FactorialMachine machine = context.getMachines().get(producedIn.simple());
            if (null != machine) {
                applicableMachines.add(machine);
            }
        }
        if (applicableMachines.isEmpty()) {
            LOG.trace("Could not associate recipe {} with a machine", source.className());
            return Optional.empty();
        }
        List<FactorialRecipeModifier> applicableModifiers = new ArrayList<>();
        for (FactorialMachine machine : applicableMachines) {
            if (!machine.recipeModifiers().isEmpty()) {
                if (!applicableModifiers.isEmpty()) {
                    LOG.error("Could not infer recipe modifiers for recipe {}, because multiple machines provide them");
                    return Optional.empty();
                }
                applicableModifiers.addAll(machine.recipeModifiers());
            }
        }
        List<FactorialItemQuantity> ingredients = new ArrayList<>();
        for (StringStructs.Node node : source.ingredients().root()) {
            QualifiedClass ingredient = QualifiedClass.of(node.get("ItemClass").asText());
            FactorialItem item = context.getItems().get(ingredient.name().simple());
            if (null == item) {
                LOG.error("Could not associate ingredient {} of recipe {} with an item", ingredient.name().simple(),
                        source.className());
                return Optional.empty();
            }
            ingredients.add(amount(item, Integer.parseInt(node.get("Amount").asText())));
        }
        List<FactorialItemQuantity> products = new ArrayList<>();
        for (StringStructs.Node node : source.products().root()) {
            QualifiedClass product = QualifiedClass.of(node.get("ItemClass").asText());
            FactorialItem item = context.getItems().get(product.name().simple());
            if (null == item) {
                LOG.error("Could not associate product {} of recipe {} with an item", product.name().simple(),
                        source.className());
                return Optional.empty();
            }
            products.add(amount(item, Integer.parseInt(node.get("Amount").asText())));
        }
        Fraction duration = Fraction.of(source.duration());
        return Optional.of(new FactorialRecipe(source.displayName(), "", ingredients, products, duration,
                applicableModifiers, applicableMachines));
    }

    /**
     * Generates a Factorial Recipe for a Satisfactory Resource and its potential Resource Extractors, based on its
     * {@link ResourceForm}.
     * 
     * @param context the enclosing Game, used for locating Items and Machines
     * @param resource the Satisfactory Resource
     * @param potentialExtractors the Satisfactory Resource Extractors that support the Resource's {@link ResourceForm};
     *        these will be the Recipe's applicable machines
     * @return the Factorial Recipe
     */
    public static Optional<FactorialRecipe> from(FactorialGame context, SatisResource resource,
                                                 Collection<SatisResourceExtractor> potentialExtractors) {
        List<FactorialMachine> applicableMachines = potentialExtractors.stream()
                .filter(resourceExtractor -> !resourceExtractor.onlyAllowCertainResources()
                        || StreamSupport.stream(resourceExtractor.allowedResources().root().spliterator(), false)
                                .map(node -> QualifiedClass.of(node.asText()).name().simple())
                                .anyMatch(resource.className()::equals))
                .map(resourceExtractor -> context.getMachines().get(resourceExtractor.className())).toList();
        if (applicableMachines.isEmpty()) {
            LOG.error("Could not associate resource {} with a resource extractor", resource.className());
            return Optional.empty();
        }
        List<FactorialItemQuantity> ingredients = emptyList();
        FactorialItem item = context.getItems().get(resource.className());
        // adjusted via the individual machine modifiers
        Fraction amount = Fraction.ONE;
        List<FactorialItemQuantity> products = Collections.singletonList(new FactorialItemQuantity(item, amount));
        // adjusted via the individual machine modifiers
        Fraction duration = Fraction.ONE;
        return Optional.of(new FactorialRecipe(resource.displayName(), "", ingredients, products, duration,
                FactorialGame.getResourceExtractionModifiers(), applicableMachines));
    }

    /**
     * Generates a Factorial Recipe for a single fuel type of a Satisfactory Fuel Generator.
     * 
     * @param context the enclosing Game, used for locating Items and Machines
     * @param fuelGenerator the Satisfactory Fuel Generator
     * @param fuel the fuel type
     * @return the Factorial Recipe
     */
    public static Optional<FactorialRecipe> from(FactorialGame context, SatisFuelGenerator fuelGenerator,
                                                 SatisFuelGenerator.Fuel fuel) {
        FactorialMachine machine = context.getMachines().get(fuelGenerator.className());
        List<FactorialItemQuantity> ingredients = new ArrayList<>(fuelGenerator.requiresSupplementalResource() ? 2 : 1);
        FactorialItem fuelItem = context.getItems().get(fuel.fuelClass());
        if (null == fuelItem) {
            LOG.error("Could not associate fuel {} of generator {} with an item", fuel.fuelClass(),
                    fuelGenerator.className());
            return Optional.empty();
        }
        ingredients.add(amount(fuelItem, fuelGenerator.fuelLoadAmount()));
        if (fuelGenerator.requiresSupplementalResource()) {
            FactorialItem supplementalItem = context.getItems().get(fuel.supplementalResourceClass());
            if (null == supplementalItem) {
                LOG.error("Could not associate supplemental resource {} of generator {} with an item",
                        fuel.supplementalResourceClass(), fuelGenerator.className());
                return Optional.empty();
            }
            ingredients.add(amount(supplementalItem, fuelGenerator.supplementalLoadAmount()));
        }
        List<FactorialItemQuantity> products = new ArrayList<>(1);
        if (!fuel.byproduct().isEmpty()) {
            FactorialItem byproductItem = context.getItems().get(fuel.byproduct());
            if (null == byproductItem) {
                LOG.error("Could not associate byproduct {} of generator {} with an item", fuel.byproduct(),
                        fuelGenerator.className());
                return Optional.empty();
            }
            products.add(amount(byproductItem, fuel.byproductAmount()));
        }
        Fraction duration = fuelItem.energy().divide(Fraction.of(fuelGenerator.powerProduction()));
        return Optional.of(new FactorialRecipe("Burn " + fuelItem.name(),
                "Inferred by Factorial: Represents burning " + fuelItem.name() + " in " + machine.name(), ingredients,
                products, duration, emptyList(), singletonList(machine)));
    }

    private static FactorialItemQuantity amount(FactorialItem item, int base) {
        return new FactorialItemQuantity(item,
                ResourceForm.RF_SOLID == item.form() ? Fraction.of(base) : Fraction.of(base / 1000));
    }

    public static Optional<FactorialRecipe> from(FactorialGame context, SatisSimpleProducer simpleProducer) {
        FactorialMachine machine = context.getMachines().get(simpleProducer.className());
        FactorialItem product = context.getItems().get(SatisSimpleProducer.PRODUCED_ITEM);
        if (null == product) {
            LOG.error("Could not associate ingredient {} of {} with an item", SatisSimpleProducer.PRODUCED_ITEM,
                    simpleProducer);
            return Optional.empty();
        }
        List<FactorialItemQuantity> products = singletonList(new FactorialItemQuantity(product, Fraction.ONE));
        Fraction duration = Fraction.of(simpleProducer.timeToProduceItem());
        return Optional.of(new FactorialRecipe(product.name(),
                "Inferred by Factorial: Represents the FICSMAS Gift Tree producing FICSMAS Gifts out of thin air",
                emptyList(), products, duration, emptyList(), singletonList(machine)));
    }

    public RecipeStandalone toStandalone() {
        return new RecipeStandalone(0, 0, name, description, null,
                ingredients.stream().map(FactorialItemQuantity::toStandalone).toList(),
                products.stream().map(FactorialItemQuantity::toStandalone).toList(), duration,
                applicableModifiers.stream().map(FactorialRecipeModifier::name).map(Object.class::cast).toList(),
                applicableMachines.stream().map(FactorialMachine::name).map(Object.class::cast).toList(), emptyList());
    }

}
