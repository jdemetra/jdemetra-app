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

import ec.nbdemetra.anomalydetection.AnomalyItem;
import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.MonikerUI;
import ec.nbdemetra.ui.NbComponents;
import ec.nbdemetra.ui.awt.ListTableModel;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsFactory;
import ec.tstoolkit.modelling.arima.CheckLast;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import ec.ui.ATsCollectionView.TsActionMouseAdapter;
import ec.ui.ATsCollectionView.TsCollectionSelectionListener;
import ec.ui.ATsCollectionView.TsCollectionTransferHandler;
import ec.ui.ATsList;
import ec.ui.DemoUtils;
import ec.ui.interfaces.ITsCollectionView.TsUpdateMode;
import ec.ui.list.TsPeriodTableCellRenderer;
import ec.util.grid.swing.XTable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.Beans;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * List component containing input and output results of a Check Last batch
 * processing
 *
 * @author Mats Maggi
 */
public class JTsCheckLastList extends ATsList {

    private static final String UNPROCESSABLE_MSG = "Check Last can't be processed !";
    private static final String NO_DATA_MSG = "Invalid or empty data !";
    public static final String COLOR_VALUES = "colorValues";
    public static final String NB_CHECK_LAST = "nbCheckLast";
    public static final String SPEC_CHANGE = "specChange";
    private boolean interactive_ = true;
    private final XTable table;
    private Map<String, AnomalyItem> map;
    private List<AnomalyItem> items2;
    private ListTableSelectionListener selectionListener;
    private AnomalyModel model;
    private TramoSpecification spec;
    private int lastChecks = 1;
    private double orangeCells = 4.0;
    private double redCells = 5.0;
    private Comparator<Double> compDouble;
    private Comparator<Ts> compTs;
    private CheckLast checkLast;

    public JTsCheckLastList() {
        map = new HashMap<>();
        items2 = new ArrayList<>();
        table = buildTable();
        setColumnsWidths();

        spec = TramoSpecification.TR4.clone();
        this.selectionListener = new ListTableSelectionListener();
        table.getSelectionModel().addListSelectionListener(selectionListener);
        table.setComponentPopupMenu(buildPopupMenu());

        checkLast = new CheckLast(spec.build());

        setLayout(new BorderLayout());
        add(NbComponents.newJScrollPane(table), BorderLayout.CENTER);

        if (Beans.isDesignTime()) {
            setTsCollection(DemoUtils.randomTsCollection(3));
            setTsUpdateMode(TsUpdateMode.None);
            setPreferredSize(new Dimension(200, 150));
        }
    }

    public double getOrangeCells() {
        return orangeCells;
    }

    public void setOrangeCells(double orangeCells) {
        if (orangeCells < 0 || orangeCells > redCells) {
            throw new IllegalArgumentException("Orange value must be >= 0 and < Red value");
        }
        double old = this.orangeCells;
        this.orangeCells = orangeCells;
        fireTableDataChanged();
        firePropertyChange(COLOR_VALUES, old, this.orangeCells);
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
        fireTableDataChanged();
        firePropertyChange(COLOR_VALUES, old, this.redCells);
    }

    public int getLastChecks() {
        return lastChecks;
    }

    public void setLastChecks(int lastChecks) {
        if (lastChecks < 1 || lastChecks > 3) {
            throw new IllegalArgumentException("Number of last checked values can only be 1, 2 or 3 !");
        }
        selectionListener.setEnabled(false);
        int old = this.lastChecks;
        this.lastChecks = lastChecks;
        resetValues();
        fireTableStructureChanged();
        setColumnsWidths();
        refreshSorter(table);
        firePropertyChange(NB_CHECK_LAST, old, this.lastChecks);
        selectionListener.setEnabled(true);
    }

    private void setColumnsWidths() {
        switch (lastChecks) {
            case 1:
                XTable.setWidthAsPercentages(table, .7, .1, .1, .1);
                break;
            case 2:
                XTable.setWidthAsPercentages(table, .5, .1, .1, .1, .1, .1);
                break;
            case 3:
                XTable.setWidthAsPercentages(table, .3, .1, .1, .1, .1, .1, .1, .1);
        }
    }

    private void resetValues() {
        for (int i = 0; i < items2.size(); i++) {
            items2.get(i).setBackCount(lastChecks);
            items2.get(i).clearValues();
            map.put(items2.get(i).getTs().getName(), items2.get(i));
            checkLast = new CheckLast(spec.build());
            checkLast.setBackCount(lastChecks);
        }
    }

    public TramoSpecification getSpec() {
        return spec;
    }

    public CheckLast getCheckLast() {
        return checkLast;
    }

    public void setSpec(TramoSpecification spec) {
        selectionListener.setEnabled(false);
        TramoSpecification old = this.spec;
        this.spec = spec;
        resetValues();
        fireTableDataChanged();
        firePropertyChange(SPEC_CHANGE, old, this.spec);
        selectionListener.setEnabled(true);
    }

    public void fireTableStructureChanged() {
        model.fireTableStructureChanged();
        setColumnsWidths();
    }

    public void fireTableDataChanged() {
        model.fireTableDataChanged();
    }

    private JPopupMenu buildPopupMenu() {
        ActionMap am = getActionMap();
        JPopupMenu result = buildListMenu().getPopupMenu();

        int index = 11;
        JMenuItem item;

        result.insert(new JSeparator(), index++);

        item = new JMenuItem(new AbstractAction("Original Order") {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                table.getRowSorter().setSortKeys(null);
            }
        });
        item.setEnabled(true);
        result.add(item, index++);

        final JMenuItem unlock = new JMenuItem(new AbstractAction("Unlock") {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (collection.isLocked()) {
                    TsCollection ncol = TsFactory.instance.createTsCollection();
                    ncol.append(collection);
                    collection = ncol;
                }
            }
        });
        result.add(unlock, index++);

        result.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                boolean locked = collection.isLocked() || updateMode == TsUpdateMode.None || !interactive_;
                unlock.setEnabled(locked);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        return result;
    }

    private XTable buildTable() {
        final XTable result = new XTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                int rowIndex = table.convertRowIndexToModel(row);
                JLabel c = (JLabel) super.prepareRenderer(renderer, row, column);
                if (collection.getCount() > row) {
                    if (!isCellSelected(row, column)) {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                        c.setToolTipText(null);
                        c.setEnabled(true);
                        if (items2.get(rowIndex).isNotProcessable()) {
                            if (column == 0) {
                                c.setIcon(DemetraUiIcon.WARNING);
                            }
                            c.setBackground(new Color(255, 255, 204));
                            c.setToolTipText(UNPROCESSABLE_MSG);
                        } else if (items2.get(rowIndex).isProcessed()) {
                            if (column > 2 && column % 2 != 0) {
                                int relIndex = (column / 2) - 1;
                                Double relative_err = items2.get(rowIndex).getRelativeError(relIndex);
                                if (relative_err != null) {
                                    relative_err = Math.abs(relative_err);
                                    if (relative_err >= orangeCells && relative_err < redCells) {
                                        c.setBackground(Color.ORANGE);
                                    } else if (relative_err > redCells) {
                                        c.setBackground(new Color(255, 102, 102));
                                    }
                                }
                            }
                        } else if (items2.get(rowIndex).isInvalid()) {
                            if (column == 0) {
                                c.setIcon(DemetraUiIcon.EXCLAMATION_MARK_16);
                            }
                            c.setBackground(new Color(255, 204, 204));
                            c.setToolTipText(NO_DATA_MSG);
                        }
                    } else {
                        if (items2.get(rowIndex).isInvalid()) {
                            if (column == 0) {
                                c.setIcon(DemetraUiIcon.EXCLAMATION_MARK_16);
                            }
                            c.setToolTipText(NO_DATA_MSG);
                        } else if (items2.get(rowIndex).isNotProcessable()) {
                            if (column == 0) {
                                c.setIcon(DemetraUiIcon.WARNING);
                            }
                            c.setToolTipText(UNPROCESSABLE_MSG);
                        }
                    }
                }
                return c;
            }
        };

        result.setNoDataRenderer(new XTable.DefaultNoDataRenderer("Drop data here", "Drop data here"));
        result.setDefaultRenderer(TsPeriod.class, new TsPeriodTableCellRenderer());
        result.setDefaultRenderer(Ts.class, new TsIdentifierTableCellRenderer());
        model = new AnomalyModel();
        result.setModel(model);

        fillActionMap(result.getActionMap());
        fillInputMap(result.getInputMap());
        result.addMouseListener(new TsActionMouseAdapter());
        result.setDragEnabled(true);
        result.setTransferHandler(new TsCollectionTransferHandler());
        result.setFillsViewportHeight(true);

        compDouble = new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                Double d1 = Math.abs(o1);
                Double d2 = Math.abs(o2);
                return d1.compareTo(d2);
            }
        };

        compTs = new Comparator<Ts>() {
            @Override
            public int compare(Ts o1, Ts o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };

        refreshSorter(result);

        result.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        return result;
    }

    private void refreshSorter(XTable t) {
        t.setAutoCreateRowSorter(true);
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
        if (lastChecks >= 1) {
            sorter.setComparator(AnomalyModel.SERIES, compTs);
            sorter.setComparator(AnomalyModel.ABSOLUTE_ERROR1, compDouble);
            sorter.setComparator(AnomalyModel.RELATIVE_ERROR1, compDouble);
        }
        if (lastChecks >= 2) {
            sorter.setComparator(AnomalyModel.ABSOLUTE_ERROR2, compDouble);
            sorter.setComparator(AnomalyModel.RELATIVE_ERROR2, compDouble);
        }
        if (lastChecks == 3) {
            sorter.setComparator(AnomalyModel.ABSOLUTE_ERROR3, compDouble);
            sorter.setComparator(AnomalyModel.RELATIVE_ERROR3, compDouble);
        }

        t.setRowSorter(sorter);
    }

    @Override
    protected void onCollectionChange() {
        selectionListener.setEnabled(false);
        Map<String, AnomalyItem> temp = new HashMap<>();
        items2.clear();
        for (int i = 0; i < collection.getCount(); i++) {
            String name = collection.get(i).getName();
            if (map.containsKey(name)) {
                temp.put(name, map.get(name));
                items2.add(map.get(name));
            } else {
                AnomalyItem item = new AnomalyItem(collection.get(i));
                item.setId(i);
                item.setBackCount(lastChecks);
                temp.put(name, item);
                items2.add(item);
            }
        }

        map = temp;

        fireTableDataChanged();

        selectionListener.setEnabled(true);
        firePropertyChange(CheckLastBatchUI.COLLECTION_CHANGE, null, collection);
        onSelectionChange();
    }

    @Override
    protected void onSelectionChange() {
        selectionListener.setEnabled(false);
        selectionListener.changeSelection(table.getSelectionModel());

        int index = table.getSelectedRow();
        AnomalyItem selected = null;
        if (index >= 0) {
            int modelIndex = table.convertRowIndexToModel(index);
            selected = items2.get(modelIndex);
            if (!selected.isProcessed() && selected.getTsData() != null) {
                CheckLast cl = new CheckLast(spec.build());
                cl.setBackCount(lastChecks);
                selected.process(cl);
                map.put(selected.getTs().getName(), selected);
                model.fireTableRowsUpdated(index, index);
            }
        }
        firePropertyChange(CheckLastBatchUI.SELECTION_PROPERTY, null, selected);
        selectionListener.setEnabled(true);
    }

    public AnomalyItem put(String key, AnomalyItem value) {
        return map.put(key, value);
    }

    @Override
    protected void onUpdateModeChange() {
        String message = getTsUpdateMode().isReadOnly() ? "No data" : "Drop data here";
        table.setNoDataRenderer(new XTable.DefaultNoDataRenderer(message, message));
    }

    @Override
    protected void onTsActionChange() {
        // Do nothing
    }

    @Override
    protected void onDropContentChange() {
        // Do nothing
    }

    @Override
    protected void onDataFormatChange() {
        // Do nothing
    }

    @Override
    protected void onColorSchemeChange() {
        // Do nothing
    }

    @Override
    protected void onMultiSelectionChange() {
        // Do nothing
    }

    @Override
    protected void onShowHeaderChange() {
        // Do nothing
    }

    @Override
    protected void onSortableChange() {
        // Do nothing
    }

    @Override
    protected void onInformationChange() {
        // Do nothing
    }

    @Override
    protected void onSortInfoChange() {
        // Do nothing
    }

    public Map<String, AnomalyItem> getMap() {
        return map;
    }

    public List<AnomalyItem> getItems2() {
        return items2;
    }

    class AnomalyModel extends ListTableModel<AnomalyItem> {

        static final int SERIES = 0, LAST_PERIOD = 1,
                ABSOLUTE_ERROR1 = 2, RELATIVE_ERROR1 = 3,
                ABSOLUTE_ERROR2 = 4, RELATIVE_ERROR2 = 5,
                ABSOLUTE_ERROR3 = 6, RELATIVE_ERROR3 = 7;
        private String col1 = "<html><p style=\"text-align:center\">Series<br>Name</p></html>";
        private String col2 = "<html><p style=\"text-align:center\">Last<br>Period</p></html>";
        private String col3 = "<html><p style=\"text-align:center\">Abs. Error<br>N-";
        private String col4 = "<html><p style=\"text-align:center\">Rel. Error<br>N-";
        private String html = "</p><html>";
        final List<String> columnNames1 = Arrays.asList(col1, col2, col3 + 1 + html, col4 + 1 + html);
        final List<String> columnNames2 = Arrays.asList(col1, col2,
                col3 + 1 + html, col4 + 1 + html,
                col3 + 2 + html, col4 + 2 + html);
        final List<String> columnNames3 = Arrays.asList(col1, col2,
                col3 + 1 + html, col4 + 1 + html,
                col3 + 2 + html, col4 + 2 + html,
                col3 + 3 + html, col4 + 3 + html);

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case SERIES:
                    return Ts.class;
                case LAST_PERIOD:
                    return TsPeriod.class;
                case ABSOLUTE_ERROR1:
                case RELATIVE_ERROR1:
                case ABSOLUTE_ERROR2:
                case RELATIVE_ERROR2:
                case ABSOLUTE_ERROR3:
                case RELATIVE_ERROR3:
                    return Double.class;
            }
            return super.getColumnClass(columnIndex);
        }

        @Override
        protected List<String> getColumnNames() {
            switch (lastChecks) {
                case 2:
                    return columnNames2;
                case 3:
                    return columnNames3;
                default:
                    return columnNames1;
            }
        }

        @Override
        protected List<AnomalyItem> getValues() {
            return items2;
        }

        @Override
        protected Object getValueAt(AnomalyItem row, int columnIndex) {
            return getValueAt(items2.indexOf(row), columnIndex);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case SERIES:
                    return items2.get(rowIndex).getTs();
                case LAST_PERIOD:
                    if (items2.get(rowIndex).getTsData() == null
                            || items2.get(rowIndex).getTsData().getLastPeriod() == null) {
                        return null;
                    }
                    return items2.get(rowIndex).getTsData().getLastPeriod();
                case ABSOLUTE_ERROR1:
                    return items2.get(rowIndex).getAbsoluteError(0);
                case RELATIVE_ERROR1:
                    return items2.get(rowIndex).getRelativeError(0);
                case ABSOLUTE_ERROR2:
                    return items2.get(rowIndex).getAbsoluteError(1);
                case RELATIVE_ERROR2:
                    return items2.get(rowIndex).getRelativeError(1);
                case ABSOLUTE_ERROR3:
                    return items2.get(rowIndex).getAbsoluteError(2);
                case RELATIVE_ERROR3:
                    return items2.get(rowIndex).getRelativeError(2);
            }
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private class ListTableSelectionListener extends TsCollectionSelectionListener {

        @Override
        protected int indexToModel(int index) {
            return table.convertRowIndexToModel(index);
        }

        @Override
        protected int indexToView(int index) {
            return table.convertRowIndexToView(index);
        }
    }

    private static class TsIdentifierTableCellRenderer extends DefaultTableCellRenderer {

        final MonikerUI monikerUI = MonikerUI.getDefault();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel result = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            Ts ts = (Ts) value;
            result.setText(ts.getName());
            result.setIcon(monikerUI.getIcon(ts.getMoniker()));
            return result;
        }
    }

    public Map getReportParameters() {
        Map parameters = new HashMap();
        parameters.put("_SPECIFICATION", spec.toString());
        parameters.put("_NB_CHECK_LAST", lastChecks);
        parameters.put("_NB_OF_SERIES", getItems2().size());
        parameters.put("_ORANGE_CELLS", orangeCells);
        parameters.put("_RED_CELLS", redCells);

        return parameters;
    }
}
