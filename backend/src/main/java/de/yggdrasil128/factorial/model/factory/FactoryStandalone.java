package de.yggdrasil128.factorial.model.factory;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class FactoryStandalone {

    private int id;
    private int saveId;
    private int ordinal;
    private String name;
    private String description;
    private Object icon;

    public FactoryStandalone() {
    }

    public FactoryStandalone(Factory model, RelationRepresentation resolveStrategy) {
        id = model.getId();
        saveId = model.getSave().getId();
        ordinal = model.getOrdinal();
        name = model.getName();
        description = model.getDescription();
        icon = NamedModel.resolve(model.getIcon(), resolveStrategy);
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

}
