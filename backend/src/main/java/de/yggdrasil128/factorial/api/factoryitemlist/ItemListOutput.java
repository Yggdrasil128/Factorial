package de.yggdrasil128.factorial.api.factoryitemlist;

import de.yggdrasil128.factorial.engine.FactoryItemList;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.transportlink.TransportLinkOutput;
import de.yggdrasil128.factorial.model.xgress.XgressOutput;

import java.util.Comparator;
import java.util.List;

public class ItemListOutput {

    private final List<ListItemOutput> items;
    private final List<ListProductionStepOutput> productionSteps;
    private final List<TransportLinkOutput> transportLinks;
    private final List<XgressOutput> ingresses;
    private final List<XgressOutput> egresses;

    public ItemListOutput(Factory factory, FactoryItemList delegate) {
        items = delegate
                .getItemBalances().entrySet().stream().map(entry -> new ListItemOutput(entry.getKey(),
                        factory.getItemOrder().get(entry.getKey()), entry.getValue()))
                .sorted(Comparator.comparing(ListItemOutput::getOrdinal)).toList();
        productionSteps = delegate.getProductionStepThroughputs().entrySet().stream()
                .map(entry -> new ListProductionStepOutput(entry.getKey(), entry.getValue())).toList();
        transportLinks = delegate.getParticipatingTransportLinks().stream().map(TransportLinkOutput::new).toList();
        ingresses = factory.getIngresses().stream().map(XgressOutput::new).toList();
        egresses = factory.getEgresses().stream().map(XgressOutput::new).toList();
    }

    public List<ListItemOutput> getItems() {
        return items;
    }

    public List<ListProductionStepOutput> getProductionSteps() {
        return productionSteps;
    }

    public List<TransportLinkOutput> getTransportLinks() {
        return transportLinks;
    }

    public List<XgressOutput> getIngresses() {
        return ingresses;
    }

    public List<XgressOutput> getEgresses() {
        return egresses;
    }

}
