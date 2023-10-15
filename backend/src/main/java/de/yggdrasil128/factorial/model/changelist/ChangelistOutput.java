package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.icon.IconOutput;

import java.util.Map;
import java.util.stream.Collectors;

public class ChangelistOutput {

    private final Changelist delegate;
    private final IconOutput icon;
    private final Map<Integer, Fraction> productionStepChanges;

    public ChangelistOutput(Changelist delegate) {
        this.delegate = delegate;
        this.icon = IconOutput.of(delegate.getIcon());
        this.productionStepChanges = delegate.getProductionStepChanges().entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().getId(), entry -> entry.getValue()));
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

    public boolean isPrimary() {
        return delegate.isPrimary();
    }

    public boolean isActive() {
        return delegate.isActive();
    }

    public IconOutput getIcon() {
        return icon;
    }

    public Map<Integer, Fraction> getProductionStepChanges() {
        return productionStepChanges;
    }

}
