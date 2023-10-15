package de.yggdrasil128.factorial.model.resource;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.item.ItemOutput;

public class ResourceOutput {

    private final Resource delegate;
    private final ItemOutput item;

    public ResourceOutput(Resource delegate) {
        this.delegate = delegate;
        this.item = new ItemOutput(delegate.getItem());
    }

    public Fraction getQuantity() {
        return delegate.getQuantity();
    }

    public ItemOutput getItem() {
        return item;
    }

}
