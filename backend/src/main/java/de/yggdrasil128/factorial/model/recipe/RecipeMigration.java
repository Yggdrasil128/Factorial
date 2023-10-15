package de.yggdrasil128.factorial.model.recipe;

import de.yggdrasil128.factorial.model.Fraction;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

public class RecipeMigration {

    private String iconName;
    private Map<String, Fraction> input = emptyMap();
    private Map<String, Fraction> output = emptyMap();
    private Fraction duration;
    private List<String> category = emptyList();

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public Map<String, Fraction> getInput() {
        return input;
    }

    public void setInput(Map<String, Fraction> input) {
        this.input = input;
    }

    public Map<String, Fraction> getOutput() {
        return output;
    }

    public void setOutput(Map<String, Fraction> output) {
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
