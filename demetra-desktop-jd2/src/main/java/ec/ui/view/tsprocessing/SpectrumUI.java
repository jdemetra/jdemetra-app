/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ui.view.tsprocessing;

import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.timeseries.simplets.TsData;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate
 */
public class SpectrumUI<V extends IProcDocumentView<?>> extends DefaultItemUI<V, TsData>{

    private final boolean wn_;

    public SpectrumUI(boolean wn){
        wn_=wn;
    }

    @Override
    public JComponent getView(V host, TsData information) {
        DescriptiveStatistics stats=new DescriptiveStatistics(information);
        if (stats.isConstant())
            return host.getToolkit().getMessageViewer("Constant series. No spectral analysis");
        return TsViewToolkit.getInstance().getSpectralView(information, wn_);
    }

}
