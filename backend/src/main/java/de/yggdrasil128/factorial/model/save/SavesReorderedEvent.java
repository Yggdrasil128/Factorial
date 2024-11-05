package de.yggdrasil128.factorial.model.save;

import java.util.Collection;

public class SavesReorderedEvent {

    private final Collection<Save> saves;

    public SavesReorderedEvent(Collection<Save> saves) {
        this.saves = saves;
    }

    public Collection<Save> getSaves() {
        return saves;
    }

}
