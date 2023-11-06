package de.yggdrasil128.factorial.model.transportline;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.item.ItemService;
import de.yggdrasil128.factorial.model.save.Save;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransportLineService extends ModelService<TransportLine, TransportLineRepository> {

    private final IconService icons;
    private final FactoryService factories;
    private final ItemService items;

    public TransportLineService(TransportLineRepository repository, IconService icons, FactoryService factories,
                                ItemService items) {
        super(repository);
        this.icons = icons;
        this.factories = factories;
        this.items = items;
    }

    public TransportLine create(Save save, TransportLineInput input) {
        Icon icon = OptionalInputField.ofId(input.getIconId(), icons::get).get();
        List<Factory> sourceFactories = OptionalInputField.ofIds(input.getSourceFactoryIds(), factories::get).asList();
        List<Factory> targetFactories = OptionalInputField.ofIds(input.getTargetFactoryIds(), factories::get).asList();
        List<Item> transportedItems = OptionalInputField.ofIds(input.getItemIds(), items::get).asList();
        return repository.save(new TransportLine(save, input.getName(), input.getDescription(), icon, sourceFactories,
                targetFactories, transportedItems));
    }

    public TransportLine update(int id, TransportLineInput input) {
        TransportLine transportLine = get(id);
        OptionalInputField.of(input.getName()).apply(transportLine::setName);
        OptionalInputField.of(input.getDescription()).apply(transportLine::setDescription);
        OptionalInputField.ofId(input.getIconId(), icons::get).apply(transportLine::setIcon);
        OptionalInputField.ofIds(input.getSourceFactoryIds(), factories::get)
                .applyList(transportLine::setSourceFactories);
        OptionalInputField.ofIds(input.getTargetFactoryIds(), factories::get)
                .applyList(transportLine::setTargetFactories);
        OptionalInputField.ofIds(input.getItemIds(), items::get).applyList(transportLine::setItems);
        return repository.save(transportLine);
    }

}
