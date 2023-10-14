package de.yggdrasil128.factorial.api.items;

import de.yggdrasil128.factorial.model.item.Item;

public class ApiItem {

    private final Item delegate;
    private final int ordinal;
    private final Balances balances = new Balances();

    public ApiItem(Item delegate, int ordinal) {
        this.delegate = delegate;
        this.ordinal = ordinal;
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

    public int getIconId() {
        return null == delegate.getIcon() ? 0 : delegate.getIcon().getId();
    }

    public Balances getBalances() {
        return balances;
    }

}
