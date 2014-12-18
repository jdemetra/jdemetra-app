/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.ui.view.wk.FinalEstimatorsView;
import javax.swing.JComponent;

/**
 *
 * @author pcuser
 */
public class WkFinalEstimatorsUI<V extends IProcDocumentView<?>> extends DefaultItemUI<V, WkInformation> {

    @Override
    public JComponent getView(V host, WkInformation information) {
        try {
            return new FinalEstimatorsView(information.estimators, information.descriptors, information.frequency);
        } catch (Exception err) {
            return host.getToolkit().getMessageViewer("Unable to compute the final estimators");
        }
    }

}
