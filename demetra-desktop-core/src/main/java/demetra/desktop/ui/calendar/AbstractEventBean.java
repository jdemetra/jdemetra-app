/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.calendar;

import demetra.desktop.design.SwingProperty;
import demetra.desktop.beans.PropertyChangeSource;
import demetra.timeseries.ValidityPeriod;
import demetra.timeseries.calendars.Holiday;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;

/**
 *
 * @author Philippe Charles
 */
public abstract class AbstractEventBean implements HasHoliday, PropertyChangeSource.WithWeakListeners {

    // PROPERTIES DEFINITIONS
    @SwingProperty
    public static final String WEIGHT_PROPERTY = "weight";

    @SwingProperty
    public static final String START_PROPERTY = "start";

    @SwingProperty
    public static final String END_PROPERTY = "end";

    @lombok.experimental.Delegate(types = PropertyChangeSource.class)
    protected final PropertyChangeSupport broadcaster = new PropertyChangeSupport(this);

    // PROPERTIES
    private LocalDate start;
    private LocalDate end;
    private double weight;

    public AbstractEventBean(LocalDate start, LocalDate end, double weight) {
        this.start = start == LocalDate.MIN ? null : start;
        this.end = end == LocalDate.MAX ? null : end;
        this.weight = weight;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        LocalDate old = this.end;
        this.end = end;
        broadcaster.firePropertyChange(END_PROPERTY, old, this.end);
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        LocalDate old = this.start;
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

    public ValidityPeriod validityPeriod() {
        if (start == null && end == null) {
            return ValidityPeriod.ALWAYS;
        }
        if (start == null) {
            return ValidityPeriod.to(end);
        }
        if (end == null) {
            return ValidityPeriod.from(start);
        } else {
            return ValidityPeriod.between(start, end);
        }
    }

    abstract public Holiday toHoliday();

}
