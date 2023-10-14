package de.yggdrasil128.factorial.model.factory;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepMigration;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepService;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepStandalone;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.save.Save;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;

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
        initItemOrder(factory, productionStep);
        repository.save(factory);
        return productionStep;
    }

    public Factory doImport(Save save, FactoryMigration input) {
        Factory factory = new Factory(save, input.getName(), input.getDescription(), null, new ArrayList<>(),
                new HashMap<>());
        for (ProductionStepMigration entry : input.getProductionSteps()) {
            ProductionStep productionStep = productionSteps.doImport(factory, entry);
            factory.getProductionSteps().add(productionStep);
            initItemOrder(factory, productionStep);
        }
        return factory;
    }

    private static void initItemOrder(Factory factory, ProductionStep productionStep) {
        for (Resource resource : productionStep.getRecipe().getInput()) {
            factory.getItemOrder().computeIfAbsent(resource.getItem(), key -> factory.getItemOrder().size());
        }
        for (Resource resource : productionStep.getRecipe().getOutput()) {
            factory.getItemOrder().computeIfAbsent(resource.getItem(), key -> factory.getItemOrder().size());
        }
    }

    @Override
    public void delete(int id) {
        Factory factory = get(id);
        if (1 == factory.getSave().getFactories().size()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "cannot delete the last factory of a save");
        }
        super.delete(id);
    }

}
