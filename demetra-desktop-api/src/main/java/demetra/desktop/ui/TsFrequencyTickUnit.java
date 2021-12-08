/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package demetra.desktop.ui;

import demetra.timeseries.calendars.CalendarUtility;
import org.jfree.chart.axis.NumberTickUnit;

/**
 *
 * @author Kristof Bayens
 */
public class TsFrequencyTickUnit extends NumberTickUnit {
    private final int freq_;

    public TsFrequencyTickUnit(int freq) {
        super(1);
        freq_ = freq;
    }

    @Override
    public String valueToString(double value) {
        return CalendarUtility.formatShortPeriod(freq_, (int)value);
    }
}
