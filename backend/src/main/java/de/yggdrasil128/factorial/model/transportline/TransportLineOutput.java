package de.yggdrasil128.factorial.model.transportline;

import de.yggdrasil128.factorial.model.factory.FactoryOutput;
import de.yggdrasil128.factorial.model.icon.IconOutput;
import de.yggdrasil128.factorial.model.item.ItemOutput;

import java.util.List;

public class TransportLineOutput {

    private final TransportLine delegate;
    private final IconOutput icon;
    private final List<FactoryOutput> sourceFactories;
    private final List<FactoryOutput> targetFactories;
    private final List<ItemOutput> items;

    public TransportLineOutput(TransportLine delegate) {
        this.delegate = delegate;
        this.icon = IconOutput.of(delegate.getIcon());
        this.sourceFactories = delegate.getSourceFactories().stream().map(FactoryOutput::new).toList();
        this.targetFactories = delegate.getTargetFactories().stream().map(FactoryOutput::new).toList();
        this.items = delegate.getItems().stream().map(ItemOutput::new).toList();
    }

    public int getId() {
        return delegate.getId();
    }

    public int getSaveId() {
        return delegate.getSave().getId();
    }

    public String getName() {
        return delegate.getName();
    }

    public IconOutput getIcon() {
        return icon;
    }

    public List<FactoryOutput> getSourceFactories() {
        return sourceFactories;
    }

    public List<FactoryOutput> getTargetFactories() {
        return targetFactories;
    }

    public List<ItemOutput> getItems() {
        return items;
    }

}
