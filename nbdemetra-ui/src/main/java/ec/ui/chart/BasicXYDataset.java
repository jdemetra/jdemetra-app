/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.chart;

import java.util.ArrayList;
import java.util.List;
import org.jfree.data.xy.AbstractXYDataset;

/**
 *
 * @author Philippe Charles
 */
public class BasicXYDataset extends AbstractXYDataset {

    final List<Series> data;

    public BasicXYDataset() {
        this(new ArrayList<Series>());
    }

    public BasicXYDataset(List<Series> data) {
        this.data = data;
    }

    public boolean addSeries(Series series) {
        if (data.add(series)) {
            fireDatasetChanged();
            return true;
        }
        return false;
    }

    public void clear() {
        data.clear();
        fireDatasetChanged();
    }

    @Override
    public int getSeriesCount() {
        return data.size();
    }

    @Override
    public Comparable getSeriesKey(int series) {
        return data.get(series).getKey();
    }

    @Override
    public int getItemCount(int series) {
        return data.get(series).getItemCount();
    }

    @Override
    public double getXValue(int series, int item) {
        return data.get(series).getXValue(item);
    }

    @Override
    public Number getX(int series, int item) {
        return getXValue(series, item);
    }

    @Override
    public double getYValue(int series, int item) {
        return data.get(series).getYValue(item);
    }

    @Override
    public Number getY(int series, int item) {
        return getYValue(series, item);
    }

    public abstract static class Series extends org.jfree.data.general.Series {

        public static Series empty(Comparable key) {
            return new Series(key) {
                @Override
                public double getXValue(int item) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public double getYValue(int item) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public int getItemCount() {
                    return 0;
                }
            };
        }

        public static Series of(Comparable key, final double[] xValues, final double[] yValues) {
            return new Series(key) {
                @Override
                public double getXValue(int item) {
                    return xValues[item];
                }

                @Override
                public double getYValue(int item) {
                    return yValues[item];
                }

                @Override
                public int getItemCount() {
                    return xValues.length;
                }
            };
        }

        public Series(Comparable key) {
            super(key);
        }

        abstract public double getXValue(int item);

        abstract public double getYValue(int item);
    }
}
