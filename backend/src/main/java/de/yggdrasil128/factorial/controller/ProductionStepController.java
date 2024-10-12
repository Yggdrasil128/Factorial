package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.machine.MachineService;
import de.yggdrasil128.factorial.model.productionstep.*;
import de.yggdrasil128.factorial.model.recipe.RecipeService;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductionStepController {

    private final RecipeService recipeService;
    private final RecipeModifierService recipeModifierService;
    private final MachineService machineService;
    private final FactoryService factoryService;
    private final ProductionStepService productionStepService;

    @Autowired
    public ProductionStepController(RecipeService recipeService, RecipeModifierService recipeModifierService,
                                    MachineService machineService, FactoryService factoryService,
                                    ProductionStepService productionStepService) {
        this.recipeService = recipeService;
        this.recipeModifierService = recipeModifierService;
        this.machineService = machineService;
        this.factoryService = factoryService;
        this.productionStepService = productionStepService;
    }

    @PostMapping("/factory/productionSteps")
    public ProductionStepStandalone create(int factoryId, @RequestBody ProductionStepStandalone input) {
        Factory factory = factoryService.get(factoryId);
        ProductionStep productionStep = new ProductionStep(factory, input);
        OptionalInputField.ofId((int) input.getMachine(), machineService::get).apply(productionStep::setMachine);
        OptionalInputField.ofId((int) input.getRecipe(), recipeService::get).apply(productionStep::setRecipe);
        OptionalInputField.ofIds(input.getModifiers(), recipeModifierService::get)
                .applyList(productionStep::setModifiers);
        productionStep = productionStepService.create(productionStep);
        factoryService.addAttachedProductionStep(factory, productionStep);
        return new ProductionStepStandalone(productionStep);
    }

    @GetMapping("/factory/productionSteps")
    public List<ProductionStepStandalone> retrieveAll(int factoryId) {
        return factoryService.get(factoryId).getProductionSteps().stream().map(ProductionStepStandalone::new).toList();
    }

    @GetMapping("/productionStep")
    public ProductionStepStandalone retrieve(int productionStepId) {
        return new ProductionStepStandalone(productionStepService.get(productionStepId));
    }

    @PatchMapping("/productionStep")
    public ProductionStepStandalone update(int productionStepId, @RequestBody ProductionStepStandalone input) {
        ProductionStep productionStep = productionStepService.get(productionStepId);
        OptionalInputField.ofId((int) input.getMachine(), machineService::get).apply(productionStep::setMachine);
        OptionalInputField.ofId((int) input.getRecipe(), recipeService::get).apply(productionStep::setRecipe);
        OptionalInputField.ofIds(input.getModifiers(), recipeModifierService::get)
                .applyList(productionStep::setModifiers);
        OptionalInputField.of(input.getMachineCount()).apply(productionStep::setMachineCount);
        return new ProductionStepStandalone(productionStepService.update(productionStep));
    }

    @DeleteMapping("/productionStep")
    public void delete(int productionStepId) {
        productionStepService.delete(productionStepId);
    }

    @PostMapping("/productionStep/applyPrimaryChangelist")
    public ProductionStepStandalone applyPrimaryChangelist(int productionStepId) {
        return new ProductionStepStandalone(productionStepService.applyPrimaryChangelist(productionStepId));
    }

}
