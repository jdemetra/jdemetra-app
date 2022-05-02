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
package demetra.desktop.core.components;

import demetra.desktop.components.JTsTable;
import demetra.desktop.components.TsSelectionBridge;
import demetra.desktop.components.parts.*;
import demetra.desktop.util.*;
import ec.util.table.swing.JTables;
import ec.util.various.swing.StandardSwingColor;
import org.netbeans.swing.etable.ETable;
import org.netbeans.swing.etable.ETableColumn;
import org.netbeans.swing.etable.ETableColumnModel;

import javax.swing.*;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static ec.util.chart.swing.SwingColorSchemeSupport.withAlpha;
import static ec.util.various.swing.ModernUI.createDropBorder;

/**
 * @author Kristof Bayens
 * @author Philippe Charles
 */
public final class TsTableUI implements InternalUI<JTsTable> {

    private JTsTable target;

    private final ETable table = new ETable();
    private final JTableHeader tableHeader = table.getTableHeader();
    private final DropRenderer dropRenderer = new DropRenderer();

    private ListTableSelectionListener selectionListener;
    private HasObsFormatResolver obsFormatResolver;

    @Override
    public void install(JTsTable component) {
        this.target = component;

        this.selectionListener = new ListTableSelectionListener(target);
        this.obsFormatResolver = new HasObsFormatResolver(target, this::onDataFormatChange);

        registerActions();
        registerInputs();

        initTable();

        enableSeriesSelection();
        enableOpenOnDoubleClick();
        enableProperties();

        target.setLayout(new BorderLayout());
        target.add(NbComponents.newJScrollPane(new JLayer<>(table, new DropUI(dropRenderer))), BorderLayout.CENTER);
    }

    private void registerActions() {
        HasTsCollectionSupport.registerActions(target, target.getActionMap());
        HasObsFormatSupport.registerActions(target, target.getActionMap());
        ActionMaps.copyEntries(target.getActionMap(), false, table.getActionMap());
    }

    private void registerInputs() {
        HasTsCollectionSupport.registerInputs(target.getInputMap());
        InputMaps.copyEntries(target.getInputMap(), false, table.getInputMap());
    }

    private void initTable() {
        int cellPaddingHeight = 2;
        table.setRowHeight(table.getFontMetrics(table.getFont()).getHeight() + cellPaddingHeight * 2 + 1);
//        table.setRowMargin(cellPaddingHeight);

        table.setFullyNonEditable(true);
        table.setShowHorizontalLines(true);
        table.setBorder(null);
        StandardSwingColor.CONTROL.lookup().ifPresent(table::setGridColor);

        table.getColumnModel().addColumnModelListener(new TableColumnModelAdapter() {
            @Override
            public void columnAdded(TableColumnModelEvent e) {
                ETableColumnModel columnModel = (ETableColumnModel) e.getSource();
                for (int i = e.getFromIndex(); i <= e.getToIndex(); i++) {
                    ETableColumn column = (ETableColumn) columnModel.getColumn(i);
                    column.setNestedComparator(target.getColumns().get(i).getComparator());
                    column.setCellRenderer(target.getColumns().get(i).getRenderer().apply(target));
                }
            }
        });

        table.setModel(new TsTableModel());
        table.setDragEnabled(true);
        table.setFillsViewportHeight(true);

        onUpdateModeChange();
        onTransferHandlerChange();
        onComponentPopupMenuChange();
        onColumnsChange();
        onWidthAsPercentagesChange();
    }

    //<editor-fold defaultstate="collapsed" desc="Interactive stuff">
    private void enableSeriesSelection() {
        table.getSelectionModel().addListSelectionListener(selectionListener);
    }

    private void enableOpenOnDoubleClick() {
        ActionMaps.onDoubleClick(target.getActionMap(), HasTsCollection.OPEN_ACTION, table);
    }

    private void enableProperties() {
        target.addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case JTsTable.SHOW_HEADER_PROPERTY:
                    onShowHeaderChange();
                    break;
                case JTsTable.COLUMNS_PROPERTY:
                    onColumnsChange();
                    break;
                case JTsTable.WIDTH_AS_PERCENTAGES_PROPERTY:
                    onWidthAsPercentagesChange();
                    break;
                case HasTsCollection.TS_COLLECTION_PROPERTY:
                    onCollectionChange();
                    break;
                case TsSelectionBridge.TS_SELECTION_PROPERTY:
                    onSelectionChange();
                    break;
                case HasTsCollection.TS_UPDATE_MODE_PROPERTY:
                    onUpdateModeChange();
                    break;
                case HasObsFormat.OBS_FORMAT_PROPERTY:
                    onDataFormatChange();
                    break;
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
    private void onDataFormatChange() {
        table.repaint();
    }

    private void onCollectionChange() {
        selectionListener.setEnabled(false);
        ((TsTableModel) table.getModel()).setData(target.getTsCollection());
        selectionListener.setEnabled(true);
        onSelectionChange();
    }

    private void onSelectionChange() {
        if (selectionListener.isEnabled()) {
            selectionListener.setEnabled(false);
            selectionListener.changeSelection(table.getSelectionModel());
            selectionListener.setEnabled(true);
        }
    }

    private void onUpdateModeChange() {
        String message = target.getTsUpdateMode().isReadOnly() ? "No data" : "Drop data here";
        dropRenderer.setMessage(message);
        dropRenderer.setOnDropMessage(message);
        table.repaint();
    }

    private void onShowHeaderChange() {
        table.setTableHeader(target.isShowHeader() ? tableHeader : null);
    }

    private void onColumnsChange() {
        ((TsTableModel) table.getModel()).setColumns(target.getColumns());
    }

    private void onWidthAsPercentagesChange() {
        Optional.ofNullable(target.getWidthAsPercentages()).ifPresent(o -> JTables.setWidthAsPercentages(table, o));
    }

    private void onTransferHandlerChange() {
        table.setTransferHandler(Optional.ofNullable(target.getTransferHandler()).orElseGet(this::getDefaultTransferHander));
    }

    private void onComponentPopupMenuChange() {
        table.setComponentPopupMenu(Optional.ofNullable(target.getComponentPopupMenu()).orElseGet(this::getDefaultPopupMenu));
    }
    //</editor-fold>

    private TransferHandler getDefaultTransferHander() {
        return HasTsCollectionSupport.newTransferHandler(target);
    }

    private JPopupMenu getDefaultPopupMenu() {
        JMenu result = new JMenu();
        result.add(HasTsCollectionSupport.newOpenMenu(target));
        result.add(HasTsCollectionSupport.newOpenWithMenu(target));
        result.add(HasTsCollectionSupport.newSaveMenu(target));
        result.add(HasTsCollectionSupport.newRenameMenu(target));
        result.add(HasTsCollectionSupport.newFreezeMenu(target));
        result.add(HasTsCollectionSupport.newCopyMenu(target));
        result.add(HasTsCollectionSupport.newPasteMenu(target));
        result.add(HasTsCollectionSupport.newDeleteMenu(target));
        result.addSeparator();
        result.add(HasTsCollectionSupport.newSelectAllMenu(target));
        result.add(HasTsCollectionSupport.newClearMenu(target));

        result.add(new AbstractAction("DEBUG") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(target.getTsCollection());
            }
        });

        return result.getPopupMenu();
    }

    private static final class TsTableModel extends AbstractTableModel {

        private demetra.timeseries.TsCollection data;
        private List<JTsTable.Column> columns;

        public TsTableModel() {
            this.data = demetra.timeseries.TsCollection.EMPTY;
            this.columns = Collections.emptyList();
        }

        public void setData(demetra.timeseries.TsCollection data) {
            this.data = data;
            fireTableDataChanged();
        }

        public void setColumns(List<JTsTable.Column> columns) {
            this.columns = columns;
            fireTableStructureChanged();
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return columns.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex == -1) {
                return null;
            }
            demetra.timeseries.Ts ts = data.get(rowIndex);
            return columns.get(columnIndex).getMapper().apply(ts);
        }

        @Override
        public String getColumnName(int column) {
            return columns.get(column).getName();
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columns.get(columnIndex).getType();
        }
    }

    private final class ListTableSelectionListener extends InternalTsSelectionAdapter {

        private ListTableSelectionListener(HasTsCollection outer) {
            super(outer);
        }

        @Override
        protected int indexToModel(int index) {
            return table.convertRowIndexToModel(index);
        }

        @Override
        protected int indexToView(int index) {
            return table.convertRowIndexToView(index);
        }
    }

    private static final class DropRenderer implements Function<JTable, Component> {

        private final JLabel label;
        private String message;
        private String onDropMessage;

        public DropRenderer() {
            this.label = new JLabel();
            this.message = "No data";
            this.onDropMessage = "Drop data";
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setOnDropMessage(String onDropMessage) {
            this.onDropMessage = onDropMessage;
        }

        @Override
        public Component apply(JTable table) {
            boolean hasDropLocation = table.getDropLocation() != null;
            if (table.getRowCount() > 0 && !hasDropLocation) {
                return null;
            }
            if (hasDropLocation) {
                label.setText(onDropMessage);
                label.setForeground(table.getSelectionForeground());
                label.setBackground(withAlpha(table.getSelectionBackground(), 200));
                label.setBorder(createDropBorder(label.getForeground()));
            } else {
                label.setText(message);
                label.setForeground(withAlpha(table.getForeground(), 100));
                label.setBackground(table.getBackground());
                label.setBorder(null);
            }
            if (label.getText().startsWith("<html>")) {
                label.setFont(table.getFont());
            } else {
                label.setFont(table.getFont().deriveFont(table.getFont().getSize2D() * 2));
            }
            return label;
        }
    }

    private static final class DropUI extends ExtLayerUI<JTable> {

        private static final String DROP_PROPERTY = "drop";

        private final TableDropSupport dropLocationSupport;

        public DropUI(DropRenderer renderer) {
            super(renderer);
            this.dropLocationSupport = new TableDropSupport((x, y) -> firePropertyChange(DROP_PROPERTY, null, y));
        }

        @Override
        public void applyPropertyChange(PropertyChangeEvent evt, JLayer<? extends JTable> l) {
            switch (evt.getPropertyName()) {
                case DROP_PROPERTY:
                    l.repaint();
                    break;
            }
        }

        private JTable getTable(JComponent c) {
            return ((JLayer<JTable>) c).getView();
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
