package de.yggdrasil128.factorial.model;

public interface NamedModel {

    static Object resolve(NamedModel relation, RelationRepresentation strategy) {
        if (null == relation) {
            return null;
        }
        switch (strategy) {
        case ID:
            return relation.getId();
        case NAME:
            return relation.getName();
        default:
            throw new AssertionError("unexpected enum constant: " + RelationRepresentation.class.getCanonicalName()
                    + '.' + strategy.name());
        }
    }

    int getId();

    String getName();

}
