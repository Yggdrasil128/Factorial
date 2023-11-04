package de.yggdrasil128.factorial.model.transportline;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.resource.ResourceService;
import de.yggdrasil128.factorial.model.save.Save;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransportLineService extends ModelService<TransportLine, TransportLineRepository> {

    private final IconService icons;
    private final FactoryService factories;
    private final ResourceService resources;

    public TransportLineService(TransportLineRepository repository, IconService icons, FactoryService factories,
                                ResourceService resources) {
        super(repository);
        this.icons = icons;
        this.factories = factories;
        this.resources = resources;
    }

    public TransportLine create(Save save, TransportLineInput input) {
        Icon icon = OptionalInputField.ofId(input.getIconId(), icons::get).get();
        Factory sourceFactory = factories.get(input.getSourceFactoryId());
        Factory targetFactory = factories.get(input.getTargetFactoryId());
        List<Resource> transportedResources = OptionalInputField.of(input.getResources()).map(resources::get).get();
        checkForLoop(input, sourceFactory, targetFactory);
        return repository.save(new TransportLine(save, input.getName(), input.getDescription(), icon, sourceFactory,
                targetFactory, transportedResources));
    }

    public TransportLine update(int id, TransportLineInput input) {
        TransportLine transportLine = get(id);
        OptionalInputField.of(input.getName()).apply(transportLine::setName);
        OptionalInputField.of(input.getDescription()).apply(transportLine::setDescription);
        OptionalInputField.ofId(input.getIconId(), icons::get).apply(transportLine::setIcon);
        OptionalInputField.ofId(input.getSourceFactoryId(), factories::get).apply(transportLine::setSourceFactory);
        OptionalInputField.ofId(input.getTargetFactoryId(), factories::get).apply(transportLine::setTargetFactory);
        OptionalInputField.of(input.getResources()).map(resources::get).apply(transportLine::setResources);
        checkForLoop(input, transportLine.getSourceFactory(), transportLine.getTargetFactory());
        return repository.save(transportLine);
    }

    private static void checkForLoop(TransportLineInput input, Factory sourceFactory, Factory targetFactory) {
        if (sourceFactory.equals(targetFactory)) {
            throw ModelService.report(HttpStatus.CONFLICT, "transport line '" + input.getName()
                    + "' constitutes a loop for factory '" + sourceFactory.getName() + "'");
        }
    }

}
