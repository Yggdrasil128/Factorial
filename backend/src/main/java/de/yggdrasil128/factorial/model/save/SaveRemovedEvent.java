package de.yggdrasil128.factorial.model.save;

public class SaveRemovedEvent extends SaveRelatedEntityRemovedEvent {

    public SaveRemovedEvent(int saveId) {
        super(saveId);
    }

}
