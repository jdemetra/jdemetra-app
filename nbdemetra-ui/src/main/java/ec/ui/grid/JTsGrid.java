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
package ec.ui.grid;

import com.google.common.base.Strings;
import ec.nbdemetra.ui.MonikerUI;
import ec.nbdemetra.ui.awt.MultiLineString;
import ec.tss.Ts;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tss.tsproviders.utils.Formatters.Formatter;
import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import static ec.ui.ATsControl.FORMAT_ACTION;
import ec.ui.ATsGrid;
import ec.ui.DemoUtils;
import ec.ui.chart.DataFeatureModel;
import ec.ui.commands.TsGridCommand;
import ec.util.chart.swing.SwingColorSchemeSupport;
import ec.util.grid.CellIndex;
import ec.util.grid.swing.JGrid;
import ec.util.grid.swing.XTable;
import ec.util.various.swing.FontAwesome;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.Beans;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * UI component allowing view of TSCollection in a grid.
 *
 * @author Jeremy Demortier
 * @author Philippe Charles
 * @author Mats Maggi
 */
public class JTsGrid extends ATsGrid {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    public static final String USE_COLOR_SCHEME_PROPERTY = "useColorScheme";
    public static final String SHOW_BARS_PROPERTY = "showBars";
    public static final String CELL_RENDERER_PROPERTY = "cellRenderer";
    public static final String CROSSHAIR_VISIBLE_PROPERTY = "crosshairVisible";

    private static final boolean DEFAULT_USE_COLOR_SCHEME = false;
    private static final boolean DEFAULT_SHOW_BARS = false;
    private static final boolean DEFAULT_CROSSHAIR_VISIBLE = false;

    private boolean useColorScheme;
    private boolean showBars;
    private TableCellRenderer customCellRenderer;
    private boolean crosshairVisible;
    //</editor-fold>

    protected final JGrid grid;
    private final JComboBox combo;
    private final GridSelectionListener selectionListener;
    protected final DataFeatureModel dataFeatureModel;
    private Font originalFont;

    public JTsGrid() {
        this.useColorScheme = DEFAULT_USE_COLOR_SCHEME;
        this.showBars = DEFAULT_SHOW_BARS;
        this.customCellRenderer = null;
        this.crosshairVisible = DEFAULT_CROSSHAIR_VISIBLE;

        this.selectionListener = new GridSelectionListener();

        this.grid = buildGrid();

        this.combo = new JComboBox();
        combo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                setSingleTsIndex(combo.getSelectedIndex());
            }
        });

        this.dataFeatureModel = new DataFeatureModel();

        onColorSchemeChange();
        onDataFormatChange();
        onUpdateModeChange();
        updateGridModel();
        updateComboModel();
        updateSelectionBehavior();
        updateComboCellRenderer();

        setLayout(new BorderLayout());
        add(grid, BorderLayout.CENTER);
        add(combo, BorderLayout.NORTH);

        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case USE_COLOR_SCHEME_PROPERTY:
                        onUseColorSchemeChange();
                        break;
                    case SHOW_BARS_PROPERTY:
                        onShowBarsChange();
                        break;
                    case CELL_RENDERER_PROPERTY:
                        onCellRendererChange();
                        break;
                    case CROSSHAIR_VISIBLE_PROPERTY:
                        onCrosshairVisibleChange();
                        break;
                }
            }
        });

        if (Beans.isDesignTime()) {
            setTsCollection(DemoUtils.randomTsCollection(3));
            setTsUpdateMode(TsUpdateMode.None);
            setPreferredSize(new Dimension(200, 150));
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Event handlers">
    @Override
    protected void onDataFormatChange() {
        updateGridCellRenderer();
    }

    @Override
    protected void onColorSchemeChange() {
        if (useColorScheme) {
            updateGridCellRenderer();
            updateComboCellRenderer();
        }
    }

    @Override
    protected void onCollectionChange() {
        selectionListener.setEnabled(false);
        dataFeatureModel.setData(collection.toArray());
        updateGridModel();
        updateComboModel();
        updateNoDataMessage();
        selectionListener.setEnabled(true);
    }

    @Override
    protected void onSelectionChange() {
        selectionListener.setEnabled(false);
        updateSelection();
        selectionListener.setEnabled(true);
    }

    @Override
    protected void onUpdateModeChange() {
        updateNoDataMessage();
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
    protected void onOrientationChange() {
        selectionListener.setEnabled(false);
        updateGridModel();
        updateSelectionBehavior();
        updateSelection();
        selectionListener.setEnabled(true);
    }

    @Override
    protected void onChronologyChange() {
        selectionListener.setEnabled(false);
        updateGridModel();
        updateSelection();
        selectionListener.setEnabled(true);
    }

    @Override
    protected void onModeChange() {
        selectionListener.setEnabled(false);
        updateGridModel();
        updateComboModel();
        updateSelectionBehavior();
        updateSelection();
        selectionListener.setEnabled(true);
    }

    @Override
    protected void onSingleTsIndexChange() {
        selectionListener.setEnabled(false);
        updateGridModel();
        updateSelection();
        selectionListener.setEnabled(true);
    }

    private void onUseColorSchemeChange() {
        updateGridCellRenderer();
        updateComboCellRenderer();
    }

    private void onShowBarsChange() {
        updateGridCellRenderer();
    }

    private void onCellRendererChange() {
        updateGridCellRenderer();
    }

    private void onCrosshairVisibleChange() {
        updateGridCellRenderer();
    }

    @Override
    protected void onZoomChange() {
        if (originalFont == null) {
            originalFont = getFont();
        }

        Font font = originalFont;

        if (this.zoomRatio != 100) {
            float floatRatio = ((float) this.zoomRatio) / 100.0f;
            float scaledSize = originalFont.getSize2D() * floatRatio;
            font = originalFont.deriveFont(scaledSize);
        }

        grid.setFont(font);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public boolean isUseColorScheme() {
        return useColorScheme;
    }

    public void setUseColorScheme(boolean useColorScheme) {
        boolean old = this.useColorScheme;
        this.useColorScheme = useColorScheme;
        firePropertyChange(USE_COLOR_SCHEME_PROPERTY, old, this.useColorScheme);
    }

    @Deprecated
    public int[] getSelectedColumns() {
        return grid.getSelectedColumns();
    }

    public boolean isShowBars() {
        return showBars;
    }

    public void setShowBars(boolean showBars) {
        boolean old = this.showBars;
        this.showBars = showBars;
        firePropertyChange(SHOW_BARS_PROPERTY, old, this.showBars);
    }

    @Nullable
    public TableCellRenderer getCellRenderer() {
        return customCellRenderer;
    }

    public void setCellRenderer(@Nullable TableCellRenderer cellRenderer) {
        TableCellRenderer old = this.customCellRenderer;
        this.customCellRenderer = cellRenderer;
        firePropertyChange(CELL_RENDERER_PROPERTY, old, this.customCellRenderer);
    }

    public boolean isCrosshairVisible() {
        return crosshairVisible;
    }

    public void setCrosshairVisible(boolean crosshairVisible) {
        boolean old = this.crosshairVisible;
        this.crosshairVisible = crosshairVisible;
        firePropertyChange(CROSSHAIR_VISIBLE_PROPERTY, old, this.crosshairVisible);
    }
    //</editor-fold>

    private void updateNoDataMessage() {
        String message;
        if (getTsUpdateMode().isReadOnly()) {
            switch (collection.getCount()) {
                case 0:
                    message = "No data";
                    break;
                case 1:
                    String cause = collection.get(0).getInvalidDataCause();
                    message = "<html><center><b>Invalid data</b><br>" + Strings.nullToEmpty(cause);
                    break;
                default:
                    message = "Invalid data";
                    break;
            }
        } else {
            message = "Drop data here";
        }
        grid.setNoDataRenderer(new XTable.DefaultNoDataRenderer(message));
    }

    private void updateGridModel() {
        int index = mode == Mode.SINGLETS ? Math.min(singleTsIndex, collection.getCount() - 1) : -1;
        grid.setModel(new GridModelAdapter(TsGridData.create(collection, index, orientation, chronology, dataFeatureModel)));
    }

    private void updateSelectionBehavior() {
        grid.getColumnSelectionModel().removeListSelectionListener(selectionListener);
        grid.getRowSelectionModel().removeListSelectionListener(selectionListener);
        grid.setColumnSelectionAllowed(false);
        grid.setRowSelectionAllowed(false);
        if (mode == Mode.MULTIPLETS) {
            if (orientation == Orientation.NORMAL) {
                grid.getColumnSelectionModel().addListSelectionListener(selectionListener);
                grid.setColumnSelectionAllowed(true);
            } else {
                grid.getRowSelectionModel().addListSelectionListener(selectionListener);
                grid.setRowSelectionAllowed(true);
            }
        }
    }

    public void fireTableDataChanged() {
        ((AbstractTableModel) grid.getModel()).fireTableDataChanged();
    }

    private void updateSelection() {
        if (mode == Mode.MULTIPLETS) {
            selectionListener.changeSelection(orientation == Orientation.NORMAL ? grid.getColumnSelectionModel() : grid.getRowSelectionModel());
        } else {
            if (!collection.isEmpty()) {
                int index = Math.min(singleTsIndex, collection.getCount() - 1);
                if (combo.isVisible()) {
                    combo.setSelectedIndex(index);
                }
                setSelection(new Ts[]{collection.get(index)});
            }
        }
    }

    private void updateComboModel() {
        if (mode == Mode.SINGLETS && collection.getCount() > 1) {
            combo.setModel(new DefaultComboBoxModel(collection.toArray()));
            combo.setVisible(true);
        } else {
            combo.setVisible(false);
        }
    }

    private void updateGridCellRenderer() {
        grid.setDefaultRenderer(TsGridObs.class, customCellRenderer != null ? customCellRenderer : new GridCellRenderer(grid, themeSupport.getDataFormat(), useColorScheme ? themeSupport : null, showBars, crosshairVisible));
        grid.repaint();
    }

    private void updateComboCellRenderer() {
        combo.setRenderer(new ComboCellRenderer(useColorScheme ? themeSupport : null));
    }

    protected JGrid buildGrid() {
        final JGrid result = new JGrid();

        result.setDragEnabled(true);
        result.setTransferHandler(new TsCollectionTransferHandler());
        result.setRowRenderer(new RowRenderer(result));
        result.setColumnRenderer(new ColumnRenderer(result));
        result.setCornerRenderer(new CornerRenderer());
        result.getRowSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        result.setComponentPopupMenu(buildGridMenu().getPopupMenu());
        result.addMouseListener(new TsActionMouseAdapter());
        result.setOddBackground(null);

        fillActionMap(result.getActionMap());
        fillInputMap(result.getInputMap(WHEN_IN_FOCUSED_WINDOW));

        return result;
    }

    @Override
    protected JMenu buildGridMenu() {
        JMenu result = super.buildGridMenu();

        JMenuItem item;

        result.addSeparator();

        item = new JMenuItem(getActionMap().get(FORMAT_ACTION));
        item.setText("Edit format...");
        item.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_GLOBE));
        result.add(item);

        item = new JCheckBoxMenuItem(JTsGridCommand.toggleUseColorScheme().toAction(this));
        item.setText("Use color scheme");
        result.add(item);

        result.add(buildColorSchemeMenu());

        item = new JCheckBoxMenuItem(JTsGridCommand.toggleShowBars().toAction(this));
        item.setText("Show bars");
        item.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_TASKS));
        result.add(item);

        item = new JCheckBoxMenuItem(JTsGridCommand.toggleCrosshairVisibility().toAction(this));
        item.setText("Show crosshair");
        item.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_CROSSHAIRS));
        result.add(item);

        JMenu zoom = new JMenu("Zoom");
        zoom.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_SEARCH));
        final JSlider slider = new JSlider(10, 200, 100);
        {
            slider.setPreferredSize(new Dimension(50, slider.getPreferredSize().height));
            slider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    setZoomRatio(slider.getValue());
                }
            });
            addPropertyChangeListener(ZOOM_PROPERTY, new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    slider.setValue(getZoomRatio());
                }
            });
        }
        zoom.add(slider);
        for (int o : new int[]{200, 100, 75, 50, 25}) {
            zoom.add(new JCheckBoxMenuItem(TsGridCommand.applyZoomRatio(o).toAction(this))).setText(o + "%");
        }
        result.add(zoom);

        return result;
    }

    private class GridSelectionListener extends TsCollectionSelectionListener {

        @Override
        protected void selectionChanged(ListSelectionModel model) {
            if (mode == Mode.MULTIPLETS) {
                super.selectionChanged(model);
            } else {
                if (collection.getCount() > singleTsIndex) {
                    setSelection(new Ts[]{collection.get(singleTsIndex)});
                } else {
                    setSelection(null);
                }
            }
        }
    }

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
                renderer.setText(MultiLineString.join(text));
                renderer.setToolTipText(MultiLineString.toHtml(text));
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
            renderer.setHorizontalAlignment(JLabel.TRAILING);
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

    private static final class GridCellRenderer extends BarTableCellRenderer {

        private final JGrid grid;
        private final GridUIResource gridResources;
        private final Formatter<? super TsPeriod> periodFormatter;
        private final Formatter<? super Number> valueFormatter;
        private final SwingColorSchemeSupport colorSchemeSupport;
        private final boolean showBars;
        private final JToolTip toolTip;
        private final boolean crosshairVisible;

        public GridCellRenderer(@Nonnull JGrid grid, @Nonnull DataFormat dataFormat, @Nullable SwingColorSchemeSupport colorSchemeSupport, boolean showBars, boolean crosshairVisible) {
            super(false);
            setHorizontalAlignment(JLabel.TRAILING);
            setOpaque(true);
            this.grid = grid;
            this.gridResources = GridUIResource.getDefault();
            this.periodFormatter = TsPeriodFormatter.INSTANCE;
            this.valueFormatter = dataFormat.numberFormatter();
            this.colorSchemeSupport = colorSchemeSupport;
            this.showBars = showBars;
            this.toolTip = super.createToolTip();
            this.crosshairVisible = crosshairVisible;
        }

        @Override
        public JToolTip createToolTip() {
            if (colorSchemeSupport != null) {
                toolTip.setBackground(getForeground());
                toolTip.setForeground(getBackground());
            }
            return toolTip;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            TsGridObs obs = (TsGridObs) value;

            setFont(table.getFont());

            CellIndex hoveredCell = grid.getHoveredCell();
            boolean focused = !crosshairVisible
                    ? hoveredCell.equals(row, column)
                    : (hoveredCell.getRow() == row || hoveredCell.getColumn() == column);

            CellUIResource resource = gridResources.getCell(isSelected, focused);
            setBorder(resource.getBorder());

            if (colorSchemeSupport != null) {
                Color plotColor = colorSchemeSupport.getPlotColor();
                Color lineColor = colorSchemeSupport.getLineColor(obs.getSeriesIndex());
                if (isSelected) {
                    setBackground(lineColor);
                    setForeground(plotColor);
                } else {
                    setBackground(plotColor);
                    setForeground(lineColor);
                }
            } else {
                setBackground(resource.getBackground());
                setForeground(resource.getForeground());
            }

            switch (obs.getInfo()) {
                case Empty:
                    setText(null);
                    setToolTipText(null);
                    setBarValues(0, 0, 0);
                    break;
                case Missing:
                    setText(".");
                    setToolTipText(periodFormatter.formatAsString(obs.getPeriod()));
                    setBarValues(0, 0, 0);
                    break;
                case Valid:
                    String valueAsString = valueFormatter.formatAsString(obs.getValue());
                    String periodAsString = periodFormatter.formatAsString(obs.getPeriod());
                    if (obs.hasFeature(Ts.DataFeature.Forecasts)) {
                        setText("<html><i>" + valueAsString);
                        setToolTipText("<html>" + periodAsString + ": " + valueAsString + "<br>Forecast");
                    } else {
                        setText(valueAsString);
                        setToolTipText(periodAsString + ": " + valueAsString);
                    }
                    if (showBars && !isSelected) {
                        DescriptiveStatistics stats = obs.getStats();
                        setBarValues(stats.getMin(), stats.getMax(), obs.getValue());
                    } else {
                        setBarValues(0, 0, 0);
                    }
                    break;
            }

            return this;
        }
    }

    private static final class ComboCellRenderer extends DefaultListCellRenderer {

        private final MonikerUI monikerUI;
        private final SwingColorSchemeSupport colorSchemeSupport;

        public ComboCellRenderer(@Nullable SwingColorSchemeSupport colorSchemeSupport) {
            this.monikerUI = MonikerUI.getDefault();
            this.colorSchemeSupport = colorSchemeSupport;
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Ts ts = (Ts) value;
            setText(ts.getName());
            setIcon(monikerUI.getIcon(ts.getMoniker()));
            if (colorSchemeSupport != null && index != -1) {
                if (isSelected) {
                    setBackground(colorSchemeSupport.getPlotColor());
                }
                setForeground(colorSchemeSupport.getLineColor(index));
            }
            return this;
        }
    }
    //</editor-fold>

    private static final class TsPeriodFormatter extends Formatter<TsPeriod> {

        private final static TsPeriodFormatter INSTANCE = new TsPeriodFormatter();

        @Override
        public CharSequence format(TsPeriod value) throws NullPointerException {
            return value.toString();
        }
    }
}
