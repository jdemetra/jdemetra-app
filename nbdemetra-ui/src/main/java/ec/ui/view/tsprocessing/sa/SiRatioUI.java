/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing.sa;

import ec.tstoolkit.timeseries.simplets.TsData;
import ec.ui.view.SIView;
import ec.ui.view.tsprocessing.IProcDocumentView;
import ec.ui.view.tsprocessing.PooledItemUI;

/**
 *
 * @author pcuser
 */
public class SiRatioUI<V extends IProcDocumentView<?>> extends PooledItemUI<V, TsData[], SIView> {

    public SiRatioUI() {
        super(SIView.class);
    }

    @Override
    protected void init(SIView c, V host, TsData[] information) {
        if (information.length == 2) {
            c.setSiData(information[0], information[1]);
        } else if (information.length == 1) {
            c.setData(information[0]);
        }
    }
}
