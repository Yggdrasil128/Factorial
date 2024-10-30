package de.yggdrasil128.factorial.model.factory;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.engine.ProductionLine;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;
import de.yggdrasil128.factorial.model.productionstep.ProductionEntryStandalone;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public class FactoryStandalone {

    @JsonProperty(access = READ_ONLY)
    private int id;
    @JsonProperty(access = READ_ONLY)
    private int saveId;
    private int ordinal;
    private String name;
    private String description;
    private Object iconId;
    private List<Object> productionStepIds;
    private List<Object> resourceIds;
    @JsonProperty(access = READ_ONLY)
    private List<ProductionEntryStandalone> inputs;
    @JsonProperty(access = READ_ONLY)
    private List<ProductionEntryStandalone> outputs;

    public FactoryStandalone() {
    }

    public FactoryStandalone(Factory model, ProductionLine productionLine) {
        this(model, RelationRepresentation.ID);
        inputs = productionLine.getInputs().entrySet().stream().map(ProductionEntryStandalone::new).toList();
        outputs = productionLine.getOutputs().entrySet().stream().map(ProductionEntryStandalone::new).toList();
    }

    public FactoryStandalone(Factory model, RelationRepresentation resolveStrategy) {
        id = model.getId();
        saveId = model.getSave().getId();
        ordinal = model.getOrdinal();
        name = model.getName();
        description = model.getDescription();
        iconId = NamedModel.resolve(model.getIcon(), resolveStrategy);
        if (RelationRepresentation.ID == resolveStrategy) {
            productionStepIds = NamedModel.resolve(model.getProductionSteps(), resolveStrategy,
                    (productionStep, strategy) -> productionStep.getId());
            resourceIds = NamedModel.resolve(model.getResources(), resolveStrategy,
                    (resource, strategy) -> resource.getId());
        }
    }

    public int getId() {
        return id;
    }

    public int getSaveId() {
        return saveId;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getIconId() {
        return iconId;
    }

    public void setIconId(Object iconId) {
        this.iconId = iconId;
    }

    public List<Object> getProductionStepIds() {
        return productionStepIds;
    }

    public void setProductionStepIds(List<Object> productionStepIds) {
        this.productionStepIds = productionStepIds;
    }

    public List<Object> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(List<Object> resourceIds) {
        this.resourceIds = resourceIds;
    }

    public List<ProductionEntryStandalone> getInputs() {
        return inputs;
    }

    public List<ProductionEntryStandalone> getOutputs() {
        return outputs;
    }

}
