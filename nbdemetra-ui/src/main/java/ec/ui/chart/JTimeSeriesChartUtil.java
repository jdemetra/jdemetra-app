/*
 * Copyright 2015 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.ui.chart;

import ec.nbdemetra.ui.ThemeSupport;
import ec.tss.tsproviders.utils.DataFormat;
import ec.ui.interfaces.ITsPrinter;
import ec.util.chart.ColorScheme;
import ec.util.chart.SeriesFunction;
import ec.util.chart.swing.JTimeSeriesChart;
import static ec.util.chart.swing.JTimeSeriesChartCommand.copyImage;
import static ec.util.chart.swing.JTimeSeriesChartCommand.printImage;
import static ec.util.chart.swing.JTimeSeriesChartCommand.saveImage;
import ec.util.chart.swing.SwingColorSchemeSupport;
import java.awt.Color;
import javax.annotation.Nonnull;
import javax.swing.JMenu;

/**
 *
 * @author Philippe Charles
 */
public final class JTimeSeriesChartUtil {

    private JTimeSeriesChartUtil() {
        // static class
    }

    @Nonnull
    public static ThemeSupport newThemeSupport(@Nonnull JTimeSeriesChart chart) {
        return new ThemeSupportImpl(chart);
    }

    public static void setColorScheme(@Nonnull JTimeSeriesChart chart, @Nonnull ColorScheme colorScheme) {
        chart.setColorSchemeSupport(SwingColorSchemeSupport.from(colorScheme));
    }

    public static void setDataFormat(@Nonnull JTimeSeriesChart chart, @Nonnull DataFormat dataFormat) {
        try {
            chart.setPeriodFormat(dataFormat.newDateFormat());
        } catch (IllegalArgumentException ex) {
            // do nothing?
        }
        try {
            chart.setValueFormat(dataFormat.newNumberFormat());
        } catch (IllegalArgumentException ex) {
            // do nothing?
        }
    }

    public static void setSeriesColorist(final @Nonnull JTimeSeriesChart chart, final @Nonnull SeriesFunction<ColorScheme.KnownColor> colorist) {
        chart.setSeriesColorist(new SeriesFunction<Color>() {
            @Override
            public Color apply(int series) {
                ColorScheme.KnownColor color = colorist.apply(series);
                return color != null ? chart.getColorSchemeSupport().getLineColor(color) : null;
            }
        });
    }

    @Nonnull
    public static JMenu newExportImageMenu(@Nonnull JTimeSeriesChart chart) {
        JMenu result = new JMenu("Export image to");
        result.add(printImage().toAction(chart)).setText("Printer...");
        result.add(copyImage().toAction(chart)).setText("Clipboard");
        result.add(saveImage().toAction(chart)).setText("File...");
        return result;
    }

    @Nonnull
    public static ITsPrinter newTsPrinter(@Nonnull JTimeSeriesChart chart) {
        return new TsPrinterImpl(chart);
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    private static final class ThemeSupportImpl extends ThemeSupport {

        private final JTimeSeriesChart chart;

        public ThemeSupportImpl(JTimeSeriesChart chart) {
            this.chart = chart;
        }

        @Override
        protected void colorSchemeChanged() {
            setColorScheme(chart, getColorScheme());
        }

        @Override
        protected void dataFormatChanged() {
            setDataFormat(chart, getDataFormat());
        }

        @Override
        public void register() {
            colorSchemeChanged();
            dataFormatChanged();
            super.register();
        }
    }

    private static final class TsPrinterImpl implements ITsPrinter {

        private final JTimeSeriesChart chart;

        public TsPrinterImpl(JTimeSeriesChart chart) {
            this.chart = chart;
        }

        @Override
        public boolean printPreview() {
            chart.printImage();
            return true;
        }

        @Override
        public boolean print() {
            return printPreview();
        }
    }
    //</editor-fold>
}
