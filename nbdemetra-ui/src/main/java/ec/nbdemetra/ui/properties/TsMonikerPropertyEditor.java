/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties;

import ec.tss.TsMoniker;
import java.beans.PropertyEditorSupport;
import org.openide.nodes.PropertyEditorRegistration;

/**
 *
 * @author Philippe Charles
 */
@PropertyEditorRegistration(targetType = TsMoniker.class)
public class TsMonikerPropertyEditor extends PropertyEditorSupport  {

    @Override
    public String getAsText() {
        TsMoniker moniker = (TsMoniker) getValue();
        return moniker != null ? moniker.toString() : "";
    }
    
}
