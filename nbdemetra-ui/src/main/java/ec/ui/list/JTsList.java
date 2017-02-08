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
package ec.ui.list;

import com.google.common.base.Strings;
import ec.nbdemetra.ui.MonikerUI;
import ec.nbdemetra.ui.NbComponents;
import ec.nbdemetra.ui.awt.ActionMaps;
import ec.nbdemetra.ui.awt.InputMaps;
import ec.nbdemetra.ui.awt.TableColumnModelAdapter;
import ec.tss.*;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tss.tsproviders.utils.Formatters.Formatter;
import ec.tss.tsproviders.utils.MultiLineNameUtil;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import ec.ui.ATsList;
import ec.ui.DemoUtils;
import ec.ui.chart.TsSparklineCellRenderer;
import ec.util.grid.swing.XTable;
import ec.util.various.swing.StandardSwingColor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;
import java.beans.Beans;
import java.beans.PropertyChangeEvent;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.plaf.LayerUI;
import javax.swing.table.*;
import org.netbeans.swing.etable.ETable;
import org.netbeans.swing.etable.ETableColumn;
import org.netbeans.swing.etable.ETableColumnModel;

/**
 *
 * @author Kristof Bayens
 * @author Philippe Charles
 */
public class JTsList extends ATsList {

    private final boolean interactive_ = true;
    private final ETable table;
    private final ListTableSelectionListener selectionListener;
    private final JTableHeader tableHeader;
    private final DropUI dropUI;

    public JTsList() {
        this.table = new ETable();
        this.selectionListener = new ListTableSelectionListener();
        this.tableHeader = table.getTableHeader();
        this.dropUI = new DropUI();

        initTable();

        ActionMaps.copyEntries(getActionMap(), false, table.getActionMap());
        InputMaps.copyEntries(getInputMap(), false, table.getInputMap());

        enableSeriesSelection();
        enableOpenOnDoubleClick();
        enableProperties();

        setLayout(new BorderLayout());
        add(new JLayer<>(NbComponents.newJScrollPane(table), dropUI), BorderLayout.CENTER);

        if (Beans.isDesignTime()) {
            applyDesignTimeProperties();
        }
    }

    private void initTable() {
        onDataFormatChange();
        onUpdateModeChange();
        onTransferHandlerChange();
        onComponentPopupMenuChange();

        int cellPaddingHeight = 2;
        table.setRowHeight(table.getFontMetrics(table.getFont()).getHeight() + cellPaddingHeight * 2 + 1);
//        table.setRowMargin(cellPaddingHeight);

        table.setFullyNonEditable(true);
        table.setShowHorizontalLines(true);
        table.setBorder(null);
        Color newGridColor = StandardSwingColor.CONTROL.value();
        if (newGridColor != null) {
            table.setGridColor(newGridColor);
        }

        table.setDefaultRenderer(TsPeriod.class, new TsPeriodTableCellRenderer());
        table.setDefaultRenderer(TsFrequency.class, new TsFrequencyTableCellRenderer());
        table.setDefaultRenderer(TsIdentifier.class, new TsIdentifierTableCellRenderer());

        table.getColumnModel().addColumnModelListener(new TableColumnModelAdapter() {
            @Override
            public void columnAdded(TableColumnModelEvent e) {
                ETableColumnModel columnModel = (ETableColumnModel) e.getSource();
                for (int i = e.getFromIndex(); i < e.getToIndex(); i++) {
                    final ETableColumn column = (ETableColumn) columnModel.getColumn(i);
                    column.setNestedComparator(new InformationComparator(i));
                }
            }
        });

        table.setModel(new CustomTableModel(getTsCollection().toArray(), information));
        XTable.setWidthAsPercentages(table, .4, .1, .1, .1, .3);

        table.setDragEnabled(true);
        table.setTransferHandler(new TsCollectionTransferHandler());
        table.setFillsViewportHeight(true);
        table.setSelectionMode(multiSelection ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION);
    }

    private void applyDesignTimeProperties() {
        setTsCollection(DemoUtils.randomTsCollection(3));
        setTsUpdateMode(TsUpdateMode.None);
        setPreferredSize(new Dimension(200, 150));
    }

    //<editor-fold defaultstate="collapsed" desc="Interactive stuff">
    private void enableSeriesSelection() {
        table.getSelectionModel().addListSelectionListener(selectionListener);
    }

    private void enableOpenOnDoubleClick() {
        table.addMouseListener(new TsActionMouseAdapter());
    }

    private void enableProperties() {
        this.addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case "transferHandler":
                    onTransferHandlerChange();
                    break;
                case "componentPopupMenu":
                    onComponentPopupMenuChange();
                    break;
            }
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Events handlers">
    @Override
    protected void onDataFormatChange() {
        table.setDefaultRenderer(TsData.class, new TsDataTableCellRenderer(themeSupport.getDataFormat()));
    }

    @Override
    protected void onColorSchemeChange() {
        // do nothing
    }

    @Override
    protected void onCollectionChange() {
        selectionListener.setEnabled(false);
        ((CustomTableModel) table.getModel()).setData(getTsCollection().toArray());
        selectionListener.setEnabled(true);
        onSelectionChange();
    }

    @Override
    protected void onSelectionChange() {
        selectionListener.setEnabled(false);
        selectionListener.changeSelection(table.getSelectionModel());
        selectionListener.setEnabled(true);
    }

    @Override
    protected void onUpdateModeChange() {
        String message = getTsUpdateMode().isReadOnly() ? "No data" : "Drop data here";
        dropUI.setRenderer(new XTable.DefaultNoDataRenderer(message, message));
    }

    @Override
    protected void onTsActionChange() {
        // do nothing
    }

    @Override
    protected void onDropContentChange() {
        // do nothing
    }

    @Override
    protected void onMultiSelectionChange() {
        table.setSelectionMode(multiSelection ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION);
    }

    @Override
    protected void onShowHeaderChange() {
        table.setTableHeader(showHeader ? tableHeader : null);
    }

    @Override
    protected void onSortableChange() {
        // do nothing ?
    }

    @Override
    protected void onInformationChange() {
        ((CustomTableModel) table.getModel()).setInformation(information);
    }

    @Override
    protected void onSortInfoChange() {
        // do nothing ?
    }

    private void onTransferHandlerChange() {
        TransferHandler th = getTransferHandler();
        table.setTransferHandler(th != null ? th : new TsCollectionTransferHandler());
    }

    private void onComponentPopupMenuChange() {
        JPopupMenu popupMenu = getComponentPopupMenu();
        table.setComponentPopupMenu(popupMenu != null ? popupMenu : buildPopupMenu());
    }
    //</editor-fold>

    protected JPopupMenu buildPopupMenu() {
        ActionMap am = getActionMap();
        JPopupMenu result = buildListMenu().getPopupMenu();

        int index = 11;
        JMenuItem item;

        result.insert(new JSeparator(), index++);

        item = new JMenuItem("Original order");
        item.setEnabled(false);
        result.add(item, index++);

        final JMenuItem unlock = new JMenuItem(new AbstractAction("Unlock") {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                TsCollection collection = getTsCollection();
                if (collection.isLocked()) {
                    TsCollection ncol = TsFactory.instance.createTsCollection();
                    ncol.quietAppend(collection);
                    setTsCollection(ncol);
                }
            }
        });
        result.add(unlock, index++);

        result.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                boolean locked = getTsCollection().isLocked() || updateMode == TsUpdateMode.None || !interactive_;
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

    private final class InformationComparator implements Comparator {

        private final int index;

        public InformationComparator(int index) {
            this.index = index;
        }

        @Override
        public int compare(Object o1, Object o2) {
            switch (information.get(index)) {
                case Name:
                case Id:
                case Source:
                    return ((String) o1).compareTo((String) o2);
                case Frequency:
                    return ((TsFrequency) o1).compareTo(((TsFrequency) o2));
                case Start:
                    return ((TsPeriod) o1).firstday().compareTo(((TsPeriod) o2).firstday());
                case End:
                    return ((TsPeriod) o1).lastday().compareTo(((TsPeriod) o2).lastday());
                case Length:
                    return ((Integer) o1).compareTo((Integer) o2);
                case Data:
                    return -1;
                case TsIdentifier:
                    return ((TsIdentifier) o1).getName().compareTo(((TsIdentifier) o2).getName());
            }
            return -1;
        }
    }

    private static final class CustomTableModel extends AbstractTableModel {

        private Ts[] data;
        private List<InfoType> information;

        public CustomTableModel(Ts[] data, List<InfoType> information) {
            this.data = data;
            this.information = information;
        }

        public void setData(Ts[] data) {
            this.data = data;
            fireTableDataChanged();
        }

        public void setInformation(List<InfoType> information) {
            this.information = information;
            fireTableStructureChanged();
        }

        @Override
        public int getRowCount() {
            return data.length;
        }

        @Override
        public int getColumnCount() {
            return information.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex == -1) {
                return null;
            }
            Ts ts = data[rowIndex];
            switch (information.get(columnIndex)) {
                case Name:
                    return ts.getName();
                case Id:
                    return ts.getMoniker().getId();
                case Source:
                    return ts.getMoniker().getSource();
                case Frequency:
                    return ts.hasData().equals(TsStatus.Valid) ? ts.getTsData().getFrequency() : null;
                case Start:
                    return ts.hasData().equals(TsStatus.Valid) ? ts.getTsData().getDomain().getStart() : null;
                case End:
                    return ts.hasData().equals(TsStatus.Valid) ? ts.getTsData().getDomain().getLast() : null;
                case Length:
                    return ts.hasData().equals(TsStatus.Valid) ? ts.getTsData().getLength() : null;
                case Data:
                    switch (ts.hasData()) {
                        case Valid:
                            return ts.getTsData();
                        case Invalid:
                            String cause = ts.getInvalidDataCause();
                            return !Strings.isNullOrEmpty(cause) ? cause : "Invalid";
                        case Undefined:
                            return "loading";
                        default:
                            return "Unknown error";
                    }
                case TsIdentifier:
                    return new TsIdentifier(ts.getName(), ts.getMoniker());
            }
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getColumnName(int column) {
            return information.get(column).name();
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (information.get(columnIndex)) {
                case Frequency:
                    return TsFrequency.class;
                case Start:
                case End:
                    return TsPeriod.class;
                case Length:
                    return Integer.class;
                case Data:
                    return TsData.class;
                case TsIdentifier:
                    return TsIdentifier.class;
            }
            return super.getColumnClass(columnIndex);
        }
    }

    private final class ListTableSelectionListener extends TsCollectionSelectionListener {

        @Override
        protected int indexToModel(int index) {
            return table.convertRowIndexToModel(index);
        }

        @Override
        protected int indexToView(int index) {
            return table.convertRowIndexToView(index);
        }
    }

    private static final class TsIdentifierTableCellRenderer extends DefaultTableCellRenderer {

        private final MonikerUI monikerUI = MonikerUI.getDefault();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel result = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value != null) {
                TsIdentifier id = (TsIdentifier) value;
                String text = id.getName();
                if (text.isEmpty()) {
                    result.setText(" ");
                    result.setToolTipText(null);
                } else if (text.startsWith("<html>")) {
                    result.setText(text);
                    result.setToolTipText(text);
                } else {
                    result.setText(MultiLineNameUtil.join(text));
                    result.setToolTipText(MultiLineNameUtil.toHtml(text));
                }
                result.setIcon(monikerUI.getIcon(id.getMoniker()));
            }
            return result;
        }
    }

    private static final class TsDataTableCellRenderer implements TableCellRenderer {

        private final Formatter<Number> formatter;
        private final TsSparklineCellRenderer dataRenderer;
        private final DefaultTableCellRenderer labelRenderer;

        public TsDataTableCellRenderer(DataFormat obsFormat) {
            this.formatter = obsFormat.numberFormatter();
            this.dataRenderer = new TsSparklineCellRenderer();
            this.labelRenderer = new DefaultTableCellRenderer();
            labelRenderer.setForeground(StandardSwingColor.TEXT_FIELD_INACTIVE_FOREGROUND.value());
            labelRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof TsData) {
                TsData data = (TsData) value;
                switch (data.getObsCount()) {
                    case 0:
                        return renderUsingLabel(table, "No obs", isSelected, hasFocus, row, column);
                    case 1:
                        return renderUsingLabel(table, "Single: " + formatter.format(data.get(0)), isSelected, hasFocus, row, column);
                    default:
                        return renderUsingSparkline(table, value, isSelected, hasFocus, row, column);
                }
            }
            return renderUsingLabel(table, value, isSelected, hasFocus, row, column);
        }

        private Component renderUsingSparkline(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return dataRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

        private Component renderUsingLabel(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            labelRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            labelRenderer.setToolTipText(labelRenderer.getText());
            return labelRenderer;
        }
    }

    private static final class DropUI extends LayerUI<JScrollPane> {

        private static final String RENDERER_PROPERTY = "renderer";
        private static final String DROP_PROPERTY = "drop";

        private final CellRendererPane cellRendererPane;
        private final TableDropSupport dropLocationSupport;
        private XTable.NoDataRenderer renderer;

        public DropUI() {
            this.cellRendererPane = new CellRendererPane();
            this.dropLocationSupport = new TableDropSupport((x, y) -> firePropertyChange(DROP_PROPERTY, null, y));
            this.renderer = new XTable.DefaultNoDataRenderer("No data", "Drop data");
        }

        public void setRenderer(XTable.NoDataRenderer renderer) {
            XTable.NoDataRenderer old = this.renderer;
            this.renderer = renderer;
            firePropertyChange(RENDERER_PROPERTY, old, this.renderer);
        }

        @Override
        public void applyPropertyChange(PropertyChangeEvent evt, JLayer<? extends JScrollPane> l) {
            switch (evt.getPropertyName()) {
                case RENDERER_PROPERTY:
                case DROP_PROPERTY:
                    l.repaint();
                    break;
            }
        }

        private JTable getTable(JComponent c) {
            return (JTable) ((JLayer<JScrollPane>) c).getView().getViewport().getView();
        }

        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            dropLocationSupport.register(getTable(c));
        }

        @Override
        public void uninstallUI(JComponent c) {
            dropLocationSupport.unregister(getTable(c));
            super.uninstallUI(c);
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            super.paint(g, c);
            JTable table = getTable(c);
            boolean drop = table.getDropLocation() != null;
            if (table.getRowCount() == 0 || drop) {
                int headerHeight = ((JLayer<JScrollPane>) c).getView().getColumnHeader().getHeight();
                cellRendererPane.paintComponent(g, renderer.getNoDataRendererComponent(table, drop), c, 0, headerHeight, c.getWidth(), c.getHeight() - headerHeight);
            }
        }
    }

    private static final class TableDropSupport {

        private final BiConsumer<JTable, Boolean> onDropChange;
        private boolean drop;

        public TableDropSupport(BiConsumer<JTable, Boolean> onDropChange) {
            this.onDropChange = onDropChange;
            this.drop = false;
        }

        public void register(JTable table) {
            table.addPropertyChangeListener(this::onPropertyChange);
        }

        public void unregister(JTable table) {
            table.removePropertyChangeListener(this::onPropertyChange);
        }

        private void onPropertyChange(PropertyChangeEvent evt) {
            switch (evt.getPropertyName()) {
                case "dropLocation":
                    boolean old = drop;
                    drop = evt.getNewValue() != null;
                    // avoid event storm
                    if (old != drop) {
                        onDropChange.accept((JTable) evt.getSource(), drop);
                    }
                    break;
            }
        }
    }
}
