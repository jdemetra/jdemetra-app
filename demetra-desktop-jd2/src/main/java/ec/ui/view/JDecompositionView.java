/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view;

import demetra.bridge.TsConverter;
import demetra.timeseries.TsCollection;
import ec.tss.Ts;
import demetra.ui.components.JTsChart;
import demetra.desktop.design.SwingComponent;
import java.awt.BorderLayout;
import java.util.List;
import java.util.stream.Stream;
import javax.swing.JComponent;

/**
 *
 * @author Kristof Bayens
 */
@SwingComponent
public final class JDecompositionView extends JComponent {

    private final JTsChart dualchart_;

    public JDecompositionView() {
        dualchart_ = new JTsChart();
        dualchart_.setDualChart(true);
    }

    public void set(List<Ts> high, List<Ts> low) {
        TsCollection all = Stream
                .concat(high.stream(), low.stream())
                .map(TsConverter::toTs)
                .collect(TsCollection.toTsCollection());
        dualchart_.getDualDispatcher().clearSelection();
        dualchart_.getDualDispatcher().setSelectionInterval(high.size(), all.size());
        dualchart_.setTsCollection(all);

        setLayout(new BorderLayout());
        add(dualchart_, BorderLayout.CENTER);
    }

    public void clear() {
        dualchart_.setTsCollection(TsCollection.EMPTY);
    }
}
