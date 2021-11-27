/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.modelling;

import demetra.desktop.ui.JResDistributionView;
import demetra.desktop.ui.processing.DefaultItemUI;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.timeseries.TsData;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate
 */
public class ResidualsDistUI<V extends IProcDocumentView<?>> extends DefaultItemUI<V, TsData> {

    @Override
    public JComponent getView(V host, TsData information) {
        JResDistributionView resdistView = new JResDistributionView();
        if (information != null) {
            int n = information.getAnnualFrequency();
            resdistView.setAutocorrelationsCount(Math.max(8, n * 3));
            resdistView.setData(information.getValues());
        }else
            resdistView.reset();
        return resdistView;
    }

}
