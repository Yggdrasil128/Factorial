package de.yggdrasil128.factorial.model.factory;

import de.yggdrasil128.factorial.model.productionstep.ProductionStepStandalone;
import de.yggdrasil128.factorial.model.resource.local.LocalResourceStandalone;

import java.util.List;

public class FactorySummary {

    private FactoryStandalone factory;
    private List<ProductionStepStandalone> productionSteps;
    private List<LocalResourceStandalone> resources;

    public FactoryStandalone getFactory() {
        return factory;
    }

    public void setFactory(FactoryStandalone factory) {
        this.factory = factory;
    }

    public List<ProductionStepStandalone> getProductionSteps() {
        return productionSteps;
    }

    public void setProductionSteps(List<ProductionStepStandalone> productionSteps) {
        this.productionSteps = productionSteps;
    }

    public List<LocalResourceStandalone> getResources() {
        return resources;
    }

    public void setResources(List<LocalResourceStandalone> resources) {
        this.resources = resources;
    }

}
