/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.calendars;

import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.ValidityPeriod;
import ec.tstoolkit.timeseries.calendars.DayEvent;
import ec.tstoolkit.timeseries.calendars.SpecialCalendarDay;

/**
 *
 * @author Philippe Charles
 */
public class SpecialEventBean extends AbstractEventBean {

    // PROPERTIES DEFINITIONS
    public static final String DAY_EVENT_PROPERTY = "dayEvent";
    public static final String OFFSET_PROPERTY = "offset";
    // PROPERTIES 
    protected DayEvent dayEvent;
    protected int offset;

    public SpecialEventBean() {
        this(DayEvent.Christmas, 0, null, null, 1);
    }

    public SpecialEventBean(SpecialCalendarDay day, ValidityPeriod vp) {
        this(day.event, day.offset, vp != null ? vp.getStart() : null, vp != null ? vp.getEnd() : null, day.getWeight());
    }

    public SpecialEventBean(DayEvent dayEvent, int offset, Day start, Day end, double weight) {
        super(start, end, weight);
        this.dayEvent = dayEvent;
        this.offset = offset;
    }

    public DayEvent getDayEvent() {
        return dayEvent;
    }

    public void setDayEvent(DayEvent dayEvent) {
        DayEvent old = this.dayEvent;
        this.dayEvent = dayEvent;
        firePropertyChange(DAY_EVENT_PROPERTY, old, this.dayEvent);
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        int old = this.offset;
        this.offset = offset;
        firePropertyChange(OFFSET_PROPERTY, old, this.offset);
    }

    @Override
    protected SpecialCalendarDay toSpecialDay() {
        return new SpecialCalendarDay(dayEvent, offset, weight);
    }
}
