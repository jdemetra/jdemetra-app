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
package internal.ui.components;

import com.google.common.base.Strings;
import demetra.bridge.TsConverter;
import demetra.ui.components.parts.HasTsCollection;
import demetra.ui.components.parts.HasTsCollection.TsUpdateMode;
import demetra.ui.components.PrintableWithPreview;
import static demetra.ui.components.ResetableZoom.RESET_ZOOM_ACTION;
import ec.nbdemetra.ui.DemetraUI;
import ec.tss.Ts;
import ec.tss.TsStatus;
import ec.util.chart.swing.JTimeSeriesChart;
import ec.util.chart.swing.JTimeSeriesChartCommand;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.JCommand;
import java.util.Arrays;
import org.checkerframework.checker.nullness.qual.NonNull;
import javax.swing.ActionMap;
import javax.swing.JMenuItem;

/**
 *
 * @author Philippe Charles
 */
@lombok.experimental.UtilityClass
public class InternalComponents {

    public static JMenuItem newCopyImageMenu(JTimeSeriesChart chart, DemetraUI demetraUI) {
        JMenuItem result = new JMenuItem(JTimeSeriesChartCommand.copyImage().toAction(chart));
        result.setText("Clipboard");
        result.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_CLIPBOARD));
        return result;
    }

    public static JMenuItem newSaveImageMenu(JTimeSeriesChart chart, DemetraUI demetraUI) {
        JMenuItem result = new JMenuItem(JTimeSeriesChartCommand.saveImage().toAction(chart));
        result.setText("File...");
        result.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_PICTURE_O));
        return result;
    }

    private static final class PrintPreviewCommand extends JCommand<PrintableWithPreview> {

        public static final PrintPreviewCommand INSTANCE = new PrintPreviewCommand();

        @Override
        public void execute(PrintableWithPreview component) throws Exception {
            component.printWithPreview();
        }
    }

    public static JMenuItem newResetZoomMenu(ActionMap am, DemetraUI demetraUI) {
        JMenuItem result = new JMenuItem(am.get(RESET_ZOOM_ACTION));
        result.setText("Show all");
        result.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_EYE));
        return result;
    }

    @NonNull
    public static JCommand<PrintableWithPreview> printPreview() {
        return PrintPreviewCommand.INSTANCE;
    }

    @NonNull
    public static JMenuItem menuItemOf(@NonNull PrintableWithPreview component) {
        JMenuItem result = new JMenuItem(printPreview().toAction(component));
        result.setIcon(DemetraUI.getDefault().getPopupMenuIcon(FontAwesome.FA_PRINT));
        result.setText("Printer...");
        return result;
    }

    public static String getNoDataMessage(HasTsCollection o) {
        return getNoDataMessage(o.getTsCollection(), o.getTsUpdateMode());
    }

    private static String getNoDataMessage(demetra.timeseries.TsCollection input, TsUpdateMode updateMode) {
        switch (input.getData().size()) {
            case 0:
                return updateMode.isReadOnly() ? "No data" : "Drop data here";
            case 1:
                Ts single = TsConverter.fromTs(input.getData().get(0));
                switch (single.hasData()) {
                    case Invalid:
                        String cause = single.getInvalidDataCause();
                        return !Strings.isNullOrEmpty(cause) ? cause : "Invalid data without cause";
                    case Undefined:
                        return "Loading" + System.lineSeparator() + single.getName();
                    case Valid:
                        return "No obs";
                }
            default:
                int[] counter = new int[TsStatus.values().length];
                Arrays.fill(counter, 0);
                input.getData().stream().map(TsConverter::fromTs).forEach(o -> counter[o.hasData().ordinal()]++);
                if (counter[TsStatus.Invalid.ordinal()] == input.getData().size()) {
                    return "Invalid data";
                }
                if (counter[TsStatus.Undefined.ordinal()] > 1) {
                    return "Loading " + counter[TsStatus.Undefined.ordinal()] + " series";
                }
                return "Nothing to display";
        }
    }
}
