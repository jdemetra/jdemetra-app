/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.calendars;

import ec.nbdemetra.ui.awt.ListenableBean;
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.ValidityPeriod;
import ec.tstoolkit.timeseries.calendars.ISpecialDay;
import ec.tstoolkit.timeseries.calendars.SpecialDayEvent;

/**
 *
 * @author Philippe Charles
 */
public abstract class AbstractEventBean extends ListenableBean {

    // PROPERTIES DEFINITIONS
    public static final String START_PROPERTY = "start";
    public static final String END_PROPERTY = "end";
    public static final String WEIGHT_PROPERTY = "weight";
    // PROPERTIES
    protected Day start;
    protected Day end;
    protected double weight;

    public AbstractEventBean(Day start, Day end, double weight) {
        this.start = start;
        this.end = end;
        this.weight = weight;
    }

    public Day getEnd() {
        return end;
    }

    public void setEnd(Day end) {
        Day old = this.end;
        this.end = end;
        firePropertyChange(END_PROPERTY, old, this.end);
    }

    public Day getStart() {
        return start;
    }

    public void setStart(Day start) {
        Day old = this.start;
        this.start = start;
        firePropertyChange(START_PROPERTY, old, this.start);
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        double old = this.weight;
        this.weight = weight;
        firePropertyChange(WEIGHT_PROPERTY, old, this.weight);
    }

    public SpecialDayEvent toEvent() {
        SpecialDayEvent result = new SpecialDayEvent(toSpecialDay());
        result.setValidityPeriod(start == null && end == null ? null : new ValidityPeriod(start == null ? Day.BEG : start, end == null ? Day.END : end));
        return result;
    }

    abstract protected ISpecialDay toSpecialDay();
}
