/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tstoolkit.timeseries.simplets.TsData;
import ec.ui.view.res.ResDistributionView;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate
 */
public class ResidualsDistUI<V extends IProcDocumentView<?>> extends DefaultItemUI<V, TsData> {

    @Override
    public JComponent getView(V host, TsData information) {
        ResDistributionView resdistView = new ResDistributionView();
        if (information != null) {
            int n = information.getFrequency().intValue();
            resdistView.setAutocorrelationsCount(Math.max(8, n * 3));
            resdistView.setData(information.getValues());
        }else
            resdistView.reset();
        return resdistView;
    }

}
