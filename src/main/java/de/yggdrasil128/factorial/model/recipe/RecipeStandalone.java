package de.yggdrasil128.factorial.model.recipe;

import static java.util.Collections.emptyList;

import java.util.List;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.resource.ResourceStandalone;

public class RecipeStandalone {

    private String name;
    private List<ResourceStandalone> input = emptyList();
    private List<ResourceStandalone> output = emptyList();
    private Fraction duration;
    private List<Integer> applicableModifierIds = emptyList();
    private List<Integer> applicableMachineIds = emptyList();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ResourceStandalone> getInput() {
        return input;
    }

    public void setInput(List<ResourceStandalone> input) {
        this.input = input;
    }

    public List<ResourceStandalone> getOutput() {
        return output;
    }

    public void setOutput(List<ResourceStandalone> output) {
        this.output = output;
    }

    public Fraction getDuration() {
        return duration;
    }

    public void setDuration(Fraction duration) {
        this.duration = duration;
    }

    public List<Integer> getApplicableModifierIds() {
        return applicableModifierIds;
    }

    public void setApplicableModifierIds(List<Integer> applicableModifierIds) {
        this.applicableModifierIds = applicableModifierIds;
    }

    public List<Integer> getApplicableMachineIds() {
        return applicableMachineIds;
    }

    public void setApplicableMachineIds(List<Integer> applicableMachineIds) {
        this.applicableMachineIds = applicableMachineIds;
    }

}
