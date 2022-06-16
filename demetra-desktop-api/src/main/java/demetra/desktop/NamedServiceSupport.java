package demetra.desktop;

import demetra.desktop.util.IconFactory;
import java.awt.Image;
import java.util.function.Supplier;
import nbbrd.design.LombokWorkaround;
import org.openide.nodes.Sheet;

@lombok.Builder
public final class NamedServiceSupport implements NamedService {

    @lombok.NonNull
    private final String name;

    @lombok.NonNull
    private final String displayName;

    @lombok.NonNull
    private final IconFactory icon;

    @lombok.NonNull
    private final Supplier<Sheet> sheet;

    @LombokWorkaround
    @lombok.NonNull
    public static Builder builder(@lombok.NonNull String name) {
        return new Builder()
                .name(name)
                .displayName(name)
                .icon(IconFactory.onNull())
                .sheet(Sheet::new);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public Image getIcon(int type, boolean opened) {
        return icon.getIcon(type, opened);
    }

    @Override
    public Sheet createSheet() {
        return sheet.get();
    }
}
