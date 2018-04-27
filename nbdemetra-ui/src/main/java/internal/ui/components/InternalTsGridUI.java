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

import demetra.ui.components.TsSelectionBridge;
import demetra.ui.components.HasColorScheme;
import demetra.ui.components.HasObsFormat;
import demetra.ui.components.HasTsCollection;
import ec.nbdemetra.ui.DemetraUI;
import ec.nbdemetra.ui.MonikerUI;
import ec.nbdemetra.ui.ThemeSupport;
import ec.nbdemetra.ui.awt.ActionMaps;
import ec.nbdemetra.ui.awt.InputMaps;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.datatransfer.TssTransferSupport;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tss.tsproviders.utils.IFormatter;
import ec.tss.tsproviders.utils.MultiLineNameUtil;
import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import demetra.ui.components.JTsGrid;
import ec.ui.chart.DataFeatureModel;
import ec.ui.grid.TsGridObs;
import static internal.ui.components.JTsGridCommands.MULTI_TS_ACTION;
import static internal.ui.components.JTsGridCommands.REVERSE_ACTION;
import static internal.ui.components.JTsGridCommands.SINGLE_TS_ACTION;
import static internal.ui.components.JTsGridCommands.TOGGLE_MODE_ACTION;
import static internal.ui.components.JTsGridCommands.TRANSPOSE_ACTION;
import ec.util.chart.ObsIndex;
import ec.util.chart.swing.SwingColorSchemeSupport;
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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

public final class InternalTsGridUI implements InternalUI<JTsGrid> {

    private JTsGrid target;

    private final JGrid grid = new JGrid();
    private final JComboBox combo = new JComboBox();
    private final GridHandler gridHandler = new GridHandler();
    private final CustomCellRenderer defaultCellRenderer = new CustomCellRenderer(grid.getDefaultRenderer(Object.class));
    private final DataFeatureModel dataFeatureModel = new DataFeatureModel();
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
                case HasObsFormat.DATA_FORMAT_PROPERTY:
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
        dataFeatureModel.setData(target.getTsCollection().toArray());
        updateGridModel();
        updateComboModel();
        updateNoDataMessage();
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
        grid.setTransferHandler(th != null ? th : new HasTsCollectionTransferHandler(target, TssTransferSupport.getDefault()));
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
        TsCollection collection = target.getTsCollection();
        int index = target.getMode() == JTsGrid.Mode.SINGLETS ? Math.min(target.getSingleTsIndex(), collection.getCount() - 1) : -1;
        grid.setModel(new GridModelAdapter(TsGridData.create(collection, index, target.getOrientation(), target.getChronology(), dataFeatureModel)));
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
            TsCollection collection = target.getTsCollection();
            if (target.getMode() == JTsGrid.Mode.MULTIPLETS) {
                selectionListener.changeSelection(target.getOrientation() == JTsGrid.Orientation.NORMAL ? grid.getColumnSelectionModel() : grid.getRowSelectionModel());
            } else if (!collection.isEmpty()) {
                int index = Math.min(target.getSingleTsIndex(), collection.getCount() - 1);
                if (combo.isVisible()) {
                    combo.setSelectedIndex(index);
                }
                target.getTsSelectionModel().clearSelection();
                target.getTsSelectionModel().setSelectionInterval(index, index);
            }
        }
    }

    private void updateComboModel() {
        TsCollection collection = target.getTsCollection();
        if (target.getMode() == JTsGrid.Mode.SINGLETS && collection.getCount() > 1) {
            combo.setModel(new DefaultComboBoxModel(collection.toArray()));
            combo.setVisible(true);
        } else {
            combo.setVisible(false);
        }
    }

    private void updateGridCellRenderer() {
        defaultCellRenderer.update(themeSupport.getDataFormat(), target.isUseColorScheme() ? themeSupport : null, target.isShowBars());
        grid.setDefaultRenderer(TsGridObs.class, target.getCellRenderer());
        grid.repaint();
    }

    private void updateComboCellRenderer() {
        combo.setRenderer(new ComboCellRenderer(target.isUseColorScheme() ? themeSupport : null));
    }

    private JMenu buildMenu(DemetraUI demetraUI) {
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
        DemetraUI demetraUI = DemetraUI.getDefault();

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

        result.add(HasColorSchemeCommands.menuOf(target, DemetraUI.getDefault().getColorSchemes()));

        item = new JCheckBoxMenuItem(JTsGridCommands.toggleShowBars().toAction(target));
        item.setText("Show bars");
        item.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_TASKS));
        result.add(item);

        item = new JCheckBoxMenuItem(HasGridImpl.toggleCrosshairVisibility().toAction(target));
        item.setText("Show crosshair");
        item.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_CROSSHAIRS));
        result.add(item);

        result.add(HasGridImpl.newZoomRationMenu(target, demetraUI));

        return result;
    }

    private class GridSelectionListener extends InternalTsSelectionAdapter {

        private GridSelectionListener(HasTsCollection outer) {
            super(outer);
        }

        @Override
        protected void selectionChanged(ListSelectionModel model) {
            TsCollection collection = target.getTsCollection();
            if (target.getMode() == JTsGrid.Mode.MULTIPLETS) {
                super.selectionChanged(model);
            } else if (collection.getCount() > target.getSingleTsIndex()) {
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
                        target.setHoveredObs(((GridModelAdapter) grid.getModel()).data.toObsIndex(grid.getHoveredCell()));
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

    private static final class TsPeriodFormatter implements IFormatter<TsPeriod> {

        private final static TsPeriodFormatter INSTANCE = new TsPeriodFormatter();

        @Override
        public CharSequence format(TsPeriod value) throws NullPointerException {
            return value.toString();
        }
    }
}
