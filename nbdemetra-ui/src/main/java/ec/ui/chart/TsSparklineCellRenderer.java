/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.chart;

import ec.tss.Ts;
import ec.tss.TsStatus;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.util.chart.swing.Charts;
import ec.util.chart.swing.SparklineCellRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

/**
 * @see Charts#createSparkLineChart(org.jfree.data.xy.XYDataset)
 * @author Philippe Charles
 */
public class TsSparklineCellRenderer extends SparklineCellRenderer {

    @Override
    protected XYDataset getDataset(Object value) {
        if (value instanceof TimeSeries) {
            return new TimeSeriesCollection((TimeSeries) value);
        }
        if (value instanceof TsData) {
            return TsCharts.newSparklineDataset((TsData) value);
        }
        if (value instanceof Ts) {
            Ts ts = (Ts) value;
            return ts.hasData().equals(TsStatus.Valid) ? TsCharts.newSparklineDataset(ts.getTsData()) : null;
        }
        return super.getDataset(value);
    }
}
