package de.yggdrasil128.factorial.model.productionstep;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.engine.ProductionEntryStandalone;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public class ProductionStepStandalone {

    private int id;
    private int factoryId;
    private Object machine;
    private Object recipe;
    private List<Object> modifiers;
    private Fraction machineCount;
    @JsonProperty(access = READ_ONLY)
    private List<ProductionEntryStandalone> inputs;
    @JsonProperty(access = READ_ONLY)
    private List<ProductionEntryStandalone> outputs;

    public ProductionStepStandalone() {
    }

    public ProductionStepStandalone(ProductionStep model) {
        this(model, RelationRepresentation.ID);
    }

    public ProductionStepStandalone(ProductionStep model, RelationRepresentation resolveStrategy) {
        id = model.getId();
        factoryId = model.getFactory().getId();
        machine = NamedModel.resolve(model.getMachine(), resolveStrategy);
        recipe = NamedModel.resolve(model.getRecipe(), resolveStrategy);
        modifiers = model.getModifiers().stream()
                .map(machineModifier -> NamedModel.resolve(machineModifier, resolveStrategy)).toList();
        machineCount = model.getMachineCount();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(int factoryId) {
        this.factoryId = factoryId;
    }

    public Object getMachine() {
        return machine;
    }

    public void setMachine(Object machine) {
        this.machine = machine;
    }

    public Object getRecipe() {
        return recipe;
    }

    public void setRecipe(Object recipe) {
        this.recipe = recipe;
    }

    public List<Object> getModifiers() {
        return modifiers;
    }

    public void setModifiers(List<Object> modifiers) {
        this.modifiers = modifiers;
    }

    public Fraction getMachineCount() {
        return machineCount;
    }

    public void setMachineCount(Fraction machineCount) {
        this.machineCount = machineCount;
    }

    public List<ProductionEntryStandalone> getInputs() {
        return inputs;
    }

    public void setInputs(List<ProductionEntryStandalone> inputs) {
        this.inputs = inputs;
    }

    public List<ProductionEntryStandalone> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<ProductionEntryStandalone> outputs) {
        this.outputs = outputs;
    }

}
