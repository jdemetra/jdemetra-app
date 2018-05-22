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
package internal.ui.components;

import demetra.ui.components.TsSelectionBridge;
import com.google.common.collect.Lists;
import demetra.ui.components.HasChart;
import demetra.ui.components.HasColorScheme;
import demetra.ui.components.HasObsFormat;
import demetra.ui.components.HasTsCollection;
import static demetra.ui.components.PrintableWithPreview.PRINT_ACTION;
import static demetra.ui.components.ResetableZoom.RESET_ZOOM_ACTION;
import ec.nbdemetra.ui.DemetraUI;
import ec.nbdemetra.ui.IConfigurable;
import ec.nbdemetra.ui.ThemeSupport;
import ec.nbdemetra.ui.awt.ActionMaps;
import ec.nbdemetra.ui.awt.InputMaps;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.datatransfer.TssTransferSupport;
import ec.tstoolkit.utilities.IntList;
import demetra.ui.components.JTsChart;
import ec.ui.chart.DataFeatureModel;
import ec.ui.chart.JTimeSeriesChartUtil;
import ec.ui.chart.TsXYDatasets;
import ec.util.chart.ObsFunction;
import ec.util.chart.ObsIndex;
import ec.util.chart.ObsPredicate;
import ec.util.chart.SeriesFunction;
import ec.util.chart.SeriesPredicate;
import ec.util.chart.TimeSeriesChart.Element;
import ec.util.chart.swing.JTimeSeriesChart;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.JCommand;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
import javax.swing.ActionMap;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jfree.data.xy.IntervalXYDataset;

public final class InternalTsChartUI implements InternalUI<JTsChart> {

    private JTsChart target;

    private final JTimeSeriesChart chartPanel = new JTimeSeriesChart();
    private final ChartHandler chartHandler = new ChartHandler();
    private final DataFeatureModel dataFeatureModel = new DataFeatureModel();
    private final IntList savedSelection = new IntList();
    private final DualDispatcherListener dualDispatcherListener = new DualDispatcherListener();
    private final ThemeSupport themeSupport = ThemeSupport.registered();

    private InternalTsSelectionAdapter selectionListener;

    @Override
    public void install(JTsChart component) {
        this.target = component;

        this.selectionListener = new InternalTsSelectionAdapter(target);

        themeSupport.setColorSchemeListener(target, this::onColorSchemeChange);
        themeSupport.setObsFormatListener(target, this::onDataFormatChange);

        registerActions();
        registerInputs();

        initChart();

        enableSeriesSelection();
        enableDropPreview();
        enableOpenOnDoubleClick();
        enableObsHovering();
        enableProperties();

        target.setLayout(new BorderLayout());
        target.add(chartPanel, BorderLayout.CENTER);
    }

    private void registerActions() {
        HasTsCollectionCommands.registerActions(target, target.getActionMap());
        HasChartCommands.registerActions(target, target.getActionMap());
        target.getActionMap().put(HasObsFormatCommands.FORMAT_ACTION, HasObsFormatCommands.editDataFormat().toAction(target));
        target.getActionMap().put(PRINT_ACTION, JCommand.of(JTimeSeriesChartUtil::printWithPreview).toAction(chartPanel));
        target.getActionMap().put(RESET_ZOOM_ACTION, JCommand.of(JTimeSeriesChart::resetZoom).toAction(chartPanel));
        ActionMaps.copyEntries(target.getActionMap(), false, chartPanel.getActionMap());
    }

    private void registerInputs() {
        HasTsCollectionCommands.registerInputs(target.getInputMap());
        InputMaps.copyEntries(target.getInputMap(), false, chartPanel.getInputMap());
    }

    private void initChart() {
        onAxisVisibleChange();
        onColorSchemeChange();
        onLegendVisibleChange();
        onTitleChange();
        onUpdateModeChange();
        onDataFormatChange();
        onTransferHandlerChange();
        onComponentPopupMenuChange();
        chartPanel.setSeriesFormatter(new SeriesFunction<String>() {
            @Override
            public String apply(int series) {
                TsCollection collection = target.getTsCollection();
                return collection.getCount() > series ? collection.get(series).getName() : chartPanel.getDataset().getSeriesKey(series).toString();
            }
        });
        chartPanel.setObsFormatter(new ObsFunction<String>() {
            @Override
            public String apply(int series, int obs) {
                IntervalXYDataset dataset = chartPanel.getDataset();
                CharSequence period = chartPanel.getPeriodFormat().format(new Date(dataset.getX(series, obs).longValue()));
                CharSequence value = chartPanel.getValueFormat().format(dataset.getY(series, obs));
                StringBuilder result = new StringBuilder();
                result.append(period).append(": ").append(value);
                if (dataFeatureModel.hasFeature(Ts.DataFeature.Forecasts, series, obs)) {
                    result.append("\nForecast");
                }
                return result.toString();
            }
        });
        chartPanel.setDashPredicate(new ObsPredicate() {
            @Override
            public boolean apply(int series, int obs) {
                return dataFeatureModel.hasFeature(Ts.DataFeature.Forecasts, series, obs);
            }
        });
        chartPanel.setLegendVisibilityPredicate(new SeriesPredicate() {
            @Override
            public boolean apply(int series) {
                return series < target.getTsCollection().getCount();
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="Interactive stuff">
    private void enableSeriesSelection() {
        chartPanel.getSeriesSelectionModel().addListSelectionListener(selectionListener);
    }

    private void enableDropPreview() {
        new HasTsCollectionDropTargetListener(target, TssTransferSupport.getDefault())
                .register(chartPanel.getDropTarget());
    }

    private void enableOpenOnDoubleClick() {
        chartPanel.addMouseListener(new OpenOnDoubleClick(target.getActionMap()));
    }

    private void enableObsHovering() {
        chartPanel.addPropertyChangeListener(chartHandler);
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
                case HasTsCollection.UDPATE_MODE_PROPERTY:
                    onUpdateModeChange();
                    break;
                case HasTsCollection.DROP_CONTENT_PROPERTY:
                    onDropContentChange();
                    break;
                case HasColorScheme.COLOR_SCHEME_PROPERTY:
                    onColorSchemeChange();
                    break;
                case HasObsFormat.DATA_FORMAT_PROPERTY:
                    onDataFormatChange();
                    break;
                case HasChart.LEGEND_VISIBLE_PROPERTY:
                    onLegendVisibleChange();
                    break;
                case HasChart.TITLE_VISIBLE_PROPERTY:
                    onTitleVisibleChange();
                    break;
                case HasChart.AXIS_VISIBLE_PROPERTY:
                    onAxisVisibleChange();
                    break;
                case HasChart.TITLE_PROPERTY:
                    onTitleChange();
                    break;
                case HasChart.LINES_THICKNESS_PROPERTY:
                    onLinesThicknessChange();
                    break;
                case JTsChart.HOVERED_OBS_PROPERTY:
                    onHoveredObsChange();
                    break;
                case JTsChart.DUAL_CHART_PROPERTY:
                    onDualChartChange();
                    break;
                case JTsChart.DUAL_DISPATCHER_PROPERTY:
                    onDualDispatcherChange(evt);
                    break;
                case "transferHandler":
                    onTransferHandlerChange();
                    break;
                case "componentPopupMenu":
                    onComponentPopupMenuChange();
                    break;
            }
        });
        target.getDualDispatcher().addListSelectionListener(dualDispatcherListener);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Event Handlers">
    private void onDataFormatChange() {
        JTimeSeriesChartUtil.setDataFormat(chartPanel, themeSupport.getDataFormat());
    }

    private void onColorSchemeChange() {
        chartPanel.setColorSchemeSupport(null);
        chartPanel.setColorSchemeSupport(themeSupport);
    }

    private void onCollectionChange() {
        selectionListener.setEnabled(false);
        Ts[] tss = target.getTsCollection().toArray();
        dataFeatureModel.setData(tss);
        chartPanel.setDataset(TsXYDatasets.from(tss));
        updateNoDataMessage();
        selectionListener.setEnabled(true);
    }

    /**
     * Redraws all the curves from the chart; unlike redrawAll() it won't cause
     * the chart to lose its zoom level.
     */
    private void onSelectionChange() {
        if (selectionListener.isEnabled()) {
            selectionListener.setEnabled(false);
            selectionListener.changeSelection(chartPanel.getSeriesSelectionModel());
            selectionListener.setEnabled(true);
        }
    }

    private void onUpdateModeChange() {
        updateNoDataMessage();
    }

    private void onDropContentChange() {
        Ts[] collection = target.getTsCollection().toArray();
        Ts[] dropContent = target.getDropContent().toArray();

        List<Ts> tmp = Lists.newArrayList(dropContent);
        tmp.removeAll(Arrays.asList(collection));

        Ts[] tss = Stream.concat(Stream.of(collection), tmp.stream()).toArray(Ts[]::new);
        dataFeatureModel.setData(tss);
        chartPanel.setDataset(TsXYDatasets.from(tss));

        selectionListener.setEnabled(false);
        ListSelectionModel m = chartPanel.getSeriesSelectionModel();
        if (dropContent.length > 0) {
            savedSelection.clear();
            for (int series = m.getMinSelectionIndex(); series <= m.getMaxSelectionIndex(); series++) {
                if (m.isSelectedIndex(series)) {
                    savedSelection.add(series);
                }
            }
            int offset = collection.length;
            m.setSelectionInterval(offset, offset + dropContent.length);
        } else {
            m.clearSelection();
            for (int series : savedSelection.toArray()) {
                m.addSelectionInterval(series, series);
            }
        }
        selectionListener.setEnabled(true);
    }

    private void onLegendVisibleChange() {
        chartPanel.setElementVisible(Element.LEGEND, target.isLegendVisible());
    }

    private void onTitleVisibleChange() {
        chartPanel.setElementVisible(Element.TITLE, target.isTitleVisible());
    }

    private void onAxisVisibleChange() {
        chartPanel.setElementVisible(Element.AXIS, target.isAxisVisible());
    }

    private void onTitleChange() {
        chartPanel.setTitle(target.getTitle());
    }

    private void onLinesThicknessChange() {
        chartPanel.setLineThickness(target.getLinesThickness() == HasChart.LinesThickness.Thin ? 1f : 2f);
    }

    private void onTransferHandlerChange() {
        TransferHandler th = target.getTransferHandler();
        chartPanel.setTransferHandler(th != null ? th : new HasTsCollectionTransferHandler(target, TssTransferSupport.getDefault()));
    }

    private void onComponentPopupMenuChange() {
        JPopupMenu popupMenu = target.getComponentPopupMenu();
        chartPanel.setComponentPopupMenu(popupMenu != null ? popupMenu : buildChartMenu(target.getActionMap()).getPopupMenu());
    }

    private void onHoveredObsChange() {
        chartHandler.applyHoveredCell(target.getHoveredObs());
    }

    private void onDualChartChange() {
        if (target.isDualChart()) {
            chartPanel.setPlotWeights(new int[]{2, 1});
            chartPanel.setPlotDispatcher(new SeriesFunction<Integer>() {
                @Override
                public Integer apply(int series) {
                    return target.getDualDispatcher().isSelectedIndex(series) ? 1 : 0;
                }
            });
        } else {
            chartPanel.setPlotWeights(null);
            chartPanel.setPlotDispatcher(null);
        }
    }

    private void onDualDispatcherChange(PropertyChangeEvent evt) {
        ListSelectionModel oldValue = (ListSelectionModel) evt.getOldValue();
        oldValue.removeListSelectionListener(dualDispatcherListener);

        ListSelectionModel newValue = (ListSelectionModel) evt.getNewValue();
        newValue.addListSelectionListener(dualDispatcherListener);
    }
    //</editor-fold>

    private void updateNoDataMessage() {
        chartPanel.setNoDataMessage(InternalComponents.getNoDataMessage(target));
    }

    private JMenu buildChartMenu(ActionMap am) {
        DemetraUI demetraUI = DemetraUI.getDefault();
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

        result.addSeparator();
        JMenuItem item = new JMenuItem(am.get(IConfigurable.CONFIGURE_ACTION));
        item.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_COGS));
        item.setText("Configure...");
        result.add(item);

        result.add(HasTsCollectionCommands.newSplitMenu(am, demetraUI));
        result.addSeparator();
        result.add(HasChartCommands.newToggleTitleVisibilityMenu(am, demetraUI));
        result.add(HasChartCommands.newToggleLegendVisibilityMenu(am, demetraUI));
        result.add(HasObsFormatCommands.newEditFormatMenu(am, demetraUI));
        result.add(HasColorSchemeCommands.menuOf(target, demetraUI.getColorSchemes()));
        result.add(HasChartCommands.newLinesThicknessMenu(am));
        result.addSeparator();
        result.add(InternalComponents.newResetZoomMenu(am, demetraUI));

        result.add(buildExportImageMenu(demetraUI));

        return result;
    }

    private JMenu buildExportImageMenu(DemetraUI demetraUI) {
        JMenu result = new JMenu("Export image to");
        result.add(InternalComponents.menuItemOf(target));
        result.add(InternalComponents.newCopyImageMenu(chartPanel, demetraUI));
        result.add(InternalComponents.newSaveImageMenu(chartPanel, demetraUI));
        return result;
    }

    private final class ChartHandler implements PropertyChangeListener {

        private boolean updating = false;

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (!updating) {
                updating = true;
                switch (evt.getPropertyName()) {
                    case JTimeSeriesChart.HOVERED_OBS_PROPERTY:
                        target.setHoveredObs(chartPanel.getHoveredObs());
                        break;
                }
                updating = false;
            }
        }

        private void applyHoveredCell(ObsIndex hoveredObs) {
            if (!updating) {
                chartPanel.setHoveredObs(hoveredObs);
            }
        }
    }

    private final class DualDispatcherListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                onCollectionChange();
            }
        }
    }
}
