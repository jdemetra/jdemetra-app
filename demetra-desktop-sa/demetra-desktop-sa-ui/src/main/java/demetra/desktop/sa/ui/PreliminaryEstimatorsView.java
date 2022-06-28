/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package demetra.desktop.sa.ui;

import demetra.desktop.components.tools.FilterView;
import demetra.desktop.components.tools.PiView;
import demetra.desktop.components.tools.ScatterView;
import demetra.sa.ComponentDescriptor;
import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import jdplus.ucarima.WienerKolmogorovEstimators;

/**
 *
 * @author Kristof Bayens
 */
public class PreliminaryEstimatorsView extends JComponent {
    private final JTabbedPane tabbedpanel_;

    public PreliminaryEstimatorsView(WienerKolmogorovEstimators wke, ComponentDescriptor[] descs, int freq) {
        tabbedpanel_ = new JTabbedPane();

        JPanel panel = new JPanel(new BorderLayout());
        WienerKolmogorovUcarimaEstimators wk = new WienerKolmogorovUcarimaEstimators(wke, descs);
        wk.setFrequency(freq);
        wk.setType(WienerKolmogorovUcarimaEstimators.EstimatorType.Preliminary);
        wk.setInformation(WienerKolmogorovUcarimaEstimators.GAIN);
        panel.add(new PiView(wk), BorderLayout.WEST);
//        wk.setInformation(WienerKolmogorovUcarimaEstimators.SPECTRUM);
//        panel.add(new PiView(wk, 600, PlotOrientation.VERTICAL, PiView.DEFAULT_MIN_X, PiView.DEFAULT_MAX_X, PiView.DEFAULT_MIN_Y, PiView.DEFAULT_MAX_Y, PiView.DEFAULT_TICKUNIT_X, PiView.DEFAULT_TICKUNIT_Y, PiView.DEFAULT_FORMAT), BorderLayout.EAST);
        tabbedpanel_.addTab("Frequency response", panel);

        WienerKolmogorovUcarimaEstimators wk2 = new WienerKolmogorovUcarimaEstimators(wke, descs);
        wk2.setFrequency(freq);
        wk2.setType(WienerKolmogorovUcarimaEstimators.EstimatorType.Preliminary);
        wk2.setInformation(WienerKolmogorovUcarimaEstimators.WKFILTER);
        tabbedpanel_.addTab("WK filter", new FilterView(wk2));

        WienerKolmogorovUcarimaEstimators wk3 = new WienerKolmogorovUcarimaEstimators(wke, descs);
        wk2.setFrequency(freq);
        wk3.setType(WienerKolmogorovUcarimaEstimators.EstimatorType.Preliminary);
        wk3.setInformation(WienerKolmogorovUcarimaEstimators.AUTOCORRELATIONS);
        tabbedpanel_.addTab("ACGF (stationary)", new ScatterView(wk));

        setLayout(new BorderLayout());
        add(tabbedpanel_, BorderLayout.CENTER);
    }
}
