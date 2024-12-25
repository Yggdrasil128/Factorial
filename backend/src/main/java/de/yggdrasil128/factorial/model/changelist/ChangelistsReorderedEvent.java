package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.model.save.SaveRelatedEvent;

import java.util.Collection;

public class ChangelistsReorderedEvent implements SaveRelatedEvent {

    private final int saveId;
    private final Collection<Changelist> changelists;

    public ChangelistsReorderedEvent(int saveId, Collection<Changelist> changelists) {
        this.saveId = saveId;
        this.changelists = changelists;
    }

    @Override
    public int getSaveId() {
        return saveId;
    }

    public Collection<Changelist> getChangelists() {
        return changelists;
    }

}
