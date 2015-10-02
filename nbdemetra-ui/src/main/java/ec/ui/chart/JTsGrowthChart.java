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
package ec.ui.chart;

import ec.nbdemetra.ui.awt.ActionMaps;
import ec.nbdemetra.ui.awt.InputMaps;
import ec.tss.*;
import ec.tss.datatransfer.TssTransferSupport;
import ec.tstoolkit.timeseries.TsPeriodSelector;
import ec.ui.ATsGrowthChart;
import ec.ui.DemoUtils;
import ec.ui.interfaces.ITsPrinter;
import ec.util.chart.ObsFunction;
import ec.util.chart.SeriesFunction;
import ec.util.chart.SeriesPredicate;
import ec.util.chart.TimeSeriesChart;
import ec.util.chart.swing.Charts;
import ec.util.chart.swing.JTimeSeriesChart;
import ec.util.chart.swing.JTimeSeriesChartCommand;
import ec.util.chart.swing.SelectionMouseListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.Beans;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TooManyListenersException;
import javax.swing.*;
import org.jfree.data.xy.IntervalXYDataset;
import org.openide.util.Exceptions;

/**
 *
 * @author Kristof Bayens
 */
public class JTsGrowthChart extends ATsGrowthChart {

    // OTHER
    protected final JTimeSeriesChart chartPanel;
    protected final ITsPrinter printer;
    protected final ListSelectionModel selectionModel;
    private final TsCollectionSelectionListener selectionListener;

    public JTsGrowthChart() {
        setLayout(new BorderLayout());

        this.chartPanel = new JTimeSeriesChart();

        chartPanel.setTransferHandler(new TsCollectionTransferHandler());
        enableDropContent();

        this.printer = new ITsPrinter() {
            @Override
            public boolean printPreview() {
                chartPanel.printImage();
                return true;
            }

            @Override
            public boolean print() {
                return printPreview();
            }
        };
        this.selectionModel = new DefaultListSelectionModel();
        this.selectionListener = new TsCollectionSelectionListener();

        selectionModel.addListSelectionListener(selectionListener);

        this.add(chartPanel, BorderLayout.CENTER);

        chartPanel.addMouseListener(new SelectionMouseListener(selectionModel, true));

        enableOpenOnDoubleClick();

        onAxisVisibleChange();
        onColorSchemeChange();
        onLegendVisibleChange();
        onTitleChange();
        onUpdateModeChange();
        onDataFormatChange();
        onComponentPopupMenuChange();

        ActionMaps.copyEntries(getActionMap(), false, chartPanel.getActionMap());
        InputMaps.copyEntries(getInputMap(), false, chartPanel.getInputMap());
        NumberFormat percent = NumberFormat.getPercentInstance();
        percent.setMaximumFractionDigits(1);
        chartPanel.setValueFormat(percent);
        chartPanel.setSeriesFormatter(new SeriesFunction<String>() {
            @Override
            public String apply(int series) {
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
                return result.toString();
            }
        });
        chartPanel.setLegendVisibilityPredicate(new SeriesPredicate() {
            @Override
            public boolean apply(int series) {
                return series < collection.getCount();
            }
        });
        chartPanel.setSeriesRenderer(SeriesFunction.always(TimeSeriesChart.RendererType.COLUMN));

        enableProperties();
        
        if (Beans.isDesignTime()) {
            setTsCollection(DemoUtils.randomTsCollection(3));
            setTsUpdateMode(TsUpdateMode.None);
            setPreferredSize(new Dimension(200, 150));
            setTitle("Chart preview");
        }
    }

    private void enableDropContent() {
        try {
            chartPanel.getDropTarget().addDropTargetListener(new DropTargetAdapter() {
                @Override
                public void dragEnter(DropTargetDragEvent dtde) {
                    if (!getTsUpdateMode().isReadOnly() && TssTransferSupport.getDefault().canImport(dtde.getCurrentDataFlavors())) {
                        TsCollection col = TssTransferSupport.getDefault().toTsCollection(dtde.getTransferable());
                        setDropContent(col != null ? col.toArray() : null);
                    }
                }

                @Override
                public void dragExit(DropTargetEvent dte) {
                    setDropContent(null);

                }

                @Override
                public void drop(DropTargetDropEvent dtde) {
                    dragExit(dtde);
                }
            });
        } catch (TooManyListenersException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void enableOpenOnDoubleClick() {
        chartPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!Charts.isPopup(e) && Charts.isDoubleClick(e)) {
                    ActionMaps.performAction(getActionMap(), OPEN_ACTION, e);
                }
            }
        });
    }
    
    private void enableProperties() {
        this.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case "componentPopupMenu":
                        onComponentPopupMenuChange();
                        break;
                }
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="Event handlers">
    @Override
    protected void onDataFormatChange() {
        try {
            chartPanel.setPeriodFormat(themeSupport.getDataFormat().newDateFormat());
        } catch (IllegalArgumentException ex) {
            // do nothing?
        }
    }

    @Override
    protected void onColorSchemeChange() {
        chartPanel.setColorSchemeSupport(null);
        chartPanel.setColorSchemeSupport(themeSupport);
    }

    @Override
    protected void onCollectionChange() {
        TsPeriodSelector selector = computeSelector(collection, lastYears);
        growthCollection.replace(Arrays.asList(computeGrowthData(collection.toArray(), growthKind, selector)));
        chartPanel.setDataset(TsXYDatasets.from(growthCollection));
        chartPanel.resetZoom();
//        refreshRange(plot);
    }

    /**
     * Redraws all the curves from the chart; unlike redrawAll() it won't cause
     * the chart to lose its zoom level.
     */
    @Override
    protected void onSelectionChange() {
        selectionListener.setEnabled(false);
        selectionModel.clearSelection();
        for (Ts o : selection) {
            int index = collection.indexOf(o);
            selectionModel.addSelectionInterval(index, index);
        }
        selectionListener.setEnabled(true);
    }

    @Override
    protected void onUpdateModeChange() {
        chartPanel.setNoDataMessage(getTsUpdateMode().isReadOnly() ? "No data" : "Drop data here");
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
    protected void onLegendVisibleChange() {
        chartPanel.setElementVisible(TimeSeriesChart.Element.LEGEND, legendVisible);
    }

    @Override
    protected void onTitleVisibleChange() {
        chartPanel.setElementVisible(TimeSeriesChart.Element.TITLE, titleVisible);
    }

    @Override
    protected void onAxisVisibleChange() {
        chartPanel.setElementVisible(TimeSeriesChart.Element.AXIS, axisVisible);
    }

    @Override
    protected void onTitleChange() {
        chartPanel.setTitle(title);
    }

    @Override
    protected void onLinesThicknessChange() {
        chartPanel.setLineThickness(linesThickness == LinesThickness.Thin ? 1f : 2f);
    }

    @Override
    protected void onGrowthKindChange() {
        onCollectionChange();
    }

    @Override
    protected void onLastYearsChange() {
        onCollectionChange();
    }

    @Override
    protected void onUseToolLayoutChange() {
        // do nothing?
    }

    private void onComponentPopupMenuChange() {
        JPopupMenu popupMenu = getComponentPopupMenu();
        chartPanel.setComponentPopupMenu(popupMenu != null ? popupMenu : buildChartMenu().getPopupMenu());
    }
    //</editor-fold>

//    protected Range calcRange(TsCollection coll) {
//        double min = Double.NEGATIVE_INFINITY, max = -Double.POSITIVE_INFINITY;
//        for (Ts s : coll) {
//            if (s.hasData() == TsStatus.Valid) {
//                DescriptiveStatistics stats = new DescriptiveStatistics(s.getTsData().getValues());
//                double smin = stats.getMin(), smax = stats.getMax();
//                if (Double.isInfinite(min) || smin < min) {
//                    min = smin;
//                }
//                if (Double.isInfinite(max) || smax > max) {
//                    max = smax;
//                }
//            }
//        }
//        if (Double.isInfinite(max) || Double.isInfinite(min)) {
//            return new Range(0, 1);
//        }
//        double length = max - min;
//        if (length == 0) {
//            return new Range(0, 1);
//        } else {
//            double eps = length * .05;
//            return new Range(min - eps, max + eps);
//        }
//    }
//
//    void refreshRange(XYPlot plot) {
//        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
//        Range range = growthCollection != null ? calcRange(growthCollection) : new Range(0, 0);
//        rangeAxis.setRange(range);
//        DecimalFormat locale = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ROOT);
//        locale.applyPattern("#0.00");
//        rangeAxis.setTickUnit(new PercentageTickUnit(Double.parseDouble(locale.format((range.getUpperBound() - range.getLowerBound()) / 10))));
//    }
    @Override
    protected JMenu buildExportImageMenu() {
        JMenu result = super.buildExportImageMenu();
        result.add(JTimeSeriesChartCommand.copyImage().toAction(chartPanel)).setText("Clipboard");
        result.add(JTimeSeriesChartCommand.saveImage().toAction(chartPanel)).setText("File...");
        return result;
    }

    @Override
    public void showAll() {
        chartPanel.resetZoom();
    }

    @Override
    public ITsPrinter getPrinter() {
        return printer;
    }
}
