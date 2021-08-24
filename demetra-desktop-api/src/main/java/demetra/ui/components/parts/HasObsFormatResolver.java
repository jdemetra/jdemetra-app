package demetra.ui.components.parts;

import demetra.tsprovider.util.ObsFormat;
import demetra.ui.DemetraOptions;
import java.beans.PropertyChangeEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class HasObsFormatResolver {

    @lombok.NonNull
    private final HasObsFormat property;

    @lombok.NonNull
    private final Runnable onChange;

    public HasObsFormatResolver(HasObsFormat property, Runnable onChange) {
        this.property = property;
        this.onChange = onChange;
        DemetraOptions.getDefault().addWeakPropertyChangeListener(this::onPropertyChange);
    }

    @NonNull
    public ObsFormat resolve() {
        ObsFormat result = property.getObsFormat();
        return result != null ? result : DemetraOptions.getDefault().getObsFormat();
    }

    private void onPropertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(DemetraOptions.OBS_FORMAT_PROPERTY) && !property.hasObsFormat()) {
            onChange.run();
        }
    }
}
