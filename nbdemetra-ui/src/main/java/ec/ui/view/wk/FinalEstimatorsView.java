/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ui.view.wk;

import ec.satoolkit.ComponentDescriptor;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.ucarima.WienerKolmogorovEstimators;
import ec.tstoolkit.uihelper.WienerKolmogorovUcarimaEstimators;
import ec.ui.view.FilterView;
import ec.ui.view.PiView;
import ec.ui.view.ScatterView;
import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;

/**
 *
 * @author Kristof Bayens
 */
public class FinalEstimatorsView extends JComponent {
    private final JTabbedPane tabbedpanel_;

    public FinalEstimatorsView(WienerKolmogorovEstimators wke, ComponentDescriptor[] descs, TsFrequency freq) {
        tabbedpanel_ = new JTabbedPane();

        WienerKolmogorovUcarimaEstimators wk = new WienerKolmogorovUcarimaEstimators(wke, descs);
        wk.setFrequency(freq);
        wk.setType(WienerKolmogorovUcarimaEstimators.EstimatorType.Final);
        wk.setInformation(WienerKolmogorovUcarimaEstimators.SPECTRUM);
        tabbedpanel_.addTab("Spectrum", new PiView(wk));

        WienerKolmogorovUcarimaEstimators wk2 = new WienerKolmogorovUcarimaEstimators(wke, descs);
        wk2.setFrequency(freq);
        wk2.setType(WienerKolmogorovUcarimaEstimators.EstimatorType.Final);
        wk2.setInformation(WienerKolmogorovUcarimaEstimators.GAIN);
        tabbedpanel_.addTab("Square gain", new PiView(wk2));

        WienerKolmogorovUcarimaEstimators wk3 = new WienerKolmogorovUcarimaEstimators(wke, descs);
        wk3.setFrequency(freq);
        wk3.setType(WienerKolmogorovUcarimaEstimators.EstimatorType.Final);
        wk3.setInformation(WienerKolmogorovUcarimaEstimators.WKFILTER);
        tabbedpanel_.addTab("WK filters", new FilterView(wk3));

        WienerKolmogorovUcarimaEstimators wk4 = new WienerKolmogorovUcarimaEstimators(wke, descs);
        wk4.setFrequency(freq);
        wk4.setType(WienerKolmogorovUcarimaEstimators.EstimatorType.Final);
        wk4.setInformation(WienerKolmogorovUcarimaEstimators.AUTOCORRELATIONS);
        tabbedpanel_.addTab("ACGF (stationary)", new ScatterView(wk4));

        WienerKolmogorovUcarimaEstimators wk5 = new WienerKolmogorovUcarimaEstimators(wke, descs);
        wk5.setFrequency(freq);
        wk5.setType(WienerKolmogorovUcarimaEstimators.EstimatorType.Final);
        wk5.setInformation(WienerKolmogorovUcarimaEstimators.PSIEWEIGHTS);
        tabbedpanel_.addTab("PsiE-weights", new FilterView(wk5));

        setLayout(new BorderLayout());
        add(tabbedpanel_, BorderLayout.CENTER);
    }
}
