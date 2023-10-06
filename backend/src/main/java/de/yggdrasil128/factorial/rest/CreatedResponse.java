package de.yggdrasil128.factorial.rest;

public class CreatedResponse<T> {

    private final T created;

    public CreatedResponse(T created) {
        this.created = created;
    }

    public T getCreated() {
        return created;
    }

}
