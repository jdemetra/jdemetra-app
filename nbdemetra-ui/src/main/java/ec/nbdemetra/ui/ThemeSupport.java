/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui;

import demetra.ui.components.HasColorScheme;
import demetra.ui.components.HasObsFormat;
import ec.tss.tsproviders.utils.DataFormat;
import ec.util.chart.ColorScheme;
import ec.util.chart.ColorScheme.KnownColor;
import ec.util.chart.swing.SwingColorSchemeSupport;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
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

    private final DemetraUI demetraUI;
    private final PropertyChangeListener listener;
    private final Map<Integer, KnownColor> forcedLineColors;

    private HasObsFormat obsFormatProperty;
    private Runnable onObsFormatChange;

    private HasColorScheme colorSchemeProperty;
    private Runnable onColorSchemeChange;

    public ThemeSupport() {
        this.demetraUI = DemetraUI.getDefault();
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

    @Nonnull
    public DataFormat getDataFormat() {
        DataFormat result = obsFormatProperty != null ? obsFormatProperty.getDataFormat() : null;
        return result != null ? result : demetraUI.getDataFormat();
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
                case DemetraUI.COLOR_SCHEME_NAME_PROPERTY:
                    if (colorSchemeProperty != null && colorSchemeProperty.getColorScheme() == null) {
                        onColorSchemeChange.run();
                    }
                    break;
                case DemetraUI.DATA_FORMAT_PROPERTY:
                    if (obsFormatProperty != null && obsFormatProperty.getDataFormat() == null) {
                        onObsFormatChange.run();
                    }
                    break;
            }
        }
    }
}
