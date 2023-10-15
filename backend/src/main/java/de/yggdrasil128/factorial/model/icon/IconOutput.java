package de.yggdrasil128.factorial.model.icon;

import java.util.List;

public class IconOutput {

    private final Icon delegate;

    public static IconOutput of(Icon delegate) {
        return null == delegate ? null : new IconOutput(delegate);
    }

    private IconOutput(Icon delegate) {
        this.delegate = delegate;
    }

    public int getId() {
        return delegate.getId();
    }

    public String getName() {
        return delegate.getName();
    }

    public int getGameVersionId() {
        return delegate.getGameVersion().getId();
    }

    public int getSize() {
        return delegate.getImageData().length;
    }

    public String getMimeType() {
        return delegate.getMimeType();
    }

    // Exception: URL for retrieving the icon's image data
    public String getUrl() {
        return "/api/icons?id=" + delegate.getId();
    }

    public List<String> getCategory() {
        return delegate.getCategory();
    }

}
