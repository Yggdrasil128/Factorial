package de.yggdrasil128.factorial.model.transportlink;

import de.yggdrasil128.factorial.model.ModelService;
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
        Icon icon = 0 == input.getIconId() ? null : icons.get(input.getIconId());
        Factory sourceFactory = factories.get(input.getSourceFactoryId());
        Factory targetFactory = factories.get(input.getTargetFactoryId());
        List<Resource> linkedResources = null == input.getResources() ? null : resources.get(input.getResources());
        return repository.save(new TransportLink(save, input.getName(), input.getDescription(), icon, sourceFactory,
                targetFactory, linkedResources));
    }

    public TransportLink update(int id, TransportLinkInput input) {
        TransportLink transportLink = get(id);
        if (null != input.getName()) {
            transportLink.setName(input.getName());
        }
        if (null != input.getDescription()) {
            transportLink.setDescription(input.getDescription());
        }
        if (0 != input.getIconId()) {
            transportLink.setIcon(icons.get(input.getIconId()));
        }
        if (0 != input.getSourceFactoryId()) {
            transportLink.setSourceFactory(factories.get(input.getSourceFactoryId()));
        }
        if (0 != input.getTargetFactoryId()) {
            transportLink.setTargetFactory(factories.get(input.getTargetFactoryId()));
        }
        if (null != input.getResources()) {
            transportLink.setResources(resources.get(input.getResources()));
        }
        return repository.save(transportLink);
    }

}
