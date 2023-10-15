package de.yggdrasil128.factorial.model.recipe;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.resource.ResourceInput;

import java.util.List;

public class RecipeInput {

    private String name;
    private int iconId;
    private List<ResourceInput> input;
    private List<ResourceInput> output;
    private Fraction duration;
    private List<String> category;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public List<ResourceInput> getInput() {
        return input;
    }

    public void setInput(List<ResourceInput> input) {
        this.input = input;
    }

    public List<ResourceInput> getOutput() {
        return output;
    }

    public void setOutput(List<ResourceInput> output) {
        this.output = output;
    }

    public Fraction getDuration() {
        return duration;
    }

    public void setDuration(Fraction duration) {
        this.duration = duration;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

}
