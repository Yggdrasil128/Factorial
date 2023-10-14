package de.yggdrasil128.factorial.api.items;

import de.yggdrasil128.factorial.model.icon.Icon;

public class ApiIcon {

    private final Icon delegate;

    public ApiIcon(Icon delegate) {
        this.delegate = delegate;
    }
    
    public int getId() {
        return delegate.getId();
    }
    
    public String getName() {
        return delegate.getName();
    }

    public String getUrl() {
        return "/api/icons?id=" + delegate.getId();
    }

}
