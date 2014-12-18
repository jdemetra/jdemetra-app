/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.openide.nodes.Node;

/**
 *
 * @author Philippe Charles
 */
public class FieldNodeProperty<T> extends Node.Property<T> {

    public static <X> FieldNodeProperty<X> create(Object instance, Class<X> valueType, String fieldName) {
        try {
            return new FieldNodeProperty(instance, valueType, instance.getClass().getField(fieldName));
        } catch (NoSuchFieldException | SecurityException ex) {
            throw Throwables.propagate(ex);
        }
    }
    //
    protected final Object instance;
    protected final Field field;

    public FieldNodeProperty(Object instance, Class<T> valueType, Field field) {
        super(valueType);
        Preconditions.checkArgument(field.getType().equals(valueType));
        this.instance = instance;
        this.field = field;
        setName(field.getName());
    }

    @Override
    public boolean canRead() {
        return true;
    }

    @Override
    public T getValue() throws IllegalAccessException, InvocationTargetException {
        return (T) field.get(instance);
    }

    @Override
    public boolean canWrite() {
        return !Modifier.isFinal(field.getModifiers());
    }

    @Override
    public void setValue(T val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        field.set(instance, val);
    }
}
