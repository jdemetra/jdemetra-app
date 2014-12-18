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
package ec.nbdemetra.sa.revisionanalysis;

import ec.nbdemetra.ui.DemetraUI;
import ec.tstoolkit.algorithm.CompositeResults;
import ec.tstoolkit.algorithm.IProcResults;
import ec.ui.ATsControl;
import ec.ui.chart.BasicXYDataset;
import ec.ui.chart.TsCharts;
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
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.ui.action.ActionMenuItem;
import org.openide.util.Exceptions;

/**
 *
 * @author Mats Maggi
 */
public class RevisionAnalysisChart extends ATsControl {

    private final CompositeResults results;
    private JChartPanel chartPanel;
    private JFreeChart chart;
    private DemetraUI demetraUI = DemetraUI.getDefault();

    public RevisionAnalysisChart(IProcResults rslts) {
        setLayout(new BorderLayout());
        results = (CompositeResults) rslts;
        if (results != null) {
            chart = createChart();
            chartPanel = new JChartPanel(chart);
            chartPanel.setPopupMenu(createPopupMenu().getPopupMenu());
            showResults();
            add(chartPanel, BorderLayout.CENTER);
        }
    }

    private JFreeChart createChart() {
        XYPlot plot = new XYPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setAutoPopulateSeriesPaint(false);
        renderer.setAutoPopulateSeriesStroke(false);
        renderer.setBaseStroke(TsCharts.getStrongStroke(ITsChart.LinesThickness.Thin));
        plot.setRenderer(renderer);

        JFreeChart result = new JFreeChart("", TsCharts.CHART_TITLE_FONT, plot, true);
        result.setPadding(TsCharts.CHART_PADDING);
        result.setTitle("Standard deviations");

        return result;
    }

    private JMenu createPopupMenu() {
        JMenu result = new JMenu();
        result.add(newExportMenu());

        return result;
    }

    private JMenu newExportMenu() {
        JMenu result = new JMenu("Export image to");
        result.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_FLOPPY_O));
        JMenuItem copy = new ActionMenuItem(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                chartPanel.doCopy();
            }
        });
        copy.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_CLIPBOARD));
        copy.setText("Clipboard");

        JMenuItem file = new ActionMenuItem(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    chartPanel.doSaveAs();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
        file.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_PICTURE_O));
        file.setText("File...");

        result.add(copy);
        result.add(file);

        return result;
    }

    private void showResults() {
        BasicXYDataset dataset = new BasicXYDataset();
        XYPlot plot = chart.getXYPlot();

        double[] sa = results.getData("summary.sastdev", double[].class);
        double[] dsa = results.getData("summary.dsastdev", double[].class);
        double[] s = results.getData("summary.sstdev", double[].class);
        double[] c = results.getData("summary.cstdev", double[].class);

        if (sa == null || dsa == null || s == null || c == null) {
            return;
        }

        double[] num = new double[sa.length];
        for (int i = 0; i < num.length; i++) {
            num[i] = i + 1;
        }

        dataset.addSeries(BasicXYDataset.Series.of("SA", num, sa));
        dataset.addSeries(BasicXYDataset.Series.of("SA changes", num, dsa));
        dataset.addSeries(BasicXYDataset.Series.of("S", num, s));
        dataset.addSeries(BasicXYDataset.Series.of("Cal", num, c));

        plot.setDataset(dataset);
        configureAxis(plot);
        onColorSchemeChange();
    }

    private void configureAxis(XYPlot plot) {
        NumberAxis xAxis = new NumberAxis();
        plot.setDomainAxis(xAxis);
        NumberAxis yaxis = new NumberAxis();
        yaxis.setAutoRangeIncludesZero(false);
        plot.setRangeAxis(yaxis);
    }

    @Override
    protected void onDataFormatChange() {

    }

    @Override
    protected void onColorSchemeChange() {
        XYPlot plot = chartPanel.getChart().getXYPlot();
        for (int i = 0; i < plot.getDataset().getSeriesCount(); i++) {
            plot.getRenderer().setSeriesPaint(i, themeSupport.getLineColor(i));
        }
        plot.setBackgroundPaint(themeSupport.getPlotColor());
        plot.setDomainGridlinePaint(themeSupport.getGridColor());
        plot.setRangeGridlinePaint(themeSupport.getGridColor());
        chartPanel.getChart().setBackgroundPaint(themeSupport.getBackColor());
    }

}
