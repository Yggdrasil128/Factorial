package de.yggdrasil128.factorial.api.factoryitemlist;

import de.yggdrasil128.factorial.engine.FactoryItemList;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.transportline.TransportLineOutput;
import de.yggdrasil128.factorial.model.xgress.XgressOutput;

import java.util.Comparator;
import java.util.List;

public class ItemListOutput {

    private final List<ListItemOutput> items;
    private final List<ListProductionStepOutput> productionSteps;
    private final List<XgressOutput> ingresses;
    private final List<XgressOutput> egresses;
    private final List<TransportLineOutput> ingoingTransportLines;
    private final List<TransportLineOutput> outgoingTransportLines;

    public ItemListOutput(Factory factory, FactoryItemList delegate) {
        items = delegate.getItemBalances().entrySet().stream()
                .map(entry -> new ListItemOutput(entry.getKey(),
                        factory.getItemOrder().getOrDefault(entry.getKey(), Integer.MAX_VALUE), entry.getValue()))
                .sorted(Comparator.comparing(ListItemOutput::getOrdinal)).toList();
        productionSteps = delegate.getProductionStepThroughputs().entrySet().stream()
                .map(entry -> new ListProductionStepOutput(entry.getKey(), entry.getValue())).toList();
        ingoingTransportLines = delegate.getIngoingTransportLines().stream().map(TransportLineOutput::new).toList();
        outgoingTransportLines = delegate.getOutgoingTransportLines().stream().map(TransportLineOutput::new).toList();
        ingresses = factory.getIngresses().stream().map(XgressOutput::new).toList();
        egresses = factory.getEgresses().stream().map(XgressOutput::new).toList();
    }

    public List<ListItemOutput> getItems() {
        return items;
    }

    public List<ListProductionStepOutput> getProductionSteps() {
        return productionSteps;
    }

    public List<XgressOutput> getIngresses() {
        return ingresses;
    }

    public List<XgressOutput> getEgresses() {
        return egresses;
    }

    public List<TransportLineOutput> getIngoingTransportLines() {
        return ingoingTransportLines;
    }

    public List<TransportLineOutput> getOutgoingTransportLines() {
        return outgoingTransportLines;
    }

}
