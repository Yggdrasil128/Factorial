package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.model.ModelChanged;

public class ChangelistRemoved implements ModelChanged {

    private final int saveId;
    private final int changelistId;

    public ChangelistRemoved(int saveId, int changelistId) {
        this.saveId = saveId;
        this.changelistId = changelistId;
    }

    @Override
    public int getSaveId() {
        return saveId;
    }

    public int getChangelistId() {
        return changelistId;
    }

}
