package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.engine.ProductionStepChanges;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.External;
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
                () -> saveService.computeProductionStepChanges(factory.getSave()));
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
        ProductionStepStandalone before = ProductionStepStandalone.of(productionStep, External.FRONTEND);
        productionStep.applyBasics(input);
        applyRelations(input, productionStep);
        productionStep = productionStepService.update(productionStep, before,
                () -> saveService.computeProductionStepChanges(factory.getSave()));
        return toOutput(productionStep);
    }

    private void applyRelations(ProductionStepStandalone input, ProductionStep productionStep) {
        OptionalInputField.ofId(input.machineId(), machineService::get).apply(productionStep::setMachine);
        OptionalInputField.ofId(input.recipeId(), recipeService::get).apply(productionStep::setRecipe);
        OptionalInputField.ofIds(input.modifierIds(), recipeModifierService::get)
                .applyList(productionStep::setModifiers);
    }

    @DeleteMapping("/productionStep")
    public void delete(int productionStepId) {
        productionStepService.delete(productionStepId);
    }

    /**
     * Applies the primary {@link Changelist} to the target {@link ProductionStep}.
     * <p>
     * In contrast to {@link ChangelistController#apply(int) /changelist/apply}, this only applies the change to the
     * target production step. Other {@link Changelist#getProductionStepChanges() changes} in the primary
     * {@link Changelist} are unaffected and the change for the target {@link ProductionStep} will be removed.
     * 
     * @param productionStepId the {@link ProductionStep#getId() id} of the target {@link ProductionStep}
     */
    @PatchMapping("/productionStep/applyPrimaryChangelist")
    public void applyPrimaryChangelist(int productionStepId) {
        ProductionStep productionStep = productionStepService.get(productionStepId);
        ProductionStepChanges changes = saveService.computeProductionStepChanges(productionStep.getFactory().getSave());
        if (!changes.contains(productionStep.getId())) {
            throw ModelService.report(HttpStatus.CONFLICT,
                    "production step " + productionStepId + " has no change in the primary changelist");
        }
        Fraction change = changes.getChanges(productionStep.getId()).getWithPrimaryChangelist();
        changelistService.reportMachineCount(changes.getPrimaryChangelistId(), productionStep, Fraction.ZERO);
        productionStepService.setCurrentMachineCount(productionStep, productionStep.getMachineCount().add(change),
                changes);
    }

    /**
     * Sets the machine count of the target {@link ProductionStep} to {@code machineCount}.
     * <p>
     * This will <b>not</b> change the production step's current machine count but rather add a corresponding
     * {@link Changelist#getProductionStepChanges() change} to the primary {@link Changelist}.
     * 
     * @param productionStepId the {@link ProductionStep#getId() id} of the target {@link ProductionStep}
     * @param machineCount the new machine count
     */
    @PatchMapping("/productionStep/machineCount")
    public void updateMachineCount(int productionStepId, String machineCount) {
        Fraction newValue = Fraction.of(machineCount);
        ProductionStep productionStep = productionStepService.get(productionStepId);
        Fraction change = newValue.subtract(productionStep.getMachineCount());
        ProductionStepChanges changes = saveService.computeProductionStepChanges(productionStep.getFactory().getSave());
        changelistService.reportMachineCount(changes.getPrimaryChangelistId(), productionStep, change);
        productionStepService.handleChangelistEntryChanged(productionStep, changes);
    }

    private ProductionStepStandalone toOutput(ProductionStep productionStep) {
        return ProductionStepStandalone.of(productionStep, productionStepService.computeThroughputs(productionStep,
                () -> saveService.computeProductionStepChanges(productionStep.getFactory().getSave())));
    }

}
