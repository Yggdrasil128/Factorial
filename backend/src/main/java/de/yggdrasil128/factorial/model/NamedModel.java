package de.yggdrasil128.factorial.model;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public interface NamedModel {

    static List<Object> resolve(Collection<? extends NamedModel> relations, RelationRepresentation strategy) {
        return resolve(relations, strategy, NamedModel::resolve);
    }

    static <T> List<Object> resolve(Collection<? extends T> relations, RelationRepresentation strategy,
                                    BiFunction<? super T, ? super RelationRepresentation, Object> downstream) {
        return relations.stream().map(relation -> downstream.apply(relation, strategy)).filter(Objects::nonNull)
                .toList();
    }

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
