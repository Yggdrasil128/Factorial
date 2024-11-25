package de.yggdrasil128.factorial.scraper.satis;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.recipe.ItemQuantityStandalone;

public record FactorialItemQuantity(FactorialItem item, Fraction quantity) {

    public ItemQuantityStandalone toStandalone() {
        return new ItemQuantityStandalone(item.name(), quantity);
    }

}
