package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;
import de.yggdrasil128.factorial.model.item.Item;

import java.util.Map;

public class ProductionEntryStandalone {

    private Object item;
    private QuantityByChangelist quantity;

    public ProductionEntryStandalone(Map.Entry<Item, QuantityByChangelist> model) {
        this(model, RelationRepresentation.ID);
    }

    public ProductionEntryStandalone(Map.Entry<Item, QuantityByChangelist> entry,
                                     RelationRepresentation resolveStrategy) {
        item = NamedModel.resolve(entry.getKey(), resolveStrategy);
        quantity = entry.getValue();
    }

    public Object getItem() {
        return item;
    }

    public QuantityByChangelist getQuantity() {
        return quantity;
    }

}
