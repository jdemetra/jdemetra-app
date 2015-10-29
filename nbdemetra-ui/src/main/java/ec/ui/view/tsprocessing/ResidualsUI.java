/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ui.view.tsprocessing;

import ec.tstoolkit.timeseries.simplets.TsData;
import ec.ui.view.res.ResidualsView;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate
 */
public class ResidualsUI<V extends IProcDocumentView<?>> extends DefaultItemUI<V, TsData>{

    @Override
    public JComponent getView(V host, TsData information) {
        ResidualsView resView = new ResidualsView();
        resView.setTsData(information);
        return resView;
    }

}
