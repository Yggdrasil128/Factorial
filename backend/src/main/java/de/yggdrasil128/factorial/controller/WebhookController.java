package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;
import de.yggdrasil128.factorial.engine.ResourceContributions;
import de.yggdrasil128.factorial.model.changelist.ChangelistRemoved;
import de.yggdrasil128.factorial.model.changelist.ChangelistUpdated;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.productionstep.*;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.resource.ResourceContributionsChanged;
import de.yggdrasil128.factorial.model.resource.ResourceRemoved;
import de.yggdrasil128.factorial.model.resource.ResourceUpdated;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveService;
import de.yggdrasil128.factorial.websocket.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

// TODO find the right stereotype
@Component
public class WebhookController {

    private final WebSocket downstream;
    private final SaveService saveService;
    private final FactoryService factoryService;
    private final ProductionStepService productionStepService;

    @Autowired
    public WebhookController(WebSocket downstream, SaveService saveService, FactoryService factoryService,
                             ProductionStepService productionStepService) {
        this.downstream = downstream;
        this.saveService = saveService;
        this.factoryService = factoryService;
        this.productionStepService = productionStepService;
    }

    @EventListener
    public void on(ProductionStepUpdated event) {
        ProductionStep productionStep = event.getProductionStep();
        Save save = productionStep.getFactory().getSave();
        ProductionStepThroughputs throughputs = event instanceof ProductionStepThroughputsChanged
                ? ((ProductionStepThroughputsChanged) event).getThroughputs()
                : productionStepService.computeThroughputs(productionStep,
                        () -> saveService.computeProductionStepChanges(save));
        downstream.productionStepUpdated(event.getSaveId(), productionStep, throughputs);
    }

    @EventListener
    public void on(ProductionStepRemoved event) {
        downstream.productionStepDeleted(event.getSaveId(), event.getProductionStepId());
    }

    @EventListener
    public void on(ResourceUpdated event) {
        Resource resource = event.getResource();
        Factory factory = resource.getFactory();
        Save save = factory.getSave();
        ResourceContributions contributions = event instanceof ResourceContributionsChanged
                ? ((ResourceContributionsChanged) event).getContributions()
                : factoryService.computeResources(factory, () -> saveService.computeProductionStepChanges(save))
                        .getContributions(resource);
        downstream.resourceUpdated(save.getId(), resource, contributions);
    }

    @EventListener
    public void on(ResourceRemoved event) {
        downstream.resourceDeleted(event.getSaveId(), event.getResourceId());
    }

    @EventListener
    public void on(ChangelistUpdated event) {
        downstream.changelistUpdated(event.getSaveId(), event.getChangelist());
    }

    @EventListener
    public void on(ChangelistRemoved event) {
        downstream.changelistDeleted(event.getSaveId(), event.getChangelistId());
    }

}
