/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.processing.stats;

import demetra.data.DoubleSeq;
import demetra.desktop.ui.JDistributionView;
import demetra.desktop.ui.JResDistributionView;
import demetra.desktop.ui.processing.ItemUI;
import demetra.timeseries.TsData;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate
 */
public class DistributionUI implements ItemUI<DoubleSeq> {

    @Override
    public JComponent getView(DoubleSeq information) {
        JDistributionView distView = new JDistributionView();
        if (information != null) {
            distView.set(information);
        }else
            distView.reset();
        return distView;
    }

}
