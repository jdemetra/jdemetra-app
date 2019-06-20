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
package ec.nbdemetra.ui.properties;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 *
 * @author Philippe Charles
 */
public abstract class ForwardingPropertyEditor implements PropertyEditor {

    private final PropertyEditor delegate;

    public ForwardingPropertyEditor(@NonNull PropertyEditor delegate) {
        this.delegate = delegate;
    }

    protected PropertyEditor getDelegate() {
        return delegate;
    }

    @Override
    public void setValue(Object value) {
        delegate.setValue(value);
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
