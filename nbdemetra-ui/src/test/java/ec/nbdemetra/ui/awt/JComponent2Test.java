/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.awt;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Philippe Charles
 */
public class JComponent2Test {

    static class CustomComponent extends JComponent2 {

        final JProperty<Integer> intValue;
        final JProperty<String> stringValue;
        final JProperty<int[]> arrayValue;

        CustomComponent() {
            intValue = newProperty("intValue", 123);
            stringValue = newProperty("stringValue", "hello");
            arrayValue = newProperty("arrayValue", new int[]{1, 2, 3});
        }
    }

    static class CustomPropertyChangeListener implements PropertyChangeListener {

        PropertyChangeEvent latestEvent;

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            latestEvent = evt;
        }
    }

    @Test
    public void testGet() {
        CustomComponent c = new CustomComponent();
        assertEquals((Integer) 123, c.intValue.get());
        assertEquals("hello", c.stringValue.get());
        assertArrayEquals(new int[]{1, 2, 3}, c.arrayValue.get());
    }
    
    @Test
    public void testSet() {
        CustomComponent c = new CustomComponent();
        CustomPropertyChangeListener listener = new CustomPropertyChangeListener();
        c.addPropertyChangeListener(listener);

        c.intValue.set(123);
        assertNull(listener.latestEvent);

        c.intValue.set(456);
        assertEquals(123, listener.latestEvent.getOldValue());
        assertEquals(456, listener.latestEvent.getNewValue());
        listener.latestEvent = null;
        
        c.arrayValue.set(new int[]{1,2,3});
        assertNull(listener.latestEvent);
        listener.latestEvent = null;
    }
}
