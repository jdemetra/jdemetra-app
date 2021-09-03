package demetra.desktop.components.parts;

import demetra.desktop.ColorSchemeManager;
import demetra.desktop.DemetraOptions;
import ec.util.chart.swing.SwingColorSchemeSupport;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.beans.PropertyChangeEvent;

public final class HasColorSchemeResolver {

    @lombok.NonNull
    private final HasColorScheme property;

    @lombok.NonNull
    private final Runnable onChange;

    public HasColorSchemeResolver(HasColorScheme property, Runnable onChange) {
        this.property = property;
        this.onChange = onChange;
        DemetraOptions.getDefault().addWeakPropertyChangeListener(this::onPropertyChange);
    }

    @NonNull
    public SwingColorSchemeSupport resolve() {
        return ColorSchemeManager.getDefault().getSupport(property.getColorScheme());
    }

    private void onPropertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(DemetraOptions.COLOR_SCHEME_NAME_PROPERTY) && !property.hasColorScheme()) {
            onChange.run();
        }
    }
}
