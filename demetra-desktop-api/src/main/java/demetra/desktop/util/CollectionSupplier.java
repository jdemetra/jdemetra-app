package demetra.desktop.util;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.openide.util.Lookup;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@FunctionalInterface
public interface CollectionSupplier<T> extends Supplier<Collection<? extends T>> {

    @NonNull
    default Stream<? extends T> stream() {
        return get().stream();
    }

    @NonNull
    static <X> CollectionSupplier<X> of(@NonNull Collection<X> items) {
        Objects.requireNonNull(items);
        Collection<X> result = Collections.unmodifiableCollection(items);
        return () -> result;
    }

    @NonNull
    static <X> CollectionSupplier<X> ofLookup(@NonNull Class<X> type) {
        Objects.requireNonNull(type);
        return ofLookup(type, Lookup.getDefault());
    }

    @NonNull
    static <X> CollectionSupplier<X> ofLookup(@NonNull Class<X> type, @NonNull Lookup lookup) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(lookup);
        return () -> lookup.lookupAll(type);
    }
}
