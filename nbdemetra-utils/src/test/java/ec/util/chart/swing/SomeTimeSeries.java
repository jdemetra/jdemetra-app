/*
 * Copyright 2013 National Bank of Belgium
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
package ec.util.chart.swing;

import org.jfree.data.time.Month;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 *
 * @author Philippe Charles
 */
final class SomeTimeSeries {

    public static TimeSeries newTimeSeries(String name, RegularTimePeriod first, double... values) {
        RegularTimePeriod current = first;
        TimeSeries result = new TimeSeries(name);
        for (double o : values) {
            result.add(current, o, false);
            current = current.next();
        }
        return result;
    }

    public static TimeSeriesCollection getCol1() {
        TimeSeriesCollection result = new TimeSeriesCollection();
        result.setXPosition(TimePeriodAnchor.MIDDLE);
        result.addSeries(newTimeSeries("S1", new Month(), 100, 104, 102));
        result.addSeries(newTimeSeries("S2", new Month(), 120, 110, 110));
        result.addSeries(newTimeSeries("S3", new Month(), 1, 2, 3));
        return result;
    }

}
