package de.yggdrasil128.factorial.model.changelist;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public class ChangelistStandalone {

    @JsonProperty(access = READ_ONLY)
    private int id;
    @JsonProperty(access = READ_ONLY)
    private int saveId;
    private int ordinal;
    private String name;
    private boolean primary;
    private boolean active;
    private Object iconId;
    private List<ProductionStepChangeStandalone> productionStepChanges;

    public ChangelistStandalone() {
    }

    public ChangelistStandalone(Changelist model) {
        this(model, RelationRepresentation.ID);
    }

    public ChangelistStandalone(Changelist model, RelationRepresentation resolveStrategy) {
        id = model.getId();
        saveId = model.getSave().getId();
        ordinal = model.getOrdinal();
        name = model.getName();
        primary = model.isPrimary();
        active = model.isActive();
        iconId = NamedModel.resolve(model.getIconId(), resolveStrategy);
        productionStepChanges = model.getProductionStepChanges().entrySet().stream()
                .map(entry -> new ProductionStepChangeStandalone(entry, resolveStrategy)).toList();
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

    public boolean isPrimary() {
        return primary;
    }

    public boolean isActive() {
        return active;
    }

    public Object getIconId() {
        return iconId;
    }

    public void setIconId(Object iconId) {
        this.iconId = iconId;
    }

    public List<ProductionStepChangeStandalone> getProductionStepChanges() {
        return productionStepChanges;
    }

}
