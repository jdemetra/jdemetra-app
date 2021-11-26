/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.calendar;

import demetra.desktop.design.SwingProperty;
import demetra.timeseries.ValidityPeriod;
import demetra.timeseries.calendars.CalendarDefinition;
import demetra.timeseries.calendars.DayEvent;
import demetra.timeseries.calendars.Holiday;
import demetra.timeseries.calendars.PrespecifiedHoliday;
import java.time.LocalDate;

/**
 *
 * @author Philippe Charles
 */
public class PrespecifiedHolidayBean extends AbstractEventBean {

    // PROPERTIES DEFINITIONS
    @SwingProperty
    public static final String DAY_EVENT_PROPERTY = "dayEvent";

    @SwingProperty
    public static final String OFFSET_PROPERTY = "offset";

    @SwingProperty
    public static final String JULIAN_PROPERTY = "julian";
    // PROPERTIES 
    private DayEvent dayEvent;
    private int offset;
    private boolean julian;

    public PrespecifiedHolidayBean() {
        this(DayEvent.Christmas, 0, false, null, null, 1);
    }

    public PrespecifiedHolidayBean(PrespecifiedHoliday day) {
        this(day.getEvent(), day.getOffset(), day.isJulian(), day.getValidityPeriod().getStart(), day.getValidityPeriod().getEnd(), day.getWeight());
    }

    public PrespecifiedHolidayBean(DayEvent dayEvent, int offset, boolean julian, LocalDate start, LocalDate end, double weight) {
        super(start, end, weight);

        this.dayEvent = dayEvent;
        this.offset = offset;
        this.julian = julian;
    }

    public DayEvent getDayEvent() {
        return dayEvent;
    }

    public void setDayEvent(DayEvent dayEvent) {
        DayEvent old = this.dayEvent;
        this.dayEvent = dayEvent;
        broadcaster.firePropertyChange(DAY_EVENT_PROPERTY, old, this.dayEvent);
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        int old = this.offset;
        this.offset = offset;
        broadcaster.firePropertyChange(OFFSET_PROPERTY, old, this.offset);
    }

    public boolean isJulian() {
        return julian;
    }

    public void setJulian(boolean julian) {
        int old = this.offset;
        this.julian = julian;
        broadcaster.firePropertyChange(JULIAN_PROPERTY, old, this.julian);
    }

    @Override
    public Holiday toHoliday() {
        return PrespecifiedHoliday.builder()
                .event(dayEvent)
                .offset(offset)
                .julian(julian)
                .validityPeriod(validityPeriod())
                .build();
    }
}
