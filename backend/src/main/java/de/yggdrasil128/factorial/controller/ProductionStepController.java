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
        return asyncHelper.submit(result -> productionStepService.setMachineCount(productionStepId,
                Fraction.of(machineCount), changelistService::getProductionStepChanges));
    }

    /**
     * Computes a machine count for the target {@link ProductionStep} that would satisfy the over-consumption or
     * -production of the given {@link Resource}.
     * 
     * @param productionStepId the {@link ProductionStep#getId() id} of the target {@link ProductionStep}
     * @param resourceId the {@link Resource#getId() id} of the target {@link Resource}
     * @return the new machine count (as fraction as JSON string)
     */
    @GetMapping("/productionStep/satisfaction")
    @ResponseStatus(HttpStatus.OK)
    public CompletableFuture<Fraction> findSatisfaction(int productionStepId, int resourceId) {
        return asyncHelper.submit(() -> factoryService.findSatisfaction(resourceId, productionStepId,
                changelistService::getProductionStepChanges));
    }

    private ProductionStepStandalone toOutput(ProductionStep productionStep) {
        return ProductionStepStandalone.of(productionStep, External.FRONTEND);
    }

}
