package de.yggdrasil128.factorial.model.itemQuantity;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;

public class ItemQuantityStandalone {

    private Object itemId;
    private Fraction quantity;

    public ItemQuantityStandalone() {
    }

    public ItemQuantityStandalone(ItemQuantity model) {
        this(model, RelationRepresentation.ID);
    }

    public ItemQuantityStandalone(ItemQuantity model, RelationRepresentation resolveStrategy) {
        itemId = NamedModel.resolve(model.getItem(), resolveStrategy);
        quantity = model.getQuantity();
    }

    public Object getItemId() {
        return itemId;
    }

    public void setItemId(Object itemId) {
        this.itemId = itemId;
    }

    public Fraction getQuantity() {
        return quantity;
    }

    public void setQuantity(Fraction quantity) {
        this.quantity = quantity;
    }

}
