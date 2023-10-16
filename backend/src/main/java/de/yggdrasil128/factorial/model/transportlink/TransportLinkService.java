package de.yggdrasil128.factorial.model.transportlink;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.resource.ResourceService;
import de.yggdrasil128.factorial.model.save.Save;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransportLinkService extends ModelService<TransportLink, TransportLinkRepository> {

    private final IconService icons;
    private final FactoryService factories;
    private final ResourceService resources;

    public TransportLinkService(TransportLinkRepository repository, IconService icons, FactoryService factories,
                                ResourceService resources) {
        super(repository);
        this.icons = icons;
        this.factories = factories;
        this.resources = resources;
    }

    public TransportLink create(Save save, TransportLinkInput input) {
        Icon icon = OptionalInputField.ofId(input.getIconId(), icons::get).get();
        Factory sourceFactory = factories.get(input.getSourceFactoryId());
        Factory targetFactory = factories.get(input.getTargetFactoryId());
        List<Resource> linkedResources = OptionalInputField.of(input.getResources()).map(resources::get).get();
        return repository.save(new TransportLink(save, input.getName(), input.getDescription(), icon, sourceFactory,
                targetFactory, linkedResources));
    }

    public TransportLink update(int id, TransportLinkInput input) {
        TransportLink transportLink = get(id);
        OptionalInputField.of(input.getName()).apply(transportLink::setName);
        OptionalInputField.of(input.getDescription()).apply(transportLink::setDescription);
        OptionalInputField.ofId(input.getIconId(), icons::get).apply(transportLink::setIcon);
        OptionalInputField.ofId(input.getSourceFactoryId(), factories::get).apply(transportLink::setSourceFactory);
        OptionalInputField.ofId(input.getTargetFactoryId(), factories::get).apply(transportLink::setTargetFactory);
        OptionalInputField.of(input.getResources()).map(resources::get).apply(transportLink::setResources);
        return repository.save(transportLink);
    }

}
