package de.yggdrasil128.factorial.api.factoryitemlist;

import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.item.ItemOutput;

public class ApiItem extends ItemOutput {

    private final int ordinal;
    private final Balances balances = new Balances();

    public ApiItem(Item delegate, int ordinal) {
        super(delegate);
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public Balances getBalances() {
        return balances;
    }

}
