/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.awt;

import ec.tstoolkit.utilities.Arrays2;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author Philippe Charles
 */
public abstract class ListenableBean implements IPropertyChangeSource {

    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        support.addPropertyChangeListener(propertyName, listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        support.removePropertyChangeListener(propertyName, listener);
    }

    protected <T> JProperty<T> newProperty(String name, T initialValue) {
        return newProperty(name, JProperty.<T>identity(), initialValue);
    }

    protected <T> JProperty<T> newProperty(String name, JProperty.Setter<T> setter, T initialValue) {
        return new JProperty<T>(name, setter, setter.apply(null, initialValue)) {
            @Override
            protected void firePropertyChange(T oldValue, T newValue) {
                ListenableBean.this.firePropertyChange(getName(), oldValue, newValue);
            }
        };
    }

    protected <T> void firePropertyChange(String propertyName, T oldValue, T newValue) {
        if (!Arrays2.arrayEquals(oldValue, newValue)) {
            support.firePropertyChange(propertyName, oldValue, newValue);
        }
    }
}
