package de.yggdrasil128.factorial.api.items;

import de.yggdrasil128.factorial.model.item.Item;

public class ApiItem {

    private final Item delegate;
    private final int ordinal;
    private final ApiIcon icon;
    private final Balances balances = new Balances();

    public ApiItem(Item delegate, int ordinal) {
        this.delegate = delegate;
        this.ordinal = ordinal;
        this.icon = null == delegate.getIcon() ? null : new ApiIcon(delegate.getIcon());
    }

    public int getId() {
        return delegate.getId();
    }

    public String getName() {
        return delegate.getName();
    }

    public String getDescription() {
        return delegate.getDescription();
    }

    public int getOrdinal() {
        return ordinal;
    }

    public ApiIcon getIcon() {
        return icon;
    }

    public Balances getBalances() {
        return balances;
    }

}
