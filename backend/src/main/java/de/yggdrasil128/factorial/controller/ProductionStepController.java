package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.engine.ProductionEntryStandalone;
import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.machine.MachineService;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepService;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepStandalone;
import de.yggdrasil128.factorial.model.recipe.RecipeService;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierService;
import de.yggdrasil128.factorial.model.save.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductionStepController {

    private final RecipeService recipeService;
    private final RecipeModifierService recipeModifierService;
    private final MachineService machineService;
    private final SaveService saveService;
    private final FactoryService factoryService;
    private final ProductionStepService productionStepService;

    @Autowired
    public ProductionStepController(RecipeService recipeService, RecipeModifierService recipeModifierService,
                                    MachineService machineService, SaveService saveService,
                                    FactoryService factoryService, ProductionStepService productionStepService) {
        this.recipeService = recipeService;
        this.recipeModifierService = recipeModifierService;
        this.machineService = machineService;
        this.saveService = saveService;
        this.factoryService = factoryService;
        this.productionStepService = productionStepService;
    }

    @PostMapping("/factory/productionSteps")
    public ProductionStepStandalone create(int factoryId, @RequestBody ProductionStepStandalone input) {
        Factory factory = factoryService.get(factoryId);
        ProductionStep productionStep = new ProductionStep(factory, input);
        applyRelations(input, productionStep);
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
        ProductionStep productionStep = productionStepService.get(productionStepId);
        ProductionStepStandalone standalone = new ProductionStepStandalone(productionStep);
        ProductionStepThroughputs throughputs = productionStepService.computeThroughputs(productionStep,
                saveService.computeChangelists(productionStep.getFactory().getSave()));
        standalone.setInputs(throughputs.getInputs().entrySet().stream().map(ProductionEntryStandalone::new).toList());
        standalone
                .setOutputs(throughputs.getOutputs().entrySet().stream().map(ProductionEntryStandalone::new).toList());
        return standalone;
    }

    @PatchMapping("/productionStep")
    public ProductionStepStandalone update(int productionStepId, @RequestBody ProductionStepStandalone input) {
        ProductionStep productionStep = productionStepService.get(productionStepId);
        applyBasics(input, productionStep);
        applyRelations(input, productionStep);
        return new ProductionStepStandalone(productionStepService.update(productionStep));
    }

    private static void applyBasics(ProductionStepStandalone input, ProductionStep productionStep) {
        OptionalInputField.of(input.getMachineCount()).apply(productionStep::setMachineCount);
    }

    private void applyRelations(ProductionStepStandalone input, ProductionStep productionStep) {
        OptionalInputField.ofId((int) input.getMachine(), machineService::get).apply(productionStep::setMachine);
        OptionalInputField.ofId((int) input.getRecipe(), recipeService::get).apply(productionStep::setRecipe);
        OptionalInputField.ofIds(input.getModifiers(), recipeModifierService::get)
                .applyList(productionStep::setModifiers);
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
