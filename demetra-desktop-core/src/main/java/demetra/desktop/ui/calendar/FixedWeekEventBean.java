/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.calendar;

import demetra.desktop.design.SwingProperty;
import demetra.timeseries.ValidityPeriod;
import demetra.timeseries.calendars.FixedWeekDay;
import demetra.timeseries.calendars.Holiday;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;

/**
 *
 * @author Philippe Charles
 */
@lombok.ToString
public class FixedWeekEventBean extends AbstractEventBean {

    // PROPERTIES DEFINITIONS
    @SwingProperty
    public static final String DAY_OF_WEEK_PROPERTY = "dayOfWeek";

    @SwingProperty
    public static final String PLACE_PROPERTY = "place";

    @SwingProperty
    public static final String MONTH_PROPERTY = "month";
    // PROPERTIES 
    private DayOfWeek dayOfWeek;
    private int place;
    private Month month;

    public FixedWeekEventBean() {
        this(DayOfWeek.MONDAY, 1, Month.JANUARY, null, null, 1);
    }

    public FixedWeekEventBean(FixedWeekDay day, ValidityPeriod vp) {
        this(day.getDayOfWeek(), day.getPlace(), Month.of(day.getMonth()), vp.getStart(), vp.getEnd(), day.getWeight());
    }

    public FixedWeekEventBean(DayOfWeek dayOfWeek, int week, Month month, LocalDate start, LocalDate end, double weight) {
        super(start, end, weight);
        this.dayOfWeek = dayOfWeek;
        this.place = week;
        this.month = month;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        DayOfWeek old = this.dayOfWeek;
        this.dayOfWeek = dayOfWeek;
        broadcaster.firePropertyChange(DAY_OF_WEEK_PROPERTY, old, this.dayOfWeek);
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        int old = this.place;
        this.place = place;
        broadcaster.firePropertyChange(PLACE_PROPERTY, old, this.place);
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
        return new FixedWeekDay(month.getValue(), place, dayOfWeek, getWeight(), validityPeriod());
    }
}
