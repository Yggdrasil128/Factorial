package de.yggdrasil128.factorial.model.icon;

public class IconUpdatedEvent {

    private final Icon icon;

    public IconUpdatedEvent(Icon icon) {
        this.icon = icon;
    }

    public Icon getIcon() {
        return icon;
    }

}
