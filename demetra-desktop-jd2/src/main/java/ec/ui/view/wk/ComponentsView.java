/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ui.view.wk;

import ec.satoolkit.ComponentDescriptor;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.ucarima.WienerKolmogorovEstimators;
import ec.tstoolkit.uihelper.WienerKolmogorovUcarimaEstimators;
import ec.ui.view.PiView;
import ec.ui.view.ScatterView;
import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;

/**
 *
 * @author Kristof Bayens
 */
public class ComponentsView extends JComponent {
    private final JTabbedPane tabbedpanel_;

    public ComponentsView(WienerKolmogorovEstimators wke, ComponentDescriptor[] descs, TsFrequency freq) {
        tabbedpanel_ = new JTabbedPane();

        WienerKolmogorovUcarimaEstimators wk = new WienerKolmogorovUcarimaEstimators(wke, descs);
        wk.setFrequency(freq);
        wk.setType(WienerKolmogorovUcarimaEstimators.EstimatorType.Component);
        wk.setInformation(WienerKolmogorovUcarimaEstimators.SPECTRUM);
        tabbedpanel_.addTab("Spectrum", new PiView(wk));

        WienerKolmogorovUcarimaEstimators wk4 = new WienerKolmogorovUcarimaEstimators(wke, descs);
        wk4.setFrequency(freq);
        wk4.setType(WienerKolmogorovUcarimaEstimators.EstimatorType.Component);
        wk4.setInformation(WienerKolmogorovUcarimaEstimators.AUTOCORRELATIONS);
        tabbedpanel_.addTab("ACGF (stationary)", new ScatterView(wk4));

        setLayout(new BorderLayout());
        add(tabbedpanel_, BorderLayout.CENTER);
    }
}
