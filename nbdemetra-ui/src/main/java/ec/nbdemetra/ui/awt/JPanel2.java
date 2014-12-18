/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.awt;

import ec.tstoolkit.utilities.Arrays2;
import javax.swing.JPanel;

/**
 *
 * @author Philippe Charles
 */
public class JPanel2 extends JPanel implements IPropertyChangeSource {

    protected <T> JProperty<T> newProperty(String name, T initialValue) {
        return newProperty(name, JProperty.<T>identity(), initialValue);
    }

    protected <T> JProperty<T> newProperty(String name, JProperty.Setter<T> setter, T initialValue) {
        return new JProperty<T>(name, setter, setter.apply(null, initialValue)) {
            @Override
            protected void firePropertyChange(T oldValue, T newValue) {
                JPanel2.this.firePropertyChange(getName(), oldValue, newValue);
            }
        };
    }

    @Override
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (!Arrays2.arrayEquals(oldValue, newValue)) {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }
}
