package de.yggdrasil128.factorial.model.resource.global;

import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.save.Save;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class GlobalResource extends Resource {

    @ManyToOne(optional = false)
    private Save save;

    public GlobalResource() {
    }

    public GlobalResource(Save save, GlobalResourceStandalone standalone) {
        this.save = save;
        applyBasics(standalone);
    }

    public void applyBasics(GlobalResourceStandalone standalone) {
        OptionalInputField.of(standalone.ordinal()).apply(this::setOrdinal);
    }

    public Save getSave() {
        return save;
    }

    public void setSave(Save save) {
        this.save = save;
    }

}
