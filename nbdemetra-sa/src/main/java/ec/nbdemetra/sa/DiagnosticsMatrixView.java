/*
 * Copyright 2017 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
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
package ec.nbdemetra.sa;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import ec.nbdemetra.ui.DemetraUI;
import ec.satoolkit.algorithm.implementation.X13ProcessingFactory;
import ec.satoolkit.x11.Mstatistics;
import ec.tss.formatters.TableFormatter;
import ec.tss.sa.diagnostics.CoherenceDiagnosticsFactory;
import ec.tss.sa.diagnostics.ResidualsDiagnosticsFactory;
import ec.tss.sa.diagnostics.SpectralDiagnosticsFactory;
import ec.tstoolkit.algorithm.CompositeResults;
import ec.tstoolkit.algorithm.IProcResults;
import ec.tstoolkit.data.Table;
import ec.tstoolkit.information.InformationSet;
import ec.util.grid.swing.AbstractGridModel;
import ec.util.grid.swing.JGrid;
import ec.util.grid.swing.XTable;
import ec.util.grid.swing.ext.TableGridCommand;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * Matrix displaying values of selected diagnostics components. Components can
 * be selected from the options panel.
 *
 * @author Mats Maggi
 */
public class DiagnosticsMatrixView extends JPanel {

    private final Map<String, CompositeResults> results;
    private final JTabbedPane tabbedPane;
    private final JGrid resMatrix, calMatrix, armaMatrix, outMatrix, testMatrix, customMatrix;
    private final DemetraUI demetraUI;

    private List<String> selectedDiagnostics;

    public DiagnosticsMatrixView(Map<String, CompositeResults> information) {
        setLayout(new BorderLayout());
        this.results = information;
        this.demetraUI = DemetraUI.getDefault();
        this.selectedDiagnostics = demetraUI.getSelectedDiagFields();

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Main", resMatrix = createMatrix());
        tabbedPane.addTab("Calendar", calMatrix = createMatrix());
        tabbedPane.addTab("Outliers", outMatrix = createMatrix());
        tabbedPane.addTab("Arma", armaMatrix = createMatrix());
        tabbedPane.addTab("Tests", testMatrix = createMatrix());
        tabbedPane.addTab("Custom", customMatrix = createMatrix());

        demetraUI.addPropertyChangeListener(DemetraUI.SELECTED_DIAG_FIELDS_PROPERTY, evt -> {
            selectedDiagnostics = demetraUI.getSelectedDiagFields();
            customMatrix.setModel(new TableModelAdapter(createTableModel(selectedDiagnostics, selectedDiagnostics)));
        });

        customMatrix.setNoDataRenderer(new XTable.DefaultNoDataRenderer("Please select diagnostics components in the options"));
        calMatrix.setNoDataRenderer(new XTable.DefaultNoDataRenderer("No significant calendar effect found for any specification"));
        outMatrix.setNoDataRenderer(new XTable.DefaultNoDataRenderer("No outlier found for any specification"));
        
        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                List<Callable<Void>> tasks = updateMatrix();
                int nThread = demetraUI.getBatchPoolSize().intValue();
                int priority = demetraUI.getBatchPriority().intValue();

                ExecutorService executorService = Executors.newFixedThreadPool(nThread, new ThreadFactoryBuilder().setDaemon(true).setPriority(priority).build());
                return executorService.invokeAll(tasks);
            }
        };

        add(tabbedPane, BorderLayout.CENTER);
        sw.execute();
    }

    private static JGrid createMatrix() {
        final JGrid result = new JGrid();
        result.setDefaultRenderer(Object.class, new TableCellRenderer() {
            final TableCellRenderer delegate = result.getDefaultRenderer(Object.class);

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component result;
                if (value != null && value.getClass().isArray()) {
                    result = delegate.getTableCellRendererComponent(table, Arrays.toString((Object[]) value), isSelected, hasFocus, row, column);
                } else {
                    result = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }

                if (result instanceof JLabel) {
                    ((JLabel) result).setHorizontalAlignment(JLabel.CENTER);
                }
                return result;
            }
        });

        JMenu menu = new JMenu();
        menu.add(TableGridCommand.copyAll(true, true).toAction(result)).setText("Copy");
        result.setComponentPopupMenu(menu.getPopupMenu());

        return result;
    }

    private List<Callable<Void>> updateMatrix() {
        List<Callable<Void>> tasks = new ArrayList<>();
        tasks.add(() -> {
            resMatrix.setModel(new TableModelAdapter(createTableModel(Arrays.asList(MAIN_TITLE), Arrays.asList(MAIN))));
            return null;
        });
        tasks.add(() -> {
            calMatrix.setModel(new TableModelAdapter(createTableModel(Arrays.asList(CALENDAR_TITLE), Arrays.asList(CALENDAR))));
            return null;
        });
        tasks.add(() -> {
            armaMatrix.setModel(new TableModelAdapter(createTableModel(Arrays.asList(ARMA_TITLE), Arrays.asList(ARMA))));
            return null;
        });
        tasks.add(() -> {
            outMatrix.setModel(new TableModelAdapter(createTableModel(Arrays.asList(OUTLIERS_TITLE), Arrays.asList(OUTLIERS))));
            return null;
        });
        tasks.add(() -> {
            testMatrix.setModel(new TableModelAdapter(createTableModel(Arrays.asList(TESTS_TITLE), Arrays.asList(TESTS))));
            return null;
        });
        tasks.add(() -> {
            customMatrix.setModel(new TableModelAdapter(createTableModel(selectedDiagnostics, selectedDiagnostics)));
            return null;
        });
        return tasks;
    }

    private TableModel createTableModel(List<String> titles, List<String> items) {
        DefaultTableModel result = new DefaultTableModel();
        List<String> names = new ArrayList<>();
        List<IProcResults> rslts = new ArrayList<>();

        for (Map.Entry<String, CompositeResults> entry : results.entrySet()) {
            if (entry.getValue().isSuccessful()) {
                rslts.add(entry.getValue());
                names.add(entry.getKey());
            }
        }

        TableFormatter formatter = new TableFormatter();
        Table<String> srslts = formatter.formatProcResults(rslts, items, true);
        int ncols = srslts.getColumnsCount();
        boolean[] ok = new boolean[ncols];
        int nused = 0;

        result.addColumn("Series");

        for (int idx = 0; idx < titles.size(); ++idx) {
            if (srslts.column(idx).isEmpty()) {
                ok[idx] = false;
            } else {
                ++nused;
                ok[idx] = true;
                result.addColumn(titles.get(idx));
            }
        }
        if (nused == 0) {
            return new DefaultTableModel();
        }

        for (int i = 0; i < names.size(); ++i) {
            String[] row = new String[nused + 1];
            row[0] = names.get(i);
            for (int j = 0, k = 0; j < ncols; ++j) {
                if (ok[j]) {
                    row[++k] = srslts.get(i, j);
                }
            }
            result.addRow(row);
        }

        return result;
    }

    private static final String[] MAIN = {"espan.n", "decomposition.seasonality", "adjust", "log", "arima.mean", "arima.p", "arima.d", "arima.q",
        "arima.bp", "arima.bd", "arima.bq", "likelihood.bicc", "residuals.ser", "residuals.lb", "decomposition.seasfilter", "decomposition.trendfilter"};
    private static final String[] MAIN_TITLE = {"N", "Seasonal", "Adjust", "Log", "Mean", "P", "D", "Q", "BP", "BD", "BQ", "BIC", "SE(res)", "Q-val", "Seas filter", "Trend filter"};
    private static final String[] CALENDAR = {"adjust", "regression.lp:2", "regression.td(1):2", "regression.td(2):2",
        "regression.td(3):2", "regression.td(4):2", "regression.td(5):2", "regression.td(6):2", "regression.td(7):2", "regression.easter:2"};
    private static final String[] CALENDAR_TITLE = {"Adjust", "Leap Year", "T-Stat", "TD(1)", "T-Stat", "TD(2)", "T-Stat", "TD(3)",
        "T-Stat", "TD(4)", "T-Stat", "TD(5)", "T-Stat", "TD(6)", "T-Stat", "TD(7)", "T-Stat", "Easter", "T-Stat"};
    private static final String[] ARMA = {"arima.phi(1):2", "arima.phi(2):2", "arima.phi(3):2", "arima.phi(4):2", "arima.th(1):2", "arima.th(2):2", "arima.th(3):2", "arima.th(4):2", "arima.bphi(1):2", "arima.bth(1):2"};
    private static final String[] ARMA_TITLE = {"phi(1)", "t-stat", "phi(2)", "t-stat", "phi(3)", "t-stat", "phi(4)", "t-stat",
        "th(1)", "t-stat", "th(2)", "t-stat", "th(3)", "t-stat", "th(4)", "t-stat",
        "bphi(1)", "t-stat", "bth(1)", "t-stat"};
    private static final String[] OUTLIERS = {
        "regression.out(1)", "regression.out(2)", "regression.out(3)", "regression.out(4)",
        "regression.out(5)", "regression.out(6)", "regression.out(7)", "regression.out(8)", "regression.out(9)", "regression.out(10)"};
    private static final String[] OUTLIERS_TITLE = {
        "OUT(1)", "OUT(2)", "OUT(3)", "OUT(4)", "OUT(5)", "OUT(6)", "OUT(7)", "OUT(8)", "OUT(9)", "OUT(10)"};
    private static final String[] TESTS_TITLE = new String[]{
        "Skewness", "Kurtosis", "Ljung-Box", "LB. on Seas", "LB on sq.",
        "Bias", "TD peak", "Seas peak", "Visual TD peak", "Visual Seas peak",
        "M1", "M2", "M3", "M4", "M5", "M6", "M7", "M8", "M9", "M10", "M11",
        "Q", "Q-M2"
    };

    private static final String[] TESTS = new String[]{
        "residuals.skewness:-3", "residuals.kurtosis:-3", "residuals.lb:-3", "residuals.seaslb:-3", "residuals.lb2:-3",
        InformationSet.item(CoherenceDiagnosticsFactory.NAME, CoherenceDiagnosticsFactory.BIAS) + ":-2",
        InformationSet.item(ResidualsDiagnosticsFactory.NAME, ResidualsDiagnosticsFactory.TD_PEAK) + ":-2",
        InformationSet.item(ResidualsDiagnosticsFactory.NAME, ResidualsDiagnosticsFactory.S_PEAK) + ":-2",
        InformationSet.item(SpectralDiagnosticsFactory.NAME, SpectralDiagnosticsFactory.TD),
        InformationSet.item(SpectralDiagnosticsFactory.NAME, SpectralDiagnosticsFactory.SEAS),
        InformationSet.item(X13ProcessingFactory.MSTATISTICS, Mstatistics.M1),
        InformationSet.item(X13ProcessingFactory.MSTATISTICS, Mstatistics.M2),
        InformationSet.item(X13ProcessingFactory.MSTATISTICS, Mstatistics.M3),
        InformationSet.item(X13ProcessingFactory.MSTATISTICS, Mstatistics.M4),
        InformationSet.item(X13ProcessingFactory.MSTATISTICS, Mstatistics.M5),
        InformationSet.item(X13ProcessingFactory.MSTATISTICS, Mstatistics.M6),
        InformationSet.item(X13ProcessingFactory.MSTATISTICS, Mstatistics.M7),
        InformationSet.item(X13ProcessingFactory.MSTATISTICS, Mstatistics.M8),
        InformationSet.item(X13ProcessingFactory.MSTATISTICS, Mstatistics.M9),
        InformationSet.item(X13ProcessingFactory.MSTATISTICS, Mstatistics.M10),
        InformationSet.item(X13ProcessingFactory.MSTATISTICS, Mstatistics.M11),
        InformationSet.item(X13ProcessingFactory.MSTATISTICS, Mstatistics.Q),
        InformationSet.item(X13ProcessingFactory.MSTATISTICS, Mstatistics.Q2)};

    private static final class TableModelAdapter extends AbstractGridModel {

        private final TableModel source;

        public TableModelAdapter(TableModel source) {
            this.source = source;
        }

        @Override
        public int getRowCount() {
            return source.getRowCount();
        }

        @Override
        public int getColumnCount() {
            return Math.max(source.getColumnCount() - 1, 0);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return source.getValueAt(rowIndex, columnIndex + 1);
        }

        @Override
        public String getRowName(int rowIndex) {
            return (String) source.getValueAt(rowIndex, 0);
        }

        @Override
        public String getColumnName(int column) {
            return source.getColumnName(column + 1);
        }
    }
}
