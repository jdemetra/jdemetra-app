/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.res;

import demetra.desktop.design.SwingComponent;
import demetra.ui.util.NbComponents;
import ec.tstoolkit.data.IReadDataBlock;
import ec.tstoolkit.dstats.Normal;
import ec.tstoolkit.stats.AutoCorrelations;
import ec.ui.view.JAutoCorrelationsView;
import ec.ui.view.JDistributionView;
import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JSplitPane;

/**
 *
 * @author Kristof Bayens
 */
@SwingComponent
public final class JResDistributionView extends JComponent {

    private final JAutoCorrelationsView acView_;
    private final JAutoCorrelationsView pacView_;
    private final JDistributionView distView_;

    public JResDistributionView() {
        acView_ = new JAutoCorrelationsView();
        acView_.setKind(JAutoCorrelationsView.ACKind.Normal);
        pacView_ = new JAutoCorrelationsView();
        pacView_.setKind(JAutoCorrelationsView.ACKind.Partial);
        JSplitPane acpane = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, acView_, pacView_);
        acpane.setDividerLocation(0.5);
        acpane.setResizeWeight(.5);
        distView_ = new JDistributionView();
        JSplitPane acdpane = NbComponents.newJSplitPane(JSplitPane.HORIZONTAL_SPLIT, acpane, distView_);
        acdpane.setDividerLocation(0.5);
        acdpane.setResizeWeight(.5);

        setLayout(new BorderLayout());
        add(acdpane, BorderLayout.CENTER);
        acpane.setResizeWeight(0.5);
        acdpane.setResizeWeight(0.5);
    }
    
    public void setAutocorrelationsCount(int na){
        acView_.setLength(na);
        pacView_.setLength(na);
    }

    public void setData(IReadDataBlock data) {
        if (data != null) {
            AutoCorrelations ac = new AutoCorrelations(data);
            acView_.setAutoCorrelations(ac);
            pacView_.setAutoCorrelations(ac);
            distView_.setDistribution(new Normal());
            distView_.setDataBlock(data);
        }
    }

    public void reset() {
        acView_.reset();
        pacView_.reset();
        distView_.reset();
    }
}
