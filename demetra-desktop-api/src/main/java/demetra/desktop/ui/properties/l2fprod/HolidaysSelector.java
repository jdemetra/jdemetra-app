/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;
import java.awt.Component;

/**
 *
 * @author Jean Palate
 */
public class HolidaysSelector extends ComboBoxPropertyEditor {

    public HolidaysSelector() {
    }

    @Override
    public Component getCustomEditor() {
        GregorianCalendarManager mgr = ProcessingContext.getActiveContext().getGregorianCalendars();
        String[] names = mgr.getNames();
        Value[] values = new Value[mgr.getCount()];
        for (int i = 0; i < values.length; i++) {
            values[i] = new Value(new Holidays(names[i]), names[i]);
        }
        setAvailableValues(values);
        return super.getCustomEditor();
    }
}
