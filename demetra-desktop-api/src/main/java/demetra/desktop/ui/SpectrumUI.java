/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package demetra.desktop.ui;


import demetra.desktop.ui.processing.DefaultItemUI;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.desktop.ui.processing.TsViewToolkit;
import demetra.timeseries.TsData;
import javax.swing.*;
import jdplus.stats.DescriptiveStatistics;

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
        DescriptiveStatistics stats = DescriptiveStatistics.of(information.getValues());
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
