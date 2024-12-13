package de.yggdrasil128.factorial.scraper.satis;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.icon.IconStandalone;
import de.yggdrasil128.factorial.model.machine.MachineStandalone;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public record FactorialMachine(String name,
                               String description,
                               Collection<FactorialRecipeModifier> machineModifiers,
                               Collection<FactorialRecipeModifier> recipeModifiers) {

    public static FactorialMachine from(SatisManufacturer manufacturer) {
        Collection<FactorialRecipeModifier> recipeModifiers = new ArrayList<>();
        if (manufacturer.canChangeProductionBoost()) {
            for (int shards = 1; shards <= manufacturer.productionShardSlotSize(); shards++) {
                String name = manufacturer.displayName() + " Production Boost (" + readableAmount(shards) + ")";
                BigDecimal outputQuantityMultiplier = manufacturer.productionShardBoosMultiplier()
                        .multiply(BigDecimal.valueOf(shards)).add(BigDecimal.ONE);
                recipeModifiers.add(new FactorialRecipeModifier(name,
                        "Inferred by Factorial: Represents having " + readableAmount(shards) + " in a "
                                + manufacturer.displayName(),
                        Fraction.ONE, Fraction.ONE, Fraction.of(outputQuantityMultiplier)));
            }
        }
        return new FactorialMachine(manufacturer.displayName(), manufacturer.description(), emptyList(),
                recipeModifiers);
    }

    private static String readableAmount(int shards) {
        return shards + (1 == shards ? " Somersloop" : " Somersloops");
    }

    public static FactorialMachine from(SatisResourceExtractor resourceExtractor,
                                        Set<ResourceForm> allowedResourcesForms) {
        Fraction extractCycleTime = Fraction.of(resourceExtractor.extractCycleTime());
        int itemsPerCycle = resourceExtractor.itemsPerCycle();
        if (!allowedResourcesForms.contains(ResourceForm.RF_SOLID)) {
            itemsPerCycle /= 1000;
        }
        FactorialRecipeModifier modifier = new FactorialRecipeModifier(resourceExtractor.displayName(),
                "Inferred by Factorial: Represents using a " + resourceExtractor.displayName()
                        + " for any of the resources it can extract.",
                extractCycleTime, Fraction.ONE, Fraction.of(itemsPerCycle));
        return new FactorialMachine(resourceExtractor.displayName(), resourceExtractor.description(),
                singletonList(modifier), emptyList());
    }

    public static FactorialMachine from(SatisFuelGenerator fuelGenerator) {
        return new FactorialMachine(fuelGenerator.displayName(), fuelGenerator.description(), emptyList(), emptyList());
    }

    public MachineStandalone toStandalone(IconStandalone icon) {
        return new MachineStandalone(0, 0, name, description, null == icon ? null : icon.name(),
                machineModifiers.stream().map(FactorialRecipeModifier::name).map(Object.class::cast).toList(),
                emptyList());
    }

}
