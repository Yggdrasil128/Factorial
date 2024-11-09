package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.AsyncHelper;
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
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class ProductionStepController {

    private final AsyncHelper asyncHelper;
    private final FactoryService factoryService;
    private final ProductionStepService productionStepService;
    private final ChangelistService changelistService;

    @Autowired
    public ProductionStepController(AsyncHelper asyncHelper, FactoryService factoryService,
                                    ProductionStepService productionStepService, ChangelistService changelistService) {
        this.asyncHelper = asyncHelper;
        this.factoryService = factoryService;
        this.productionStepService = productionStepService;
        this.changelistService = changelistService;
    }

    @PostMapping("/factory/productionSteps")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompletableFuture<Void> create(int factoryId, @RequestBody ProductionStepStandalone input) {
        return asyncHelper.submit(result -> productionStepService.create(factoryId, input, result));
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
    public CompletableFuture<Void> update(int productionStepId, @RequestBody ProductionStepStandalone input) {
        return asyncHelper.submit(result -> productionStepService.update(productionStepId, input, result));
    }

    @DeleteMapping("/productionStep")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompletableFuture<Void> delete(int productionStepId) {
        return asyncHelper.submit(result -> productionStepService.delete(productionStepId, result));
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
    public CompletableFuture<Void> applyPrimaryChangelist(int productionStepId) {
        return asyncHelper.submit(result -> changelistService.applyPrimaryChangelist(productionStepId, result));
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
    public CompletableFuture<Void> updateMachineCount(int productionStepId, String machineCount) {
        return asyncHelper.submit(result -> changelistService.setPrimaryMachineCount(productionStepId,
                Fraction.of(machineCount), result));
    }

    private ProductionStepStandalone toOutput(ProductionStep productionStep) {
        return ProductionStepStandalone.of(productionStep, External.FRONTEND);
    }

}
