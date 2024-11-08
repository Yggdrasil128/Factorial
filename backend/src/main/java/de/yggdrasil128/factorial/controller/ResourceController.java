package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.engine.ProductionLine;
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

/**
 * Manages {@link Resource Resources}.
 * <p>
 * In contrast to most other entities, resources cannot be created or deleted explicitly. Instead, they are spawned
 * and/or destroyed when computing a {@link ProductionLine}.
 */
@RestController
@RequestMapping("/api")
public class ResourceController {

    private final FactoryService factoryService;
    private final ResourceService resourceService;

    @Autowired
    public ResourceController(FactoryService factoryService, ResourceService resourceService) {
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
    public void order(int factoryId, @RequestBody List<EntityPosition> input) {
        resourceService.reorder(factoryId, input);
    }

    @PatchMapping("/resource")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void update(int resourceId, ResourceStandalone input) {
        resourceService.update(resourceId, input);
    }

    private static ResourceStandalone toOutput(Resource resource) {
        return ResourceStandalone.of(resource, External.FRONTEND);
    }

}
