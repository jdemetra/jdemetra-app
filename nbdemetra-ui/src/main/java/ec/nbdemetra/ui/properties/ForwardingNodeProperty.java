/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
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

import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import javax.annotation.Nonnull;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;

/**
 *
 * @author Philippe Charles
 * @param <T>
 */
public abstract class ForwardingNodeProperty<T> extends Node.Property<T> {

    private final Node.Property<T> delegate;

    public ForwardingNodeProperty(@Nonnull Node.Property<T> delegate) {
        super(delegate.getValueType());
        this.delegate = delegate;
    }

    @Override
    public String getDisplayName() {
        return delegate.getDisplayName();
    }

    @Override
    public void setDisplayName(String displayName) {
        delegate.setDisplayName(displayName);
    }

    @Override
    public String getHtmlDisplayName() {
        return delegate.getHtmlDisplayName();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public void setName(String name) {
        delegate.setName(name);
    }

    @Override
    public String getShortDescription() {
        return delegate.getShortDescription();
    }

    @Override
    public void setShortDescription(String text) {
        delegate.setShortDescription(text);
    }

    @Override
    public PropertyEditor getPropertyEditor() {
        return delegate.getPropertyEditor();
    }

    @Override
    public boolean canRead() {
        return delegate.canRead();
    }

    @Override
    public boolean canWrite() {
        return delegate.canWrite();
    }

    @Override
    public T getValue() throws IllegalAccessException, InvocationTargetException {
        return delegate.getValue();
    }

    @Override
    public void setValue(T val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        delegate.setValue(val);
    }

    @Override
    public Object getValue(String attributeName) {
        return delegate.getValue(attributeName);
    }

    @Override
    public void setValue(String attributeName, Object value) {
        delegate.setValue(attributeName, value);
    }

    @Override
    public boolean isDefaultValue() {
        return delegate.isDefaultValue();
    }

    @Override
    public boolean supportsDefaultValue() {
        return delegate.supportsDefaultValue();
    }

    @Override
    public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException {
        delegate.restoreDefaultValue();
    }

    @Nonnull
    public static <T> Node.Property<T> readOnly(@Nonnull Node.Property<T> p) {
        return p instanceof PropertySupport.ReadOnly || p instanceof ReadOnly ? p : new ReadOnly(p);
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    private static final class ReadOnly<T> extends ForwardingNodeProperty<T> {

        private ReadOnly(Node.Property<T> p) {
            super(p);
        }

        @Override
        public boolean canWrite() {
            return false;
        }

        @Override
        public void setValue(Object val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    //</editor-fold>
}
