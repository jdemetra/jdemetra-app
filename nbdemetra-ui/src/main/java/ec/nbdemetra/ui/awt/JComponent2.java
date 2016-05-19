/*
 * Copyright 2013 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.nbdemetra.ui.awt;

import ec.nbdemetra.ui.awt.JProperty.Setter;
import ec.tstoolkit.utilities.Arrays2;
import javax.swing.JComponent;

/**
 *
 * @author Philippe Charles
 */
public class JComponent2 extends JComponent implements IPropertyChangeSource {

    @Deprecated
    protected <T> JProperty<T> newProperty(String name, T initialValue) {
        return newProperty(name, JProperty.<T>identity(), initialValue);
    }

    @Deprecated
    protected <T> JProperty<T> newProperty(String name, Setter<T> setter, T initialValue) {
        return new JProperty<T>(name, setter, setter.apply(null, initialValue)) {
            @Override
            protected void firePropertyChange(T oldValue, T newValue) {
                JComponent2.this.firePropertyChange(getName(), oldValue, newValue);
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
