package demetra.desktop.util;

import java.awt.Image;
import java.util.function.Supplier;
import lombok.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@FunctionalInterface
public interface IconFactory {

    @Nullable
    Image getIcon(int type, boolean opened);
    
    @NonNull
    static IconFactory onNull() {
        return (type, opened) -> null;
    }
    
    @NonNull
    static IconFactory onSupplier(Supplier<Image> supplier) {
        return (type, opened) -> supplier.get();
    }
}
