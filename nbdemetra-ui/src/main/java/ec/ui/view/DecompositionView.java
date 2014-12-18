/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ui.view;

import ec.tss.Ts;
import ec.ui.chart.JTsDualChart;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author Kristof Bayens
 */
public class DecompositionView extends JComponent {
    private JTsDualChart dualchart_;

    public DecompositionView() {
        dualchart_ = new JTsDualChart();
    }

    public void set(List<Ts> high, List<Ts> low) {
        List<Ts> coll = new ArrayList<>();
        coll.addAll(high);
        coll.addAll(low);
        for (int i = high.size(); i < coll.size(); ++i) {
            dualchart_.setTsLevel(i, true);
        }
        dualchart_.getTsCollection().replace(coll);

        setLayout(new BorderLayout());
        add(dualchart_, BorderLayout.CENTER);
    }

    public void clear() {
        dualchart_.reset();
    }
}
