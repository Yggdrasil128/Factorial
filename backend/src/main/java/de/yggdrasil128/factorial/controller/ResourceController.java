package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.engine.ProductionLine;
import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.EntityPosition;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.resource.ResourceService;
import de.yggdrasil128.factorial.model.resource.ResourceStandalone;
import de.yggdrasil128.factorial.model.save.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final SaveService saveService;
    private final FactoryService factoryService;
    private final ResourceService resourceService;

    @Autowired
    public ResourceController(SaveService saveService, FactoryService factoryService, ResourceService resourceService) {
        this.saveService = saveService;
        this.factoryService = factoryService;
        this.resourceService = resourceService;
    }

    @GetMapping("/factory/resources")
    public List<ResourceStandalone> retrieveAll(int factoryId) {
        Factory factory = factoryService.get(factoryId);
        ProductionLine productionLine = factoryService.computeProductionLine(factory,
                () -> saveService.computeProductionStepChanges(factory.getSave()));
        return factory.getResources().stream()
                .map(resource -> ResourceStandalone.of(resource, productionLine.getContributions(resource))).toList();
    }

    @PatchMapping("/factory/resources/order")
    public void order(int factoryId, @RequestBody List<EntityPosition> input) {
        resourceService.reorder(factoryService.get(factoryId), input);
    }

    @PatchMapping("/resource")
    public ResourceStandalone update(int resourceId, ResourceStandalone input) {
        Resource resource = resourceService.get(resourceId);
        resource.applyBasics(input);
        return ResourceStandalone.of(resource, External.FRONTEND);
    }

}
