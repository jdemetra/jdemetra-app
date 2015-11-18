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
import ec.nbdemetra.ui.awt.ActionMaps;
import ec.nbdemetra.ui.awt.InputMaps;
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
import ec.util.chart.ObsIndex;
import ec.util.chart.swing.SwingColorSchemeSupport;
import ec.util.grid.swing.GridModel;
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
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
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
    public static final String HOVERED_OBS_PROPERTY = "hoveredObs";

    private static final boolean DEFAULT_USE_COLOR_SCHEME = false;
    private static final boolean DEFAULT_SHOW_BARS = false;
    private static final boolean DEFAULT_CROSSHAIR_VISIBLE = false;
    private static final ObsIndex DEFAULT_HOVERED_OBS = ObsIndex.NULL;

    private boolean useColorScheme;
    private boolean showBars;
    private TableCellRenderer cellRenderer;
    private boolean crosshairVisible;
    private ObsIndex hoveredObs;
    //</editor-fold>

    protected final JGrid grid;
    private final JComboBox combo;
    private final GridSelectionListener selectionListener;
    private final GridHandler gridHandler;
    private final CustomCellRenderer defaultCellRenderer;
    protected final DataFeatureModel dataFeatureModel;
    private Font originalFont;

    public JTsGrid() {
        this.useColorScheme = DEFAULT_USE_COLOR_SCHEME;
        this.showBars = DEFAULT_SHOW_BARS;
        this.crosshairVisible = DEFAULT_CROSSHAIR_VISIBLE;
        this.hoveredObs = DEFAULT_HOVERED_OBS;

        this.selectionListener = new GridSelectionListener();
        this.gridHandler = new GridHandler();
        this.grid = new JGrid();
        this.defaultCellRenderer = new CustomCellRenderer(grid.getDefaultRenderer(Object.class));
        this.cellRenderer = defaultCellRenderer;
        this.combo = new JComboBox();
        this.dataFeatureModel = new DataFeatureModel();

        initGrid();

        ActionMaps.copyEntries(getActionMap(), false, grid.getActionMap());
        InputMaps.copyEntries(getInputMap(), false, grid.getInputMap(WHEN_IN_FOCUSED_WINDOW));

        enableSingleTsSelection();
        enableOpenOnDoubleClick();
        enableObsHovering();
        enableProperties();

        setLayout(new BorderLayout());
        add(grid, BorderLayout.CENTER);
        add(combo, BorderLayout.NORTH);

        if (Beans.isDesignTime()) {
            applyDesignTimeProperties();
        }
    }

    private void initGrid() {
        onColorSchemeChange();
        onDataFormatChange();
        onUpdateModeChange();
        updateGridModel();
        updateComboModel();
        updateSelectionBehavior();
        updateComboCellRenderer();
        onTransferHandlerChange();
        onComponentPopupMenuChange();
        grid.setDragEnabled(true);
        grid.setRowRenderer(new CustomRowRenderer(grid));
        grid.getRowSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    private void applyDesignTimeProperties() {
        setTsCollection(DemoUtils.randomTsCollection(3));
        setTsUpdateMode(TsUpdateMode.None);
        setPreferredSize(new Dimension(200, 150));
    }

    //<editor-fold defaultstate="collapsed" desc="Interactive stuff">
    private void enableSingleTsSelection() {
        combo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                setSingleTsIndex(combo.getSelectedIndex());
            }
        });
    }

    private void enableOpenOnDoubleClick() {
        grid.addMouseListener(new TsActionMouseAdapter());
    }

    private void enableObsHovering() {
        grid.addPropertyChangeListener(gridHandler);
    }

    private void enableProperties() {
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
                    case HOVERED_OBS_PROPERTY:
                        onHoveredObsChange();
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
    //</editor-fold>

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
        grid.setCrosshairVisible(crosshairVisible);
    }

    private void onHoveredObsChange() {
        gridHandler.applyHoveredCell(hoveredObs);
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

    private void onTransferHandlerChange() {
        TransferHandler th = getTransferHandler();
        grid.setTransferHandler(th != null ? th : new TsCollectionTransferHandler());
    }

    private void onComponentPopupMenuChange() {
        JPopupMenu popupMenu = getComponentPopupMenu();
        grid.setComponentPopupMenu(popupMenu != null ? popupMenu : buildGridMenu().getPopupMenu());
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

    @Nonnull
    public TableCellRenderer getCellRenderer() {
        return cellRenderer;
    }

    public void setCellRenderer(@Nullable TableCellRenderer cellRenderer) {
        TableCellRenderer old = this.cellRenderer;
        this.cellRenderer = cellRenderer != null ? cellRenderer : defaultCellRenderer;
        firePropertyChange(CELL_RENDERER_PROPERTY, old, this.cellRenderer);
    }

    public boolean isCrosshairVisible() {
        return crosshairVisible;
    }

    public void setCrosshairVisible(boolean crosshairVisible) {
        boolean old = this.crosshairVisible;
        this.crosshairVisible = crosshairVisible;
        firePropertyChange(CROSSHAIR_VISIBLE_PROPERTY, old, this.crosshairVisible);
    }

    @Nonnull
    public ObsIndex getHoveredObs() {
        return hoveredObs;
    }

    public void setHoveredObs(@Nullable ObsIndex hoveredObs) {
        ObsIndex old = this.hoveredObs;
        this.hoveredObs = hoveredObs != null ? hoveredObs : DEFAULT_HOVERED_OBS;
        firePropertyChange(HOVERED_OBS_PROPERTY, old, this.hoveredObs);
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
        } else if (!collection.isEmpty()) {
            int index = Math.min(singleTsIndex, collection.getCount() - 1);
            if (combo.isVisible()) {
                combo.setSelectedIndex(index);
            }
            setSelection(new Ts[]{collection.get(index)});
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
        defaultCellRenderer.update(themeSupport.getDataFormat(), useColorScheme ? themeSupport : null, showBars);
        grid.setDefaultRenderer(TsGridObs.class, cellRenderer);
        grid.repaint();
    }

    private void updateComboCellRenderer() {
        combo.setRenderer(new ComboCellRenderer(useColorScheme ? themeSupport : null));
    }

    @Deprecated
    protected JGrid buildGrid() {
        final JGrid result = new JGrid();

        result.setDragEnabled(true);
        result.setTransferHandler(new TsCollectionTransferHandler());
        result.setRowRenderer(new CustomRowRenderer(result));
        result.getRowSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        result.addMouseListener(new TsActionMouseAdapter());

        ActionMaps.copyEntries(getActionMap(), false, result.getActionMap());
        InputMaps.copyEntries(getInputMap(), false, result.getInputMap(WHEN_IN_FOCUSED_WINDOW));

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
            } else if (collection.getCount() > singleTsIndex) {
                setSelection(new Ts[]{collection.get(singleTsIndex)});
            } else {
                setSelection(null);
            }
        }
    }

    private final class GridHandler implements PropertyChangeListener {

        private boolean updating = false;

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (!updating) {
                updating = true;
                switch (evt.getPropertyName()) {
                    case JGrid.HOVERED_CELL_PROPERTY:
                        setHoveredObs(((GridModelAdapter) grid.getModel()).data.toObsIndex(grid.getHoveredCell()));
                        break;
                }
                updating = false;
            }
        }

        private void applyHoveredCell(ObsIndex hoveredObs) {
            if (!updating) {
                grid.setHoveredCell(((GridModelAdapter) grid.getModel()).data.toCellIndex(hoveredObs));
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Renderers">
    private static final class CustomRowRenderer implements TableCellRenderer {

        private final TableCellRenderer delegate;

        public CustomRowRenderer(JGrid grid) {
            this.delegate = grid.getRowRenderer();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component result = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (result instanceof JLabel) {
                ((JLabel) result).setHorizontalAlignment(JLabel.TRAILING);
            }
            return result;
        }
    }

    private static final class CustomCellRenderer extends BarTableCellRenderer {

        private final TableCellRenderer delegate;
        private final Formatter<? super TsPeriod> periodFormatter;
        private final JToolTip toolTip;
        private Formatter<? super Number> valueFormatter;
        private SwingColorSchemeSupport colorSchemeSupport;
        private boolean showBars;

        public CustomCellRenderer(@Nonnull TableCellRenderer delegate) {
            super(false);
            setHorizontalAlignment(JLabel.TRAILING);
            setOpaque(true);
            this.delegate = delegate;
            this.periodFormatter = TsPeriodFormatter.INSTANCE;
            this.toolTip = super.createToolTip();
            this.valueFormatter = DataFormat.DEFAULT.numberFormatter();
            this.colorSchemeSupport = null;
            this.showBars = false;
        }

        void update(@Nonnull DataFormat dataFormat, @Nullable SwingColorSchemeSupport colorSchemeSupport, boolean showBars) {
            this.valueFormatter = dataFormat.numberFormatter();
            this.colorSchemeSupport = colorSchemeSupport;
            this.showBars = showBars;
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
            Component resource = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setFont(resource.getFont());

            TsGridObs obs = (TsGridObs) value;

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

    private static final class GridModelAdapter extends AbstractTableModel implements GridModel {

        private final TsGridData data;

        public GridModelAdapter(TsGridData data) {
            this.data = data;
        }

        @Override
        public int getRowCount() {
            return data.getRowCount();
        }

        @Override
        public int getColumnCount() {
            return data.getColumnCount();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data.getObs(rowIndex, columnIndex);
        }

        @Override
        public String getRowName(int rowIndex) {
            return data.getRowName(rowIndex);
        }

        @Override
        public String getColumnName(int column) {
            return data.getColumnName(column);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return TsGridObs.class;
        }
    }

    private static final class TsPeriodFormatter extends Formatter<TsPeriod> {

        private final static TsPeriodFormatter INSTANCE = new TsPeriodFormatter();

        @Override
        public CharSequence format(TsPeriod value) throws NullPointerException {
            return value.toString();
        }
    }
}
