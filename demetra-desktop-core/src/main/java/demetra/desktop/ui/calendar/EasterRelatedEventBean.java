/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.calendar;

import demetra.desktop.design.SwingProperty;
import demetra.timeseries.ValidityPeriod;
import demetra.timeseries.calendars.EasterRelatedDay;
import demetra.timeseries.calendars.Holiday;

import java.time.LocalDate;

/**
 *
 * @author Philippe Charles
 */
@lombok.ToString
public class EasterRelatedEventBean extends AbstractEventBean {

    // PROPERTIES DEFINITIONS
    @SwingProperty
    public static final String OFFSET_PROPERTY = "offset", JULIAN_PROPERTY = "julian";
    // PROPERTIES 
    private int offset;
    private boolean julian;

    public EasterRelatedEventBean() {
        this(1, false, null, null, 1);
    }
    
    public EasterRelatedEventBean(EasterRelatedDay day, ValidityPeriod vp) {
        this(day.getOffset(), day.isJulian(), vp.getStart(), vp.getEnd(), day.getWeight());
    }

    public EasterRelatedEventBean(int offset, boolean julian, LocalDate start, LocalDate end, double weight) {
        super(start, end, weight);
        this.offset = offset;
        this.julian = julian;
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
        if (julian)
            return EasterRelatedDay.julian(offset, getWeight(), validityPeriod());
        else
            return EasterRelatedDay.gregorian(offset, getWeight(), validityPeriod());
    }
}
