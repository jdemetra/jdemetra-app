/*
 * Copyright 2016 National Bank of Belgium
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
package ec.nbdemetra.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.EnumerationPropertyEditor;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import javax.swing.JComboBox;

/**
 * 
 * @author Philippe Charles
 * @since 2.2.0
 */
public final class EnumPropertyEditor implements PropertyEditor {

    private final PropertyEditor delegate;

    public EnumPropertyEditor() {
        this.delegate = new EnumerationPropertyEditor();
    }

    private void setSelectedItem(Object value) {
        JComboBox e = (JComboBox) getCustomEditor();
        e.getModel().setSelectedItem(value);
    }

    @Override
    public void setValue(Object value) {
        delegate.setValue(value);
        setSelectedItem(value);
    }

    @Override
    public Object getValue() {
        return delegate.getValue();
    }

    @Override
    public boolean isPaintable() {
        return delegate.isPaintable();
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box) {
        delegate.paintValue(gfx, box);
    }

    @Override
    public String getJavaInitializationString() {
        return delegate.getJavaInitializationString();
    }

    @Override
    public String getAsText() {
        return delegate.getAsText();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        delegate.setAsText(text);
    }

    @Override
    public String[] getTags() {
        return delegate.getTags();
    }

    @Override
    public Component getCustomEditor() {
        return delegate.getCustomEditor();
    }

    @Override
    public boolean supportsCustomEditor() {
        return delegate.supportsCustomEditor();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        delegate.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        delegate.removePropertyChangeListener(listener);
    }
}
