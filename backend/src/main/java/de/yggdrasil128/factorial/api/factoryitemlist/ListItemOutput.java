package de.yggdrasil128.factorial.api.factoryitemlist;

import de.yggdrasil128.factorial.engine.Balances;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.item.ItemOutput;

public class ListItemOutput extends ItemOutput {

    private final int ordinal;
    private final Balances balances;

    public ListItemOutput(Item delegate, int ordinal, Balances balances) {
        super(delegate);
        this.ordinal = ordinal;
        this.balances = balances;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public Balances getBalances() {
        return balances;
    }

}
