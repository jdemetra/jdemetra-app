/*
 * Copyright 2018 National Bank of Belgium
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
package demetra.desktop.core.components;

import demetra.tsprovider.util.ObsFormat;
import demetra.ui.DemetraOptions;
import demetra.ui.IconManager;
import demetra.ui.actions.PrintableWithPreview;
import demetra.ui.components.parts.HasTsCollection;
import demetra.ui.components.parts.HasTsCollection.TsUpdateMode;
import ec.util.chart.swing.JTimeSeriesChart;
import ec.util.chart.swing.JTimeSeriesChartCommand;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.JCommand;
import nbbrd.io.text.Formatter;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.swing.*;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Date;

import static demetra.ui.actions.ResetableZoom.RESET_ZOOM_ACTION;

/**
 * @author Philippe Charles
 */
@lombok.experimental.UtilityClass
public class InternalComponents {

    public static JMenuItem newCopyImageMenu(JTimeSeriesChart chart) {
        JMenuItem result = new JMenuItem(JTimeSeriesChartCommand.copyImage().toAction(chart));
        result.setText("Clipboard");
        result.setIcon(IconManager.getDefault().getPopupMenuIcon(FontAwesome.FA_CLIPBOARD));
        return result;
    }

    public static JMenuItem newSaveImageMenu(JTimeSeriesChart chart) {
        JMenuItem result = new JMenuItem(JTimeSeriesChartCommand.saveImage().toAction(chart));
        result.setText("File...");
        result.setIcon(IconManager.getDefault().getPopupMenuIcon(FontAwesome.FA_PICTURE_O));
        return result;
    }

    private static final class PrintPreviewCommand extends JCommand<PrintableWithPreview> {

        public static final PrintPreviewCommand INSTANCE = new PrintPreviewCommand();

        @Override
        public void execute(PrintableWithPreview component) throws Exception {
            component.printWithPreview();
        }
    }

    public static JMenuItem newResetZoomMenu(ActionMap am) {
        JMenuItem result = new JMenuItem(am.get(RESET_ZOOM_ACTION));
        result.setText("Show all");
        result.setIcon(IconManager.getDefault().getPopupMenuIcon(FontAwesome.FA_EYE));
        return result;
    }

    @NonNull
    public static JCommand<PrintableWithPreview> printPreview() {
        return PrintPreviewCommand.INSTANCE;
    }

    @NonNull
    public static JMenuItem menuItemOf(@NonNull PrintableWithPreview component) {
        JMenuItem result = new JMenuItem(printPreview().toAction(component));
        result.setIcon(IconManager.getDefault().getPopupMenuIcon(FontAwesome.FA_PRINT));
        result.setText("Printer...");
        return result;
    }

    public static String getNoDataMessage(HasTsCollection o) {
        return getNoDataMessage(o.getTsCollection(), o.getTsUpdateMode());
    }

    private static String getNoDataMessage(demetra.timeseries.TsCollection input, TsUpdateMode updateMode) {
        switch (input.size()) {
            case 0:
                return updateMode.isReadOnly() ? "No data" : "Drop data here";
            case 1:
                demetra.timeseries.Ts single = input.get(0);
                if (single.getType().hasData()) {
                    String cause = single.getData().getEmptyCause();
                    return cause != null && !cause.isEmpty() ? cause : "No obs";
                } else {
                    return "Loading" + System.lineSeparator() + single.getName();
                }
            default:
                long count = input.stream().filter(ts -> !ts.getType().hasData()).count();
                if (count > 1) {
                    return "Loading " + count + " series";
                }
                return "Nothing to display";
        }
    }

    static class DateFormatAdapter extends DateFormat {

        final Formatter<Date> dateFormatter;

        public DateFormatAdapter(ObsFormat obsFormat) {
            dateFormatter = obsFormat.calendarFormatter();
        }

        @Override
        public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
            return toAppendTo.append(dateFormatter.format(date));
        }

        @Override
        public Date parse(String source, ParsePosition pos) {
            return null;
        }
    }

    static class NumberFormatAdapter extends NumberFormat {

        final Formatter<Number> numberFormatter;

        public NumberFormatAdapter(ObsFormat obsFormat) {
            numberFormatter = obsFormat.numberFormatter();
        }

        @Override
        public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
            return toAppendTo.append(numberFormatter.format(number));
        }

        @Override
        public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
            return toAppendTo.append(numberFormatter.format(number));
        }

        @Override
        public Number parse(String source, ParsePosition parsePosition) {
            return null;
        }
    }
}
