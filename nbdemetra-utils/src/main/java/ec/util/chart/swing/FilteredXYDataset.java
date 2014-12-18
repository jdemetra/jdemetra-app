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
package ec.util.chart.swing;

import org.jfree.data.DomainOrder;
import org.jfree.data.general.AbstractSeriesDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.TableXYDataset;

/**
 *
 * @author Philippe Charles
 */
final class FilteredXYDataset extends AbstractSeriesDataset implements IntervalXYDataset, TableXYDataset {

    private final IntervalXYDataset original;
    private final int[] map;

    public FilteredXYDataset(IntervalXYDataset original, int[] map) {
        this.original = original;
        this.map = map;
    }

    public int originalIndexOf(int series) {
        return map[series];
    }

    @Override
    public int getSeriesCount() {
        return map.length;
    }

    @Override
    public Comparable getSeriesKey(int series) {
        return original.getSeriesKey(map[series]);
    }

    @Override
    public int getItemCount(int series) {
        return original.getItemCount(map[series]);
    }

    @Override
    public Number getX(int series, int item) {
        return original.getX(map[series], item);
    }

    @Override
    public Number getY(int series, int item) {
        return original.getY(map[series], item);
    }

    @Override
    public Number getStartX(int series, int item) {
        return original.getStartX(map[series], item);
    }

    @Override
    public Number getEndX(int series, int item) {
        return original.getEndX(map[series], item);
    }

    @Override
    public Number getStartY(int series, int item) {
        return original.getStartY(map[series], item);
    }

    @Override
    public Number getEndY(int series, int item) {
        return original.getEndY(map[series], item);
    }

    @Override
    public double getStartXValue(int series, int item) {
        return original.getStartXValue(map[series], item);
    }

    @Override
    public double getEndXValue(int series, int item) {
        return original.getEndXValue(map[series], item);
    }

    @Override
    public double getStartYValue(int series, int item) {
        return original.getStartYValue(map[series], item);
    }

    @Override
    public double getEndYValue(int series, int item) {
        return original.getEndYValue(map[series], item);
    }

    @Override
    public DomainOrder getDomainOrder() {
        return original.getDomainOrder();
    }

    @Override
    public double getXValue(int series, int item) {
        return original.getXValue(map[series], item);
    }

    @Override
    public double getYValue(int series, int item) {
        return original.getYValue(map[series], item);
    }

    @Override
    public int getItemCount() {
        return original instanceof TableXYDataset
                ? ((TableXYDataset) original).getItemCount()
                : (getSeriesCount() > 0 ? getItemCount(0) : 0);
    }
}
