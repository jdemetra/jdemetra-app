/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.util.chart.swing;

import ec.util.various.swing.JCommand;
import java.io.IOException;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;

/**
 *
 * @author Philippe Charles
 */
public abstract class ChartCommand extends JCommand<ChartPanel> {

    @Override
    public ActionAdapter toAction(ChartPanel chartPanel) {
        ChartActionAdapter result = new ChartActionAdapter(chartPanel);
        result.registerChartChange(chartPanel.getChart());
        return result;
    }

    public static ChartCommand copyImage() {
        return CopyImage.INSTANCE;
    }

    public static ChartCommand saveImage() {
        return SaveImage.INSTANCE;
    }

    public static ChartCommand printImage() {
        return PrintImage.INSTANCE;
    }

    public static ChartCommand resetZoom() {
        return ResetZoom.INSTANCE;
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation">
    private static class CopyImage extends ChartCommand {

        static final CopyImage INSTANCE = new CopyImage();

        @Override
        public void execute(ChartPanel chartPanel) {
            Charts.copyChart(chartPanel);
        }
    }

    private static class SaveImage extends ChartCommand {

        static final SaveImage INSTANCE = new SaveImage();

        @Override
        public void execute(ChartPanel chartPanel) throws IOException {
            Charts.saveChart(chartPanel);
        }
    }

    private static class PrintImage extends ChartCommand {

        static final PrintImage INSTANCE = new PrintImage();

        @Override
        public void execute(ChartPanel chartPanel) {
            chartPanel.createChartPrintJob();
        }
    }

    private static class ResetZoom extends ChartCommand {

        static final ResetZoom INSTANCE = new ResetZoom();

        @Override
        public void execute(ChartPanel chartPanel) {
            chartPanel.restoreAutoBounds();
        }
    }
    //</editor-fold>

    private class ChartActionAdapter extends ActionAdapter {

        public ChartActionAdapter(ChartPanel component) {
            super(component);
        }

        public void registerChartChange(JFreeChart source) {
            ChartChangeListener realListener = new ChartChangeListener() {
                @Override
                public void chartChanged(ChartChangeEvent event) {
                    refreshActionState();
                }
            };
            putValue("ChartChangeListener", realListener);
            source.addChangeListener(new WeakChartChangeListener(realListener) {
                @Override
                protected void unregister(Object source) {
                    ((JFreeChart) source).removeChangeListener(this);
                }
            });
        }
    }

    private abstract static class WeakChartChangeListener extends WeakEventListener<ChartChangeListener> implements ChartChangeListener {

        public WeakChartChangeListener(ChartChangeListener delegate) {
            super(delegate);
        }

        @Override
        public void chartChanged(ChartChangeEvent event) {
            ChartChangeListener listener = delegate.get();
            if (listener != null) {
                listener.chartChanged(event);
            } else {
                unregister(event.getSource());
            }
        }
    }
}
