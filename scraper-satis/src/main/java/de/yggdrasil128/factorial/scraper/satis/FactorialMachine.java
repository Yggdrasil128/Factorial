package de.yggdrasil128.factorial.scraper.satis;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.icon.IconStandalone;
import de.yggdrasil128.factorial.model.machine.MachineStandalone;

import java.util.Collection;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public record FactorialMachine(String name, String description, Collection<FactorialRecipeModifier> machineModifiers) {

    public static FactorialMachine from(SatisManufacturer manufacturer) {
        return new FactorialMachine(manufacturer.displayName(), manufacturer.description(), emptyList());
    }

    public static FactorialMachine from(SatisResourceExtractor resourceExtractor,
                                        Set<ResourceForm> allowedResourcesForms) {
        Fraction extractCycleTime = Fraction.of(resourceExtractor.extractCycleTime());
        int itemsPerCycle = resourceExtractor.itemsPerCycle();
        if (!allowedResourcesForms.contains(ResourceForm.RF_SOLID)) {
            itemsPerCycle /= 1000;
        }
        FactorialRecipeModifier modifier = new FactorialRecipeModifier(resourceExtractor.displayName(), "",
                extractCycleTime, Fraction.ONE, Fraction.of(itemsPerCycle));
        return new FactorialMachine(resourceExtractor.displayName(), resourceExtractor.description(),
                singletonList(modifier));
    }

    public static FactorialMachine from(SatisFuelGenerator fuelGenerator) {
        return new FactorialMachine(fuelGenerator.displayName(), fuelGenerator.description(), emptyList());
    }

    public MachineStandalone toStandalone(IconStandalone icon) {
        return new MachineStandalone(0, 0, name, description, null == icon ? null : icon.name(),
                machineModifiers.stream().map(FactorialRecipeModifier::name).map(Object.class::cast).toList(),
                emptyList());
    }

}
