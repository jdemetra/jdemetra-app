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

import demetra.tsprovider.util.ObsFormat;
import demetra.ui.DemetraOptions;
import demetra.ui.IconManager;
import demetra.ui.actions.Configurable;
import demetra.ui.components.ComponentBackendSpi;
import demetra.ui.components.JTsChart;
import demetra.ui.components.TsFeatureHelper;
import demetra.ui.components.TsSelectionBridge;
import demetra.ui.components.parts.*;
import demetra.ui.jfreechart.TsXYDataset;
import demetra.ui.util.ActionMaps;
import demetra.ui.util.InputMaps;
import demetra.util.IntList;
import ec.util.chart.*;
import ec.util.chart.TimeSeriesChart.Element;
import ec.util.chart.swing.JTimeSeriesChart;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.JCommand;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;
import org.jfree.data.xy.IntervalXYDataset;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static demetra.ui.actions.PrintableWithPreview.PRINT_ACTION;
import static demetra.ui.actions.ResetableZoom.RESET_ZOOM_ACTION;

public final class InternalTsChartUI implements InternalUI<JTsChart> {

    @DirectImpl
    @ServiceProvider
    public static final class Factory implements ComponentBackendSpi {

        @Override
        public boolean handles(Class<? extends JComponent> type) {
            return JTsChart.class.equals(type);
        }

        @Override
        public void install(JComponent component) {
            new InternalTsChartUI().install((JTsChart) component);
        }
    }

    private JTsChart target;

    private final JTimeSeriesChart chartPanel = new JTimeSeriesChart();
    private final ChartHandler chartHandler = new ChartHandler();
    private final IntList savedSelection = new IntList();
    private final DualDispatcherListener dualDispatcherListener = new DualDispatcherListener();

    private TsFeatureHelper tsFeatures = TsFeatureHelper.of(Collections.emptyList());
    private InternalTsSelectionAdapter selectionListener;
    private HasObsFormatResolver obsFormatResolver;
    private HasColorSchemeResolver colorSchemeResolver;

    @Override
    public void install(JTsChart component) {
        this.target = component;

        this.selectionListener = new InternalTsSelectionAdapter(target);
        this.obsFormatResolver = new HasObsFormatResolver(target, this::onDataFormatChange);
        this.colorSchemeResolver = new HasColorSchemeResolver(target, this::onColorSchemeChange);

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
        HasTsCollectionSupport.registerActions(target, target.getActionMap());
        HasChartSupport.registerActions(target, target.getActionMap());
        HasObsFormatSupport.registerActions(target, target.getActionMap());
        target.getActionMap().put(PRINT_ACTION, JCommand.of(JTimeSeriesChart::printImage).toAction(chartPanel));
        target.getActionMap().put(RESET_ZOOM_ACTION, JCommand.of(JTimeSeriesChart::resetZoom).toAction(chartPanel));
        ActionMaps.copyEntries(target.getActionMap(), false, chartPanel.getActionMap());
    }

    private void registerInputs() {
        HasTsCollectionSupport.registerInputs(target.getInputMap());
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
                return target.getTsCollection().size() > series
                        ? target.getTsCollection().get(series).getName()
                        : chartPanel.getDataset().getSeriesKey(series).toString();
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
                if (series < target.getTsCollection().size() && tsFeatures.hasFeature(TsFeatureHelper.Feature.Forecasts, series, obs)) {
                    result.append("\nForecast");
                }
                return result.toString();
            }
        });
        chartPanel.setDashPredicate(new ObsPredicate() {
            @Override
            public boolean apply(int series, int obs) {
                return series < target.getTsCollection().size() && tsFeatures.hasFeature(TsFeatureHelper.Feature.Forecasts, series, obs);
            }
        });
        chartPanel.setLegendVisibilityPredicate(new SeriesPredicate() {
            @Override
            public boolean apply(int series) {
                return series < target.getTsCollection().size();
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="Interactive stuff">
    private void enableSeriesSelection() {
        chartPanel.getSeriesSelectionModel().addListSelectionListener(selectionListener);
    }

    private void enableDropPreview() {
        HasTsCollectionSupport.newDropTargetListener(target, chartPanel.getDropTarget());
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
                case HasTsCollection.TS_UPDATE_MODE_PROPERTY:
                    onUpdateModeChange();
                    break;
                case HasTsCollection.DROP_CONTENT_PROPERTY:
                    onDropContentChange();
                    break;
                case HasColorScheme.COLOR_SCHEME_PROPERTY:
                    onColorSchemeChange();
                    break;
                case HasObsFormat.OBS_FORMAT_PROPERTY:
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
        ObsFormat obsFormat = obsFormatResolver.resolve();
        chartPanel.setPeriodFormat(new InternalComponents.DateFormatAdapter(obsFormat));
        chartPanel.setValueFormat(new InternalComponents.NumberFormatAdapter(obsFormat));
    }

    private void onColorSchemeChange() {
        chartPanel.setColorSchemeSupport(null);
        chartPanel.setColorSchemeSupport(colorSchemeResolver.resolve());
    }

    private void onCollectionChange() {
        selectionListener.setEnabled(false);
        demetra.timeseries.TsCollection tss = target.getTsCollection();
        tsFeatures = TsFeatureHelper.of(tss.getItems());
        chartPanel.setDataset(TsXYDataset.of(tss.getItems()));
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
        demetra.timeseries.TsCollection collection = target.getTsCollection();
        demetra.timeseries.TsCollection dropContent = target.getDropContent();

        List<demetra.timeseries.Ts> tmp = new ArrayList<>(dropContent.getItems());
        tmp.removeAll(collection.getItems());

        List<demetra.timeseries.Ts> tss = Stream
                .concat(collection.stream(), tmp.stream())
                .collect(Collectors.toList());
        chartPanel.setDataset(TsXYDataset.of(tss));

        selectionListener.setEnabled(false);
        ListSelectionModel m = chartPanel.getSeriesSelectionModel();
        if (dropContent.size() > 0) {
            savedSelection.clear();
            for (int series = m.getMinSelectionIndex(); series <= m.getMaxSelectionIndex(); series++) {
                if (m.isSelectedIndex(series)) {
                    savedSelection.add(series);
                }
            }
            int offset = target.getTsCollection().size();
            m.setSelectionInterval(offset, offset + dropContent.size());
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
        chartPanel.setTransferHandler(th != null ? th : HasTsCollectionSupport.newTransferHandler(target));
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
        JMenu result = new JMenu();

        result.add(HasTsCollectionSupport.newOpenMenu(target));
        result.add(HasTsCollectionSupport.newOpenWithMenu(target));

        JMenu menu = HasTsCollectionSupport.newSaveMenu(target);
        if (menu.getSubElements().length > 0) {
            result.add(menu);
        }

        result.add(HasTsCollectionSupport.newRenameMenu(target));
        result.add(HasTsCollectionSupport.newFreezeMenu(target));
        result.add(HasTsCollectionSupport.newCopyMenu(target));
        result.add(HasTsCollectionSupport.newPasteMenu(target));
        result.add(HasTsCollectionSupport.newDeleteMenu(target));
        result.addSeparator();
        result.add(HasTsCollectionSupport.newSelectAllMenu(target));
        result.add(HasTsCollectionSupport.newClearMenu(target));

        result.addSeparator();
        JMenuItem item = new JMenuItem(am.get(Configurable.CONFIGURE_ACTION));
        item.setIcon(IconManager.getDefault().getPopupMenuIcon(FontAwesome.FA_COGS));
        item.setText("Configure...");
        result.add(item);

        result.add(HasTsCollectionSupport.newSplitMenu(target));
        result.addSeparator();
        result.add(HasChartSupport.newToggleTitleVisibilityMenu(target));
        result.add(HasChartSupport.newToggleLegendVisibilityMenu(target));
        result.add(HasObsFormatSupport.newEditFormatMenu(target));
        result.add(HasColorSchemeSupport.menuOf(target));
        result.add(HasChartSupport.newLinesThicknessMenu(target));
        result.addSeparator();
        result.add(InternalComponents.newResetZoomMenu(am));

        result.add(buildExportImageMenu());

        return result;
    }

    private JMenu buildExportImageMenu() {
        JMenu result = new JMenu("Export image to");
        result.add(InternalComponents.menuItemOf(target));
        result.add(InternalComponents.newCopyImageMenu(chartPanel));
        result.add(InternalComponents.newSaveImageMenu(chartPanel));
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
