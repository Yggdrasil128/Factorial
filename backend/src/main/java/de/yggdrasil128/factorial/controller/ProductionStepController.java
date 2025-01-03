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
import de.yggdrasil128.factorial.model.resource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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
    public CompletableFuture<Void> create(int factoryId, @RequestBody List<ProductionStepStandalone> input) {
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

    @PatchMapping("/productionSteps")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompletableFuture<Void> update(@RequestBody List<ProductionStepStandalone> input) {
        return asyncHelper.submit(result -> productionStepService.update(input, result));
    }

    @DeleteMapping("/productionSteps")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompletableFuture<Void> delete(String productionStepIds) {
        List<Integer> productionStepIdsList = Arrays.stream(productionStepIds.split(",")).map(Integer::parseInt)
                .toList();
        return asyncHelper.submit(result -> productionStepService.delete(productionStepIdsList, result));
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
     * This will <b>not</b> change the production step's current machine count but rather add (or update) a
     * corresponding {@link Changelist#getProductionStepChanges() change} to the primary {@link Changelist}.
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

    /**
     * Sets the machine count of the target {@link ProductionStep} to a value that satisfies the over-consumption of the
     * given {@link Resource}.
     * <p>
     * This will <b>not</b> change the production step's current machine count but rather add (or update) a
     * corresponding {@link Changelist#getProductionStepChanges() change} to the primary {@link Changelist}.
     * 
     * @param productionStepId the {@link ProductionStep#getId() id} of the target {@link ProductionStep}
     * @param resourceId the {@link Resource#getId() id} of the target {@link Resource}
     */
    @PatchMapping("/productionStep/satisfyConsumption")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompletableFuture<Void> satisfyConsumption(int productionStepId, int resourceId) {
        return asyncHelper.submit(result -> factoryService.satisfyConsumption(resourceId, productionStepId,
                changelistService::getProductionStepChanges, result));
    }

    /**
     * Sets the machine count of the target {@link ProductionStep} to a value that satisfies the over-production of the
     * given {@link Resource}.
     * <p>
     * This will <b>not</b> change the production step's current machine count but rather add (or update) a
     * corresponding {@link Changelist#getProductionStepChanges() change} to the primary {@link Changelist}.
     * 
     * @param productionStepId the {@link ProductionStep#getId() id} of the target {@link ProductionStep}
     * @param resourceId the {@link Resource#getId() id} of the target {@link Resource}
     */
    @PatchMapping("/productionStep/satisfyProduction")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompletableFuture<Void> satisfyProduction(int productionStepId, int resourceId) {
        return asyncHelper.submit(result -> factoryService.satisfyProduction(resourceId, productionStepId,
                changelistService::getProductionStepChanges, result));
    }

    private ProductionStepStandalone toOutput(ProductionStep productionStep) {
        return ProductionStepStandalone.of(productionStep, External.FRONTEND);
    }

}
