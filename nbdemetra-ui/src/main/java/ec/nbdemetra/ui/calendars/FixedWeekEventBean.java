/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.calendars;

import com.google.common.base.MoreObjects;
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.DayOfWeek;
import ec.tstoolkit.timeseries.Month;
import ec.tstoolkit.timeseries.ValidityPeriod;
import ec.tstoolkit.timeseries.calendars.FixedWeekDay;
import ec.tstoolkit.timeseries.calendars.ISpecialDay;
import ec.tstoolkit.timeseries.calendars.ISpecialDay.Context;

/**
 *
 * @author Philippe Charles
 */
public class FixedWeekEventBean extends AbstractEventBean {

    // PROPERTIES DEFINITIONS
    public static final String DAY_OF_WEEK_PROPERTY = "dayOfWeek";
    public static final String WEEK_PROPERTY = "week";
    public static final String MONTH_PROPERTY = "month";
    // PROPERTIES 
    protected DayOfWeek dayOfWeek;
    protected int week;
    protected Month month;

    public FixedWeekEventBean() {
        this(DayOfWeek.Monday, 1, Month.January, null, null, 1);
    }

    public FixedWeekEventBean(FixedWeekDay day, ValidityPeriod vp) {
        this(day.dayOfWeek, day.week, day.month, vp != null ? vp.getStart() : null, vp != null ? vp.getEnd() : null, day.getWeight());
    }

    public FixedWeekEventBean(DayOfWeek dayOfWeek, int week, Month month, Day start, Day end, double weight) {
        super(start, end, weight);
        this.dayOfWeek = dayOfWeek;
        this.week = week;
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

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        int old = this.week;
        this.week = week;
        broadcaster.firePropertyChange(WEEK_PROPERTY, old, this.week);
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
    protected ISpecialDay toSpecialDay(Context context) {
        return new FixedWeekDay(week, dayOfWeek, month, weight);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("dayOfWeek", dayOfWeek).add("week", week).add("month", month).add("start", start).add("end", end).add("weigth", weight).toString();
    }
}
