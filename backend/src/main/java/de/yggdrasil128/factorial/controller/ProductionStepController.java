package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.RelationRepresentation;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.changelist.ChangelistService;
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
import org.springframework.http.HttpStatus;
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
    private final ChangelistService changelistService;

    @Autowired
    public ProductionStepController(RecipeService recipeService, RecipeModifierService recipeModifierService,
                                    MachineService machineService, SaveService saveService,
                                    FactoryService factoryService, ProductionStepService productionStepService,
                                    ChangelistService changelistService) {
        this.recipeService = recipeService;
        this.recipeModifierService = recipeModifierService;
        this.machineService = machineService;
        this.saveService = saveService;
        this.factoryService = factoryService;
        this.productionStepService = productionStepService;
        this.changelistService = changelistService;
    }

    @PostMapping("/factory/productionSteps")
    public ProductionStepStandalone create(int factoryId, @RequestBody ProductionStepStandalone input) {
        Factory factory = factoryService.get(factoryId);
        ProductionStep productionStep = new ProductionStep(factory, input);
        applyRelations(input, productionStep);
        productionStep = productionStepService.create(productionStep);
        factoryService.addAttachedProductionStep(factory, productionStep,
                () -> saveService.computeChangelists(factory.getSave()));
        return toOutput(productionStep);
    }

    @GetMapping("/factory/productionSteps")
    public List<ProductionStepStandalone> retrieveAll(int factoryId) {
        return factoryService.get(factoryId).getProductionSteps().stream().map(this::toOutput).toList();
    }

    @GetMapping("/productionStep")
    public ProductionStepStandalone retrieve(int productionStepId) {
        return toOutput(productionStepService.get(productionStepId));
    }

    @PatchMapping("/productionStep")
    public ProductionStepStandalone update(int productionStepId, @RequestBody ProductionStepStandalone input) {
        ProductionStep productionStep = productionStepService.get(productionStepId);
        Factory factory = productionStep.getFactory();
        ProductionStepStandalone before = new ProductionStepStandalone(productionStep, RelationRepresentation.ID);
        applyBasics(input, productionStep);
        applyRelations(input, productionStep);
        productionStep = productionStepService.update(productionStep, before,
                () -> saveService.computeChangelists(factory.getSave()));
        return toOutput(productionStep);
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

    /**
     * Applies the primary {@link Changelist} to the target {@link ProductionStep}.
     * <p>
     * Other {@link Changelist#getProductionStepChanges() changes} in the {@link Changelist} are unaffected and the
     * change for the target {@link ProductionStep} will be removed.
     * @param productionStepId the {@link ProductionStep#getId() id} of the target {@link ProductionStep}
     */
    @PatchMapping("/productionStep/applyPrimaryChangelist")
    public void applyPrimaryChangelist(int productionStepId) {
        ProductionStep productionStep = productionStepService.get(productionStepId);
        Changelist primary = saveService.computeChangelists(productionStep.getFactory().getSave()).getPrimary();
        if (!primary.getProductionStepChanges().containsKey(productionStep)) {
            throw ModelService.report(HttpStatus.CONFLICT,
                    "production step " + productionStepId + " has no change in the primary changelist");
        }
        productionStepService.applyPrimaryChangelist(productionStep, primary);
        changelistService.reportMachineCount(primary, productionStep, Fraction.ZERO);
    }

    /**
     * Sets the machine count of the target {@link ProductionStep} to {@code machineCount}.
     * <p>
     * This will <b>not</b> change the production step's current machine count but rather add a corresponding
     * {@link Changelist#getProductionStepChanges() change} to the primary {@link Changelist}.
     * @param productionStepId the {@link ProductionStep#getId() id} of the target {@link ProductionStep}
     * @param machineCount the new machine count
     */
    @PatchMapping("/productionStep/machineCount")
    public void updateMachineCount(int productionStepId, String machineCount) {
        Fraction change = Fraction.of(machineCount);
        ProductionStep productionStep = productionStepService.get(productionStepId);
        Changelist primary = saveService.computeChangelists(productionStep.getFactory().getSave()).getPrimary();
        Fraction oldValue = productionStep.getMachineCount()
                .add(primary.getProductionStepChanges().getOrDefault(productionStep, Fraction.ZERO));
        productionStepService.applyChangelistMachineCountChange(productionStep, true, oldValue, oldValue.add(change));
        changelistService.reportMachineCount(primary, productionStep, change);
    }

    private ProductionStepStandalone toOutput(ProductionStep productionStep) {
        return new ProductionStepStandalone(productionStep, productionStepService.computeThroughputs(productionStep,
                () -> saveService.computeChangelists(productionStep.getFactory().getSave())));
    }

}
