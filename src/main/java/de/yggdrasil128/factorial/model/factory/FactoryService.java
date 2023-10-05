package de.yggdrasil128.factorial.model.factory;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepMigration;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepService;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepStandalone;
import de.yggdrasil128.factorial.model.save.Save;

@Service
public class FactoryService extends ModelService<Factory, FactoryRepository> {

    private final IconService icons;
    private final ProductionStepService productionSteps;

    public FactoryService(FactoryRepository repository, IconService icons, ProductionStepService productionSteps) {
        super(repository);
        this.icons = icons;
        this.productionSteps = productionSteps;
    }

    public Factory create(Save save, FactoryStandalone input) {
        Icon icon = 0 == input.getIconId() ? null : icons.get(input.getIconId());
        return repository.save(input.with(save, icon));
    }

    public ProductionStep addProductionStep(int factoryId, ProductionStepStandalone input) {
        Factory factory = get(factoryId);
        ProductionStep productionStep = productionSteps.create(factory, input);
        factory.getProductionSteps().add(productionStep);
        repository.save(factory);
        return productionStep;
    }

    public Factory doImport(Save save, FactoryMigration input) {
        Factory factory = new Factory(save, input.getName(), input.getDescription(), null, new ArrayList<>());
        for (ProductionStepMigration entry : input.getProductionSteps()) {
            factory.getProductionSteps().add(productionSteps.doImport(factory, entry));
        }
        return factory;
    }

}
