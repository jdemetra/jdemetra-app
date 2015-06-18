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
import ec.util.chart.swing.JTimeSeriesChart;
import ec.util.chart.swing.SwingColorSchemeSupport;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
        if (!Objects.equal(this.localColorScheme, localColorScheme)) {
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
        if (!Objects.equal(this.localDataFormat, localDataFormat)) {
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

    @Nonnull
    public static ThemeSupport create(@Nonnull JTimeSeriesChart chart) {
        return new ThemeSupportImpl(chart);
    }

    private static final class ThemeSupportImpl extends ThemeSupport {

        private final JTimeSeriesChart chart;

        public ThemeSupportImpl(JTimeSeriesChart chart) {
            this.chart = chart;
        }

        @Override
        protected void colorSchemeChanged() {
            chart.setColorSchemeSupport(SwingColorSchemeSupport.from(getColorScheme()));
        }

        @Override
        protected void dataFormatChanged() {
            DataFormat df = getDataFormat();
            try {
                chart.setPeriodFormat(df.newDateFormat());
            } catch (IllegalArgumentException ex) {
                // do nothing?
            }
            try {
                chart.setValueFormat(df.newNumberFormat());
            } catch (IllegalArgumentException ex) {
                // do nothing?
            }
        }

        @Override
        public void register() {
            colorSchemeChanged();
            dataFormatChanged();
            super.register();
        }
    }
}
