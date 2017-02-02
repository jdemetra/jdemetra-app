/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa;

import com.google.common.collect.Maps;
import ec.nbdemetra.ui.DemetraUI;
import ec.nbdemetra.ui.NbComponents;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.satoolkit.algorithm.implementation.X13ProcessingFactory;
import ec.satoolkit.x11.Mstatistics;
import ec.tss.ITsIdentified;
import ec.tss.TsCollection;
import ec.tss.TsFactory;
import ec.tss.formatters.TableFormatter;
import ec.tss.sa.SaItem;
import ec.tss.sa.SaProcessing;
import ec.tss.sa.diagnostics.*;
import ec.tstoolkit.algorithm.AlgorithmDescriptor;
import ec.tstoolkit.algorithm.CompositeResults;
import ec.tstoolkit.algorithm.IProcResults;
import ec.tstoolkit.data.Table;
import ec.tstoolkit.information.InformationSet;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.ui.grid.JTsGrid;
import ec.ui.interfaces.ITsCollectionView;
import ec.util.grid.swing.AbstractGridModel;
import ec.util.grid.swing.JGrid;
import ec.util.grid.swing.ext.TableGridCommand;
import ec.util.list.swing.JLists;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;

/**
 *
 * @author Philippe Charles
 */
public class MatrixView extends AbstractSaProcessingTopComponent implements MultiViewElement {

    private static DecimalFormat df3 = new DecimalFormat("0.000");
    private final static int MAXBIAS = 1, SKEWNESS = 2, KURTOSIS = 3, LB = 4, LBS = 5, LB2 = 6, TD_PEAK = 7, S_PEAK = 8, TD_VPEAK = 9, S_VPEAK = 10, S_VAR = 11, I_VAR = 12, SI_CORR = 13, M_START = 11;
//    private final static String[] TESTS_TS = new String[]{"max bias", "skewness", "kurtosis", "ljung-box", "lb on seas.", "lb on sq.", "td peak", "seas peak", "visual td peak", "visual s. peak", "s_var", "i var", "s-i corr"};
//    private final static String[] TESTS_X12 = new String[]{"max bias", "skewness", "kurtosis", "ljung-box", "lb on seas.", "lb on sq.", "td peak", "seas peak", "visual td peak", "visual s. peak", "m1", "m2", "m3", "m4", "m5", "m6", "m7", "m8", "m9", "m10", "m11", "q", "q-m2"};
    // main components
    private final JComponent visualRepresentation;
    private final JToolBar toolBarRepresentation;
    // subcomponents
    private final JComboBox<Entry<Integer, AlgorithmDescriptor>> comboBox;
    private final JGrid resMatrix_, calMatrix_, armaMatrix_, outMatrix_, testMatrix_;
    private final JTsGrid customMatrix_;
    private final JTabbedPane matrixTabPane_;
    // data
    private List<SaItem> saItems;
    private List<String> selectedComponents;
    private final DemetraUI demetraUI;

    public MatrixView(WorkspaceItem<MultiProcessingDocument> doc, MultiProcessingController controller) {
        super(doc, controller);
        this.saItems = new ArrayList<>();
        this.demetraUI = DemetraUI.getDefault();
        this.comboBox = new JComboBox<>();
        this.selectedComponents = demetraUI.getSelectedSeriesFields();

        comboBox.setRenderer(JLists.cellRendererOf((label, value) -> {
            if (value != null) {
                label.setText(TaggedTreeNode.freqName(value.getKey()) + " > " + value.getValue().name);
            }
        }));
        comboBox.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED && event.getItem() != null) {
                Entry<Integer, AlgorithmDescriptor> item = (Entry<Integer, AlgorithmDescriptor>) event.getItem();
                updateMatrix(item.getValue(), item.getKey());
            } else {
                clearMatrices();
            }
        });

        matrixTabPane_ = new JTabbedPane();
        matrixTabPane_.addTab("Main", resMatrix_ = createMatrix());
        matrixTabPane_.addTab("Calendar", calMatrix_ = createMatrix());
        matrixTabPane_.addTab("Outliers", outMatrix_ = createMatrix());
        matrixTabPane_.addTab("Arma", armaMatrix_ = createMatrix());
        matrixTabPane_.addTab("Tests", testMatrix_ = createMatrix());
        matrixTabPane_.addTab("Custom", customMatrix_ = new JTsGrid());

        customMatrix_.setTsUpdateMode(ITsCollectionView.TsUpdateMode.None);

        toolBarRepresentation = NbComponents.newInnerToolbar();
        toolBarRepresentation.addSeparator();
        toolBarRepresentation.add(comboBox);

        visualRepresentation = matrixTabPane_;

        demetraUI.addPropertyChangeListener(DemetraUI.SELECTED_SERIES_FIELDS_PROPERTY, evt -> {
            selectedComponents = demetraUI.getSelectedSeriesFields();
            Entry<Integer, AlgorithmDescriptor> item = (Entry<Integer, AlgorithmDescriptor>) comboBox.getSelectedItem();
            customMatrix_.setTsCollection(createTsCollection(item.getValue(), item.getKey()));
        });

        updateData(Collections.<SaItem>emptyList());

        setLayout(new BorderLayout());
        add(toolBarRepresentation, BorderLayout.NORTH);
        add(visualRepresentation, BorderLayout.CENTER);
    }

    @Override
    protected void onSaProcessingStateChange() {
        super.onSaProcessingStateChange();
        SaProcessing current = getCurrentProcessing();
        if (getState().isFinished()) {
            updateData(current);
        } else {
            updateData(Collections.<SaItem>emptyList());
        }

    }

    private static JGrid createMatrix() {
        final JGrid result = new JGrid();
        result.setDefaultRenderer(Object.class, new TableCellRenderer() {

            final TableCellRenderer delegate = result.getDefaultRenderer(Object.class);

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component result = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
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

    // MultiViewElement >
    @Override
    public JComponent getVisualRepresentation() {
        return visualRepresentation;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return toolBarRepresentation;
    }

    @Override
    public void componentOpened() {
    }

    @Override
    public void componentClosed() {
    }

    @Override
    public void componentShowing() {
    }

    @Override
    public void componentHidden() {
    }

    @Override
    public void componentActivated() {
    }

    @Override
    public void componentDeactivated() {
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }
    // < MultiViewElement

    private static ComboBoxModel<Entry<Integer, AlgorithmDescriptor>> asComboBoxModel(Map<Integer, List<AlgorithmDescriptor>> m) {
        DefaultComboBoxModel<Entry<Integer, AlgorithmDescriptor>> result = new DefaultComboBoxModel<>();
        for (Map.Entry<Integer, List<AlgorithmDescriptor>> item : m.entrySet()) {
            for (AlgorithmDescriptor ritem : item.getValue()) {
                result.addElement(Maps.immutableEntry(item.getKey(), ritem));
            }
        }
        return result;
    }

    private void updateData(List<SaItem> saItems) {
        this.saItems = saItems;
        Map<Integer, List<AlgorithmDescriptor>> methods = getCurrentProcessing().methods();
        long count = methods.values().stream().flatMap(m -> m.stream()).count();
        comboBox.setVisible(count > 1);
        comboBox.setModel(asComboBoxModel(methods));
        comboBox.setSelectedIndex(-1);
        if (!methods.isEmpty()) {
            comboBox.setSelectedIndex(0);
        }
    }

    private void clearMatrices() {
        resMatrix_.setModel(null);
        calMatrix_.setModel(null);
        outMatrix_.setModel(null);
        armaMatrix_.setModel(null);
        testMatrix_.setModel(null);
        customMatrix_.getTsCollection().clear();
    }

    private void updateMatrix(AlgorithmDescriptor desc, int freq) {
        new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                resMatrix_.setModel(new TableModelAdapter(createTableModel(desc, freq, Arrays.asList(MAIN_TITLE), Arrays.asList(MAIN))));
                calMatrix_.setModel(new TableModelAdapter(createTableModel(desc, freq, Arrays.asList(CALENDAR_TITLE), Arrays.asList(CALENDAR))));
                armaMatrix_.setModel(new TableModelAdapter(createTableModel(desc, freq, Arrays.asList(ARMA_TITLE), Arrays.asList(ARMA))));
                outMatrix_.setModel(new TableModelAdapter(createTableModel(desc, freq, Arrays.asList(OUTLIERS_TITLE), Arrays.asList(OUTLIERS))));
                testMatrix_.setModel(new TableModelAdapter(createTableModel(desc, freq, Arrays.asList(TESTS_TITLE), Arrays.asList(TESTS))));
                customMatrix_.setTsCollection(createTsCollection(desc, freq));
                return true;
            }
        }.execute();
    }
    private static final String[] MAIN = {"espan.n", "decomposition.seasonality", "adjust", "log", "arima.mean", "arima.p", "arima.d", "arima.q", "arima.bp", "arima.bd", "arima.bq", "likelihood.bicc", "residuals.ser", "residuals.lb", "decomposition.seasfilter", "decomposition.trendfilter"};
    private static final String[] MAIN_TITLE = {"N", "Seasonal", "Adjust", "Log", "Mean", "P", "D", "Q", "BP", "BD", "BQ", "BIC", "SE(res)", "Q-val", "Seas filter", "Trend filter"};

    private TsCollection createTsCollection(AlgorithmDescriptor method, int freq) {
        TsCollection collection = TsFactory.instance.createTsCollection();
        for (SaItem sa : this.saItems) {
            if (sa.getEstimationMethod().equals(method) && sa.getTsData() != null && sa.getTsData().getFrequency().intValue() == freq) {
                CompositeResults cr = sa.process();
                for (String component : selectedComponents) {
                    TsData data = cr.getData(component, TsData.class);
                    if (data != null) {
                        collection.add(TsFactory.instance.createTs("[" + component + "] " + sa.getName(), sa.getMetaData(), data));
                    }
                }
            }
        }
        return collection;
    }

    private TableModel createTableModel(AlgorithmDescriptor method, int freq, List<String> titles, List<String> items) {
        DefaultTableModel rslt = new DefaultTableModel();
        List<ITsIdentified> names = new ArrayList<>();
        List<IProcResults> rslts = new ArrayList<>();
        for (SaItem sa : this.saItems) {
            if (sa.getEstimationMethod().equals(method) && sa.getTsData() != null && sa.getTsData().getFrequency().intValue() == freq) {
                rslts.add(sa.process());
                names.add(sa.getTs());
            }
        }

        TableFormatter formatter = new TableFormatter();
        Table<String> srslts = formatter.formatProcResults(rslts, items, true);
        int ncols = srslts.getColumnsCount();
        boolean[] ok = new boolean[ncols];
        int nused = 0;

        rslt.addColumn("Series");

        for (int idx = 0; idx < titles.size(); ++idx) {
            if (srslts.column(idx).isEmpty()) {
                ok[idx] = false;
            } else {
                ++nused;
                ok[idx] = true;
                rslt.addColumn(titles.get(idx));
            }
        }
        if (nused == 0) {
            return new DefaultTableModel();
        }

        for (int i = 0; i < names.size(); ++i) {
            String[] row = new String[nused + 1];
            row[0] = names.get(i).getName();
            for (int j = 0, k = 0; j < ncols; ++j) {
                if (ok[j]) {
                    row[++k] = srslts.get(i, j);
                }
            }
            rslt.addRow(row);
        }

        return rslt;
    }

    private static final String[] CALENDAR = {"adjust", "regression.lp:2", "regression.td(1):2", "regression.td(2):2",
        "regression.td(3):2", "regression.td(4):2", "regression.td(5):2", "regression.td(6):2", "regression.td(7):2", "regression.easter:2"};
    private static final String[] CALENDAR_TITLE = {"Adjust", "Leap Year", "T-Stat", "TD(1)", "T-Stat", "TD(2)", "T-Stat", "TD(3)", "T-Stat", "TD(4)", "T-Stat", "TD(5)", "T-Stat", "TD(6)", "T-Stat", "TD(7)", "T-Stat", "Easter", "T-Stat"
    };

    private static final String[] ARMA = {"arima.phi(1):2", "arima.phi(2):2", "arima.phi(3):2", "arima.phi(4):2", "arima.th(1):2", "arima.th(2):2", "arima.th(3):2", "arima.th(4):2", "arima.bphi(1):2", "arima.bth(1):2"};
    private static final String[] ARMA_TITLE = {"phi(1)", "t-stat", "phi(2)", "t-stat", "phi(3)", "t-stat", "phi(4)", "t-stat",
        "th(1)", "t-stat", "th(2)", "t-stat", "th(3)", "t-stat", "th(4)", "t-stat",
        "bphi(1)", "t-stat", "bth(1)", "t-stat"
    };

    private static final String[] OUTLIERS = {
        "regression.out(1)", "regression.out(2)", "regression.out(3)", "regression.out(4)", "regression.out(5)", "regression.out(6)", "regression.out(7)", "regression.out(8)", "regression.out(9)", "regression.out(10)"};
    private static final String[] OUTLIERS_TITLE = {
        "OUT(1)", "OUT(2)", "OUT(3)", "OUT(4)", "OUT(5)", "OUT(6)", "OUT(7)", "OUT(8)", "OUT(9)", "OUT(10)"
    };

    private static final String[] TESTS_TITLE = new String[]{
        "Skewness", "Kurtosis", "Ljung-Box", "LB. on Seas", "LB on sq.",
        "Bias", "TD peak", "Seas peak", "Visual TD peak", "Visual Seas peak",
        "M1", "M2", "M3", "M4", "M5", "M6", "M7", "M8", "M9", "M10", "M11",
        "Q", "Q-M2"
    };

    private static final String[] TESTS = new String[]{
        "residuals.skewness:-3", "residuals.kurtosis:-3", "residuals.lb:-3", "residuals.seaslb:-3", "residuals.lb2:-3",
        InformationSet.item(CoherenceDiagnostics.NAME, CoherenceDiagnostics.BIAS) + ":-2",
        InformationSet.item(ResidualsDiagnostics.NAME, ResidualsDiagnostics.TD_PEAK) + ":-2",
        InformationSet.item(ResidualsDiagnostics.NAME, ResidualsDiagnostics.S_PEAK) + ":-2",
        InformationSet.item(SpectralDiagnostics.NAME, SpectralDiagnostics.TD),
        InformationSet.item(SpectralDiagnostics.NAME, SpectralDiagnostics.SEAS),
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
            return source.getColumnCount() - 1;
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
