/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties;

import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;

/**
 *
 * @author Philippe Charles
 */
public abstract class ForwardingNodeProperty<T> extends Node.Property<T> {

    final Node.Property<T> p;

    public ForwardingNodeProperty(Node.Property<T> p) {
        super(p.getValueType());
        this.p = p;
    }

    @Override
    public String getDisplayName() {
        return p.getDisplayName();
    }

    @Override
    public void setDisplayName(String displayName) {
        p.setDisplayName(displayName);
    }

    @Override
    public String getHtmlDisplayName() {
        return p.getHtmlDisplayName();
    }

    @Override
    public String getName() {
        return p.getName();
    }

    @Override
    public void setName(String name) {
        p.setName(name);
    }

    @Override
    public String getShortDescription() {
        return p.getShortDescription();
    }

    @Override
    public void setShortDescription(String text) {
        p.setShortDescription(text);
    }

    @Override
    public PropertyEditor getPropertyEditor() {
        return p.getPropertyEditor();
    }

    @Override
    public boolean canRead() {
        return p.canRead();
    }

    @Override
    public boolean canWrite() {
        return p.canWrite();
    }

    @Override
    public T getValue() throws IllegalAccessException, InvocationTargetException {
        return p.getValue();
    }

    @Override
    public void setValue(T val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        p.setValue(val);
    }

    @Override
    public Object getValue(String attributeName) {
        return p.getValue(attributeName);
    }

    @Override
    public void setValue(String attributeName, Object value) {
        p.setValue(attributeName, value);
    }

    @Override
    public boolean isDefaultValue() {
        return p.isDefaultValue();
    }

    @Override
    public boolean supportsDefaultValue() {
        return p.supportsDefaultValue();
    }

    @Override
    public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException {
        p.restoreDefaultValue();
    }

    public static <T> Node.Property<T> readOnly(Node.Property<T> p) {
        return p instanceof PropertySupport.ReadOnly || p instanceof ReadOnly ? p : new ReadOnly(p);
    }

    private static class ReadOnly<T> extends ForwardingNodeProperty<T> {

        ReadOnly(Node.Property<T> p) {
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
}
