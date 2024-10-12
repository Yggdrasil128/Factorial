package de.yggdrasil128.factorial.model.recipe;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;
import de.yggdrasil128.factorial.model.resource.ResourceStandalone;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class RecipeStandalone {

    private int id;
    private int gameVersionId;
    private String name;
    private Object icon;
    private List<ResourceStandalone> input;
    private List<ResourceStandalone> output;
    private Fraction duration;
    private List<Object> applicableModifiers;
    private List<Object> applicableMachines;
    private List<String> category;

    public RecipeStandalone() {
    }

    public RecipeStandalone(Recipe model, RelationRepresentation resolveStrategy) {
        id = model.getId();
        gameVersionId = model.getGameVersion().getId();
        name = model.getName();
        icon = NamedModel.resolve(model.getIcon(), resolveStrategy);
        input = model.getInput().stream().map(resource -> new ResourceStandalone(resource, resolveStrategy)).toList();
        output = model.getOutput().stream().map(resource -> new ResourceStandalone(resource, resolveStrategy)).toList();
        duration = model.getDuration();
        applicableModifiers = model.getApplicableModifiers().stream()
                .map(recipeModifier -> NamedModel.resolve(recipeModifier, resolveStrategy)).toList();
        applicableMachines = model.getApplicableMachines().stream()
                .map(machine -> NamedModel.resolve(machine, resolveStrategy)).toList();
        category = model.getCategory();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGameVersionId() {
        return gameVersionId;
    }

    public void setGameVersionId(int gameVersionId) {
        this.gameVersionId = gameVersionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getIcon() {
        return icon;
    }

    public void setIcon(Object icon) {
        this.icon = icon;
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

    public List<Object> getApplicableModifiers() {
        return applicableModifiers;
    }

    public void setApplicableModifiers(List<Object> applicableModifiers) {
        this.applicableModifiers = applicableModifiers;
    }

    public List<Object> getApplicableMachines() {
        return applicableMachines;
    }

    public void setApplicableMachines(List<Object> applicableMachines) {
        this.applicableMachines = applicableMachines;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

}
