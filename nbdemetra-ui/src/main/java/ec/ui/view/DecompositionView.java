/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view;

import ec.tss.Ts;
import demetra.ui.components.JTsChart;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author Kristof Bayens
 */
public class DecompositionView extends JComponent {

    private JTsChart dualchart_;

    public DecompositionView() {
        dualchart_ = new JTsChart();
        dualchart_.setDualChart(true);
    }

    public void set(List<Ts> high, List<Ts> low) {
        List<Ts> coll = new ArrayList<>();
        coll.addAll(high);
        coll.addAll(low);
        dualchart_.getDualDispatcher().clearSelection();
        for (int i = high.size(); i < coll.size(); ++i) {
            dualchart_.getDualDispatcher().setSelectionInterval(i, i);
        }
        dualchart_.getTsCollection().replace(coll);

        setLayout(new BorderLayout());
        add(dualchart_, BorderLayout.CENTER);
    }

    public void clear() {
        dualchart_.getTsCollection().clear();
    }
}
