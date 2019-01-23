/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.util.chart.swing.ext;

import com.google.common.base.Preconditions;
import ec.tstoolkit.maths.matrices.Matrix;
import ec.util.chart.swing.ChartCommand;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import javax.annotation.Nonnull;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import demetra.ui.OldDataTransfer;

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
        Transferable t = OldDataTransfer.getDefault().fromMatrix(matrix);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(t, null);
    }

    /**
     * Extracts a matrix from a chart.
     *
     * @param chartPanel the source of data
     * @return a non-null matrix
     */
    @Nonnull
    abstract protected Matrix toMatrix(@Nonnull ChartPanel chartPanel);

    /**
     * Creates a command that extracts a single series from a chart and put in
     * into the clipboard as a matrix.
     *
     * @param index
     * @param series
     * @return a non-null command
     */
    @Nonnull
    public static ChartCommand copySeries(int index, int series) {
        Preconditions.checkArgument(index >= 0, "index must be positive");
        Preconditions.checkArgument(series >= 0, "series must be positive");
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
            Matrix result = new Matrix(dataset.getItemCount(series), 2);
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
