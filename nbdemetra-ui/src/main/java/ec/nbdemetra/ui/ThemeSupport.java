/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui;

import ec.tss.tsproviders.utils.DataFormat;
import ec.ui.interfaces.IDisposable;
import ec.util.chart.ColorScheme;
import ec.util.chart.ColorScheme.KnownColor;
import ec.util.chart.swing.SwingColorSchemeSupport;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.openide.util.WeakListeners;

/**
 *
 * @author Philippe Charles
 */
public abstract class ThemeSupport extends SwingColorSchemeSupport implements IDisposable {

    private final DemetraUI demetraUI;
    private final PropertyChangeListener listener;
    private final Map<Integer, KnownColor> forcedLineColors;
    private ColorScheme localColorScheme;
    private DataFormat localDataFormat;

    public ThemeSupport() {
        this.demetraUI = DemetraUI.getDefault();
        this.listener = new DemetraUIListener();
        this.forcedLineColors = new HashMap<>();
        this.localColorScheme = null;
    }

    protected void colorSchemeChanged() {
        // does nothing by default
    }

    protected void dataFormatChanged() {
        // does nothing by default
    }

    public void setLocalColorScheme(@Nullable ColorScheme localColorScheme) {
        if (!Objects.equals(this.localColorScheme, localColorScheme)) {
            this.localColorScheme = localColorScheme;
            colorSchemeChanged();
        }
    }

    @Nullable
    public ColorScheme getLocalColorScheme() {
        return localColorScheme;
    }

    @Override
    public ColorScheme getColorScheme() {
        return localColorScheme != null ? localColorScheme : demetraUI.getColorScheme();
    }

    @Nullable
    public DataFormat getLocalDataFormat() {
        return localDataFormat;
    }

    public void setLocalDataFormat(@Nullable DataFormat localDataFormat) {
        if (!Objects.equals(this.localDataFormat, localDataFormat)) {
            this.localDataFormat = localDataFormat;
            dataFormatChanged();
        }
    }

    public DataFormat getDataFormat() {
        return localDataFormat != null ? localDataFormat : demetraUI.getDataFormat();
    }

    @Deprecated
    public void clearLineColors() {
        forcedLineColors.clear();
    }

    @Deprecated
    public void setLineColor(int index, KnownColor color) {
        forcedLineColors.put(index, color);
    }

    @Override
    public Color getLineColor(int index) {
        KnownColor kc = forcedLineColors.get(index);
        return kc != null ? super.getLineColor(kc) : super.getLineColor(index);
    }

    public void register() {
        demetraUI.addPropertyChangeListener(WeakListeners.propertyChange(listener, this));
    }

    @Deprecated
    @Override
    public void dispose() {
    }

    private final class DemetraUIListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            switch (evt.getPropertyName()) {
                case DemetraUI.COLOR_SCHEME_NAME_PROPERTY:
                    if (localColorScheme == null) {
                        ThemeSupport.this.colorSchemeChanged();
                    }
                    break;
                case DemetraUI.DATA_FORMAT_PROPERTY:
                    if (localDataFormat == null) {
                        ThemeSupport.this.dataFormatChanged();
                    }
                    break;
            }
        }
    }
}
