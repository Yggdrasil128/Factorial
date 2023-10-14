package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.productionstepchange.ProductionStepChange;
import de.yggdrasil128.factorial.model.save.Save;

import java.util.List;

public class ChangeListStandalone {

    private String name;
    private boolean primary;
    private boolean active;
    private int iconId;

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

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public Changelist with(Save save, Icon icon, List<ProductionStepChange> prodductionStepChanges) {
        return new Changelist(save, name, primary, active, icon, prodductionStepChanges);
    }

}
