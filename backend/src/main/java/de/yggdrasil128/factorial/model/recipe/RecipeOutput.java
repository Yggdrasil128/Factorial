package de.yggdrasil128.factorial.model.recipe;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.icon.IconOutput;
import de.yggdrasil128.factorial.model.machine.MachineOutput;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierOutput;
import de.yggdrasil128.factorial.model.resource.ResourceOutput;

import java.util.List;

public class RecipeOutput {

    private final Recipe delegate;
    private final IconOutput icon;
    private final List<ResourceOutput> input;
    private final List<ResourceOutput> output;
    private final List<MachineOutput> applicableMachines;
    private final List<RecipeModifierOutput> applicableModifiers;

    public RecipeOutput(Recipe delegate) {
        this.delegate = delegate;
        this.icon = IconOutput.of(delegate.getIcon());
        this.input = delegate.getInput().stream().map(ResourceOutput::new).toList();
        this.output = delegate.getOutput().stream().map(ResourceOutput::new).toList();
        this.applicableMachines = delegate.getApplicableMachines().stream().map(MachineOutput::new).toList();
        this.applicableModifiers = delegate.getApplicableModifiers().stream().map(RecipeModifierOutput::new).toList();
    }

    public int getId() {
        return delegate.getId();
    }

    public int getGameVersionId() {
        return delegate.getGameVersion().getId();
    }

    public String getName() {
        return delegate.getName();
    }

    public IconOutput getIcon() {
        return icon;
    }

    public Fraction getDuration() {
        return delegate.getDuration();
    }

    public List<String> getCategory() {
        return delegate.getCategory();
    }

    public List<ResourceOutput> getInput() {
        return input;
    }

    public List<ResourceOutput> getOutput() {
        return output;
    }

    public List<MachineOutput> getApplicableMachines() {
        return applicableMachines;
    }

    public List<RecipeModifierOutput> getApplicableModifiers() {
        return applicableModifiers;
    }
}
