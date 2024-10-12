package de.yggdrasil128.factorial.model.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ResourceStandalone {

    private Object item;
    private Fraction quantity;

    public ResourceStandalone() {
    }

    public ResourceStandalone(Resource model) {
        this(model, RelationRepresentation.ID);
    }

    public ResourceStandalone(Resource model, RelationRepresentation resolveStrategy) {
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
