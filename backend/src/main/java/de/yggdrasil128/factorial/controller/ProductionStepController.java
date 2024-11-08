package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.changelist.ChangelistService;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepService;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepStandalone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductionStepController {

    private final FactoryService factoryService;
    private final ProductionStepService productionStepService;
    private final ChangelistService changelistService;

    @Autowired
    public ProductionStepController(FactoryService factoryService, ProductionStepService productionStepService,
                                    ChangelistService changelistService) {
        this.factoryService = factoryService;
        this.productionStepService = productionStepService;
        this.changelistService = changelistService;
    }

    @PostMapping("/factory/productionSteps")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void create(int factoryId, @RequestBody ProductionStepStandalone input) {
        productionStepService.create(factoryId, input);
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
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void update(int productionStepId, @RequestBody ProductionStepStandalone input) {
        productionStepService.update(productionStepId, input);
    }

    @DeleteMapping("/productionStep")
    @ResponseStatus(HttpStatus.ACCEPTED)
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
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void applyPrimaryChangelist(int productionStepId) {
        changelistService.applyPrimaryChangelist(productionStepId);
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
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateMachineCount(int productionStepId, String machineCount) {
        changelistService.setPrimaryMachineCount(productionStepId, Fraction.of(machineCount));
    }

    private ProductionStepStandalone toOutput(ProductionStep productionStep) {
        return ProductionStepStandalone.of(productionStep, External.FRONTEND);
    }

}
