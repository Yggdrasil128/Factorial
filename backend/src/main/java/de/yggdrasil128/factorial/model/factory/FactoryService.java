package de.yggdrasil128.factorial.model.factory;

import de.yggdrasil128.factorial.engine.Changelists;
import de.yggdrasil128.factorial.engine.ProductionLineResources;
import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;
import de.yggdrasil128.factorial.engine.Resource;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.ReorderInputEntry;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
public class FactoryService extends ModelService<Factory, FactoryRepository> {

    private final SaveRepository saves;

    public FactoryService(FactoryRepository repository, SaveRepository saves) {
        super(repository);
        this.saves = saves;
    }

    @Override
    public Factory create(Factory factory) {
        if (0 >= factory.getOrdinal()) {
            factory.setOrdinal(
                    factory.getSave().getFactories().stream().mapToInt(Factory::getOrdinal).max().orElse(0) + 1);
        }
        return super.create(factory);
    }

    public void addAttachedProductionStep(Factory factory, ProductionStep productionStep) {
        factory.getProductionSteps().add(productionStep);
        repository.save(factory);
    }

    public List<Resource> computeResources(Factory factory, Changelists changelists) {
        return ProductionLineResources.of(factory.getProductionSteps().stream()
                .map(productionStep -> new ProductionStepThroughputs(productionStep, changelists)).toList());
    }

    public void reorder(Save save, List<ReorderInputEntry> input) {
        Map<Integer, Integer> order = input.stream()
                .collect(toMap(ReorderInputEntry::getId, ReorderInputEntry::getOrdinal));
        for (Factory factory : save.getFactories()) {
            Integer ordinal = order.get(factory.getId());
            if (null != ordinal) {
                factory.setOrdinal(ordinal.intValue());
                repository.save(factory);
            }
        }
    }

    @Override
    public void delete(int id) {
        Save save = saves.findByFactoriesId(id).orElse(null);
        if (null != save && 1 == repository.countBySaveId(save.getId())) {
            throw report(HttpStatus.CONFLICT, "cannot delete the last factory of a save");
        }
        repository.deleteById(id);
    }

}
