/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.jfreechart;

import ec.util.chart.swing.ChartCommand;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import demetra.desktop.datatransfer.DataTransfer;
import demetra.math.matrices.Matrix;

/**
 * A command that extract a matrix from a chart and put it into the system
 * clipboard.
 *
 * @author Philippe Charles
 */
public abstract class MatrixChartCommand extends ChartCommand {

    @Override
    public void execute(ChartPanel chartPanel) {
        Matrix matrix = toMatrix(chartPanel);
        Transferable t = DataTransfer.getDefault().fromMatrix(matrix);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(t, null);
    }

    /**
     * Extracts a matrix from a chart.
     *
     * @param chartPanel the source of data
     * @return a non-null matrix
     */
    @NonNull
    abstract protected Matrix toMatrix(@NonNull ChartPanel chartPanel);

    /**
     * Creates a command that extracts a single series from a chart and put in
     * into the clipboard as a matrix.
     *
     * @param index
     * @param series
     * @return a non-null command
     */
    @NonNull
    public static ChartCommand copySeries(int index, int series) {
        if (index < 0) {
            throw new IllegalArgumentException("index must be positive");
        }
        if (series < 0) {
            throw new IllegalArgumentException("series must be positive");
        }
        return new CopySeries(index, series);
    }

    private static class CopySeries extends MatrixChartCommand {

        final int index;
        final int series;

        CopySeries(int index, int series) {
            this.index = index;
            this.series = series;
        }

        @Override
        protected Matrix toMatrix(ChartPanel chartPanel) {
            XYDataset dataset = chartPanel.getChart().getXYPlot().getDataset(index);
            Matrix.Mutable result = Matrix.Mutable.make(dataset.getItemCount(series), 2);
            for (int i = 0; i < result.getRowsCount(); i++) {
                result.set(i, 0, dataset.getXValue(series, i));
                result.set(i, 1, dataset.getYValue(series, i));
            }
            return result;
        }

        @Override
        public boolean isEnabled(ChartPanel chartPanel) {
            XYPlot plot = chartPanel.getChart().getXYPlot();
            return plot.getDatasetCount() > index
                    && plot.getDataset(index).getSeriesCount() > series
                    && plot.getDataset(index).getItemCount(series) > 0;
        }
    }
}
