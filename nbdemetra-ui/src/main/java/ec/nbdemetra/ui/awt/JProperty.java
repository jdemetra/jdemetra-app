/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.awt;

import com.google.common.base.Supplier;

/**
 *
 * @author Philippe Charles
 */
public abstract class JProperty<T> {

    private final String name;
    private final Setter<T> setter;
    private T value;

    public JProperty(String name, T initialValue) {
        this(name, JProperty.<T>identity(), initialValue);
    }

    public JProperty(String name, Setter<T> setter, T initialValue) {
        this.name = name;
        this.setter = setter;
        this.value = initialValue;
    }

    public String getName() {
        return name;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        T old = this.value;
        this.value = setter.apply(old, value);
        firePropertyChange(old, this.value);
    }
    
    public T getAndSet(T value) {
        T result = get();
        set(value);
        return result;
    }

    abstract protected void firePropertyChange(T oldValue, T newValue);

    public interface Setter<X> {

        X apply(X oldValue, X newValue);
    }

    public static <T> Setter<T> nullTo(final T defaultValue) {
        return new Setter<T>() {
            @Override
            public T apply(T oldValue, T newValue) {
                return newValue != null ? newValue : defaultValue;
            }
        };
    }

    public static <T> Setter<T> nullTo(final Supplier<T> defaultValue) {
        return new Setter<T>() {
            @Override
            public T apply(T oldValue, T newValue) {
                return newValue != null ? newValue : defaultValue.get();
            }
        };
    }

    public static <T extends Comparable<T>> Setter<T> min(final T min) {
        return new Setter<T>() {
            @Override
            public T apply(T oldValue, T newValue) {
                return newValue != null && min.compareTo(newValue) <= 0 ? newValue : min;
            }
        };
    }

    public static <T> Setter<T> identity() {
        return new Setter<T>() {
            @Override
            public T apply(T oldValue, T newValue) {
                return newValue;
            }
        };
    }
}
