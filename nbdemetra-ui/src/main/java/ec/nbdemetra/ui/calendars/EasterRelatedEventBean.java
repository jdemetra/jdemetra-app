/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.calendars;

import com.google.common.base.MoreObjects;
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.ValidityPeriod;
import ec.tstoolkit.timeseries.calendars.EasterRelatedDay;
import ec.tstoolkit.timeseries.calendars.ISpecialDay;

/**
 *
 * @author Philippe Charles
 */
public class EasterRelatedEventBean extends AbstractEventBean {

    // PROPERTIES DEFINITIONS
    public static final String OFFSET_PROPERTY = "offset";
    // PROPERTIES 
    protected int offset;

    public EasterRelatedEventBean() {
        this(1, null, null, 1);
    }
    
    public EasterRelatedEventBean(EasterRelatedDay day, ValidityPeriod vp) {
        this(day.offset, vp != null ? vp.getStart() : null, vp != null ? vp.getEnd() : null, day.getWeight());
    }

    public EasterRelatedEventBean(int offset, Day start, Day end, double weight) {
        super(start, end, weight);
        this.offset = offset;
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
    protected ISpecialDay toSpecialDay() {
        return new EasterRelatedDay(offset, weight);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("offset", offset).add("start", start).add("end", end).add("weigth", weight).toString();
    }
}
