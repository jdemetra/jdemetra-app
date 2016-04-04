/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.calendars;

import com.google.common.base.MoreObjects;
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.Month;
import ec.tstoolkit.timeseries.ValidityPeriod;
import ec.tstoolkit.timeseries.calendars.FixedDay;
import ec.tstoolkit.timeseries.calendars.ISpecialDay;
import ec.tstoolkit.timeseries.calendars.ISpecialDay.Context;

/**
 *
 * @author Philippe Charles
 */
public class FixedEventBean extends AbstractEventBean {

    // PROPERTIES DEFINITIONS
    public static final String DAY_PROPERTY = "day";
    public static final String MONTH_PROPERTY = "month";
    // PROPERTIES 
    protected int day;
    protected Month month;

    public FixedEventBean() {
        this(1, Month.January, null, null, 1);
    }
    
    public FixedEventBean(FixedDay day, ValidityPeriod vp) {
        this(day.day+1, day.month, vp != null ? vp.getStart() : null, vp != null ? vp.getEnd() : null, day.getWeight());
    }

    public FixedEventBean(int day, Month month, Day start, Day end, double weight) {
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
        firePropertyChange(DAY_PROPERTY, old, this.day);
    }

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        Month old = this.month;
        this.month = month;
        firePropertyChange(MONTH_PROPERTY, old, this.month);
    }

    @Override
    protected ISpecialDay toSpecialDay(Context context) {
        return new FixedDay(day-1, month, weight);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("day", day).add("month", month).add("start", start).add("end", end).add("weigth", weight).toString();
    }
}
