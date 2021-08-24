/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.calendars;

import demetra.ui.beans.PropertyChangeSource;
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.ValidityPeriod;
import ec.tstoolkit.timeseries.calendars.ISpecialDay;
import ec.tstoolkit.timeseries.calendars.ISpecialDay.Context;
import ec.tstoolkit.timeseries.calendars.SpecialDayEvent;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author Philippe Charles
 */
public abstract class AbstractEventBean implements PropertyChangeSource.WithWeakListeners {

    // PROPERTIES DEFINITIONS
    public static final String START_PROPERTY = "start";
    public static final String END_PROPERTY = "end";
    public static final String WEIGHT_PROPERTY = "weight";

    @lombok.experimental.Delegate(types = PropertyChangeSource.class)
    protected final PropertyChangeSupport broadcaster = new PropertyChangeSupport(this);

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
        broadcaster.firePropertyChange(END_PROPERTY, old, this.end);
    }

    public Day getStart() {
        return start;
    }

    public void setStart(Day start) {
        Day old = this.start;
        this.start = start;
        broadcaster.firePropertyChange(START_PROPERTY, old, this.start);
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        double old = this.weight;
        this.weight = weight;
        broadcaster.firePropertyChange(WEIGHT_PROPERTY, old, this.weight);
    }

    public SpecialDayEvent toEvent(Context context) {
        SpecialDayEvent result = new SpecialDayEvent(toSpecialDay(context));
        result.setValidityPeriod(start == null && end == null ? null : new ValidityPeriod(start == null ? Day.BEG : start, end == null ? Day.END : end));
        return result;
    }

    abstract protected ISpecialDay toSpecialDay(Context context);
}
