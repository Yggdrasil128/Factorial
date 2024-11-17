package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.engine.ProductionLine;
import de.yggdrasil128.factorial.model.AsyncHelper;
import de.yggdrasil128.factorial.model.EntityPosition;
import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.resource.local.LocalResource;
import de.yggdrasil128.factorial.model.resource.local.LocalResourceService;
import de.yggdrasil128.factorial.model.resource.local.LocalResourceStandalone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Manages {@link LocalResource LocalResources}.
 * <p>
 * In contrast to most other entities, resources cannot be created or deleted explicitly. Instead, they are spawned
 * and/or destroyed when computing a {@link ProductionLine}.
 */
@RestController
@RequestMapping("/api")
public class LocalResourceController {

    private final AsyncHelper asyncHelper;
    private final FactoryService factoryService;
    private final LocalResourceService resourceService;

    @Autowired
    public LocalResourceController(AsyncHelper asyncHelper, FactoryService factoryService,
                                   LocalResourceService resourceService) {
        this.asyncHelper = asyncHelper;
        this.factoryService = factoryService;
        this.resourceService = resourceService;
    }

    @GetMapping("/factory/resources")
    public List<LocalResourceStandalone> retrieveAll(int factoryId) {
        return factoryService.get(factoryId).getResources().stream()
                .map(resource -> LocalResourceStandalone.of(resource, External.FRONTEND)).toList();
    }

    @PatchMapping("/factory/resources/order")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompletableFuture<Void> order(int factoryId, @RequestBody List<EntityPosition> input) {
        return asyncHelper.submit(result -> resourceService.reorder(factoryId, input, result));
    }

    @PatchMapping("/resources")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompletableFuture<Void> update(@RequestBody List<LocalResourceStandalone> input) {
        return asyncHelper.submit(result -> resourceService.update(input, result));
    }

}
