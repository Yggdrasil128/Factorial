package de.yggdrasil128.factorial.api.transportlineusages;

import de.yggdrasil128.factorial.engine.QuantityByChangelist;
import de.yggdrasil128.factorial.engine.TransportLineUsage;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryOutput;

public class UsageFactoryOutput extends FactoryOutput {

    private final QuantityByChangelist supply;
    private final QuantityByChangelist demand;

    public UsageFactoryOutput(Factory delegate, TransportLineUsage usage) {
        super(delegate);
        this.supply = ifNotZero(usage.getSupply());
        this.demand = ifNotZero(usage.getDemand());
    }

    private static QuantityByChangelist ifNotZero(QuantityByChangelist qbc) {
        boolean isZero = Fraction.ZERO.equals(qbc.getCurrent()) && Fraction.ZERO.equals(qbc.getWithPrimaryChangelist())
                && Fraction.ZERO.equals(qbc.getWithActiveChangelists());
        return isZero ? null : qbc;
    }

    public QuantityByChangelist getSupply() {
        return supply;
    }

    public QuantityByChangelist getDemand() {
        return demand;
    }

}
