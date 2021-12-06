/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package demetra.desktop.ui.processing.stats;

import demetra.desktop.components.JResidualsView;
import demetra.desktop.ui.processing.ItemUI;
import demetra.timeseries.TsData;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate
 */
public class ResidualsUI implements ItemUI<TsData>{

    @Override
    public JComponent getView(TsData information) {
        JResidualsView resView = new JResidualsView();
        resView.setTsData(information);
        return resView;
    }

}
