package demetra.desktop.beans;

/**
 *
 */
@FunctionalInterface
public interface PropertyChangeBroadcaster {

    void firePropertyChange(String propertyName, Object oldValue, Object newValue);
}
