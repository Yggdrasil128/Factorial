package de.yggdrasil128.factorial.api.items;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.changelist.ChangelistService;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/factoryItemList")
public class ItemListController {

    private final ItemListService service;
    private final FactoryService factories;
    private final ChangelistService changelists;
    private final ProductionStepService productionSteps;

    @Autowired
    public ItemListController(ItemListService service, FactoryService factories, ProductionStepService productionSteps,
                              ChangelistService changelists) {
        this.service = service;
        this.factories = factories;
        this.productionSteps = productionSteps;
        this.changelists = changelists;
    }

    @GetMapping
    public ItemList getFullList(int factoryId) {
        return service.getFullList(factories.get(factoryId));
    }

    @PatchMapping("reportProductionStepMachineCount")
    public ItemList reportProductionStepMachineCount(int productionStepId, String machineCount) {
        ProductionStep productionStep = productionSteps.get(productionStepId);
        Changelist primary = service.getPrimaryChangelist(productionStep.getFactory());
        changelists.reportMachineCount(primary, productionStep, Fraction.of(machineCount));
        return service.getFullList(productionStep.getFactory());
    }

}
