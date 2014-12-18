/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties;

import com.google.common.base.Optional;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.beans.PropertyEditor;
import javax.swing.JComboBox;
import javax.swing.KeyStroke;
import javax.swing.event.EventListenerList;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;

/**
 *
 * @author Philippe Charles
 */
public abstract class AbstractInplaceEditor implements InplaceEditor {

    protected final EventListenerList listenerList;
    protected PropertyEditor editor;
    protected PropertyModel model;

    protected AbstractInplaceEditor() {
        this.listenerList = new EventListenerList();
        this.editor = null;
        this.model = null;
    }

    @Override
    public void connect(PropertyEditor propertyEditor, PropertyEnv env) {
        editor = propertyEditor;
        reset();
    }

    @Override
    public void clear() {
        //avoid memory leaks:
        editor = null;
        model = null;
    }

    @Override
    public boolean supportsTextEntry() {
        return true;
    }

    @Override
    public void reset() {
        Object d = editor.getValue();
        if (d != null) {
            setValue(d);
        }
    }

    @Override
    public KeyStroke[] getKeyStrokes() {
        return new KeyStroke[0];
    }

    @Override
    public PropertyEditor getPropertyEditor() {
        return editor;
    }

    @Override
    public PropertyModel getPropertyModel() {
        return model;
    }

    @Override
    public void setPropertyModel(PropertyModel propertyModel) {
        this.model = propertyModel;
    }

    @Override
    public boolean isKnownComponent(Component component) {
        return component == getComponent() || getComponent().isAncestorOf(component);
    }

    /**
     * @see JComboBox#fireActionEvent()
     */
    protected void fireActionPerformed(String command) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command,
                EventQueue.getMostRecentEventTime(), getModifiers());

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                ((ActionListener) listeners[i + 1]).actionPerformed(e);
            }
        }
    }

    protected int getModifiers() {
        AWTEvent currentEvent = EventQueue.getCurrentEvent();
        if (currentEvent instanceof InputEvent) {
            return ((InputEvent) currentEvent).getModifiers();
        }
        if (currentEvent instanceof ActionEvent) {
            return ((ActionEvent) currentEvent).getModifiers();
        }
        return 0;
    }

    @Override
    public void addActionListener(ActionListener actionListener) {
        listenerList.add(ActionListener.class, actionListener);
    }

    @Override
    public void removeActionListener(ActionListener actionListener) {
        listenerList.remove(ActionListener.class, actionListener);
    }

    protected static <T> T getAttribute(PropertyEnv env, String attrName, Class<T> attrType, T defaultValue) {
        Object value = env.getFeatureDescriptor().getValue(attrName);
        return attrType.isInstance(value) ? attrType.cast(value) : defaultValue;
    }

    protected <T> Optional<T> getAttribute(PropertyEnv env, String attrName, Class<T> attrType) {
        Object value = env.getFeatureDescriptor().getValue(attrName);
        return attrType.isInstance(value) ? Optional.of(attrType.cast(value)) : Optional.<T>absent();
    }
}
