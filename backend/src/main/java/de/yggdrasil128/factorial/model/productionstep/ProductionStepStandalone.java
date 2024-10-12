package de.yggdrasil128.factorial.model.productionstep;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ProductionStepStandalone {

    private int id;
    private int factoryId;
    private Object machine;
    private Object recipe;
    private List<Object> modifiers;
    private Fraction machineCount;

    public ProductionStepStandalone() {
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

}
