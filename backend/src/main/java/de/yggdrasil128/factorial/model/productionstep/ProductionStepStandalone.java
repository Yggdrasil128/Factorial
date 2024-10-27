package de.yggdrasil128.factorial.model.productionstep;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public class ProductionStepStandalone {

    @JsonProperty(access = READ_ONLY)
    private int id;
    @JsonProperty(access = READ_ONLY)
    private int factoryId;
    private Object machineId;
    private Object recipeId;
    private List<Object> modifierIds;
    private Fraction machineCount;
    @JsonProperty(access = READ_ONLY)
    private List<ProductionEntryStandalone> inputs;
    @JsonProperty(access = READ_ONLY)
    private List<ProductionEntryStandalone> outputs;

    public ProductionStepStandalone() {
    }

    public ProductionStepStandalone(ProductionStep model, ProductionStepThroughputs throughputs) {
        this(model, RelationRepresentation.ID);
        inputs = throughputs.getInputs().entrySet().stream().map(ProductionEntryStandalone::new).toList();
        outputs = throughputs.getOutputs().entrySet().stream().map(ProductionEntryStandalone::new).toList();
    }

    public ProductionStepStandalone(ProductionStep model, RelationRepresentation resolveStrategy) {
        id = model.getId();
        factoryId = model.getFactory().getId();
        machineId = NamedModel.resolve(model.getMachine(), resolveStrategy);
        recipeId = NamedModel.resolve(model.getRecipe(), resolveStrategy);
        modifierIds = NamedModel.resolve(model.getModifiers(), resolveStrategy);
        machineCount = model.getMachineCount();
    }

    public int getId() {
        return id;
    }

    public int getFactoryId() {
        return factoryId;
    }

    public Object getMachineId() {
        return machineId;
    }

    public void setMachineId(Object machineId) {
        this.machineId = machineId;
    }

    public Object getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Object recipeId) {
        this.recipeId = recipeId;
    }

    public List<Object> getModifierIds() {
        return modifierIds;
    }

    public void setModifierIds(List<Object> modifierIds) {
        this.modifierIds = modifierIds;
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

    public List<ProductionEntryStandalone> getOutputs() {
        return outputs;
    }

}
