/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package demetra.desktop.ui.processing.stats;


import demetra.data.DoubleSeq;
import demetra.desktop.components.tools.PeriodogramView;
import demetra.desktop.ui.JSpectralView;
import demetra.desktop.ui.processing.ItemUI;
import demetra.desktop.ui.processing.TsViewToolkit;
import demetra.timeseries.TsData;
import javax.swing.*;
import jdplus.stats.DescriptiveStatistics;

/**
 * @author Jean Palate
 */
public class PeriodogramUI implements ItemUI<DoubleSeq> {

 
    public PeriodogramUI() {
    }

    @Override
    public JComponent getView(DoubleSeq information) {
        DescriptiveStatistics stats = DescriptiveStatistics.of(information);
        if (stats.isConstant())
            return TsViewToolkit.getMessageViewer("Constant series. No spectral analysis");
        return getPeriodogramView(information);
    }

    public static JComponent getPeriodogramView(DoubleSeq s) {
        if (s == null) {
            return null;
        }
        PeriodogramView periodogram = new PeriodogramView();
        periodogram.setDb(true);
        periodogram.setData("periodogram", 0, s);
        return periodogram;
    }
}
