package de.yggdrasil128.factorial.model.factory;

import de.yggdrasil128.factorial.model.icon.IconOutput;
import de.yggdrasil128.factorial.model.item.ItemOutput;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class FactoryOutput {

    private final Factory delegate;
    private final IconOutput icon;
    private final List<ItemOutput> itemOrder;

    public FactoryOutput(Factory delegate) {
        this.delegate = delegate;
        this.icon = IconOutput.of(delegate.getIcon());
        this.itemOrder = delegate.getItemOrder().entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue))
                .map(entry -> new ItemOutput(entry.getKey())).toList();
    }

    public int getId() {
        return delegate.getId();
    }

    public int getSaveId() {
        return delegate.getSave().getId();
    }

    public int getOrdinal() {
        return delegate.getOrdinal();
    }

    public String getName() {
        return delegate.getName();
    }

    public IconOutput getIcon() {
        return icon;
    }

    public List<ItemOutput> getItemOrder() {
        return itemOrder;
    }

}
