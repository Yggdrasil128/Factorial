package de.yggdrasil128.factorial.model.recipe;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.External;

public record ItemQuantityStandalone(Object itemId, Fraction quantity) {

    @JsonCreator
    public static ItemQuantityStandalone create(Object itemId, Fraction quantity) {
        return new ItemQuantityStandalone(itemId, quantity);
    }

    public ItemQuantityStandalone(ItemQuantity model) {
        this(model, External.FRONTEND);
    }

    public ItemQuantityStandalone(ItemQuantity model, External destination) {
        this(NamedModel.resolve(model.getItem(), destination), model.getQuantity());
    }

}
