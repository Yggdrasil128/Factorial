package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;

import java.util.List;

public class ChangelistStandalone {

    private int id;
    private int saveId;
    private int ordinal;
    private String name;
    private boolean primary;
    private boolean active;
    private Object icon;
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
        icon = NamedModel.resolve(model.getIcon(), resolveStrategy);
        productionStepChanges = model.getProductionStepChanges().entrySet().stream()
                .map(entry -> new ProductionStepChangeStandalone(entry, resolveStrategy)).toList();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSaveId() {
        return saveId;
    }

    public void setSaveId(int saveId) {
        this.saveId = saveId;
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

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Object getIcon() {
        return icon;
    }

    public void setIcon(Object icon) {
        this.icon = icon;
    }

    public List<ProductionStepChangeStandalone> getProductionStepChanges() {
        return productionStepChanges;
    }

    public void setProductionStepChanges(List<ProductionStepChangeStandalone> productionStepChanges) {
        this.productionStepChanges = productionStepChanges;
    }

}
