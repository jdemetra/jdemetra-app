package ec.nbdemetra.ui.star;

import ec.tss.tsproviders.DataSource;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.EventListenerList;

public final class StarList implements Iterable<DataSource> {

    private static final StarList INSTANCE = new StarList();
    
    public static StarList getInstance() {
        return INSTANCE;
    }
    
    final EventListenerList listeners;
    final Set<DataSource> list;

    private StarList() {
        list = new HashSet<>();
        listeners = new EventListenerList();
    }
    
    public void clear() {
        list.clear();
        firePropertyChange("123", null, list);
    }

    public void toggle(DataSource item) {
        if (list.contains(item))
            list.remove(item);
        else
            list.add(item);
        firePropertyChange("123", null, list);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.add(PropertyChangeListener.class, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.remove(PropertyChangeListener.class, listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        // Guaranteed to return a non-null array
        Object[] tmp = listeners.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        for (int i = tmp.length - 2; i >= 0; i -= 2) {
            if (tmp[i] == PropertyChangeListener.class) {
                ((PropertyChangeListener) tmp[i + 1]).propertyChange(event);
            }
        }
    }

    @Override
    public Iterator<DataSource> iterator() {
        return list.iterator();
    }

    boolean isStarred(DataSource dataSource) {
        return list.contains(dataSource);
    }
}