package de.yggdrasil128.factorial.model.save;

import de.yggdrasil128.factorial.model.changelist.ChangelistStandalone;
import de.yggdrasil128.factorial.model.factory.FactorySummary;

import java.util.List;

public class SaveSummary {

    private SaveStandalone save;
    private List<FactorySummary> factories;
    private List<ChangelistStandalone> changelists;

    public SaveStandalone getSave() {
        return save;
    }

    public void setSave(SaveStandalone save) {
        this.save = save;
    }

    public List<FactorySummary> getFactories() {
        return factories;
    }

    public void setFactories(List<FactorySummary> factories) {
        this.factories = factories;
    }

    public List<ChangelistStandalone> getChangelists() {
        return changelists;
    }

    public void setChangelists(List<ChangelistStandalone> changelists) {
        this.changelists = changelists;
    }

}
