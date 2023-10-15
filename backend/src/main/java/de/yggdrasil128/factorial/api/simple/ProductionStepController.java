package de.yggdrasil128.factorial.api.simple;

import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepInput;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepOutput;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductionStepController {

    private final FactoryService factoryService;
    private final ProductionStepService productionStepService;

    @Autowired
    public ProductionStepController(FactoryService factoryService, ProductionStepService productionStepService) {
        this.factoryService = factoryService;
        this.productionStepService = productionStepService;
    }

    @PostMapping("/factory/productionSteps")
    public ProductionStepOutput create(int factoryId, @RequestBody ProductionStepInput input) {
        Factory factory = factoryService.get(factoryId);
        ProductionStep productionStep = productionStepService.create(factory, input);
        factoryService.addAttachedProductionStep(factory, productionStep);
        return new ProductionStepOutput(productionStep);
    }

    @GetMapping("factory/productionSteps")
    public List<ProductionStepOutput> retrieveAll(int factoryId) {
        return factoryService.get(factoryId).getProductionSteps().stream().map(ProductionStepOutput::new).toList();
    }

    @GetMapping("productionStep")
    public ProductionStepOutput retrieve(int productionStepId) {
        return new ProductionStepOutput(productionStepService.get(productionStepId));
    }

    @PatchMapping("/productionStep")
    public ProductionStepOutput update(int productionStepId, @RequestBody ProductionStepInput input) {
        return new ProductionStepOutput(productionStepService.update(productionStepId, input));
    }

    @DeleteMapping("/productionStep")
    public void delete(int productionStepId) {
        productionStepService.delete(productionStepId);
    }

}
