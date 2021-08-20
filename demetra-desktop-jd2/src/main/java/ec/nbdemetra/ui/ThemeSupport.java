/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui;

import demetra.bridge.TsConverter;
import demetra.tsprovider.util.ObsFormat;
import demetra.ui.DemetraOptions;
import demetra.ui.components.parts.HasColorScheme;
import demetra.ui.components.parts.HasObsFormat;
import ec.tss.tsproviders.utils.DataFormat;
import ec.util.chart.ColorScheme;
import ec.util.chart.ColorScheme.KnownColor;
import ec.util.chart.swing.SwingColorSchemeSupport;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openide.util.WeakListeners;

/**
 *
 * @author Philippe Charles
 */
public final class ThemeSupport extends SwingColorSchemeSupport {

    @Deprecated
    public static ThemeSupport registered() {
        ThemeSupport result = new ThemeSupport();
        result.register();
        return result;
    }

    private final DemetraOptions demetraUI;
    private final PropertyChangeListener listener;
    private final Map<Integer, KnownColor> forcedLineColors;

    private HasObsFormat obsFormatProperty;
    private Runnable onObsFormatChange;

    private HasColorScheme colorSchemeProperty;
    private Runnable onColorSchemeChange;

    public ThemeSupport() {
        this.demetraUI = DemetraOptions.getDefault();
        this.listener = new DemetraUIListener();
        this.forcedLineColors = new HashMap<>();
    }

    public void setObsFormatListener(HasObsFormat property, Runnable listener) {
        this.obsFormatProperty = property;
        this.onObsFormatChange = listener;
    }

    public void setColorSchemeListener(HasColorScheme property, Runnable listener) {
        this.colorSchemeProperty = property;
        this.onColorSchemeChange = listener;
    }

    @Override
    public ColorScheme getColorScheme() {
        ColorScheme result = colorSchemeProperty != null ? colorSchemeProperty.getColorScheme() : null;
        return result != null ? result : demetraUI.getColorScheme();
    }

    @NonNull
    public DataFormat getDataFormat() {
        ObsFormat result = obsFormatProperty != null ? obsFormatProperty.getObsFormat() : null;
        return TsConverter.fromObsFormat(result != null ? result : demetraUI.getObsFormat());
    }

    @Override
    public Color getLineColor(int index) {
        KnownColor kc = forcedLineColors.get(index);
        return kc != null ? super.getLineColor(kc) : super.getLineColor(index);
    }

    public void register() {
        demetraUI.addPropertyChangeListener(WeakListeners.propertyChange(listener, this));
    }

    private final class DemetraUIListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            switch (evt.getPropertyName()) {
                case DemetraOptions.COLOR_SCHEME_NAME_PROPERTY:
                    if (colorSchemeProperty != null && colorSchemeProperty.getColorScheme() == null) {
                        onColorSchemeChange.run();
                    }
                    break;
                case DemetraOptions.OBS_FORMAT_PROPERTY:
                    if (obsFormatProperty != null && obsFormatProperty.getObsFormat() == null) {
                        onObsFormatChange.run();
                    }
                    break;
            }
        }
    }
}
