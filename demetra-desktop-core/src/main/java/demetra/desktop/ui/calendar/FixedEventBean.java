/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.calendar;

import demetra.desktop.design.SwingProperty;
import demetra.timeseries.ValidityPeriod;
import demetra.timeseries.calendars.FixedDay;
import demetra.timeseries.calendars.Holiday;

import java.time.LocalDate;
import java.time.Month;

/**
 *
 * @author Philippe Charles
 */
@lombok.ToString
public class FixedEventBean extends AbstractEventBean {

    // PROPERTIES DEFINITIONS
    @SwingProperty
    public static final String DAY_PROPERTY = "day";

    @SwingProperty
    public static final String MONTH_PROPERTY = "month";
    // PROPERTIES 
    private int day;
    private Month month;

    public FixedEventBean() {
        this(1, Month.JANUARY , null, null, 1);
    }
    
    public FixedEventBean(FixedDay day, ValidityPeriod vp) {
        this(day.getDay(), Month.of(day.getMonth()), vp.getStart(), vp.getEnd(), day.getWeight());
    }

    public FixedEventBean(int day, Month month, LocalDate start, LocalDate end, double weight) {
        super(start, end, weight);
        this.day = day;
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        int old = this.day;
        this.day = day;
        broadcaster.firePropertyChange(DAY_PROPERTY, old, this.day);
    }

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        Month old = this.month;
        this.month = month;
        broadcaster.firePropertyChange(MONTH_PROPERTY, old, this.month);
    }

    @Override
    public Holiday toHoliday() {
        return new FixedDay(month.getValue(), day, getWeight(), validityPeriod());
    }
}
