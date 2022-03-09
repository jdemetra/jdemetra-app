package internal.favicon;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface FaviconSupplier {
    
    @NonNull
    String getName();

    @Nullable
    Image getFaviconOrNull(@NonNull URL url) throws IOException;
}
