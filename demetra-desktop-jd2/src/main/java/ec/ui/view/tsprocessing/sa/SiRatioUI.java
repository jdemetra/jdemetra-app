/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing.sa;

import ec.tstoolkit.timeseries.simplets.TsData;
import ec.ui.view.JSIView;
import ec.ui.view.tsprocessing.IProcDocumentView;
import ec.ui.view.tsprocessing.PooledItemUI;

/**
 *
 * @author Jean Palate
 */
public class SiRatioUI<V extends IProcDocumentView<?>> extends PooledItemUI<V, TsData[], JSIView> {

    public SiRatioUI() {
        super(JSIView.class);
    }

    @Override
    protected void init(JSIView c, V host, TsData[] information) {
        if (information.length == 2) {
            c.setSiData(information[0], information[1]);
        } else if (information.length == 1) {
            c.setData(information[0]);
        }
    }
}
