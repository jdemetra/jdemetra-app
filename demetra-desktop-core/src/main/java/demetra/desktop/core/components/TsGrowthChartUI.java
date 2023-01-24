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

import demetra.desktop.util.DateFormatAdapter;
import demetra.desktop.actions.Actions;
import demetra.desktop.components.JTsGrowthChart;
import demetra.desktop.components.TsSelectionBridge;
import demetra.desktop.components.parts.*;
import demetra.desktop.components.parts.HasChart.LinesThickness;
import demetra.desktop.jfreechart.TsXYDataset;
import demetra.desktop.util.ActionMaps;
import demetra.desktop.util.InputMaps;
import demetra.tsprovider.util.ObsFormat;
import ec.util.chart.ObsFunction;
import ec.util.chart.SeriesFunction;
import ec.util.chart.SeriesPredicate;
import ec.util.chart.TimeSeriesChart;
import ec.util.chart.swing.JTimeSeriesChart;
import ec.util.chart.swing.SelectionMouseListener;
import ec.util.list.swing.JLists;
import ec.util.various.swing.JCommand;
import org.jfree.data.xy.IntervalXYDataset;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Date;

import static demetra.desktop.actions.PrintableWithPreview.PRINT_ACTION;
import static demetra.desktop.actions.ResetableZoom.RESET_ZOOM_ACTION;
import static demetra.desktop.core.components.TsGrowthChartCommands.*;

/**
 * @author Kristof Bayens
 */
public final class TsGrowthChartUI implements InternalUI<JTsGrowthChart> {

    private JTsGrowthChart target;

    private final JTimeSeriesChart chartPanel = new JTimeSeriesChart();

    private InternalTsSelectionAdapter selectionListener;
    private HasObsFormatResolver obsFormatResolver;
    private HasColorSchemeResolver colorSchemeResolver;

    @Override
    public void install(JTsGrowthChart component) {
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
        enableProperties();

        target.setLayout(new BorderLayout());
        target.add(chartPanel, BorderLayout.CENTER);
    }

    private void registerActions() {
        ActionMap am = target.getActionMap();
        HasChartSupport.registerActions(target, am);
        am.put(JTsGrowthChart.PREVIOUS_PERIOD_ACTION, applyGrowthKind(JTsGrowthChart.GrowthKind.PreviousPeriod).toAction(target));
        am.put(JTsGrowthChart.PREVIOUS_YEAR_ACTION, applyGrowthKind(JTsGrowthChart.GrowthKind.PreviousYear).toAction(target));
        HasObsFormatSupport.registerActions(target, am);
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
        NumberFormat percent = NumberFormat.getPercentInstance();
        percent.setMaximumFractionDigits(1);
        chartPanel.setValueFormat(percent);
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
                return result.toString();
            }
        });
        chartPanel.setLegendVisibilityPredicate(new SeriesPredicate() {
            @Override
            public boolean apply(int series) {
                return series < target.getTsCollection().size();
            }
        });
        chartPanel.setSeriesRenderer(SeriesFunction.always(TimeSeriesChart.RendererType.COLUMN));
    }

    //<editor-fold defaultstate="collapsed" desc="Interactive stuff">
    private void enableSeriesSelection() {
        chartPanel.getSeriesSelectionModel().addListSelectionListener(selectionListener);
    }

    private void enableDropPreview() {
        HasTsCollectionSupport.newDropTargetListener(target, chartPanel.getDropTarget());
    }

    private void enableOpenOnDoubleClick() {
        ActionMaps.onDoubleClick(target.getActionMap(), HasTsCollection.OPEN_ACTION, chartPanel);
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
                case JTsGrowthChart.GROWTH_KIND_PROPERTY:
                    onGrowthKindChange();
                    break;
                case JTsGrowthChart.LAST_YEARS_PROPERTY:
                    onLastYearsChange();
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
        ObsFormat obsFormat = obsFormatResolver.resolve();
        chartPanel.setPeriodFormat(new DateFormatAdapter(obsFormat));
    }

    private void onColorSchemeChange() {
        chartPanel.setColorSchemeSupport(null);
        chartPanel.setColorSchemeSupport(colorSchemeResolver.resolve());
    }

    private void onCollectionChange() {
        selectionListener.setEnabled(false);
        chartPanel.setDataset(TsXYDataset.of(target.computeGrowthData()));
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
        chartPanel.setNoDataMessage(target.getTsUpdateMode().isReadOnly() ? "No data" : "Drop data here");
    }

    private void onLegendVisibleChange() {
        chartPanel.setElementVisible(TimeSeriesChart.Element.LEGEND, target.isLegendVisible());
    }

    private void onTitleVisibleChange() {
        chartPanel.setElementVisible(TimeSeriesChart.Element.TITLE, target.isTitleVisible());
    }

    private void onAxisVisibleChange() {
        chartPanel.setElementVisible(TimeSeriesChart.Element.AXIS, target.isAxisVisible());
    }

    private void onTitleChange() {
        chartPanel.setTitle(target.getTitle());
    }

    private void onLinesThicknessChange() {
        chartPanel.setLineThickness(target.getLinesThickness() == LinesThickness.Thin ? 1f : 2f);
    }

    private void onGrowthKindChange() {
        onCollectionChange();
    }

    private void onLastYearsChange() {
        onCollectionChange();
    }

    private void onTransferHandlerChange() {
        TransferHandler th = target.getTransferHandler();
        chartPanel.setTransferHandler(th != null ? th : HasTsCollectionSupport.newTransferHandler(target));
    }

    private void onComponentPopupMenuChange() {
        JPopupMenu popupMenu = target.getComponentPopupMenu();
        chartPanel.setComponentPopupMenu(popupMenu != null ? popupMenu : buildChartMenu().getPopupMenu());
    }
    //</editor-fold>

    private JMenu buildKindMenu() {
        ActionMap am = target.getActionMap();
        JMenu result = new JMenu("Kind");

        JMenuItem item;

        item = new JCheckBoxMenuItem(am.get(JTsGrowthChart.PREVIOUS_PERIOD_ACTION));
        item.setText("Previous Period");
        result.add(item);

        item = new JCheckBoxMenuItem(am.get(JTsGrowthChart.PREVIOUS_YEAR_ACTION));
        item.setText("Previous Year");
        result.add(item);

        return result;
    }

    private JMenu buildExportImageMenu() {
        JMenu result = new JMenu("Export image to");
        result.add(InternalComponents.menuItemOf(target));
        result.add(InternalComponents.newCopyImageMenu(chartPanel));
        result.add(InternalComponents.newSaveImageMenu(chartPanel));
        return result;
    }

    private JMenu buildMenu() {
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

        return result;
    }

    private JMenu buildChartMenu() {
        ActionMap am = target.getActionMap();
        JMenu result = buildMenu();

        JMenuItem item;

        result.add(HasTsCollectionSupport.newSplitMenu(target));
        result.addSeparator();
        result.add(HasChartSupport.newToggleTitleVisibilityMenu(target));
        result.add(HasChartSupport.newToggleLegendVisibilityMenu(target));
        result.add(HasObsFormatSupport.newEditFormatMenu(target));
        result.add(HasColorSchemeSupport.menuOf(target));
        result.add(InternalComponents.newResetZoomMenu(am));

        result.add(buildExportImageMenu());

        // NEXT
        item = new JMenuItem(copyGrowthData().toAction(target));
        item.setText("Copy growth data");
        Actions.hideWhenDisabled(item);
        result.add(item);

        result.add(buildKindMenu());

        item = new JMenuItem(editLastYears().toAction(target));
        item.setText("Edit last years...");
        result.add(item);

        return result;
    }
}
