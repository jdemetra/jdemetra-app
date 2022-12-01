/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.calendar;

import demetra.desktop.beans.PropertyChangeSource;
import demetra.desktop.design.SwingProperty;
import demetra.timeseries.calendars.Holiday;
import demetra.timeseries.calendars.SingleDate;

import java.beans.PropertyChangeSupport;
import java.time.LocalDate;

/**
 *
 * @author Philippe Charles
 */
@lombok.ToString
public class SingleDateBean implements HasHoliday, PropertyChangeSource.WithWeakListeners {

    // PROPERTIES DEFINITIONS
    @SwingProperty
    public static final String DATE_PROPERTY = "date";
    @SwingProperty
    public static final String WEIGHT_PROPERTY = "weight";

    // PROPERTIES 
    private LocalDate date;
    private double weight;

    @lombok.experimental.Delegate(types = PropertyChangeSource.class)
    private final PropertyChangeSupport broadcaster = new PropertyChangeSupport(this);

    public SingleDateBean() {
        this(LocalDate.now(), 1);
    }

    public SingleDateBean(LocalDate date, double weight) {
        this.date = date;
        this.weight = weight;
    }

    public SingleDateBean(SingleDate sdate) {
        this.date = sdate.getDate();
        this.weight = sdate.getWeight();
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        LocalDate old = this.date;
        this.date = date;
        broadcaster.firePropertyChange(DATE_PROPERTY, old, this.date);
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        double old = this.weight;
        this.weight = weight;
        broadcaster.firePropertyChange(WEIGHT_PROPERTY, old, this.weight);
    }

    @Override
    public Holiday toHoliday() {
        return new SingleDate(date, weight);
    }
}
