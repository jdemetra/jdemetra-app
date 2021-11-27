/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package demetra.desktop.ui.modelling;

import demetra.desktop.components.JResidualsView;
import demetra.desktop.ui.processing.DefaultItemUI;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.timeseries.TsData;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate
 */
public class ResidualsUI<V extends IProcDocumentView<?>> extends DefaultItemUI<V, TsData>{

    @Override
    public JComponent getView(V host, TsData information) {
        JResidualsView resView = new JResidualsView();
        resView.setTsData(information);
        return resView;
    }

}
