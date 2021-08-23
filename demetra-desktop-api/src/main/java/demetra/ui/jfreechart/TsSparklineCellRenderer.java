/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.ui.jfreechart;

import demetra.timeseries.Ts;
import demetra.timeseries.TsData;
import ec.util.chart.swing.Charts;
import ec.util.chart.swing.SparklineCellRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

/**
 * @see Charts#createSparkLineChart(org.jfree.data.xy.XYDataset)
 * @author Philippe Charles
 */
public final class TsSparklineCellRenderer extends SparklineCellRenderer {

    @Override
    protected XYDataset getDataset(Object value) {
        if (value instanceof TimeSeries) {
            return new TimeSeriesCollection((TimeSeries) value);
        }
        if (value instanceof TsData) {
            return TsCharts.newSparklineDataset((TsData) value);
        }
        if (value instanceof Ts) {
            return TsCharts.newSparklineDataset(((Ts) value).getData());
        }
        if (value instanceof demetra.timeseries.TsData) {
            return TsCharts.newSparklineDataset((demetra.timeseries.TsData) value);
        }
        return super.getDataset(value);
    }
}
