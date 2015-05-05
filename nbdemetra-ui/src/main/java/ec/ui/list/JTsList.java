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
import ec.nbdemetra.ui.awt.MultiLineString;
import ec.nbdemetra.ui.awt.TableColumnModelAdapter;
import ec.tss.*;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import ec.ui.ATsList;
import ec.ui.DemoUtils;
import ec.ui.chart.TsSparklineCellRenderer;
import ec.util.chart.swing.SwingColorSchemeSupport;
import ec.util.grid.swing.XTable;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.StandardSwingColor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.beans.Beans;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Comparator;
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

    public JTsList() {
        this.table = buildTable();
        this.selectionListener = new ListTableSelectionListener();
        table.getSelectionModel().addListSelectionListener(selectionListener);
        table.setComponentPopupMenu(buildPopupMenu());
        this.tableHeader = table.getTableHeader();

        setLayout(new BorderLayout());
        add(new JLayer<>(NbComponents.newJScrollPane(table), new DropUI()), BorderLayout.CENTER);

        onUpdateModeChange();

        if (Beans.isDesignTime()) {
            setTsCollection(DemoUtils.randomTsCollection(3));
            setTsUpdateMode(TsUpdateMode.None);
            setPreferredSize(new Dimension(200, 150));
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Events handlers">
    @Override
    protected void onDataFormatChange() {
        // do nothing
    }

    @Override
    protected void onColorSchemeChange() {
        // do nothing
    }

    @Override
    protected void onCollectionChange() {
        selectionListener.setEnabled(false);
        ((CustomTableModel) table.getModel()).fireTableDataChanged();
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
        ((DropUI) ((JLayer<?>) getComponent(0)).getUI()).setMessage(message);
        ((DropUI) ((JLayer<?>) getComponent(0)).getUI()).setOnDropMessage(message);
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
        ((CustomTableModel) table.getModel()).fireTableStructureChanged();
    }

    @Override
    protected void onSortInfoChange() {
        // do nothing ?
    }
    //</editor-fold>

    private ETable buildTable() {
        final ETable result = new ETable();

        int cellPaddingHeight = 2;
        result.setRowHeight(result.getFontMetrics(result.getFont()).getHeight() + cellPaddingHeight * 2 + 1);
//        result.setRowMargin(cellPaddingHeight);

        result.setFullyNonEditable(true);
        result.setShowHorizontalLines(true);
        result.setBorder(null);
        Color newGridColor = StandardSwingColor.CONTROL.value();
        if (newGridColor != null) {
            result.setGridColor(newGridColor);
        }

        result.setDefaultRenderer(TsData.class, new TsDataTableCellRenderer());
        result.setDefaultRenderer(TsPeriod.class, new TsPeriodTableCellRenderer());
        result.setDefaultRenderer(TsFrequency.class, new TsFrequencyTableCellRenderer());
        result.setDefaultRenderer(TsIdentifier.class, new TsIdentifierTableCellRenderer());

        result.getColumnModel().addColumnModelListener(new TableColumnModelAdapter() {
            @Override
            public void columnAdded(TableColumnModelEvent e) {
                ETableColumnModel columnModel = (ETableColumnModel) e.getSource();
                for (int i = e.getFromIndex(); i < e.getToIndex(); i++) {
                    final ETableColumn column = (ETableColumn) columnModel.getColumn(i);
                    column.setNestedComparator(new InformationComparator(i));
                }
            }
        });

        result.setModel(new CustomTableModel());
        XTable.setWidthAsPercentages(result, .4, .1, .1, .1, .3);

        fillActionMap(result.getActionMap());
        fillInputMap(result.getInputMap());
        result.addMouseListener(new TsActionMouseAdapter());
        result.setDragEnabled(true);
        result.setTransferHandler(new TsCollectionTransferHandler());
        result.setFillsViewportHeight(true);
        result.setSelectionMode(multiSelection ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION);

        return result;
    }

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

    private final class CustomTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return collection.getCount();
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
            Ts ts = collection.get(rowIndex);
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
                    result.setText(MultiLineString.join(text));
                    result.setToolTipText(MultiLineString.toHtml(text));
                }
                result.setIcon(monikerUI.getIcon(id.getMoniker()));
            }
            return result;
        }
    }

    private static final class TsDataTableCellRenderer implements TableCellRenderer {

        private final TsSparklineCellRenderer dataRenderer;
        private final DefaultTableCellRenderer labelRenderer;

        public TsDataTableCellRenderer() {
            this.dataRenderer = new TsSparklineCellRenderer();
            this.labelRenderer = new DefaultTableCellRenderer();
            labelRenderer.setForeground(StandardSwingColor.TEXT_FIELD_INACTIVE_FOREGROUND.value());
            labelRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof TsData) {
                return dataRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
            labelRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            labelRenderer.setToolTipText(labelRenderer.getText());
            return labelRenderer;
        }
    }

    private static final class DropUI extends LayerUI<JScrollPane> {

        public static final String MESSAGE_PROPERTY = "message";
        public static final String ON_DROP_MESSAGE_PROPERTY = "onDropMessage";

        private final PropertyChangeListener dropLocationListener;
        private String message = "No data";
        private String onDropMessage = "Drop data";
        private boolean hasDropLocation = false;

        public DropUI() {
            this.dropLocationListener = new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    switch (evt.getPropertyName()) {
                        case "dropLocation":
                            boolean old = hasDropLocation;
                            hasDropLocation = evt.getNewValue() != null;
                            if (old != hasDropLocation) {
                                ((Component) evt.getSource()).repaint();
                            }
                            break;
                    }
                }
            };
        }

        //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            String old = this.message;
            this.message = message;
            firePropertyChange(MESSAGE_PROPERTY, old, this.message);
        }

        public String getOnDropMessage() {
            return onDropMessage;
        }

        public void setOnDropMessage(String onDropMessage) {
            String old = this.onDropMessage;
            this.onDropMessage = onDropMessage;
            firePropertyChange(ON_DROP_MESSAGE_PROPERTY, old, this.onDropMessage);
        }
        //</editor-fold>

        @Override
        public void applyPropertyChange(PropertyChangeEvent evt, JLayer<? extends JScrollPane> l) {
            l.repaint();
        }

        private JTable getTable(JComponent c) {
            return (JTable) ((JLayer<JScrollPane>) c).getView().getViewport().getView();
        }

        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            getTable(c).addPropertyChangeListener(dropLocationListener);
        }

        @Override
        public void uninstallUI(JComponent c) {
            getTable(c).removePropertyChangeListener(dropLocationListener);
            super.uninstallUI(c);
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            super.paint(g, c);
            JTable table = getTable(c);
            if (table.getRowCount() == 0 || hasDropLocation) {
                String text = hasDropLocation ? onDropMessage : message;
                Color background = hasDropLocation ? table.getSelectionBackground() : table.getBackground();
                Color foreground = hasDropLocation ? table.getSelectionForeground() : table.getForeground();
                Font font = table.getFont();

                Graphics2D g2d = (Graphics2D) g.create();

                JViewport xxx = ((JLayer<JScrollPane>) c).getView().getColumnHeader();
                int headerHeight = xxx.getHeight();

                g2d.setColor(SwingColorSchemeSupport.withAlpha(background, 200));
                g2d.fillRect(0, headerHeight, c.getWidth(), c.getHeight());
                g2d.setColor(foreground);
                g2d.setFont(font);

                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                FontMetrics fm = g2d.getFontMetrics();
                float x = (c.getWidth() - fm.stringWidth(text)) / 2f;
                float y = (fm.getAscent() + (headerHeight + c.getHeight() - (fm.getAscent() + fm.getDescent())) / 2f);

                g2d.drawString(text, x, y);

                if (hasDropLocation) {
                    Image image = FontAwesome.FA_DOWNLOAD.getImage(foreground, font.getSize2D() * 2);
                    g2d.drawImage(image, (c.getWidth() - image.getWidth(table)) / 2, (headerHeight + c.getHeight() - image.getHeight(table)) / 2 - 15, table);
                }

                g2d.dispose();
            }
        }
    }
}
