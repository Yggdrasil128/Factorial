package de.yggdrasil128.factorial.scraper.satis;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.icon.IconStandalone;
import de.yggdrasil128.factorial.model.item.ItemStandalone;

import java.math.BigDecimal;

import static java.util.Collections.emptyList;

public record FactorialItem(String name, String description, boolean resource, Fraction energy, ResourceForm form) {

    public static FactorialItem from(SatisItem source) {
        return new FactorialItem(source.displayName(), source.description(), false,
                energyValue(source.energyValue(), source.form()), source.form());
    }

    public static FactorialItem from(SatisResource source) {
        return new FactorialItem(source.displayName(), source.description(), true,
                energyValue(source.energyValue(), source.form()), source.form());
    }

    private static Fraction energyValue(BigDecimal base, ResourceForm form) {
        return Fraction.of(ResourceForm.RF_SOLID == form ? base : base.multiply(BigDecimal.valueOf(1000)));
    }

    public ItemStandalone toStandalone(IconStandalone icon) {
        return new ItemStandalone(0, 0, name, description, null == icon ? null : icon.name(), emptyList());
    }

}
