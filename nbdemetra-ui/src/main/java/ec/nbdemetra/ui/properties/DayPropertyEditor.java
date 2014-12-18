/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties;

import com.toedter.calendar.JDateChooser;
import ec.tstoolkit.timeseries.Day;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import javax.swing.JComponent;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.nodes.PropertyEditorRegistration;

/**
 *
 * @author Philippe Charles
 */
@PropertyEditorRegistration(targetType = Day.class)
public class DayPropertyEditor extends AbstractExPropertyEditor {

    @Override
    protected InplaceEditor createInplaceEditor() {
        return new AbstractInplaceEditor() {
            final JDateChooser component = new JDateChooser("yyyy-MM-dd", "####-##-##", '_');

            {
                component.addPropertyChangeListener("date", new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        fireActionPerformed(COMMAND_SUCCESS);
                    }
                });
            }

            @Override
            public JComponent getComponent() {
                return component;
            }

            @Override
            public Object getValue() {
                Date date = component.getDate();
                return date != null ? new Day(date) : null;
            }

            @Override
            public void setValue(Object o) {
                Day day = (Day) o;
                component.setDate(o != null ? day.getTime() : null);
            }
        };
    }
}
