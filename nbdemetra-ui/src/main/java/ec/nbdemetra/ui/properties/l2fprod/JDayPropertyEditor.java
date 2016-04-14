package ec.nbdemetra.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import com.toedter.calendar.JTextFieldDateEditor;
import ec.tstoolkit.timeseries.Day;
import java.util.Date;

/**
 *
 * @author Demortier Jeremy
 */
public class JDayPropertyEditor extends AbstractPropertyEditor {

    private final JTextFieldDateEditor component = new JTextFieldDateEditor("yyyy-MM-dd", "####-##-##", '_');

    public JDayPropertyEditor() {
        component.addPropertyChangeListener("date", evt -> {
            Day oday = null, nday = null;
            if (evt.getOldValue() instanceof Date) {
                oday = new Day((Date) evt.getOldValue());
            }
            if (evt.getNewValue() instanceof Date) {
                nday = new Day((Date) evt.getNewValue());
            }
            JDayPropertyEditor.this.firePropertyChange(oday, nday);
        });
       editor=component;
   }

    @Override
    public Object getValue() {
        Date date = component.getDate();
        return date != null ? new Day(date) : Day.BEG;
    }

    @Override
    public void setValue(Object o) {
        Day day = (Day) o;
        component.setDate(o != null ? day.getTime() : Day.toDay().getTime());
    }

 }