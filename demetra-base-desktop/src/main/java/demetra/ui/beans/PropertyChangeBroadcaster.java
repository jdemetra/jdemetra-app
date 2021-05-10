package demetra.ui.beans;

/**
 *
 */
@FunctionalInterface
public interface PropertyChangeBroadcaster {

    void firePropertyChange(String propertyName, Object oldValue, Object newValue);
}
