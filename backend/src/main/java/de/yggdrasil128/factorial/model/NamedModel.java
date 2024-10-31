package de.yggdrasil128.factorial.model;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public interface NamedModel {

    static List<Object> resolve(Collection<? extends NamedModel> relations, External destination) {
        return resolve(relations, destination, NamedModel::resolve);
    }

    static <T> List<Object> resolve(Collection<? extends T> relations, External destination,
                                    BiFunction<? super T, ? super External, Object> downstream) {
        return relations.stream().map(relation -> downstream.apply(relation, destination)).filter(Objects::nonNull)
                .toList();
    }

    static Object resolve(NamedModel relation, External destination) {
        if (null == relation) {
            return resolveNull(destination);
        }
        switch (destination) {
        case FRONTEND:
            return relation.getId();
        case SAVE_FILE:
            return relation.getName();
        default:
            throw new AssertionError(
                    "unexpected enum constant: " + External.class.getCanonicalName() + '.' + destination.name());
        }
    }

    static Object resolveNull(External destination) {
        switch (destination) {
        case FRONTEND:
            return 0;
        case SAVE_FILE:
            return null;
        default:
            throw new AssertionError(
                    "unexpected enum constant: " + External.class.getCanonicalName() + '.' + destination.name());
        }
    }

    int getId();

    String getName();

}
