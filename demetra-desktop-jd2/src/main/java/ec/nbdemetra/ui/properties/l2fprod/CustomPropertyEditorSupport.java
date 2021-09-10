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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;
import java.util.function.BiConsumer;

/**
 *
 * @author Philippe Charles
 */
final class CustomPropertyEditorSupport implements PropertyEditor {

    public interface Resource<C extends Component, V> {

        void bindValue(C editor, BiConsumer<V, V> broadcaster);

        Object getValue(C editor);

        void setValue(C editor, V value);
    }

    public static <C extends Component> CustomPropertyEditorSupport of(C customEditor, Object source, Resource<C, ?> resource) {
        PropertyChangeSupport listeners = new PropertyChangeSupport(source);
        resource.bindValue(customEditor, (o, n) -> listeners.firePropertyChange("value", o, n));
        return new CustomPropertyEditorSupport(customEditor, listeners, resource);
    }

    private final Component customEditor;
    private final PropertyChangeSupport listeners;
    private final Resource resource;

    private CustomPropertyEditorSupport(Component customEditor, PropertyChangeSupport listeners, Resource resource) {
        this.customEditor = customEditor;
        this.listeners = listeners;
        this.resource = resource;
    }

    @Override
    public void setValue(Object value) {
        resource.setValue(customEditor, value);
    }

    @Override
    public Object getValue() {
        return resource.getValue(customEditor);
    }

    @Override
    public boolean isPaintable() {
        return false;
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box) {
    }

    @Override
    public String getJavaInitializationString() {
        return null;
    }

    @Override
    public String getAsText() {
        return null;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }

    @Override
    public String[] getTags() {
        return null;
    }

    @Override
    public Component getCustomEditor() {
        return customEditor;
    }

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.removePropertyChangeListener(listener);
    }
}
