package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.engine.ProductionLine;
import de.yggdrasil128.factorial.model.AsyncHelper;
import de.yggdrasil128.factorial.model.EntityPosition;
import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.resource.ResourceService;
import de.yggdrasil128.factorial.model.resource.ResourceStandalone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Manages {@link Resource Resources}.
 * <p>
 * In contrast to most other entities, resources cannot be created or deleted explicitly. Instead, they are spawned
 * and/or destroyed when computing a {@link ProductionLine}.
 */
@RestController
@RequestMapping("/api")
public class ResourceController {

    private final AsyncHelper asyncHelper;
    private final FactoryService factoryService;
    private final ResourceService resourceService;

    @Autowired
    public ResourceController(AsyncHelper asyncHelper, FactoryService factoryService, ResourceService resourceService) {
        this.asyncHelper = asyncHelper;
        this.factoryService = factoryService;
        this.resourceService = resourceService;
    }

    @GetMapping("/factory/resources")
    public List<ResourceStandalone> retrieveAll(int factoryId) {
        Factory factory = factoryService.get(factoryId);
        return factory.getResources().stream().map(ResourceController::toOutput).toList();
    }

    @PatchMapping("/factory/resources/order")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompletableFuture<Void> order(int factoryId, @RequestBody List<EntityPosition> input) {
        return asyncHelper.submit(result -> resourceService.reorder(factoryId, input, result));
    }

    @PatchMapping("/resources")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompletableFuture<Void> update(List<ResourceStandalone> input) {
        return asyncHelper.submit(result -> resourceService.update(input, result));
    }

    private static ResourceStandalone toOutput(Resource resource) {
        return ResourceStandalone.of(resource, External.FRONTEND);
    }

}
