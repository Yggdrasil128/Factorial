package de.yggdrasil128.factorial.model.recipe;

import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.NamedModel;

public record ItemQuantityStandalone(Object itemId, Fraction quantity) {

    public static ItemQuantityStandalone of(ItemQuantity model) {
        return of(model, External.FRONTEND);
    }

    public static ItemQuantityStandalone of(ItemQuantity model, External destination) {
        return new ItemQuantityStandalone(NamedModel.resolve(model.getItem(), destination), model.getQuantity());
    }

}
