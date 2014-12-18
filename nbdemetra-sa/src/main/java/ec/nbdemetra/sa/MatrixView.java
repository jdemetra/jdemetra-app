/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa;

import com.google.common.collect.Maps;
import ec.nbdemetra.ui.NbComponents;
import ec.nbdemetra.ui.awt.PopupListener;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.satoolkit.algorithm.implementation.X13ProcessingFactory;
import ec.satoolkit.x11.Mstatistics;
import ec.tss.ITsIdentified;
import ec.tss.formatters.TableFormatter;
import ec.tss.sa.RegArimaReport;
import ec.tss.sa.SaItem;
import ec.tss.sa.SaProcessing;
import ec.tss.sa.diagnostics.*;
import ec.tstoolkit.algorithm.AlgorithmDescriptor;
import ec.tstoolkit.algorithm.IProcResults;
import ec.tstoolkit.data.Table;
import ec.tstoolkit.information.InformationSet;
import ec.util.grid.swing.AbstractGridModel;
import ec.util.grid.swing.GridRowHeaderRenderer;
import ec.util.grid.swing.JGrid;
import ec.util.grid.swing.ext.TableGridCommand;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.windows.TopComponent;

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
    private final JTabbedPane matrixTabPane_;
    // data
    private List<SaItem> saItems;

    public MatrixView(WorkspaceItem<MultiProcessingDocument> doc, MultiProcessingController controller) {
        super(doc, controller);
        this.saItems = new ArrayList<>();

        this.comboBox = new JComboBox<>();
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel result = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    Entry<Integer, AlgorithmDescriptor> item = (Entry<Integer, AlgorithmDescriptor>) value;
                    result.setText(TaggedTreeNode.freqName(item.getKey()) + " > " + item.getValue().name);
                }
                return result;
            }
        });
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED && e.getItem() != null) {
                    Entry<Integer, AlgorithmDescriptor> item = (Entry<Integer, AlgorithmDescriptor>) e.getItem();
                    updateMatrix(item.getValue(), item.getKey());
                } else {
                    clearMatrices();
                }
            }
        });

        matrixTabPane_ = new JTabbedPane();
        matrixTabPane_.addTab("Main", resMatrix_ = createMatrix());
        matrixTabPane_.addTab("Calendar", calMatrix_ = createMatrix());
        matrixTabPane_.addTab("Outliers", outMatrix_ = createMatrix());
        matrixTabPane_.addTab("Arma", armaMatrix_ = createMatrix());
        matrixTabPane_.addTab("Tests", testMatrix_ = createMatrix());

        toolBarRepresentation = NbComponents.newInnerToolbar();
        toolBarRepresentation.addSeparator();
        toolBarRepresentation.add(comboBox);

        visualRepresentation = matrixTabPane_;

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
        result.setRowRenderer(new GridRowHeaderRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel result = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                result.setToolTipText(result.getText());
                return result;
            }
        });
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(JLabel.CENTER);
        result.setDefaultRenderer(Object.class, cellRenderer);

        JMenu menu = new JMenu();
        menu.add(TableGridCommand.copyAll(true, true).toAction(result)).setText("Copy");
        result.addMouseListener(new PopupListener.PopupAdapter(menu.getPopupMenu()));

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
        comboBox.setVisible(methods.size() > 1);
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
    }

    private void updateMatrix(AlgorithmDescriptor desc, int freq) {
        resMatrix_.setModel(new TableModelAdapter(createTableModel(desc, freq, Arrays.asList(MAIN_TITLE), Arrays.asList(MAIN))));
        calMatrix_.setModel(new TableModelAdapter(createTableModel(desc, freq, Arrays.asList(CALENDAR_TITLE), Arrays.asList(CALENDAR))));
        armaMatrix_.setModel(new TableModelAdapter(createTableModel(desc, freq, Arrays.asList(ARMA_TITLE), Arrays.asList(ARMA))));
        outMatrix_.setModel(new TableModelAdapter(createTableModel(desc, freq, Arrays.asList(OUTLIERS_TITLE), Arrays.asList(OUTLIERS))));
        testMatrix_.setModel(new TableModelAdapter(createTableModel(desc, freq, Arrays.asList(TESTS_TITLE), Arrays.asList(TESTS))));
    }
    private static final String[] MAIN = {"espan.n", "decomposition.seasonality", "adjust", "log", "arima.mean", "arima.p", "arima.d", "arima.q", "arima.bp", "arima.bd", "arima.bq", "likelihood.bicc", "residuals.ser", "residuals.lb", "decomposition.seasfilter", "decomposition.trendfilter"};
    private static final String[] MAIN_TITLE = {"N", "Seasonal", "Adjust", "Log", "Mean", "P", "D", "Q", "BP", "BD", "BQ", "BIC", "SE(res)", "Q-val", "Seas filter", "Trend filter"};

    private TableModel createTableModel(AlgorithmDescriptor method, int freq, List<String> titles, List<String> items) {
        DefaultTableModel result = new DefaultTableModel();
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
            row[0] = names.get(i).getName();
            for (int j = 0, k = 0; j < ncols; ++j) {
                if (ok[j]) {
                    row[++k] = srslts.get(i, j);
                }
            }
            result.addRow(row);
        }

        return result;
    }

    private TableModel createTestModel(AlgorithmDescriptor method, int freq, List<String> titles, List<String> items) {
        DefaultTableModel model = new DefaultTableModel();
        List<ITsIdentified> names = new ArrayList<>();
        List<IProcResults> rslts = new ArrayList<>();
        List<InformationSet> drslts = new ArrayList<>();
        for (SaItem sa : this.saItems) {
            if (sa.getEstimationMethod().equals(method) && sa.getTsData() != null && sa.getTsData().getFrequency().intValue() == freq) {
                rslts.add(sa.process());
                drslts.add(sa.getDiagnostics());
                names.add(sa.getTs());
            }
        }

        TableFormatter formatter = new TableFormatter();
        Table<String> srslts = formatter.formatProcResults(rslts, items, true);
        Table<String> sdrslts = formatter.formatInformation(drslts, items, true);
        int ncols = srslts.getColumnsCount() + sdrslts.getColumnsCount();
        boolean[] ok = new boolean[ncols];

        model.addColumn("Series");
        HashSet<String> used = new HashSet<>();

        int icol = 0;
        for (int idx = 0; idx < srslts.getColumnsCount(); ++idx, ++icol) {
            if (srslts.column(idx).isEmpty()) {
                ok[icol] = false;
            } else {
                used.add(titles.get(idx));
                ok[icol] = true;
                model.addColumn(titles.get(idx));
            }
        }
        for (int idx = 0; idx < sdrslts.getColumnsCount(); ++idx, ++icol) {
            if (sdrslts.column(idx).isEmpty() || used.contains(titles.get(idx))) {
                ok[icol] = false;
            } else {
                used.add(titles.get(idx));
                ok[icol] = true;
                model.addColumn(titles.get(idx));
            }
        }
        if (used.isEmpty()) {
            return new DefaultTableModel();
        }
        int nused = used.size();

        for (int i = 0; i < names.size(); ++i) {
            String[] row = new String[nused + 1];
            row[0] = names.get(i).getName();
            int k = 0, l = 0;
            for (int j = 0; j < srslts.getColumnsCount(); ++j, ++l) {
                if (ok[l]) {
                    row[++k] = srslts.get(i, j);
                }
            }
            for (int j = 0; j < sdrslts.getColumnsCount(); ++j, ++l) {
                if (ok[l]) {
                    row[++k] = sdrslts.get(i, j);
                }
            }
            model.addRow(row);
        }

        return model;
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
        //        InformationSet.item(MDiagnostics.NAME, MDiagnostics.M1),
        //        InformationSet.item(MDiagnostics.NAME, MDiagnostics.M2),
        //        InformationSet.item(MDiagnostics.NAME, MDiagnostics.M3),
        //        InformationSet.item(MDiagnostics.NAME, MDiagnostics.M4),
        //        InformationSet.item(MDiagnostics.NAME, MDiagnostics.M5),
        //        InformationSet.item(MDiagnostics.NAME, MDiagnostics.M6),
        //        InformationSet.item(MDiagnostics.NAME, MDiagnostics.M7),
        //        InformationSet.item(MDiagnostics.NAME, MDiagnostics.M8),
        //        InformationSet.item(MDiagnostics.NAME, MDiagnostics.M9),
        //        InformationSet.item(MDiagnostics.NAME, MDiagnostics.M10),
        //        InformationSet.item(MDiagnostics.NAME, MDiagnostics.M11),
        //        InformationSet.item(MDiagnostics.NAME, MDiagnostics.Q) + ":-2",
        //        InformationSet.item(MDiagnostics.NAME, MDiagnostics.Q2) + ":-2",};
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

//    private void updateTests(JLabel[] cur, SaItem item, AlgorithmDescriptor m, InformationSet details) {
//        InformationSet res = details.getSubSet(RegArimaDictionary.RESIDUALS);
//        if (res != null) {
//            Double test = res.get(RegArimaDictionary.SKEW, Double.class);
//            cur[SKEWNESS].setText(test != null ? df3.format(test) : "");
//            test = res.get(RegArimaDictionary.KURT, Double.class);
//            cur[KURTOSIS].setText(test != null ? df3.format(test) : "");
//            test = res.get(RegArimaDictionary.LB, Double.class);
//            cur[LB].setText(test != null ? df3.format(test) : "");
//            test = res.get(RegArimaDictionary.SEASLB, Double.class);
//            cur[LBS].setText(test != null ? df3.format(test) : "");
//            test = res.get(RegArimaDictionary.LB2, Double.class);
//            cur[LB2].setText(test != null ? df3.format(test) : "");
//        }
//
//        InformationSet sum = item.getDiagnostics();
//        if (sum != null) {
//            ProcDiagnostic diag = sum.search(new String[]{CoherenceDiagnostics.NAME, CoherenceDiagnostics.BIAS}, ProcDiagnostic.class);
//            cur[MAXBIAS].setText(diag != null ? df3.format(diag.value) : "");
//            diag = sum.search(new String[]{ResidualsDiagnostics.NAME, ResidualsDiagnostics.TD_PEAK}, ProcDiagnostic.class);
//            cur[TD_PEAK].setText(diag != null ? df3.format(diag.value) : "");
//            diag = sum.search(new String[]{ResidualsDiagnostics.NAME, ResidualsDiagnostics.S_PEAK}, ProcDiagnostic.class);
//            cur[S_PEAK].setText(diag != null ? df3.format(diag.value) : "");
//            diag = sum.search(new String[]{SpectralDiagnostics.NAME, SpectralDiagnostics.TD}, ProcDiagnostic.class);
//            cur[TD_VPEAK].setText((diag != null && diag.quality.isLower(ProcQuality.Uncertain)) ? "X" : "");
//            diag = sum.search(new String[]{SpectralDiagnostics.NAME, SpectralDiagnostics.SEAS}, ProcDiagnostic.class);
//            cur[S_VPEAK].setText((diag != null && diag.quality.isLower(ProcQuality.Uncertain)) ? "X" : "");
//        } else {
//            for (int i = MAXBIAS; i <= S_VPEAK; ++i) {
//                cur[i].setText("");
//            }
//        }
//        if (m.equals(TramoSeatsProcessor.DESCRIPTOR)) {
//            if (sum != null) {
//                ProcDiagnostic diag = sum.search(new String[]{SeatsDiagnostics.NAME, SeatsDiagnostics.SEAS_VAR}, ProcDiagnostic.class);
//                cur[S_VAR].setText(diag != null ? df3.format(diag.value) : "");
//                diag = sum.search(new String[]{SeatsDiagnostics.NAME, SeatsDiagnostics.IRR_VAR}, ProcDiagnostic.class);
//                cur[I_VAR].setText(diag != null ? df3.format(diag.value) : "");
//                diag = sum.search(new String[]{SeatsDiagnostics.NAME, SeatsDiagnostics.SEAS_I_CORR}, ProcDiagnostic.class);
//                cur[SI_CORR].setText(diag != null ? df3.format(diag.value) : "");
//            } else {
//                for (int i = S_VAR; i <= SI_CORR; ++i) {
//                    cur[i].setText("");
//                }
//            }
//        } else {
//            if (sum != null) {
//                InformationSet mstats = sum.subSet(MDiagnostics.NAME);
//            }
//        }
//    }
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
