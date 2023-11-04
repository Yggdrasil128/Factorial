package de.yggdrasil128.factorial.model.transportline;

import de.yggdrasil128.factorial.model.factory.FactoryOutput;
import de.yggdrasil128.factorial.model.icon.IconOutput;
import de.yggdrasil128.factorial.model.resource.ResourceOutput;

import java.util.List;

public class TransportLineOutput {

    private final TransportLine delegate;
    private final IconOutput icon;
    private final FactoryOutput sourceFactory;
    private final FactoryOutput targetFactory;
    private final List<ResourceOutput> resources;

    public TransportLineOutput(TransportLine delegate) {
        this.delegate = delegate;
        this.icon = IconOutput.of(delegate.getIcon());
        this.sourceFactory = new FactoryOutput(delegate.getSourceFactory());
        this.targetFactory = new FactoryOutput(delegate.getTargetFactory());
        this.resources = delegate.getResources().stream().map(ResourceOutput::new).toList();
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

    public FactoryOutput getSourceFactory() {
        return sourceFactory;
    }

    public FactoryOutput getTargetFactory() {
        return targetFactory;
    }

    public List<ResourceOutput> getResources() {
        return resources;
    }

}
