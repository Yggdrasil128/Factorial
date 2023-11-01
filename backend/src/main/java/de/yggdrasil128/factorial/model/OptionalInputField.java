package de.yggdrasil128.factorial.model;

import de.yggdrasil128.factorial.util.BooleanConsumer;
import de.yggdrasil128.factorial.util.OptionalBoolean;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class OptionalInputField {

    public static OptionalInputField.OfString of(String value) {
        return new OfString(value);
    }

    public static class OfString {
        private final String value;

        OfString(String value) {
            this.value = value;
        }

        public String get() {
            return value.isEmpty() ? null : value;
        }

        public void apply(Consumer<? super String> sink) {
            if (null != value) {
                sink.accept(get());
            }
        }

    }

    public static OptionalInputField.OfBoolean of(Boolean value) {
        return new OfBoolean(value);
    }

    public static class OfBoolean {

        private final Boolean value;

        OfBoolean(Boolean value) {
            this.value = value;
        }

        public OptionalBoolean get() {
            return OptionalBoolean.ofNullable(value);
        }

        public void apply(BooleanConsumer sink) {
            if (null != value) {
                sink.accept(value.booleanValue());
            }
        }

    }

    public static <T> OptionalInputField.OfId<T> ofId(int id, IntFunction<? extends T> fetcher) {
        return new OfId<>(id, fetcher);
    }

    public static class OfId<T> {

        private final int id;
        private final IntFunction<? extends T> fetcher;

        OfId(int id, IntFunction<? extends T> fetcher) {
            this.id = id;
            this.fetcher = fetcher;
        }

        public T get() {
            return 0 < id ? fetcher.apply(id) : null;
        }

        public void apply(Consumer<? super T> sink) {
            if (0 <= id) {
                sink.accept(get());
            }
        }

    }

    public static <T> OptionalInputField.OfList<T> of(List<T> values) {
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

        public List<T> get() {
            return null == values ? emptyList() : values;
        }

        public void apply(Consumer<? super List<T>> sink) {
            if (null != values) {
                sink.accept(values);
            }
        }

    }

    public static <T> OptionalInputField.OfIds<T> ofIds(Collection<Integer> ids, IntFunction<? extends T> fetcher) {
        return new OfIds<>(ids, fetcher);
    }

    public static class OfIds<T> {

        private final Collection<Integer> ids;
        private final IntFunction<? extends T> fetcher;

        OfIds(Collection<Integer> ids, IntFunction<? extends T> fetcher) {
            this.ids = ids;
            this.fetcher = fetcher;
        }

        public List<T> asList() {
            // eclipse cannot infer the generic type properly when using Stream#toList()
            return null == ids ? emptyList() : ids.stream().map(fetcher::apply).collect(toList());
        }

        public Set<T> asSet() {
            return null == ids ? emptySet() : ids.stream().map(fetcher::apply).collect(toSet());
        }

        public void applyList(Consumer<? super List<T>> sink) {
            if (null != ids) {
                sink.accept(asList());
            }
        }

        public void applySet(Consumer<? super Set<T>> sink) {
            if (null != ids) {
                sink.accept(asSet());
            }
        }

    }

    private OptionalInputField() {
    }

}
