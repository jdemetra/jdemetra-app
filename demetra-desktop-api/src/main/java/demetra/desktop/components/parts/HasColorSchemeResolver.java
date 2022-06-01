package demetra.desktop.components.parts;

import demetra.desktop.ColorSchemeManager;
import demetra.desktop.DemetraUI;
import ec.util.chart.swing.SwingColorSchemeSupport;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public final class HasColorSchemeResolver {

    @lombok.NonNull
    private final HasColorScheme property;

    @lombok.NonNull
    private final Runnable onChange;

    @lombok.NonNull
    private final PropertyChangeListener listener;

    public HasColorSchemeResolver(HasColorScheme property, Runnable onChange) {
        this.property = property;
        this.onChange = onChange;
        this.listener = this::onPropertyChange;
        DemetraUI.get().addWeakPropertyChangeListener(listener);
    }

    @NonNull
    public SwingColorSchemeSupport resolve() {
        return ColorSchemeManager.get().getSupport(property.getColorScheme());
    }

    private void onPropertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(DemetraUI.COLOR_SCHEME_NAME_PROPERTY) && !property.hasColorScheme()) {
            onChange.run();
        }
    }
}
