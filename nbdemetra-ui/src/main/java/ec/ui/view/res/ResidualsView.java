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
package ec.ui.view.res;

import ec.nbdemetra.ui.NbComponents;
import ec.tss.TsFactory;
import ec.tstoolkit.MetaData;
import ec.tstoolkit.data.DataBlock;
import ec.tstoolkit.data.DescriptiveStatistics;
import ec.ui.ATsCollectionView;
import ec.ui.ATsDataView;
import ec.ui.chart.TsXYDatasets;
import ec.ui.chart.TsCharts;
import ec.ui.grid.JTsGrid;
import ec.ui.interfaces.ITsCollectionView.TsUpdateMode;
import ec.ui.interfaces.ITsGrid.Mode;
import ec.util.chart.ColorScheme.KnownColor;
import ec.util.chart.swing.ChartCommand;
import ec.util.chart.swing.Charts;
import java.awt.BorderLayout;
import java.text.DateFormat;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.Range;
import org.jfree.ui.RectangleInsets;

/**
 *
 * @author Kristof Bayens
 */
public class ResidualsView extends ATsDataView {

    // CONSTANTS
    protected static final KnownColor MAIN_COLOR = KnownColor.BLUE;
    // OTHER
    protected final ChartPanel chartPanel;
    protected final JTsGrid grid;

    public ResidualsView() {
        this.chartPanel = new ChartPanel(buildResidualViewChart());
        Charts.avoidScaling(chartPanel);
        this.grid = new JTsGrid();
        this.grid.setTsUpdateMode(TsUpdateMode.None);
        this.grid.setMode(Mode.SINGLETS);

        onDataFormatChange();
        onColorSchemeChange();
        onComponentPopupMenuChange();

        enableProperties();

        JSplitPane splitPane = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, chartPanel, grid);
        splitPane.setDividerLocation(0.5);
        splitPane.setResizeWeight(.5);
        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
    }
    
    @Override
    public void dispose(){
        grid.dispose();
    }

    private void enableProperties() {
        this.addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case "componentPopupMenu":
                    onComponentPopupMenuChange();
                    break;
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLERS">
    @Override
    protected void onTsDataChange() {
        chartPanel.getChart().getXYPlot().setDataset(TsXYDatasets.from("", tsData));
        if (tsData != DEFAULT_TS_DATA) {
            Range rng = calcRange(tsData.internalStorage());
            ((NumberAxis) chartPanel.getChart().getXYPlot().getRangeAxis()).setTickUnit(new NumberTickUnit(calcTick(rng)), true, false);

            grid.getTsCollection().replace(TsFactory.instance.createTs("Residuals", new MetaData(), tsData));
        } else {
            grid.getTsCollection().clear();
        }
        onColorSchemeChange();
    }

    @Override
    protected void onDataFormatChange() {
        grid.setDataFormat(getDataFormat());
        try {
            DateFormat dateFormat = themeSupport.getDataFormat().newDateFormat();
            ((DateAxis) chartPanel.getChart().getXYPlot().getDomainAxis()).setDateFormatOverride(dateFormat);
        } catch (IllegalArgumentException ex) {
            // do nothing?
        }
    }

    @Override
    protected void onColorSchemeChange() {
        XYPlot plot = chartPanel.getChart().getXYPlot();
        plot.setBackgroundPaint(themeSupport.getPlotColor());
        plot.setDomainGridlinePaint(themeSupport.getGridColor());
        plot.setRangeGridlinePaint(themeSupport.getGridColor());
        chartPanel.getChart().setBackgroundPaint(themeSupport.getBackColor());

        XYItemRenderer renderer = plot.getRenderer();
        renderer.setBasePaint(themeSupport.getAreaColor(MAIN_COLOR));
        renderer.setBaseOutlinePaint(themeSupport.getLineColor(MAIN_COLOR));
    }

    private void onComponentPopupMenuChange() {
        JPopupMenu popupMenu = getComponentPopupMenu();
        chartPanel.setComponentPopupMenu(popupMenu != null ? popupMenu : buildMenu().getPopupMenu());
    }
    //</editor-fold>

    private static JFreeChart buildResidualViewChart() {
        JFreeChart result = ChartFactory.createXYBarChart("Full residuals", "", false, "", Charts.emptyXYDataset(), PlotOrientation.VERTICAL, false, false, false);
        result.setPadding(TsCharts.CHART_PADDING);
        result.getTitle().setFont(TsCharts.CHART_TITLE_FONT);

        XYPlot plot = result.getXYPlot();

        DateAxis domainAxis = new DateAxis();
        domainAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        domainAxis.setLowerMargin(0);
        domainAxis.setUpperMargin(0);
        domainAxis.setTickLabelPaint(TsCharts.CHART_TICK_LABEL_COLOR);
        plot.setDomainAxis(domainAxis);

        NumberAxis rangeAxis = new NumberAxis();
        rangeAxis.setAutoRangeIncludesZero(false);
        rangeAxis.setTickLabelInsets(new RectangleInsets(10, 5, 10, 2));
        rangeAxis.setLowerMargin(0.02);
        rangeAxis.setUpperMargin(0.02);
        rangeAxis.setTickLabelPaint(TsCharts.CHART_TICK_LABEL_COLOR);
        plot.setRangeAxis(rangeAxis);

        XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setShadowVisible(false);
        renderer.setDrawBarOutline(true);
        renderer.setAutoPopulateSeriesPaint(false);
        renderer.setAutoPopulateSeriesOutlinePaint(false);

        return result;
    }

    private JMenu buildMenu() {
        JMenu result = new JMenu();

        result.add(grid.getActionMap().get(ATsCollectionView.COPY_ALL_ACTION)).setText("Copy series");

        JMenu export = new JMenu("Export image to");
        export.add(ChartCommand.printImage().toAction(chartPanel)).setText("Printer...");
        export.add(ChartCommand.copyImage().toAction(chartPanel)).setText("Clipboard");
        export.add(ChartCommand.saveImage().toAction(chartPanel)).setText("File...");
        result.add(export);

        return result;
    }

    private Range calcRange(double[] values) {
        double min = Double.NEGATIVE_INFINITY, max = -Double.POSITIVE_INFINITY;

        DescriptiveStatistics stats = new DescriptiveStatistics(new DataBlock(values));
        double smin = stats.getMin(), smax = stats.getMax();
        if (Double.isInfinite(min) || smin < min) {
            min = smin;
        }
        if (Double.isInfinite(max) || smax > max) {
            max = smax;
        }

        if (Double.isInfinite(max) || Double.isInfinite(min)) {
            return new Range(0, 1);
        }
        double length = max - min;
        if (length == 0) {
            return new Range(0, 1);
        } else {
            //double eps = length * .05;
            //return new Range(min - eps, max + eps);
            return new Range(min, max);
        }
    }

    private double calcTick(Range rng) {
        double tick = 0;
        double avg = (rng.getUpperBound() - rng.getLowerBound()) / 6;
        if (avg > 1) {
            for (int i = 0; i < 10 && tick == 0; i++) {
                double power = Math.pow(10, i);
                if (avg > (0.01 * power) && avg <= (0.02 * power)) {
                    tick = (0.02 * power);
                } else if (avg > (0.02 * power) && avg <= (0.05 * power)) {
                    tick = (0.05 * power);
                } else if (avg > (0.05 * power) && avg <= (0.1 * power)) {
                    tick = (0.1 * power);
                }
            }
        } else {
            for (int i = 0; i < 10 && tick == 0; i++) {
                double power = Math.pow(.1, i);
                if (avg > (0.01 * power) && avg <= (0.02 * power)) {
                    tick = (0.02 * power);
                } else if (avg > (0.02 * power) && avg <= (0.05 * power)) {
                    tick = (0.05 * power);
                } else if (avg > (0.05 * power) && avg <= (0.1 * power)) {
                    tick = (0.1 * power);
                }
            }
        }
        if (tick == 0) {
            tick = avg;
        }
        return tick;
    }
}
