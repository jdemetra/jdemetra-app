/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui;

import com.google.common.base.Objects;
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

/**
 *
 * @author Philippe Charles
 */
public abstract class ThemeSupport extends SwingColorSchemeSupport implements IDisposable {

    final PropertyChangeListener listener;
    final Map<Integer, KnownColor> forcedLineColors;
    ColorScheme localColorScheme;
    DataFormat localDataFormat;

    public ThemeSupport() {
        this.listener = new Listener();
        this.forcedLineColors = new HashMap<>();
        this.localColorScheme = null;
    }

    protected void colorSchemeChanged() {
        // does nothing by default
    }

    protected void dataFormatChanged() {
        // does nothing by default
    }

    public void setLocalColorScheme(ColorScheme localColorScheme) {
        if (!Objects.equal(this.localColorScheme, localColorScheme)) {
            this.localColorScheme = localColorScheme;
            colorSchemeChanged();
        }
    }

    public ColorScheme getLocalColorScheme() {
        return localColorScheme;
    }

    @Override
    public ColorScheme getColorScheme() {
        return localColorScheme != null ? localColorScheme : DemetraUI.getDefault().getColorScheme();
    }

    public DataFormat getLocalDataFormat() {
        return localDataFormat;
    }

    public void setLocalDataFormat(DataFormat localDataFormat) {
        if (!Objects.equal(this.localDataFormat, localDataFormat)) {
            this.localDataFormat = localDataFormat;
            dataFormatChanged();
        }
    }

    public DataFormat getDataFormat() {
        return localDataFormat != null ? localDataFormat : DemetraUI.getDefault().getDataFormat();
    }

    public void clearLineColors() {
        forcedLineColors.clear();
    }

    public void setLineColor(int index, KnownColor color) {
        forcedLineColors.put(index, color);
    }

    @Override
    public Color getLineColor(int index) {
        KnownColor kc = forcedLineColors.get(index);
        return kc != null ? super.getLineColor(kc) : super.getLineColor(index);
    }

    public void register() {
        DemetraUI.getDefault().addPropertyChangeListener(listener);
    }

    @Override
    public void dispose() {
        DemetraUI.getDefault().removePropertyChangeListener(listener);
    }

    private class Listener implements PropertyChangeListener {

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
