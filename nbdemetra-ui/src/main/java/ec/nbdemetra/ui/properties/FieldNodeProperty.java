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

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import javax.annotation.Nonnull;
import org.openide.nodes.Node;

/**
 *
 * @author Philippe Charles
 * @param <T>
 */
public class FieldNodeProperty<T> extends Node.Property<T> {

    @Nonnull
    public static <X> FieldNodeProperty<X> create(@Nonnull Object instance, @Nonnull Class<X> valueType, @Nonnull String fieldName) {
        try {
            return new FieldNodeProperty(instance, valueType, instance.getClass().getField(fieldName));
        } catch (NoSuchFieldException | SecurityException ex) {
            throw Throwables.propagate(ex);
        }
    }

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
