/*
 * Copyright 2018 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
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
package demetra.desktop.jfreechart;

import demetra.timeseries.Ts;
import demetra.timeseries.TsPeriod;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.jfree.data.DomainOrder;
import org.jfree.data.general.AbstractSeriesDataset;
import org.jfree.data.xy.IntervalXYDataset;

import java.util.List;

/**
 *
 * @author Philippe Charles
 */
@lombok.AllArgsConstructor(staticName = "of")
public final class TsXYDataset extends AbstractSeriesDataset implements IntervalXYDataset {

    @lombok.NonNull
    private final List<Ts> delegate;

    @Override
    public Number getStartX(int series, int item) {
        return getStartXValue(series, item);
    }

    @Override
    public Number getEndX(int series, int item) {
        return getEndXValue(series, item);
    }

    @Override
    public Number getStartY(int series, int item) {
        return getEndYValue(series, item);
    }

    @Override
    public double getStartYValue(int series, int item) {
        return getYValue(series, item);
    }

    @Override
    public Number getEndY(int series, int item) {
        return getEndYValue(series, item);
    }

    @Override
    public double getEndYValue(int series, int item) {
        return getYValue(series, item);
    }

    @Override
    public DomainOrder getDomainOrder() {
        return DomainOrder.NONE;
    }

    @Override
    public Number getX(int series, int item) {
        return getXValue(series, item);
    }

    @Override
    public Number getY(int series, int item) {
        return getYValue(series, item);
    }

    @Override
    public int getSeriesCount() {
        return delegate.size();
    }

    @Override
    public Comparable getSeriesKey(int series) {
        return delegate.get(series).getMoniker().toString();
    }

    @Override
    public double getStartXValue(int series, int item) {
        return toEpochMilli(getPeriod(series, item).start());
    }

    @Override
    public double getEndXValue(int series, int item) {
        // FIXME: TsPeriod#end() is upper bound excluded but IntervalXYDataset#getEndXValue(int,int) is upper bound included !
        return toEpochMilli(getPeriod(series, item).end());
    }

    @Override
    public int getItemCount(int series) {
        return delegate.get(series).getData().length();
    }

    @Override
    public double getXValue(int series, int item) {
        // FIXME: use middle instead of start ?
        return toEpochMilli(getPeriod(series, item).start());
    }

    @Override
    public double getYValue(int series, int item) {
        return delegate.get(series).getData().getValue(item);
    }

    private TsPeriod getPeriod(int series, int item) throws IndexOutOfBoundsException {
        return delegate.get(series).getData().getDomain().get(item);
    }

    private long toEpochMilli(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
