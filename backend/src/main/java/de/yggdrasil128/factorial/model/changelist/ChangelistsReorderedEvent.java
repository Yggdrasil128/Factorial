package de.yggdrasil128.factorial.model.changelist;

import java.util.Collection;

public class ChangelistsReorderedEvent {

    private final int saveId;
    private final Collection<Changelist> changelists;

    public ChangelistsReorderedEvent(int saveId, Collection<Changelist> changelists) {
        this.saveId = saveId;
        this.changelists = changelists;
    }

    public int getSaveId() {
        return saveId;
    }

    public Collection<Changelist> getChangelists() {
        return changelists;
    }

}
