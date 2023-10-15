package de.yggdrasil128.factorial.model.xgress;

import de.yggdrasil128.factorial.model.resource.ResourceOutput;

import java.util.List;

public class XgressOutput {

    private final Xgress delegate;
    private final List<ResourceOutput> resources;

    public XgressOutput(Xgress delegate) {
        this.delegate = delegate;
        this.resources = delegate.getResources().stream().map(ResourceOutput::new).toList();
    }

    public int getId() {
        return delegate.getId();
    }

    public int getFactoryId() {
        return delegate.getFactory().getId();
    }

    public String getName() {
        return delegate.getName();
    }

    public List<ResourceOutput> getResources() {
        return resources;
    }

}
