package de.yggdrasil128.factorial.model;

import de.yggdrasil128.factorial.util.BooleanConsumer;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Provides a fluent API to obtain optional input fields from <i>standalone</i> objects.
 * <p>
 * Essentially, this is similar to {@link java.util.Optional}, but more aware of our specific model.
 */
public class OptionalInputField {

    public static <E> Generic<E> of(E value) {
        return new Generic<>(value);
    }

    public static class Generic<E> {

        private final E value;

        Generic(E value) {
            this.value = value;
        }

        public void apply(Consumer<? super E> sink) {
            if (null != value) {
                sink.accept(value);
            }
        }

    }

    public static OfInteger of(Integer value) {
        return new OfInteger(value);
    }

    public static class OfInteger {

        private final Integer value;

        OfInteger(Integer value) {
            this.value = value;
        }

        public void apply(IntConsumer sink) {
            if (null != value) {
                sink.accept(value.intValue());
            }
        }

    }

    public static OfString of(String value) {
        return new OfString(value);
    }

    public static class OfString {

        private final String value;

        OfString(String value) {
            this.value = value;
        }

        public void apply(Consumer<? super String> sink) {
            if (null != value) {
                sink.accept(value);
            }
        }

    }

    public static OfBoolean of(Boolean value) {
        return new OfBoolean(value);
    }

    public static class OfBoolean {

        private final Boolean value;

        OfBoolean(Boolean value) {
            this.value = value;
        }

        public void apply(BooleanConsumer sink) {
            if (null != value) {
                sink.accept(value.booleanValue());
            }
        }

    }

    public static <T> OfId<T> ofId(Object id, IntFunction<? extends T> fetcher) {
        return new OfId<>(id, fetcher);
    }

    public static class OfId<T> {

        private final Object id;
        private final IntFunction<? extends T> fetcher;

        OfId(Object id, IntFunction<? extends T> fetcher) {
            this.id = id;
            this.fetcher = fetcher;
        }

        public void apply(Consumer<? super T> sink) {
            if (null != id) {
                int intValue = ((Integer) id).intValue();
                sink.accept(0 >= intValue ? fetcher.apply(intValue) : null);
            }
        }

    }

    public static <T> OfList<T> of(List<T> values) {
        return new OfList<>(values);
    }

    public static class OfList<T> {

        private final List<T> values;

        OfList(List<T> value) {
            this.values = value;
        }

        @SuppressWarnings("unchecked")
        public <U> OptionalInputField.OfList<U> map(Function<? super T, ? extends U> mapper) {
            if (null == values) {
                return (OfList<U>) this;
            }
            // eclipse cannot infer the generic type properly when using Stream#toList()
            return new OptionalInputField.OfList<>(values.stream().map(mapper).collect(toList()));
        }

        public void apply(Consumer<? super List<T>> sink) {
            if (null != values) {
                sink.accept(values);
            }
        }

    }

    public static <T> OfIds<T> ofIds(Collection<?> ids, IntFunction<? extends T> fetcher) {
        return new OfIds<>(ids, fetcher);
    }

    public static class OfIds<T> {

        private final Collection<?> ids;
        private final IntFunction<? extends T> fetcher;

        OfIds(Collection<?> ids, IntFunction<? extends T> fetcher) {
            this.ids = ids;
            this.fetcher = fetcher;
        }

        public void applyList(Consumer<? super List<T>> sink) {
            if (null != ids) {
                // eclipse cannot infer the generic type properly when using Stream#toList()
                sink.accept(ids.stream().map(Integer.class::cast).map(fetcher::apply).collect(toList()));
            }
        }

        public void applySet(Consumer<? super Set<T>> sink) {
            if (null != ids) {
                sink.accept(ids.stream().map(Integer.class::cast).map(fetcher::apply).collect(toSet()));
            }
        }

    }

    private OptionalInputField() {
    }

}
