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
package demetra.ui.jfreechart;

import demetra.timeseries.Ts;
import java.time.ZoneOffset;
import java.util.List;
import org.jfree.data.DomainOrder;
import org.jfree.data.general.AbstractSeriesDataset;
import org.jfree.data.xy.IntervalXYDataset;

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
        return delegate.get(series).getData().getDomain().get(item).start().toEpochSecond(ZoneOffset.UTC);
    }

    @Override
    public double getEndXValue(int series, int item) {
        return delegate.get(series).getData().getDomain().get(item).end().toEpochSecond(ZoneOffset.UTC);
    }

    @Override
    public int getItemCount(int series) {
        return delegate.get(series).getData().length();
    }

    @Override
    public double getXValue(int series, int item) {
        // FIXME: use middle instead of start
        return delegate.get(series).getData().getDomain().get(item).start().toEpochSecond(ZoneOffset.UTC);
    }

    @Override
    public double getYValue(int series, int item) {
        return delegate.get(series).getData().getValue(item);
    }
}
