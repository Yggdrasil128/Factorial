package de.yggdrasil128.factorial.model.save;

import de.yggdrasil128.factorial.model.changelist.ChangelistStandalone;
import de.yggdrasil128.factorial.model.factory.FactoryStandalone;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepStandalone;

import java.util.List;

public class SaveSummary {

    private SaveStandalone save;
    private List<FactoryStandalone> factories;
    private List<List<ProductionStepStandalone>> productionSteps;
    private List<ChangelistStandalone> changelists;

    public SaveStandalone getSave() {
        return save;
    }

    public void setSave(SaveStandalone save) {
        this.save = save;
    }

    public List<FactoryStandalone> getFactories() {
        return factories;
    }

    public void setFactories(List<FactoryStandalone> factories) {
        this.factories = factories;
    }

    public List<List<ProductionStepStandalone>> getProductionSteps() {
        return productionSteps;
    }

    public void setProductionSteps(List<List<ProductionStepStandalone>> productionSteps) {
        this.productionSteps = productionSteps;
    }

    public List<ChangelistStandalone> getChangelists() {
        return changelists;
    }

    public void setChangelists(List<ChangelistStandalone> changelists) {
        this.changelists = changelists;
    }

}
