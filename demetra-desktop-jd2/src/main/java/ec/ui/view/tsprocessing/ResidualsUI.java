/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ui.view.tsprocessing;

import demetra.bridge.TsConverter;
import ec.tss.tsproviders.utils.OptionalTsData;
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
        resView.setTsData(information != null ? TsConverter.toTsData(OptionalTsData.present(information)) : null);
        return resView;
    }

}
