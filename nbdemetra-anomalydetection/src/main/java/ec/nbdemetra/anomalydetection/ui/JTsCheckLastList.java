/*
 * Copyright 2013 National Bank of Belgium
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
package ec.nbdemetra.anomalydetection.ui;

import demetra.bridge.TsConverter;
import demetra.demo.DemoTsBuilder;
import demetra.ui.components.parts.HasTsCollection;
import static demetra.ui.components.parts.HasTsCollection.TS_COLLECTION_PROPERTY;
import ec.nbdemetra.anomalydetection.AnomalyItem;
import ec.nbdemetra.ui.DemetraUiIcon;
import demetra.ui.util.ActionMaps;
import demetra.ui.util.InputMaps;
import ec.tss.Ts;
import ec.tss.tsproviders.utils.MultiLineNameUtil;
import ec.tstoolkit.data.Table;
import ec.tstoolkit.modelling.arima.CheckLast;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import demetra.ui.components.JTsTable;
import demetra.ui.components.TimeSeriesComponent;
import ec.nbdemetra.ui.DemetraUI;
import ec.tss.TsIdentifier;
import ec.util.table.swing.JTables;
import ec.util.various.swing.JCommand;
import demetra.ui.components.TsSelectionBridge;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.beans.Beans;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import demetra.ui.OldDataTransfer;
import demetra.ui.components.TmpHasTsCollection;

/**
 * List component containing input and output results of a Check Last batch
 * processing
 *
 * @author Mats Maggi
 */
public final class JTsCheckLastList extends JComponent implements TimeSeriesComponent,
        HasTsCollection {

    public static final String COLOR_VALUES_PROPERTY = "colorValues";
    public static final String LAST_CHECKS_PROPERTY = "lastChecks";
    public static final String SPEC_PROPERTY = "spec";

    @lombok.experimental.Delegate(types = HasTsCollection.class)
    private final JTsTable table;

    private double orangeCells;
    private double redCells;
    private int lastChecks;
    private TramoSpecification spec;

    private Map<String, AnomalyItem> map;
    private final List<AnomalyItem> items;
    private CheckLast checkLast;

    public JTsCheckLastList() {
        this.table = new JTsTable();
        this.orangeCells = 4.0;
        this.redCells = 5.0;
        this.lastChecks = 1;
        this.spec = TramoSpecification.TRfull.clone();

        map = new HashMap<>();
        items = new ArrayList<>();
        initTable();

        checkLast = new CheckLast(spec.build());

        onComponentPopupMenuChange();
        enableProperties();

        setLayout(new BorderLayout());
        add(table, BorderLayout.CENTER);

        if (Beans.isDesignTime()) {
            setTsCollection(DemoTsBuilder.randomTsCollection(3));
            setTsUpdateMode(TsUpdateMode.None);
            setPreferredSize(new Dimension(200, 150));
        }
    }

    private void enableProperties() {
        this.addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case TS_COLLECTION_PROPERTY:
                    onCollectionChange();
                    break;
                case TsSelectionBridge.TS_SELECTION_PROPERTY:
                    onSelectionChange();
                    break;
                case "componentPopupMenu":
                    onComponentPopupMenuChange();
                    break;
                case COLOR_VALUES_PROPERTY:
                    onColorValuesChange();
                    break;
                case LAST_CHECKS_PROPERTY:
                    onLastChecksChange();
                    break;
                case SPEC_PROPERTY:
                    onSpecChange();
                    break;
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public double getOrangeCells() {
        return orangeCells;
    }

    public void setOrangeCells(double orangeCells) {
        if (orangeCells < 0 || orangeCells > redCells) {
            throw new IllegalArgumentException("Orange value must be >= 0 and < Red value");
        }
        double old = this.orangeCells;
        this.orangeCells = orangeCells;
        firePropertyChange(COLOR_VALUES_PROPERTY, old, this.orangeCells);
    }

    public double getRedCells() {
        return redCells;
    }

    public void setRedCells(double redCells) {
        if (redCells < orangeCells) {
            throw new IllegalArgumentException("Red value must be greater than Orange value");
        }
        double old = this.redCells;
        this.redCells = redCells;
        firePropertyChange(COLOR_VALUES_PROPERTY, old, this.redCells);
    }

    public int getLastChecks() {
        return lastChecks;
    }

    public void setLastChecks(int lastChecks) {
        if (lastChecks < 1 || lastChecks > 3) {
            throw new IllegalArgumentException("Number of last checked values can only be 1, 2 or 3 !");
        }
        int old = this.lastChecks;
        this.lastChecks = lastChecks;
        firePropertyChange(LAST_CHECKS_PROPERTY, old, this.lastChecks);
    }

    public TramoSpecification getSpec() {
        return spec;
    }

    public void setSpec(TramoSpecification spec) {
        TramoSpecification old = this.spec;
        this.spec = spec;
        firePropertyChange(SPEC_PROPERTY, old, this.spec);
    }
    //</editor-fold>

    private void resetValues() {
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setBackCount(lastChecks);
            items.get(i).clearValues();
            map.put(items.get(i).getTs().getName(), items.get(i));
            checkLast = new CheckLast(spec.build());
            checkLast.setBackCount(lastChecks);
        }
    }

    public CheckLast getCheckLast() {
        return checkLast;
    }

    public Map<String, AnomalyItem> getMap() {
        return map;
    }

    public List<AnomalyItem> getItems() {
        return items;
    }

    public void fireTableStructureChanged() {
        List<JTsTable.Column> columns = new ArrayList<>();
        columns.add(seriesColumn);
        columns.add(lastPeriodColumn);
        columns.add(abs1Column);
        columns.add(rel1Column);
        if (lastChecks > 1) {
            columns.add(abs2Column);
            columns.add(rel2Column);
        }
        if (lastChecks > 2) {
            columns.add(abs3Column);
            columns.add(rel3Column);
        }
        table.setColumns(columns);
        switch (lastChecks) {
            case 1:
                table.setWidthAsPercentages(new double[]{.7, .1, .1, .1});
                break;
            case 2:
                table.setWidthAsPercentages(new double[]{.5, .1, .1, .1, .1, .1});
                break;
            case 3:
                table.setWidthAsPercentages(new double[]{.3, .1, .1, .1, .1, .1, .1, .1});
                break;
        }
    }

    public void fireTableDataChanged() {
        table.repaint();
    }

    private JPopupMenu buildPopupMenu() {
        ActionMap am = getActionMap();
        JPopupMenu result = TmpHasTsCollection.newDefaultMenu(this, DemetraUI.getDefault()).getPopupMenu();

        int index = 11;
        JMenuItem item;

        result.insert(new JSeparator(), index++);

        JMenu sub = new JMenu("Export results to");
        sub.add(new CopyToClipoard().toAction(this)).setText("Clipboard");
        result.add(sub, index++);

        item = new JMenuItem(new AbstractAction("Original Order") {
            @Override
            public void actionPerformed(ActionEvent arg0) {
//                table.getRowSorter().setSortKeys(null);
            }
        });
        item.setEnabled(true);
        result.add(item, index++);

        return result;
    }

    private void initTable() {
//        table.setMultiSelection(false);
        table.getTsSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        result.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
//        ((JLabel) result.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        table.addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case HasTsCollection.DROP_CONTENT_PROPERTY:
                case HasTsCollection.FREEZE_ON_IMPORT_PROPERTY:
                case HasTsCollection.TS_COLLECTION_PROPERTY:
                case HasTsCollection.TS_SELECTION_MODEL_PROPERTY:
                case HasTsCollection.UDPATE_MODE_PROPERTY:
                case TsSelectionBridge.TS_SELECTION_PROPERTY:
                    firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
                    break;
            }
        });

        ActionMaps.copyEntries(getActionMap(), false, table.getActionMap());
        InputMaps.copyEntries(getInputMap(), false, table.getInputMap());

        fireTableStructureChanged();
    }

    private void onColorValuesChange() {
        fireTableDataChanged();
    }

    private void onLastChecksChange() {
        resetValues();
        fireTableStructureChanged();
    }

    private void onSpecChange() {
        resetValues();
        fireTableDataChanged();
    }

    private void onCollectionChange() {
//        table.setTsCollection(getTsCollection());

        Map<String, AnomalyItem> temp = new HashMap<>();
        items.clear();
        List<demetra.timeseries.Ts> collection = getTsCollection().getData();
        for (int i = 0; i < collection.size(); i++) {
            String name = collection.get(i).getName();
            if (map.containsKey(name)) {
                temp.put(name, map.get(name));
                items.add(map.get(name));
            } else {
                AnomalyItem item = new AnomalyItem(TsConverter.fromTs(collection.get(i)));
                item.setId(i);
                item.setBackCount(lastChecks);
                temp.put(name, item);
                items.add(item);
            }
        }

        map = temp;

        fireTableDataChanged();

        firePropertyChange(CheckLastBatchUI.COLLECTION_CHANGE, null, collection);
        onSelectionChange();
    }

    private void onSelectionChange() {
        OptionalInt index = table.getTsSelectionIndexStream().findFirst();
        AnomalyItem selected = null;
        if (index.isPresent()) {
            selected = items.get(index.getAsInt());
            if (!selected.isProcessed() && selected.getTsData() != null) {
                CheckLast cl = new CheckLast(spec.build());
                cl.setBackCount(lastChecks);
                selected.process(cl);
                map.put(selected.getTs().getName(), selected);
                table.repaint();
            }
        }
        firePropertyChange(CheckLastBatchUI.SELECTION_PROPERTY, null, selected);
    }

    public AnomalyItem put(String key, AnomalyItem value) {
        return map.put(key, value);
    }

    private Optional<AnomalyItem> getAnomaly(demetra.timeseries.Ts ts) {
        int index = getTsCollection().getData().indexOf(ts);
        return index != -1 ? Optional.ofNullable(getItems().get(index)) : Optional.empty();
    }

    private void onComponentPopupMenuChange() {
        JPopupMenu popupMenu = getComponentPopupMenu();
        table.setComponentPopupMenu(popupMenu != null ? popupMenu : buildPopupMenu());
    }

    private final JTsTable.Column seriesColumn = JTsTable.Column.builder()
            .name("<html><center>&nbsp;<br>Series Name<br>&nbsp;")
            .type(Ts.class)
            .mapper(ts -> getAnomaly(ts).map(AnomalyItem::getTs).map(TsIdentifier::new).orElse(null))
            .comparator(TS_COMP)
            .comparator(JTsTable.Column.TS_IDENTIFIER.getComparator())
            .renderer(o -> new Decorator(JTsTable.Column.TS_IDENTIFIER.getRenderer().apply(o)))
            .build();

    private final JTsTable.Column lastPeriodColumn = JTsTable.Column.builder()
            .name("<html><center>&nbsp;Last<br>Period<br>&nbsp;")
            .type(TsPeriod.class)
            .mapper(ts -> getAnomaly(ts).filter(o -> o.getTsData() != null).map(o -> TsConverter.toTsPeriod(o.getTsData().getLastPeriod())).orElse(null))
            .comparator(JTsTable.Column.LAST.getComparator())
            .renderer(o -> new Decorator(JTsTable.Column.LAST.getRenderer().apply(o)))
            .build();

    private final JTsTable.Column abs1Column = JTsTable.Column.builder()
            .name("<html><center>Abs.<br>Error<br>N-1")
            .type(Double.class)
            .mapper(ts -> getAnomaly(ts).map(o -> o.getAbsoluteError(0)).orElse(null))
            .comparator(DOUBLE_COMP)
            .renderer(o -> new Decorator(JTables.cellRendererOf(JTsCheckLastList::apply)))
            .build();

    private final JTsTable.Column rel1Column = JTsTable.Column.builder()
            .name("<html><center>Rel.<br>Error<br>N-1")
            .type(Double.class)
            .mapper(ts -> getAnomaly(ts).map(o -> o.getRelativeError(0)).orElse(null))
            .comparator(DOUBLE_COMP)
            .renderer(o -> new Decorator(JTables.cellRendererOf(JTsCheckLastList::apply)))
            .build();

    private final JTsTable.Column abs2Column = JTsTable.Column.builder()
            .name("<html><center>Abs.<br>Error<br>N-2")
            .type(Double.class)
            .mapper(ts -> getAnomaly(ts).map(o -> o.getAbsoluteError(1)).orElse(null))
            .comparator(DOUBLE_COMP)
            .renderer(o -> new Decorator(JTables.cellRendererOf(JTsCheckLastList::apply)))
            .build();

    private final JTsTable.Column rel2Column = JTsTable.Column.builder()
            .name("<html><center>Rel.<br>Error<br>N-2")
            .type(Double.class)
            .mapper(ts -> getAnomaly(ts).map(o -> o.getRelativeError(1)).orElse(null))
            .comparator(DOUBLE_COMP)
            .renderer(o -> new Decorator(JTables.cellRendererOf(JTsCheckLastList::apply)))
            .build();

    private final JTsTable.Column abs3Column = JTsTable.Column.builder()
            .name("<html><center>Abs.<br>Error<br>N-3")
            .type(Double.class)
            .mapper(ts -> getAnomaly(ts).map(o -> o.getAbsoluteError(2)).orElse(null))
            .comparator(DOUBLE_COMP)
            .renderer(o -> new Decorator(JTables.cellRendererOf(JTsCheckLastList::apply)))
            .build();

    private final JTsTable.Column rel3Column = JTsTable.Column.builder()
            .name("<html><center>Rel.<br>Error<br>N-3")
            .type(Double.class)
            .mapper(ts -> getAnomaly(ts).map(o -> o.getRelativeError(2)).orElse(null))
            .comparator(DOUBLE_COMP)
            .renderer(o -> new Decorator(JTables.cellRendererOf(JTsCheckLastList::apply)))
            .build();

    private static void apply(JLabel l, Double value) {
        l.setHorizontalAlignment(JLabel.TRAILING);
    }

    public Map getReportParameters() {
        Map parameters = new HashMap();
        parameters.put("_SPECIFICATION", spec.toString());
        parameters.put("_NB_CHECK_LAST", lastChecks);
        parameters.put("_NB_OF_SERIES", getItems().size());
        parameters.put("_ORANGE_CELLS", orangeCells);
        parameters.put("_RED_CELLS", redCells);

        return parameters;
    }

    private static abstract class ModelCommand extends JCommand<JTsCheckLastList> {

        @Override
        public boolean isEnabled(JTsCheckLastList list) {
            return list != null && list.getItems() != null && !list.getItems().isEmpty();
        }

        @Override
        public JCommand.ActionAdapter toAction(JTsCheckLastList list) {
            return super.toAction(list).withWeakPropertyChangeListener(list, CheckLastBatchUI.COLLECTION_CHANGE);
        }
    }

    private static Table<Object> toTable(JTsCheckLastList list) {
        Map<String, AnomalyItem> map = list.getMap();
        int nback = list.getLastChecks();
        int cols = nback < 2 ? 5 : nback > 2 ? 9 : 7;
        Table<Object> table = new Table<>(map.size() + 1, cols);

        table.set(0, 0, "Series name");
        table.set(0, 1, "Last Period");
        table.set(0, 2, "Status");
        table.set(0, 3, "Abs. Error (n-1)");
        table.set(0, 4, "Rel. Error (n-1)");
        if (nback > 1) {
            table.set(0, 5, "Abs. Error (n-2)");
            table.set(0, 6, "Rel. Error (n-2)");
        }
        if (nback > 2) {
            table.set(0, 7, "Abs. Error (n-3)");
            table.set(0, 8, "Rel. Error (n-3)");
        }

        int row = 1;
        for (Map.Entry<String, AnomalyItem> entry : map.entrySet()) {
            AnomalyItem item = entry.getValue();
            table.set(row, 0, MultiLineNameUtil.join(entry.getKey()));

            if (item.getTsData() != null && !item.getTsData().isEmpty()) {
                table.set(row, 1, item.getTsData().getLastPeriod().toString());
            }

            table.set(row, 2, item.getStatus().toString());
            table.set(row, 3, item.getAbsoluteError(0));
            table.set(row, 4, item.getRelativeError(0));

            if (nback > 1) {
                table.set(row, 5, item.getAbsoluteError(1));
                table.set(row, 6, item.getRelativeError(1));
            }

            if (nback > 2) {
                table.set(row, 7, item.getAbsoluteError(2));
                table.set(row, 8, item.getRelativeError(2));
            }
            row++;
        }
        return table;
    }

    private static final class CopyToClipoard extends ModelCommand {

        @Override
        public void execute(JTsCheckLastList component) throws Exception {
            Transferable t = OldDataTransfer.getDefault().fromTable(toTable(component));
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(t, null);
        }
    }

    @lombok.AllArgsConstructor
    private final class Decorator implements TableCellRenderer {

        @lombok.NonNull
        private final TableCellRenderer delegate;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component result = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (result instanceof JLabel) {
                JLabel c = (JLabel) result;
                int rowIndex = table.convertRowIndexToModel(row);
                if (getTsCollection().getData().size() > row) {
                    c.setOpaque(true);
                    if (!isSelected) {
                        c.setOpaque(true);
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                        c.setToolTipText(null);
                        c.setEnabled(true);
                        if (items.get(rowIndex).isNotProcessable()) {
                            if (column == 0) {
                                c.setIcon(DemetraUiIcon.WARNING);
                            }
                            c.setBackground(new Color(255, 255, 204));
                            c.setToolTipText(UNPROCESSABLE_MSG);
                        } else if (items.get(rowIndex).isProcessed()) {
                            if (column > 2 && column % 2 != 0) {
                                int relIndex = (column / 2) - 1;
                                Double relative_err = items.get(rowIndex).getRelativeError(relIndex);
                                if (relative_err != null) {
                                    relative_err = Math.abs(relative_err);
                                    if (relative_err >= orangeCells && relative_err < redCells) {
                                        c.setBackground(Color.ORANGE);
                                    } else if (relative_err > redCells) {
                                        c.setBackground(new Color(255, 102, 102));
                                    }
                                }
                            }
                        } else if (items.get(rowIndex).isInvalid()) {
                            if (column == 0) {
                                c.setIcon(DemetraUiIcon.EXCLAMATION_MARK_16);
                            }
                            c.setBackground(new Color(255, 204, 204));
                            c.setToolTipText(NO_DATA_MSG);
                        }
                    } else if (items.get(rowIndex).isInvalid()) {
                        if (column == 0) {
                            c.setIcon(DemetraUiIcon.EXCLAMATION_MARK_16);
                        }
                        c.setToolTipText(NO_DATA_MSG);
                    } else if (items.get(rowIndex).isNotProcessable()) {
                        if (column == 0) {
                            c.setIcon(DemetraUiIcon.WARNING);
                        }
                        c.setToolTipText(UNPROCESSABLE_MSG);
                    }
                }
            }
            return result;
        }
    }

    private static final String UNPROCESSABLE_MSG = "Check Last can't be processed !";
    private static final String NO_DATA_MSG = "Invalid or empty data !";
    private static final Comparator<Double> DOUBLE_COMP = Comparator.comparingDouble(Math::abs);
    private static final Comparator<Ts> TS_COMP = Comparator.comparing(Ts::getName);
}
