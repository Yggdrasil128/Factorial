package de.yggdrasil128.factorial.model.factory;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;

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
    private Object icon;
    private List<Object> productionSteps;
    private List<Object> resources;

    public FactoryStandalone() {
    }

    public FactoryStandalone(Factory model) {
        this(model, RelationRepresentation.ID);
    }

    public FactoryStandalone(Factory model, RelationRepresentation resolveStrategy) {
        id = model.getId();
        saveId = model.getSave().getId();
        ordinal = model.getOrdinal();
        name = model.getName();
        description = model.getDescription();
        icon = NamedModel.resolve(model.getIcon(), resolveStrategy);
        if (RelationRepresentation.ID == resolveStrategy) {
            productionSteps = NamedModel.resolve(model.getProductionSteps(), resolveStrategy,
                    (productionStep, strategy) -> productionStep.getId());
            resources = NamedModel.resolve(model.getResources(), resolveStrategy,
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

    public Object getIcon() {
        return icon;
    }

    public void setIcon(Object icon) {
        this.icon = icon;
    }

    public List<Object> getProductionSteps() {
        return productionSteps;
    }

    public void setProductionSteps(List<Object> productionSteps) {
        this.productionSteps = productionSteps;
    }

    public List<Object> getResources() {
        return resources;
    }

    public void setResources(List<Object> resources) {
        this.resources = resources;
    }

}
