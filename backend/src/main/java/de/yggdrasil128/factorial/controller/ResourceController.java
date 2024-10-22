package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.engine.ProductionLineResources;
import de.yggdrasil128.factorial.model.RelationRepresentation;
import de.yggdrasil128.factorial.model.ReorderInputEntry;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.resource.ResourceService;
import de.yggdrasil128.factorial.model.resource.ResourceStandalone;
import de.yggdrasil128.factorial.model.save.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        ProductionLineResources resources = factoryService.computeResources(factory,
                () -> saveService.computeChangelists(factory.getSave()));
        return factory.getResources().stream().map(resource -> new ResourceStandalone(resource,
                resources.getContributions().get(resource.getItem().getId()))).toList();
    }

    @PatchMapping("/factory/resources/order")
    public void reorder(int factoryId, @RequestBody List<ReorderInputEntry> input) {
        resourceService.reorder(factoryService.get(factoryId), input);
    }

    @PatchMapping("/resource")
    public ResourceStandalone update(int resourceId, ResourceStandalone input) {
        Resource resource = resourceService.get(resourceId);
        applyBasics(input, resource);
        return new ResourceStandalone(resource, RelationRepresentation.ID);
    }

    private static void applyBasics(ResourceStandalone input, Resource resource) {
        resource.setImported(input.isImported());
        resource.setExported(input.isExported());
    }

}
