/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view;

import demetra.bridge.TsConverter;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsSeq;
import ec.tss.Ts;
import demetra.ui.components.JTsChart;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
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
        TsSeq all = Stream
                .concat(high.stream(), low.stream())
                .map(TsConverter::toTs)
                .collect(TsSeq.toTsSeq());
        dualchart_.getDualDispatcher().clearSelection();
        dualchart_.getDualDispatcher().setSelectionInterval(high.size(), all.size());
        dualchart_.setTsCollection(TsCollection.of(all));

        setLayout(new BorderLayout());
        add(dualchart_, BorderLayout.CENTER);
    }

    public void clear() {
        dualchart_.setTsCollection(TsCollection.EMPTY);
    }
}
