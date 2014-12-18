/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.list;

import ec.tstoolkit.timeseries.simplets.TsFrequency;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Philippe Charles
 */
public class TsFrequencyTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    protected void setValue(Object value) {
        if (value instanceof TsFrequency) {
            TsFrequency freq = (TsFrequency) value;
            setText(freq.name() + " (" + freq.intValue() + ")");
        } else {
            super.setValue(value);
        }
    }
}
