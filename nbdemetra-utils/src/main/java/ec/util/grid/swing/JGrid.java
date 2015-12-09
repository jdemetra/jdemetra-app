/*
 * Copyright 2013 National Bank of Belgium
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
package ec.util.grid.swing;

import static ec.util.chart.swing.SwingColorSchemeSupport.withAlpha;
import ec.util.grid.CellIndex;
import static ec.util.grid.swing.AGrid.HOVERED_CELL_PROPERTY;
import ec.util.various.swing.LineBorder2;
import static ec.util.various.swing.ModernUI.withEmptyBorders;
import static ec.util.various.swing.StandardSwingColor.TABLE_BACKGROUND;
import static ec.util.various.swing.StandardSwingColor.TABLE_FOREGROUND;
import static ec.util.various.swing.StandardSwingColor.TABLE_HEADER_BACKGROUND;
import static ec.util.various.swing.StandardSwingColor.TABLE_HEADER_FOREGROUND;
import static ec.util.various.swing.StandardSwingColor.TABLE_SELECTION_BACKGROUND;
import static ec.util.various.swing.StandardSwingColor.TABLE_SELECTION_FOREGROUND;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.LayerUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * A grid component for Swing that differs from a JTable by adding a row header.
 *
 * @author Jeremy Demortier
 * @author Philippe Charles
 * @author Mats Maggi
 */
public final class JGrid extends AGrid {

    // Setting constants used to implement zoom
    private static final int COLUMN_WIDTH = new TableColumn().getPreferredWidth();

    private final XTable main;
    private final JScrollPane scrollPane;
    private final FixedColumnTable fct;
    private final InternalTableModel internalModel;
    private final CellSelectionListener cellSelectionListener;
    private final float initialFontSize;
    private final int initialRowHeight;
    private float zoomRatio;

    public JGrid() {
        this.main = new XTable();
        this.scrollPane = withEmptyBorders(new JScrollPane(main));
        // This splits the original table into two distinct tables. The first
        // one has only one column used for the headers,
        // while the other has all the remaining columns. They share the same
        // model but not the columns model!
        this.fct = new FixedColumnTable(1, scrollPane);
        this.internalModel = new InternalTableModel();
        this.cellSelectionListener = new CellSelectionListener();
        this.initialFontSize = main.getFont().getSize2D();
        this.initialRowHeight = main.getRowHeight();
        this.zoomRatio = 1f;

        initComponents();

        // InputMap and ActionMap
        setInputMap(WHEN_FOCUSED, main.getInputMap(WHEN_FOCUSED));
        setInputMap(WHEN_IN_FOCUSED_WINDOW, main.getInputMap(WHEN_IN_FOCUSED_WINDOW));
        setInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, main.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT));

        enableSelectColumnOnHeader();
        // we have to split enableDragOnHeader to prevent event nigthmare
        enableDragOnHeader(main.getTableHeader());
        enableSelectAllOnHeader();
        enableDragOnHeader(fct.getFixedTable().getTableHeader());
        enableCellHovering();
        enableCellSelection();
        enableProperties();

        setLayout(new BorderLayout());
        add(new JLayer<>(scrollPane, new NoDataUI()), BorderLayout.CENTER);
    }

    private void initComponents() {
        main.setModel(internalModel);
        main.setOddBackground(null);

        // This makes sure user can drop on all component and not only on present cell
        main.setFillsViewportHeight(true);

        main.getTableHeader().setReorderingAllowed(false);
        main.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        scrollPane.setViewportView(main);

        // This makes the viewport background same as table background
        // http://www.jroller.com/santhosh/date/20050524#jtable_becomes_uglier_with_auto
        scrollPane.getViewport().setBackground(main.getBackground());

        fct.getFixedTable().getTableHeader().setReorderingAllowed(false);
        fct.getFixedTable().setFillsViewportHeight(true);
        fct.getFixedTable().setIntercellSpacing(new Dimension(0, 0));
        fct.getFixedTable().setShowGrid(false);

        // FixedColumnTable#makeColumns(int) doesn't use zoomRatio, so we have to apply it afterwards
        internalModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    applyZoomRatioOnColumnsWidth();
                }
            }
        });

        setRowRenderer(new RowRenderer(this));
        setColumnRenderer(new ColumnRenderer(this));
        setCornerRenderer(new CornerRenderer());
        setDefaultRenderer(Object.class, new GridCellRenderer(this));

        onModelChange();
        onRowSelectionAllowedChange();
        onColumnSelectionAllowedChange();
        onDragEnabledChange();
        onGridColorChange();
        onNoDataRendererChange();
        onTransferHandlerChange();
        onRowSelectionModelChange();
        onColumnSelectionModelChange();
        onComponentPopupMenuChange();
    }

    public TableCellRenderer getDefaultRenderer(Class<?> aClass) {
        return main.getDefaultRenderer(aClass);
    }

    public void setDefaultRenderer(Class<?> aClass, TableCellRenderer tableCellRenderer) {
        main.setDefaultRenderer(aClass, tableCellRenderer);
    }

    public void setColumnRenderer(TableCellRenderer renderer) {
        main.getTableHeader().setDefaultRenderer(renderer);
    }

    public TableCellRenderer getRowRenderer() {
        return fct.getFixedTable().getDefaultRenderer(Object.class);
    }

    public void setRowRenderer(TableCellRenderer renderer) {
        fct.getFixedTable().setDefaultRenderer(Object.class, renderer);
    }

    public void setCornerRenderer(TableCellRenderer renderer) {
        fct.getFixedTable().getTableHeader().setDefaultRenderer(renderer);
    }

    @Deprecated
    public void setOddBackground(@Nullable Color oddBackground) {
        main.setOddBackground(oddBackground);
    }

    @Override
    public synchronized void addMouseListener(MouseListener l) {
        super.addMouseListener(l);
        main.addMouseListener(l);
    }

    @Override
    public synchronized void removeMouseListener(MouseListener l) {
        main.removeMouseListener(l);
        super.removeMouseListener(l);
    }

    @Deprecated
    public void setRowHeight(int rowHeight) {
        main.setRowHeight(rowHeight);
    }

    @Deprecated
    public int getRowHeight() {
        return main.getRowHeight();
    }

    @Deprecated
    public JTableHeader getTableHeader() {
        return main.getTableHeader();
    }

    @Deprecated
    public ListSelectionModel getSelectionModel() {
        return getRowSelectionModel();
    }

    @Deprecated
    public TableColumnModel getColumnModel() {
        return main.getColumnModel();
    }

    @Deprecated
    public int[] getSelectedColumns() {
        return main.getSelectedColumns();
    }

    @Deprecated
    public int[] getSelectedRows() {
        return main.getSelectedRows();
    }

    //<editor-fold defaultstate="collapsed" desc="Event handlers">
    private void onModelChange() {
        internalModel.setGridModel(model);
    }

    private void onRowSelectionAllowedChange() {
        main.setRowSelectionAllowed(rowSelectionAllowed);
        fct.getFixedTable().setRowSelectionAllowed(rowSelectionAllowed);
    }

    private void onColumnSelectionAllowedChange() {
        main.setColumnSelectionAllowed(columnSelectionAllowed);
    }

    private void onHoveredCellChange() {
        scrollPane.repaint();
    }

    private void onSelectedCellChange() {
        if (cellSelectionListener.enabled) {
            cellSelectionListener.enabled = false;
            CellIndex index = getSelectedCell();
            if (CellIndex.NULL.equals(index)) {
                getRowSelectionModel().clearSelection();
                getColumnSelectionModel().clearSelection();
            } else {
                getRowSelectionModel().setSelectionInterval(index.getRow(), index.getRow());
                getColumnSelectionModel().setSelectionInterval(index.getColumn(), index.getColumn());
            }
            cellSelectionListener.enabled = true;
        }
        scrollPane.repaint();
    }

    private void onCrosshairVisibleChange() {
        repaint();
    }

    private void onDragEnabledChange() {
        main.setDragEnabled(dragEnabled);
        fct.getFixedTable().setDragEnabled(dragEnabled);
    }

    private void onFontChange() {
        // setFont() method now scales all elements when the font has the size changed
        Font font = getFont();

        zoomRatio = font.getSize2D() / initialFontSize;
        int rowHeight = (int) (initialRowHeight * zoomRatio);
        main.setRowHeight(rowHeight);
        fct.getFixedTable().setRowHeight(rowHeight);

        applyZoomRatioOnColumnsWidth();

        // Resize of the fonts according to the zoom ratio
        main.setFont(font);
        main.getTableHeader().setFont(font);
        fct.getFixedTable().setFont(font);
    }

    private void onTransferHandlerChange() {
        TransferHandler handler = getTransferHandler();
        main.setTransferHandler(handler);
        main.getTableHeader().setTransferHandler(handler);
        fct.getFixedTable().setTransferHandler(handler);
        fct.getFixedTable().getTableHeader().setTransferHandler(handler);
        scrollPane.setTransferHandler(handler);
    }

    private void onGridColorChange() {
        main.setGridColor(gridColor);
    }

    private void onNoDataRendererChange() {
        repaint();
    }

    private void onRowSelectionModelChange() {
        main.setSelectionModel(rowSelectionModel);
    }

    private void onColumnSelectionModelChange() {
        main.getColumnModel().setSelectionModel(columnSelectionModel);
    }

    private void onComponentPopupMenuChange() {
        JPopupMenu popupMenu = getComponentPopupMenu();
        main.setComponentPopupMenu(popupMenu);
        fct.getFixedTable().setComponentPopupMenu(popupMenu);
        scrollPane.setComponentPopupMenu(popupMenu);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Interactive stuff">
    private void enableProperties() {
        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case MODEL_PROPERTY:
                        onModelChange();
                        break;
                    case ROW_SELECTION_ALLOWED_PROPERTY:
                        onRowSelectionAllowedChange();
                        break;
                    case COLUMN_SELECTION_ALLOWED_PROPERTY:
                        onColumnSelectionAllowedChange();
                        break;
                    case HOVERED_CELL_PROPERTY:
                        onHoveredCellChange();
                        break;
                    case SELECTED_CELL_PROPERTY:
                        onSelectedCellChange();
                        break;
                    case CROSSHAIR_VISIBLE_PROPERTY:
                        onCrosshairVisibleChange();
                        break;
                    case DRAG_ENABLED_PROPERTY:
                        onDragEnabledChange();
                        break;
                    case GRID_COLOR_PROPERTY:
                        onGridColorChange();
                        break;
                    case NO_DATA_RENDERER_PROPERTY:
                        onNoDataRendererChange();
                        break;
                    case ROW_SELECTION_MODEL_PROPERTY:
                        onRowSelectionModelChange();
                        break;
                    case COLUMN_SELECTION_MODEL_PROPERTY:
                        onColumnSelectionModelChange();
                        break;
                    case "font":
                        onFontChange();
                        break;
                    case "transferHandler":
                        onTransferHandlerChange();
                        break;
                    case "componentPopupMenu":
                        onComponentPopupMenuChange();
                        break;
                }
            }
        });
    }

    private void enableSelectColumnOnHeader() {
        // click on header select the column
        main.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selection = main.getTableHeader().columnAtPoint(e.getPoint());
                getColumnSelectionModel().setSelectionInterval(selection, selection);
            }
        });
    }

    private void enableSelectAllOnHeader() {
        // click on header select/unselect all the columns
        fct.getFixedTable().getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ListSelectionModel columnSelectionModel = getColumnSelectionModel();
                ListSelectionModel rowSelectionModel = getRowSelectionModel();
                int columnCount = main.getColumnCount();
                int rowCount = main.getRowCount();

                if (columnCount == getCount(columnSelectionModel) || rowCount == getCount(rowSelectionModel)) {
                    columnSelectionModel.clearSelection();
                    rowSelectionModel.clearSelection();
                } else {
                    columnSelectionModel.setSelectionInterval(0, columnCount - 1);
                    rowSelectionModel.setSelectionInterval(0, rowCount - 1);
                }
            }
        });
    }

    private static void enableDragOnHeader(final JTableHeader tableHeader) {
        DragSource dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(tableHeader, DnDConstants.ACTION_COPY_OR_MOVE, new DragGestureListener() {
            @Override
            public void dragGestureRecognized(DragGestureEvent dge) {
                if (tableHeader.getResizingColumn() == null) {
                    TransferHandler transferHandler = tableHeader.getTransferHandler();
                    if (transferHandler != null) {
                        transferHandler.exportAsDrag(tableHeader, dge.getTriggerEvent(), TransferHandler.COPY);
                    }
                }
            }
        });
    }

    private CellIndex getIndex(MouseEvent e) {
        if (e.getSource() instanceof JTable) {
            JTable table = (JTable) e.getSource();
            Point point = e.getPoint();
            return CellIndex.valueOf(table.rowAtPoint(point), table.columnAtPoint(point));
        }
        return CellIndex.NULL;
    }

    private void enableCellHovering() {
        MouseMotionListener cellFocus = new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                setHoveredCell(getIndex(e));
            }

        };
        main.addMouseMotionListener(cellFocus);
        main.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setHoveredCell(getIndex(e));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setHoveredCell(getIndex(e));
            }
        });
    }

    private final class CellSelectionListener implements ListSelectionListener {

        boolean enabled = true;

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (enabled && !e.getValueIsAdjusting()) {
                enabled = false;
                ListSelectionModel rowSelectionModel = getRowSelectionModel();
                ListSelectionModel columnSelectionModel = getColumnSelectionModel();
                if (rowSelectionModel.isSelectionEmpty() || columnSelectionModel.isSelectionEmpty()) {
                    setSelectedCell(CellIndex.NULL);
                } else {
                    int row = rowSelectionModel.getLeadSelectionIndex();
                    int column = columnSelectionModel.getLeadSelectionIndex();
                    if (!getSelectedCell().equals(row, column)) {
                        setSelectedCell(CellIndex.valueOf(row, column));
                    }
                }
                enabled = true;
            }
        }
    }

    private void enableCellSelection() {
        getRowSelectionModel().addListSelectionListener(cellSelectionListener);
        getColumnSelectionModel().addListSelectionListener(cellSelectionListener);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Internal implementation">
    private static final class InternalTableModel extends AbstractTableModel implements TableModelListener {

        private GridModel gridModel;

        InternalTableModel() {
            this.gridModel = GridModels.empty();
        }

        public void setGridModel(GridModel gridModel) {
            this.gridModel.removeTableModelListener(this);
            this.gridModel = gridModel != null ? gridModel : GridModels.empty();
            this.gridModel.addTableModelListener(this);
            fireTableStructureChanged();
        }

        public GridModel getGridModel() {
            return gridModel;
        }

        public boolean hasData() {
            return gridModel.getRowCount() > 0 || gridModel.getColumnCount() > 0;
        }

        @Override
        public int getRowCount() {
            return gridModel.getRowCount();
        }

        @Override
        public int getColumnCount() {
            return gridModel.getColumnCount() + 1;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return columnIndex == 0 ? gridModel.getRowName(rowIndex) : gridModel.getValueAt(rowIndex, columnIndex - 1);
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnIndex == 0 ? null : gridModel.getColumnName(columnIndex - 1);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? String.class : gridModel.getColumnClass(columnIndex - 1);
        }

        @Override
        public void tableChanged(TableModelEvent e) {
            fireTableChanged(e);
        }
    }

    private static int getCount(ListSelectionModel m) {
        return !m.isSelectionEmpty() ? (m.getMaxSelectionIndex() - m.getMinSelectionIndex() + 1) : 0;
    }

    private void applyZoomRatioOnColumnsWidth() {
        int columnWidth = (int) (COLUMN_WIDTH * zoomRatio);

        // Resize of data columns according to the zoom ratio
        applyColumnsWidth(columnWidth, main);

        // Resize of row headers according to the zoom ratio
        JTable j = fct.getFixedTable();
        j.setPreferredScrollableViewportSize(new Dimension(columnWidth, main.getRowHeight()));
        applyColumnsWidth(columnWidth, j);
    }

    private static void applyColumnsWidth(int columnWidth, JTable table) {
        // Resize of data columns according to the zoom ratio
        Enumeration<TableColumn> cols = table.getTableHeader().getColumnModel().getColumns();
        while (cols.hasMoreElements()) {
            cols.nextElement().setPreferredWidth(columnWidth);
        }
    }

    private final class NoDataUI extends LayerUI<JScrollPane> {

        private static final String HAS_DROP_LOCATION_PROPERTY = "hasDropLocation";

        private boolean hasDropLocation = false;
        private DropTarget dropTarget = null;

        private void setHasDropLocation(boolean hasDropLocation) {
            boolean old = this.hasDropLocation;
            this.hasDropLocation = hasDropLocation;
            firePropertyChange(HAS_DROP_LOCATION_PROPERTY, old, this.hasDropLocation);
        }

        @Override
        public void applyPropertyChange(PropertyChangeEvent evt, JLayer<? extends JScrollPane> l) {
            if (evt.getPropertyName().equals(HAS_DROP_LOCATION_PROPERTY)) {
                l.repaint();
            }
        }

        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            dropTarget = new DropTarget(scrollPane, new ForwardingListener() {

                @Override
                protected DropTarget getDelegate() {
                    return main.getDropTarget();
                }

                @Override
                public void dragEnter(DropTargetDragEvent dtde) {
                    super.dragEnter(dtde);
                    // FIXME: value still set to true even if import refused
                    setHasDropLocation(true);
                }

                @Override
                public void dragExit(DropTargetEvent dte) {
                    super.dragExit(dte);
                    setHasDropLocation(false);
                }

                @Override
                public void drop(DropTargetDropEvent dtde) {
                    super.drop(dtde);
                    setHasDropLocation(false);
                }
            });
            PropertyChangeListener listener = new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    switch (evt.getPropertyName()) {
                        case "dropLocation":
                            JTable table = (JTable) evt.getSource();
                            setHasDropLocation(table.getDropLocation() != null);
                            break;
                    }
                }
            };
            main.addPropertyChangeListener(listener);
            fct.getFixedTable().addPropertyChangeListener(listener);
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            super.paint(g, c);
            if (!internalModel.hasData()/* || hasDropLocation*/) {
                Component renderer = noDataRenderer.getNoDataRendererComponent(main, hasDropLocation);
                renderer.setSize(scrollPane.getSize());
                renderer.paint(g);
            }
        }
    }

    private static abstract class ForwardingListener implements DropTargetListener {

        @Nullable
        abstract protected DropTarget getDelegate();

        @Override
        public void dragEnter(DropTargetDragEvent dtde) {
            DropTarget dt = getDelegate();
            if (dt != null) {
                dt.dragEnter(dtde);
            }
        }

        @Override
        public void dragOver(DropTargetDragEvent dtde) {
            DropTarget dt = getDelegate();
            if (dt != null) {
                dt.dragOver(dtde);
            }
        }

        @Override
        public void dropActionChanged(DropTargetDragEvent dtde) {
            DropTarget dt = getDelegate();
            if (dt != null) {
                dt.dropActionChanged(dtde);
            }
        }

        @Override
        public void dragExit(DropTargetEvent dte) {
            DropTarget dt = getDelegate();
            if (dt != null) {
                dt.dragExit(dte);
            }
        }

        @Override
        public void drop(DropTargetDropEvent dtde) {
            DropTarget dt = getDelegate();
            if (dt != null) {
                dt.drop(dtde);
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Renderers">
    private abstract static class HeaderRenderer implements TableCellRenderer {

        protected final JLabel renderer;
        protected final GridUIResource gridResource;

        public HeaderRenderer() {
            this.renderer = new DefaultTableCellRenderer();
            this.gridResource = GridUIResource.getDefault();
            renderer.setOpaque(true);
        }

        abstract protected boolean isHeaderSelected(JTable table, int row, int column);

        abstract protected boolean isHeaderHovered(JTable table, int row, int column);

        @Override
        public JLabel getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String text = value != null ? value.toString() : "";
            if (text.isEmpty()) {
                renderer.setText(" ");
                renderer.setToolTipText(null);
            } else if (text.startsWith("<html>")) {
                renderer.setText(text);
                renderer.setToolTipText(text);
            } else {
                renderer.setText(text);
                renderer.setToolTipText(text);
            }
            renderer.setFont(table.getFont());
            CellUIResource cellResource = gridResource.getHeader(isHeaderSelected(table, row, column), isHeaderHovered(table, row, column));
            renderer.setBackground(cellResource.getBackground());
            renderer.setForeground(cellResource.getForeground());
            renderer.setBorder(cellResource.getBorder());
            int preferredHeight = table.getRowHeight() + 1;
            if (renderer.getPreferredSize().height != preferredHeight) {
                renderer.setPreferredSize(new Dimension(10, preferredHeight));
            }
            return renderer;
        }
    }

    private static final class RowRenderer extends HeaderRenderer {

        private final JGrid grid;

        public RowRenderer(JGrid grid) {
            this.grid = grid;
            renderer.setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        protected boolean isHeaderSelected(JTable table, int row, int column) {
            return table.getRowSelectionAllowed() && table.isRowSelected(row);
        }

        @Override
        protected boolean isHeaderHovered(JTable table, int row, int column) {
            return grid.getHoveredCell().getRow() == row;
        }
    }

    private static final class ColumnRenderer extends HeaderRenderer {

        private final JGrid grid;

        public ColumnRenderer(JGrid grid) {
            this.grid = grid;
            renderer.setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        protected boolean isHeaderSelected(JTable table, int row, int column) {
            return table.getColumnSelectionAllowed() && table.isColumnSelected(column);
        }

        @Override
        protected boolean isHeaderHovered(JTable table, int row, int column) {
            return grid.getHoveredCell().getColumn() == column;
        }
    }

    private static final class CornerRenderer extends HeaderRenderer {

        @Override
        protected boolean isHeaderSelected(JTable table, int row, int column) {
            return false;
        }

        @Override
        protected boolean isHeaderHovered(JTable table, int row, int column) {
            return false;
        }
    }

    private static final class GridCellRenderer implements TableCellRenderer {

        private final JGrid grid;
        protected final JLabel renderer;
        private final GridUIResource gridResource;

        public GridCellRenderer(@Nonnull JGrid grid) {
            this.grid = grid;
            this.renderer = new DefaultTableCellRenderer();
            this.gridResource = GridUIResource.getDefault();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            renderer.setFont(table.getFont());

            CellIndex hoveredCell = grid.getHoveredCell();
            boolean focused = !grid.isCrosshairVisible()
                    ? hoveredCell.equals(row, column)
                    : (hoveredCell.getRow() == row || hoveredCell.getColumn() == column);

            CellUIResource resource = gridResource.getCell(isSelected, focused);
            renderer.setBorder(resource.getBorder());

            renderer.setBackground(resource.getBackground());
            renderer.setForeground(resource.getForeground());
            renderer.setText(value != null ? value.toString() : null);

            return renderer;
        }
    }

    private static abstract class GridUIResource {

        @Nonnull
        abstract public CellUIResource getHeader(boolean selected, boolean hovered);

        @Nonnull
        abstract public CellUIResource getCell(boolean selected, boolean hovered);

        @Nonnull
        public static GridUIResource getDefault() {
            return GridColorsImpl.INSTANCE;
        }

        //<editor-fold defaultstate="collapsed" desc="Implementation details">
        private static final class GridColorsImpl extends GridUIResource {

            private static final GridColorsImpl INSTANCE = new GridColorsImpl();

            private final CellUIResource header;
            private final CellUIResource headerSelection;
            private final CellUIResource headerFocus;
            private final CellUIResource headerBoth;
            private final CellUIResource cell;
            private final CellUIResource cellSelection;
            private final CellUIResource cellFocus;
            private final CellUIResource cellBoth;

            private GridColorsImpl() {
                Color headerBackground = TABLE_HEADER_BACKGROUND.or(new Color(240, 240, 240));
                Color headerForeground = TABLE_HEADER_FOREGROUND.or(Color.BLACK);
                Color background = TABLE_BACKGROUND.or(Color.WHITE);
                Color foreground = TABLE_FOREGROUND.or(Color.BLACK);
                Color selectionBackground = TABLE_SELECTION_BACKGROUND.or(new Color(51, 153, 255));
                Color selectionForeground = TABLE_SELECTION_FOREGROUND.or(new Color(255, 255, 255));

                Border headerBorder = BorderFactory.createCompoundBorder(
                        new LineBorder2(headerBackground.brighter(), 0, 0, 1, 1),
                        BorderFactory.createEmptyBorder(0, 4, 0, 4));
                Border noBorder = BorderFactory.createEmptyBorder();

                this.header = CellUIResource.of(headerBackground, headerForeground, headerBorder);
                this.headerSelection = CellUIResource.of(selectionBackground.darker(), selectionForeground, headerBorder);
                this.headerFocus = CellUIResource.of(selectionBackground, selectionForeground, headerBorder);
                this.headerBoth = CellUIResource.of(selectionBackground, selectionForeground, headerBorder);

                this.cell = CellUIResource.of(background, foreground, noBorder);
                this.cellSelection = CellUIResource.of(selectionBackground, selectionForeground, noBorder);
                this.cellFocus = CellUIResource.of(withAlpha(selectionBackground, 200), selectionForeground, noBorder);
                this.cellBoth = CellUIResource.of(withAlpha(selectionBackground, 200), selectionForeground, noBorder);
            }

            @Override
            public CellUIResource getHeader(boolean selected, boolean hovered) {
                return selected ? (hovered ? headerBoth : headerSelection) : (hovered ? headerFocus : header);
            }

            @Override
            public CellUIResource getCell(boolean selected, boolean hovered) {
                return selected ? (hovered ? cellBoth : cellSelection) : (hovered ? cellFocus : cell);
            }
        }
        //</editor-fold>
    }

    private static abstract class CellUIResource {

        @Nonnull
        abstract public Color getBackground();

        @Nonnull
        abstract public Color getForeground();

        @Nonnull
        abstract public Border getBorder();

        @Nonnull
        public static CellUIResource of(final Color background, final Color foreground, final Border border) {
            return new CellUIResource() {
                @Override
                public Color getBackground() {
                    return background;
                }

                @Override
                public Color getForeground() {
                    return foreground;
                }

                @Override
                public Border getBorder() {
                    return border;
                }
            };
        }
    }
    //</editor-fold>
}
