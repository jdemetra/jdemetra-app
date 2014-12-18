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
package ec.nbdemetra.chainlinking.outlineview;

import ec.nbdemetra.ui.DemetraUI;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.ui.ATsControl;
import ec.ui.chart.TsCharts;
import ec.ui.chart.TsXYDatasets;
import ec.ui.interfaces.ITsChart;
import ec.ui.view.JChartPanel;
import ec.util.various.swing.FontAwesome;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.QuarterDateFormat;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.ui.action.ActionMenuItem;
import org.openide.util.Exceptions;

/**
 * Chart displaying the different results of the Chain Linking methods
 *
 * @author Mats Maggi
 */
public class ChainLinkingChart extends ATsControl {

    private JChartPanel chartPanel;
    private JFreeChart chart;
    private TsData result;
    private DemetraUI demetraUI = DemetraUI.getDefault();

    public ChainLinkingChart() {
        setLayout(new BorderLayout());
        chart = createChart();
        chartPanel = new JChartPanel(chart);
        chartPanel.setComponentPopupMenu(createPopupMenu().getPopupMenu());
        add(chartPanel, BorderLayout.CENTER);
    }

    private JFreeChart createChart() {
        XYPlot plot = new XYPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setAutoPopulateSeriesPaint(false);
        renderer.setAutoPopulateSeriesStroke(false);
        renderer.setBaseStroke(TsCharts.getStrongStroke(ITsChart.LinesThickness.Thin));
        plot.setRenderer(renderer);

        JFreeChart r = new JFreeChart("", TsCharts.CHART_TITLE_FONT, plot, true);
        r.setPadding(TsCharts.CHART_PADDING);
        r.setTitle("Chain Linking");

        return r;
    }

    private void configureAxis(XYPlot plot) {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setAutoRangeIncludesZero(false);
        plot.setRangeAxis(xAxis);

        QuarterDateFormat qdf = new QuarterDateFormat();
        DateAxis dateAxis = new DateAxis();
        dateAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        dateAxis.setDateFormatOverride(qdf);
        plot.setDomainAxis(dateAxis);
    }

    public void setResult(TsData result) {
        this.result = result;
        showResults();
    }

    private void showResults() {
        XYPlot plot = chart.getXYPlot();
        if (result == null) {
            plot.setDataset(null);
        } else {
            plot.setDataset(TsXYDatasets.from("Annual Overlap", result));
        }

        configureAxis(plot);
        onColorSchemeChange();
    }

    @Override
    protected void onDataFormatChange() {

    }

    @Override
    protected void onColorSchemeChange() {
        XYPlot plot = chartPanel.getChart().getXYPlot();
        if (plot.getDataset() != null) {
            for (int i = 0; i < plot.getDataset().getSeriesCount(); i++) {
                plot.getRenderer().setSeriesPaint(i, themeSupport.getLineColor(i));
            }
        }
        plot.setBackgroundPaint(themeSupport.getPlotColor());
        plot.setDomainGridlinePaint(themeSupport.getGridColor());
        plot.setRangeGridlinePaint(themeSupport.getGridColor());
        chartPanel.getChart().setBackgroundPaint(themeSupport.getBackColor());
    }

    private JMenu createPopupMenu() {
        JMenu rslt = new JMenu();
        rslt.add(newExportMenu());

        return rslt;
    }

    private JMenu newExportMenu() {
        JMenu rslt = new JMenu("Export image to");
        rslt.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_FLOPPY_O));
        JMenuItem copy = new ActionMenuItem(new AbstractAction("Clipboard...", demetraUI.getPopupMenuIcon(FontAwesome.FA_CLIPBOARD)) {

            @Override
            public void actionPerformed(ActionEvent e) {
                chartPanel.doCopy();
            }
        });

        JMenuItem file = new ActionMenuItem(new AbstractAction("File...", demetraUI.getPopupMenuIcon(FontAwesome.FA_PICTURE_O)) {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    chartPanel.doSaveAs();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });

        rslt.add(copy);
        rslt.add(file);

        return rslt;
    }
}
