package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.engine.Changelists;
import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import org.springframework.stereotype.Service;

@Service
public class ProductionStepService extends ModelService<ProductionStep, ProductionStepRepository> {

    public ProductionStepService(ProductionStepRepository repository) {
        super(repository);
    }

    public ProductionStepThroughputs computeThroughputs(ProductionStep productionStep, Changelists changelists) {
        return new ProductionStepThroughputs(productionStep, changelists);
    }

    public void applyChange(ProductionStep productionStep, Fraction change) {
        productionStep.setMachineCount(productionStep.getMachineCount().add(change));
        repository.save(productionStep);
    }

    public ProductionStep applyPrimaryChangelist(int id) {
        ProductionStep productionStep = get(id);
        Changelist primary = Changelists.getPrimary(productionStep.getFactory().getSave());
        Fraction change = primary.getProductionStepChanges().get(productionStep);
        if (null != change) {
            applyChange(productionStep, change);
        }
        return productionStep;
    }

}
