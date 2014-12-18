/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ui.view.tsprocessing;

import ec.ui.view.wk.ComponentsView;
import javax.swing.JComponent;


/**
 *
 * @author pcuser
 */
public class WkComponentsUI <V extends IProcDocumentView<?>> extends DefaultItemUI<V, WkInformation>{

    @Override
    public JComponent getView(V host, WkInformation information) {
        return new ComponentsView(information.estimators, information.descriptors, information.frequency);
    }

}
