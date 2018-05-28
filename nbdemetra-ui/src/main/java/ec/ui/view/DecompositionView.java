/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view;

import demetra.bridge.TsConverter;
import demetra.tsprovider.TsCollection;
import ec.tss.Ts;
import demetra.ui.components.JTsChart;
import java.awt.BorderLayout;
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
        TsCollection.Builder b = TsCollection                .builder();
        high.forEach(o->b.data(TsConverter.toTs(o)));
        low.forEach(o->b.data(TsConverter.toTs(o)));
        TsCollection coll = b.build();
        dualchart_.getDualDispatcher().clearSelection();
        for (int i = high.size(); i < coll.getData().size(); ++i) {
            dualchart_.getDualDispatcher().setSelectionInterval(i, i);
        }
        dualchart_.setTsCollection(coll);

        setLayout(new BorderLayout());
        add(dualchart_, BorderLayout.CENTER);
    }

    public void clear() {
        dualchart_.setTsCollection(TsCollection.EMPTY);
    }
}
