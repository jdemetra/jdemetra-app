/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ui.view.tsprocessing;

import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.ui.view.JSpectralView;

import javax.swing.*;

/**
 * @author Jean Palate
 */
public class SpectrumUI<V extends IProcDocumentView<?>> extends DefaultItemUI<V, TsData> {

    private final boolean wn_;

    public SpectrumUI(boolean wn) {
        wn_ = wn;
    }

    @Override
    public JComponent getView(V host, TsData information) {
        DescriptiveStatistics stats = new DescriptiveStatistics(information);
        if (stats.isConstant())
            return TsViewToolkit.getMessageViewer("Constant series. No spectral analysis");
        return getSpectralView(information, wn_);
    }

    public static JComponent getSpectralView(TsData s, boolean wn) {
        if (s == null) {
            return null;
        }
        JSpectralView spectrum = new JSpectralView();
        spectrum.set(s, wn);
        return spectrum;
    }
}
