package de.yggdrasil128.factorial.model.itemQuantity;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ItemQuantityStandalone {

    private Object item;
    private Fraction quantity;

    public ItemQuantityStandalone() {
    }

    public ItemQuantityStandalone(ItemQuantity model) {
        this(model, RelationRepresentation.ID);
    }

    public ItemQuantityStandalone(ItemQuantity model, RelationRepresentation resolveStrategy) {
        item = NamedModel.resolve(model.getItem(), resolveStrategy);
        quantity = model.getQuantity();
    }

    public Object getItem() {
        return item;
    }

    public void setItem(Object item) {
        this.item = item;
    }

    public Fraction getQuantity() {
        return quantity;
    }

    public void setQuantity(Fraction quantity) {
        this.quantity = quantity;
    }

}
