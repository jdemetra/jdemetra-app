/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.res;

import demetra.ui.util.NbComponents;
import ec.tstoolkit.data.IReadDataBlock;
import ec.tstoolkit.dstats.Normal;
import ec.tstoolkit.stats.AutoCorrelations;
import ec.ui.view.AutoCorrelationsView;
import ec.ui.view.DistributionView;
import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JSplitPane;

/**
 *
 * @author Kristof Bayens
 */
public class ResDistributionView extends JComponent {

    private AutoCorrelationsView acView_;
    private AutoCorrelationsView pacView_;
    private DistributionView distView_;

    public ResDistributionView() {
        acView_ = new AutoCorrelationsView();
        acView_.setKind(AutoCorrelationsView.ACKind.Normal);
        pacView_ = new AutoCorrelationsView();
        pacView_.setKind(AutoCorrelationsView.ACKind.Partial);
        JSplitPane acpane = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, acView_, pacView_);
        acpane.setDividerLocation(0.5);
        acpane.setResizeWeight(.5);
        distView_ = new DistributionView();
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
