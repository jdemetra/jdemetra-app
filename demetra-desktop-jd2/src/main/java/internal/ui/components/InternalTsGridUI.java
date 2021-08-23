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
package internal.ui.components;

import com.google.common.base.Suppliers;
import demetra.timeseries.TsDataTable;
import demetra.timeseries.TsPeriod;
import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.ui.DemetraOptions;
import demetra.ui.components.TsSelectionBridge;
import demetra.ui.components.parts.HasColorScheme;
import demetra.ui.components.parts.HasObsFormat;
import demetra.ui.components.parts.HasTsCollection;
import demetra.ui.TsMonikerUI;
import ec.nbdemetra.ui.ThemeSupport;
import demetra.ui.util.ActionMaps;
import demetra.ui.util.InputMaps;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tss.tsproviders.utils.IFormatter;
import ec.tss.tsproviders.utils.MultiLineNameUtil;
import ec.tstoolkit.data.DescriptiveStatistics;
import demetra.ui.components.JTsGrid;
import demetra.ui.components.TsFeatureHelper;
import demetra.ui.components.TsGridObs;
import static internal.ui.components.JTsGridCommands.MULTI_TS_ACTION;
import static internal.ui.components.JTsGridCommands.REVERSE_ACTION;
import static internal.ui.components.JTsGridCommands.SINGLE_TS_ACTION;
import static internal.ui.components.JTsGridCommands.TOGGLE_MODE_ACTION;
import static internal.ui.components.JTsGridCommands.TRANSPOSE_ACTION;
import ec.util.chart.ObsIndex;
import ec.util.chart.swing.SwingColorSchemeSupport;
import ec.util.grid.CellIndex;
import ec.util.grid.swing.GridModel;
import ec.util.grid.swing.JGrid;
import ec.util.grid.swing.XTable;
import ec.util.various.swing.FontAwesome;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import javax.swing.ActionMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import demetra.ui.datatransfer.DataTransfer;
import javax.swing.JComponent;
import nbbrd.service.ServiceProvider;
import demetra.ui.components.ComponentBackendSpi;

public final class InternalTsGridUI implements InternalUI<JTsGrid> {

    @ServiceProvider
    public static final class Factory implements ComponentBackendSpi {

        @Override
        public boolean handles(Class<? extends JComponent> type) {
            return JTsGrid.class.equals(type);
        }

        @Override
        public void install(JComponent component) {
            new InternalTsGridUI().install((JTsGrid) component);
        }
    }

    private JTsGrid target;

    private final JGrid grid = new JGrid();
    private final JComboBox combo = new JComboBox();
    private final GridHandler gridHandler = new GridHandler();
    private final CustomCellRenderer defaultCellRenderer = new CustomCellRenderer(grid.getDefaultRenderer(Object.class));
    private final ThemeSupport themeSupport = ThemeSupport.registered();
    private Font originalFont;

    private GridSelectionListener selectionListener;

    @Override
    public void install(JTsGrid component) {
        this.target = component;

        this.selectionListener = new GridSelectionListener(target);

        target.setCellRenderer(defaultCellRenderer);

        themeSupport.setColorSchemeListener(target, this::onColorSchemeChange);
        themeSupport.setObsFormatListener(target, this::onDataFormatChange);

        registerActions();
        registerInputs();

        initGrid();

        enableSingleTsSelection();
        enableOpenOnDoubleClick();
        enableObsHovering();
        enableProperties();

        target.setLayout(new BorderLayout());
        target.add(grid, BorderLayout.CENTER);
        target.add(combo, BorderLayout.NORTH);
    }

    private void registerActions() {
        ActionMap am = target.getActionMap();
        am.put(TRANSPOSE_ACTION, JTsGridCommands.transpose().toAction(target));
        am.put(REVERSE_ACTION, JTsGridCommands.reverseChronology().toAction(target));
        am.put(SINGLE_TS_ACTION, JTsGridCommands.applyMode(JTsGrid.Mode.SINGLETS).toAction(target));
        am.put(MULTI_TS_ACTION, JTsGridCommands.applyMode(JTsGrid.Mode.MULTIPLETS).toAction(target));
        am.put(TOGGLE_MODE_ACTION, JTsGridCommands.toggleMode().toAction(target));
        am.put(HasObsFormatCommands.FORMAT_ACTION, HasObsFormatCommands.editDataFormat().toAction(target));
        HasTsCollectionCommands.registerActions(target, target.getActionMap());
        target.getActionMap().put(HasObsFormatCommands.FORMAT_ACTION, HasObsFormatCommands.editDataFormat().toAction(target));
        ActionMaps.copyEntries(target.getActionMap(), false, grid.getActionMap());
    }

    private void registerInputs() {
        HasTsCollectionCommands.registerInputs(target.getInputMap());
        InputMaps.copyEntries(target.getInputMap(), false, grid.getInputMap(JGrid.WHEN_IN_FOCUSED_WINDOW));
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
        grid.setColumnRenderer(new CustomColumnRenderer(grid));
    }

    //<editor-fold defaultstate="collapsed" desc="Interactive stuff">
    private void enableSingleTsSelection() {
        combo.addItemListener(event -> target.setSingleTsIndex(combo.getSelectedIndex()));
    }

    private void enableOpenOnDoubleClick() {
        grid.addMouseListener(new OpenOnDoubleClick(target.getActionMap()));
    }

    private void enableObsHovering() {
        grid.addPropertyChangeListener(gridHandler);
    }

    private void enableProperties() {
        target.addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case HasTsCollection.TS_COLLECTION_PROPERTY:
                    onCollectionChange();
                    break;
                case TsSelectionBridge.TS_SELECTION_PROPERTY:
                    onSelectionChange();
                    break;
                case JTsGrid.UDPATE_MODE_PROPERTY:
                    onUpdateModeChange();
                    break;
                case HasColorScheme.COLOR_SCHEME_PROPERTY:
                    onColorSchemeChange();
                    break;
                case HasObsFormat.OBS_FORMAT_PROPERTY:
                    onDataFormatChange();
                    break;
                case JTsGrid.ORIENTATION_PROPERTY:
                    onOrientationChange();
                    break;
                case JTsGrid.CHRONOLOGY_PROPERTY:
                    onChronologyChange();
                    break;
                case JTsGrid.MODE_PROPERTY:
                    onModeChange();
                    break;
                case JTsGrid.SINGLE_TS_INDEX_PROPERTY:
                    onSingleTsIndexChange();
                    break;
                case JTsGrid.ZOOM_PROPERTY:
                    onZoomChange();
                    break;
                case JTsGrid.USE_COLOR_SCHEME_PROPERTY:
                    onUseColorSchemeChange();
                    break;
                case JTsGrid.SHOW_BARS_PROPERTY:
                    onShowBarsChange();
                    break;
                case JTsGrid.CELL_RENDERER_PROPERTY:
                    onCellRendererChange();
                    break;
                case JTsGrid.CROSSHAIR_VISIBLE_PROPERTY:
                    onCrosshairVisibleChange();
                    break;
                case JTsGrid.HOVERED_OBS_PROPERTY:
                    onHoveredObsChange();
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

    //<editor-fold defaultstate="collapsed" desc="Event handlers">
    private void onDataFormatChange() {
        updateGridCellRenderer();
    }

    private void onColorSchemeChange() {
        if (target.isUseColorScheme()) {
            updateGridCellRenderer();
            updateComboCellRenderer();
        }
    }

    private void onCollectionChange() {
        selectionListener.setEnabled(false);
        updateGridModel();
        updateComboModel();
        updateNoDataMessage();
        updateGridCellRenderer();
        selectionListener.setEnabled(true);
    }

    private void onSelectionChange() {
        selectionListener.setEnabled(false);
        updateSelection();
        selectionListener.setEnabled(true);
    }

    private void onUpdateModeChange() {
        updateNoDataMessage();
    }

    private void onOrientationChange() {
        selectionListener.setEnabled(false);
        updateGridModel();
        updateSelectionBehavior();
        updateSelection();
        selectionListener.setEnabled(true);
    }

    private void onChronologyChange() {
        selectionListener.setEnabled(false);
        updateGridModel();
        updateSelection();
        selectionListener.setEnabled(true);
    }

    private void onModeChange() {
        selectionListener.setEnabled(false);
        updateGridModel();
        updateComboModel();
        updateSelectionBehavior();
        updateSelection();
        selectionListener.setEnabled(true);
    }

    private void onSingleTsIndexChange() {
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
        grid.setCrosshairVisible(target.isCrosshairVisible());
    }

    private void onHoveredObsChange() {
        gridHandler.applyHoveredCell(target.getHoveredObs());
    }

    private void onZoomChange() {
        if (originalFont == null) {
            originalFont = target.getFont();
        }

        Font font = originalFont;

        if (target.getZoomRatio() != 100) {
            float floatRatio = ((float) target.getZoomRatio()) / 100.0f;
            float scaledSize = originalFont.getSize2D() * floatRatio;
            font = originalFont.deriveFont(scaledSize);
        }

        grid.setFont(font);
    }

    private void onTransferHandlerChange() {
        TransferHandler th = target.getTransferHandler();
        grid.setTransferHandler(th != null ? th : new HasTsCollectionTransferHandler(target, DataTransfer.getDefault()));
    }

    private void onComponentPopupMenuChange() {
        JPopupMenu popupMenu = target.getComponentPopupMenu();
        grid.setComponentPopupMenu(popupMenu != null ? popupMenu : buildGridMenu().getPopupMenu());
    }
    //</editor-fold>

    private void updateNoDataMessage() {
        String msg = InternalComponents.getNoDataMessage(target);
        grid.setNoDataRenderer(new XTable.DefaultNoDataRenderer(msg.replace(System.lineSeparator(), " ")));
    }

    private void updateGridModel() {
        int index = target.getMode() == JTsGrid.Mode.SINGLETS ? Math.min(target.getSingleTsIndex(), target.getTsCollection().size() - 1) : -1;
        TsGridData data = TsGridData.create(target.getTsCollection().getItems(), index);
        boolean transposed = target.getOrientation().equals(JTsGrid.Orientation.REVERSED);
        boolean ascending = target.getChronology().equals(JTsGrid.Chronology.ASCENDING);
        boolean single = index != -1;
        grid.setModel(new GridModelAdapter(
                data,
                transposed,
                ascending ? i -> i : i -> data.getRowCount() - i - 1,
                ascending || !single ? j -> j : j -> data.getColumnCount() - j - 1
        ));
    }

    private void updateSelectionBehavior() {
        grid.getColumnSelectionModel().removeListSelectionListener(selectionListener);
        grid.getRowSelectionModel().removeListSelectionListener(selectionListener);
        grid.setColumnSelectionAllowed(false);
        grid.setRowSelectionAllowed(false);
        if (target.getMode() == JTsGrid.Mode.MULTIPLETS) {
            if (target.getOrientation() == JTsGrid.Orientation.NORMAL) {
                grid.getColumnSelectionModel().addListSelectionListener(selectionListener);
                grid.setColumnSelectionAllowed(true);
            } else {
                grid.getRowSelectionModel().addListSelectionListener(selectionListener);
                grid.setRowSelectionAllowed(true);
            }
        }
    }

    private void updateSelection() {
        if (selectionListener.isEnabled()) {
            if (target.getMode() == JTsGrid.Mode.MULTIPLETS) {
                selectionListener.changeSelection(target.getOrientation() == JTsGrid.Orientation.NORMAL ? grid.getColumnSelectionModel() : grid.getRowSelectionModel());
            } else if (target.getTsCollection().size() > 0) {
                int index = Math.min(target.getSingleTsIndex(), target.getTsCollection().size() - 1);
                if (combo.isVisible()) {
                    combo.setSelectedIndex(index);
                }
                target.getTsSelectionModel().clearSelection();
                target.getTsSelectionModel().setSelectionInterval(index, index);
            }
        }
    }

    private void updateComboModel() {
        if (target.getMode() == JTsGrid.Mode.SINGLETS && target.getTsCollection().size() > 1) {
            combo.setModel(new DefaultComboBoxModel<>(target.getTsCollection().toArray(Ts[]::new)));
            combo.setVisible(true);
        } else {
            combo.setVisible(false);
        }
    }

    private void updateGridCellRenderer() {
        TsCollection data = target.getTsCollection();
        Supplier<TsFeatureHelper> tsFeatures = Suppliers.memoize(() -> TsFeatureHelper.of(data.getItems()));
        Supplier<DescriptiveStatistics> stats = Suppliers.memoize(() -> {
            return target.getSingleTsIndex() != -1
                    ? new DescriptiveStatistics(data.stream().flatMapToDouble(o -> o.getData().getValues().stream()).toArray())
                    : new DescriptiveStatistics(data.get(target.getSingleTsIndex()).getData().getValues().toArray());
        });
        defaultCellRenderer.update(themeSupport.getDataFormat(), target.isUseColorScheme() ? themeSupport : null, target.isShowBars(), tsFeatures, stats);
        grid.setDefaultRenderer(TsGridObs.class, target.getCellRenderer());
        grid.repaint();
    }

    private void updateComboCellRenderer() {
        combo.setRenderer(new ComboCellRenderer(target.isUseColorScheme() ? themeSupport : null));
    }

    private JMenu buildMenu(DemetraOptions demetraUI) {
        ActionMap am = target.getActionMap();
        JMenu result = new JMenu();

        result.add(HasTsCollectionCommands.newOpenMenu(am, demetraUI));
        result.add(HasTsCollectionCommands.newOpenWithMenu(target, demetraUI));

        JMenu menu = HasTsCollectionCommands.newSaveMenu(target, demetraUI);
        if (menu.getSubElements().length > 0) {
            result.add(menu);
        }

        result.add(HasTsCollectionCommands.newRenameMenu(am, demetraUI));
        result.add(HasTsCollectionCommands.newFreezeMenu(am, demetraUI));
        result.add(HasTsCollectionCommands.newCopyMenu(am, demetraUI));
        result.add(HasTsCollectionCommands.newPasteMenu(am, demetraUI));
        result.add(HasTsCollectionCommands.newDeleteMenu(am, demetraUI));
        result.addSeparator();
        result.add(HasTsCollectionCommands.newSelectAllMenu(am, demetraUI));
        result.add(HasTsCollectionCommands.newClearMenu(am, demetraUI));

        return result;
    }

    private JMenu buildGridMenu() {
        DemetraOptions demetraUI = DemetraOptions.getDefault();

        ActionMap am = target.getActionMap();

        JMenu result = buildMenu(demetraUI);

        int index = 0;
        JMenuItem item;

        index += 10;
        result.insertSeparator(index++);

        item = new JCheckBoxMenuItem(am.get(TRANSPOSE_ACTION));
        item.setText("Transpose");
        result.add(item, index++);

        item = new JCheckBoxMenuItem(am.get(REVERSE_ACTION));
        item.setText("Reverse chronology");
        item.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_SORT_NUMERIC_DESC));
        result.add(item, index++);

        item = new JCheckBoxMenuItem(am.get(TOGGLE_MODE_ACTION));
        item.setText("Single time series");
        result.add(item, index++);

        result.addSeparator();

        item = new JMenuItem(am.get(HasObsFormatCommands.FORMAT_ACTION));
        item.setText("Edit format...");
        item.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_GLOBE));
        result.add(item);

        item = new JCheckBoxMenuItem(JTsGridCommands.toggleUseColorScheme().toAction(target));
        item.setText("Use color scheme");
        result.add(item);

        result.add(HasColorSchemeCommands.menuOf(target, DemetraOptions.getDefault().getColorSchemes()));

        item = new JCheckBoxMenuItem(JTsGridCommands.toggleShowBars().toAction(target));
        item.setText("Show bars");
        item.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_TASKS));
        result.add(item);

        item = new JCheckBoxMenuItem(HasGridCommands.toggleCrosshairVisibility().toAction(target));
        item.setText("Show crosshair");
        item.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_CROSSHAIRS));
        result.add(item);

        result.add(HasGridCommands.newZoomRationMenu(target));

        return result;
    }

    private class GridSelectionListener extends InternalTsSelectionAdapter {

        private GridSelectionListener(HasTsCollection outer) {
            super(outer);
        }

        @Override
        protected void selectionChanged(ListSelectionModel model) {
            if (target.getMode() == JTsGrid.Mode.MULTIPLETS) {
                super.selectionChanged(model);
            } else if (target.getTsCollection().size() > target.getSingleTsIndex()) {
                int index = target.getSingleTsIndex();
                target.getTsSelectionModel().clearSelection();
                target.getTsSelectionModel().setSelectionInterval(index, index);
            } else {
                target.getTsSelectionModel().clearSelection();
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
                        target.setHoveredObs(((GridModelAdapter) grid.getModel()).toObsIndex(grid.getHoveredCell()));
                        break;
                }
                updating = false;
            }
        }

        private void applyHoveredCell(ObsIndex hoveredObs) {
            if (!updating) {
                grid.setHoveredCell(((GridModelAdapter) grid.getModel()).toCellIndex(hoveredObs));
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
                JLabel label = (JLabel) result;
                String text = label.getText();
                label.setText(MultiLineNameUtil.join(text));
                label.setToolTipText(MultiLineNameUtil.toHtml(text));
                label.setHorizontalAlignment(JLabel.TRAILING);
            }
            return result;
        }
    }

    private static final class CustomColumnRenderer implements TableCellRenderer {

        private final TableCellRenderer delegate;

        public CustomColumnRenderer(JGrid grid) {
            this.delegate = grid.getColumnRenderer();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component result = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (result instanceof JLabel) {
                JLabel label = (JLabel) result;
                String text = label.getText();
                label.setText(MultiLineNameUtil.join(text));
                label.setToolTipText(MultiLineNameUtil.toHtml(text));
            }
            return result;
        }
    }

    private static final class CustomCellRenderer extends BarTableCellRenderer {

        private final TableCellRenderer delegate;
        private final IFormatter<? super TsPeriod> periodFormatter;
        private final JToolTip toolTip;
        private IFormatter<? super Number> valueFormatter;
        private SwingColorSchemeSupport colorSchemeSupport;
        private boolean showBars;
        private Supplier<TsFeatureHelper> tsFeatures;
        private Supplier<DescriptiveStatistics> stats;

        public CustomCellRenderer(@NonNull TableCellRenderer delegate) {
            super(false);
            setHorizontalAlignment(JLabel.TRAILING);
            setOpaque(true);
            this.delegate = delegate;
            this.periodFormatter = TsPeriod::toString;
            this.toolTip = super.createToolTip();
            this.valueFormatter = DataFormat.DEFAULT.numberFormatter();
            this.colorSchemeSupport = null;
            this.showBars = false;
            this.tsFeatures = () -> TsFeatureHelper.EMPTY;
            this.stats = () -> new DescriptiveStatistics();
        }

        void update(@NonNull DataFormat dataFormat, @Nullable SwingColorSchemeSupport colorSchemeSupport, boolean showBars, Supplier<TsFeatureHelper> tsFeatures, Supplier<DescriptiveStatistics> stats) {
            this.valueFormatter = dataFormat.numberFormatter();
            this.colorSchemeSupport = colorSchemeSupport;
            this.showBars = showBars;
            this.tsFeatures = tsFeatures;
            this.stats = stats;
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

            switch (obs.getStatus()) {
                case AFTER:
                case BEFORE:
                case EMPTY:
                case UNUSED:
                    setText(null);
                    setToolTipText(null);
                    setBarValues(0, 0, 0);
                    break;
                case PRESENT:
                    if (Double.isNaN(obs.getValue())) {
                        setText(".");
                        setToolTipText(periodFormatter.formatAsString(obs.getPeriod()));
                        setBarValues(0, 0, 0);
                    } else {
                        String valueAsString = valueFormatter.formatAsString(obs.getValue());
                        String periodAsString = periodFormatter.formatAsString(obs.getPeriod());
                        if (tsFeatures.get().hasFeature(TsFeatureHelper.Feature.Forecasts, obs.getSeriesIndex(), obs.getIndex())) {
                            setText("<html><i>" + valueAsString);
                            setToolTipText("<html>" + periodAsString + ": " + valueAsString + "<br>Forecast");
                        } else {
                            setText(valueAsString);
                            setToolTipText(periodAsString + ": " + valueAsString);
                        }
                        if (showBars && !isSelected) {
                            DescriptiveStatistics tmp = stats.get();
                            setBarValues(tmp.getMin(), tmp.getMax(), obs.getValue());
                        } else {
                            setBarValues(0, 0, 0);
                        }
                        break;
                    }
            }

            return this;
        }
    }

    private static final class ComboCellRenderer extends DefaultListCellRenderer {

        private final TsMonikerUI monikerUI;
        private final SwingColorSchemeSupport colorSchemeSupport;

        public ComboCellRenderer(@Nullable SwingColorSchemeSupport colorSchemeSupport) {
            this.monikerUI = TsMonikerUI.getDefault();
            this.colorSchemeSupport = colorSchemeSupport;
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            demetra.timeseries.Ts ts = (demetra.timeseries.Ts) value;
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

    @lombok.AllArgsConstructor
    private static final class GridModelAdapter extends AbstractTableModel implements GridModel {

        private final TsGridData data;
        private final boolean transposed;
        private final IntUnaryOperator rowIndexer;
        private final IntUnaryOperator columnIndexer;

        @Override
        public int getRowCount() {
            return transposed
                    ? data.getColumnCount()
                    : data.getRowCount();
        }

        @Override
        public int getColumnCount() {
            return transposed
                    ? data.getRowCount()
                    : data.getColumnCount();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return transposed
                    ? data.getObs(rowIndexer.applyAsInt(columnIndex), columnIndexer.applyAsInt(rowIndex))
                    : data.getObs(rowIndexer.applyAsInt(rowIndex), columnIndexer.applyAsInt(columnIndex));
        }

        @Override
        public String getRowName(int rowIndex) {
            return transposed
                    ? data.getColumnName(columnIndexer.applyAsInt(rowIndex))
                    : data.getRowName(rowIndexer.applyAsInt(rowIndex));
        }

        @Override
        public String getColumnName(int columnIndex) {
            return transposed
                    ? data.getRowName(rowIndexer.applyAsInt(columnIndex))
                    : data.getColumnName(columnIndexer.applyAsInt(columnIndex));
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return TsGridObs.class;
        }

        @NonNull
        public ObsIndex toObsIndex(@NonNull CellIndex index) {
            if (CellIndex.NULL.equals(index)) {
                return ObsIndex.NULL;
            }
            TsGridObs obs = transposed
                    ? data.getObs(rowIndexer.applyAsInt(index.getColumn()), columnIndexer.applyAsInt(index.getRow()))
                    : data.getObs(rowIndexer.applyAsInt(index.getRow()), columnIndexer.applyAsInt(index.getColumn()));
            if (TsDataTable.ValueStatus.PRESENT.equals(obs.getStatus())) {
                return ObsIndex.valueOf(obs.getSeriesIndex(), obs.getIndex());
            }
            return ObsIndex.NULL;
        }

        @NonNull
        public CellIndex toCellIndex(@NonNull ObsIndex index) {
            return transposed
                    ? CellIndex.valueOf(rowIndexer.applyAsInt(data.getColumnIndex(index)), columnIndexer.applyAsInt(data.getRowIndex(index)))
                    : CellIndex.valueOf(rowIndexer.applyAsInt(data.getRowIndex(index)), columnIndexer.applyAsInt(data.getColumnIndex(index)));
        }
    }
}
