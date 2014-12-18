/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties;

import ec.tstoolkit.timeseries.simplets.TsPeriod;
import java.beans.PropertyEditorSupport;
import org.openide.nodes.PropertyEditorRegistration;

/**
 *
 * @author Philippe Charles
 */
@PropertyEditorRegistration(targetType = TsPeriod.class)
public class TsPeriodPropertyEditor extends PropertyEditorSupport {

    @Override
    public String getAsText() {
        TsPeriod value = (TsPeriod) getValue();
        return value != null ? value.toString() : "";
    }
}
