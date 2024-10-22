package de.yggdrasil128.factorial.model.factory;

import de.yggdrasil128.factorial.model.productionstep.ProductionStepStandalone;
import de.yggdrasil128.factorial.model.resource.ResourceStandalone;

import java.util.List;

public class FactorySummary {

    private FactoryStandalone factory;
    private List<ProductionStepStandalone> productionSteps;
    private List<ResourceStandalone> resources;

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

    public List<ResourceStandalone> getResources() {
        return resources;
    }

    public void setResources(List<ResourceStandalone> resources) {
        this.resources = resources;
    }

}
