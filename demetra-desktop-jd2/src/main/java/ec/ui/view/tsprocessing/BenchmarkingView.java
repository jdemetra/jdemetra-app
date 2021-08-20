/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import demetra.bridge.TsConverter;
import demetra.timeseries.TsCollection;
import ec.nbdemetra.ui.OldTsUtil;
import demetra.ui.components.parts.HasTsCollection.TsUpdateMode;
import demetra.ui.util.NbComponents;
import ec.tss.Ts;
import ec.tss.html.implementation.HtmlTsDifferenceDocument;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.ui.Disposables;
import demetra.ui.components.JTsChart;
import demetra.ui.components.JTsGrid;
import java.awt.BorderLayout;
import java.util.stream.Stream;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JSplitPane;

/**
 *
 * @author Jean Palate
 */
public final class BenchmarkingView extends JComponent {

    private final JTsGrid grid_;
    private final JTsChart chart_;
    private final JTsChart dchart_;
    private final Box documentPanel_;
    private ITsViewToolkit toolkit_ = TsViewToolkit.getInstance();

    public BenchmarkingView() {
        setLayout(new BorderLayout());

        grid_ = new JTsGrid();
        grid_.setTsUpdateMode(TsUpdateMode.None);
        chart_ = new JTsChart();
        chart_.setTsUpdateMode(TsUpdateMode.None);
        dchart_ = new JTsChart();
        documentPanel_ = Box.createHorizontalBox();

        JSplitPane splitpane1 = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, true, chart_, dchart_);
        splitpane1.setDividerLocation(0.7);
        splitpane1.setResizeWeight(0.3);

        JSplitPane splitpane2 = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, true, grid_, documentPanel_);
        splitpane2.setDividerLocation(0.7);
        splitpane2.setResizeWeight(0.3);

        JSplitPane splitpane3 = NbComponents.newJSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, splitpane1, splitpane2);
        splitpane3.setDividerLocation(0.5);
        splitpane3.setResizeWeight(0.5);

        this.add(splitpane3, BorderLayout.CENTER);
    }

    public void setTsToolkit(ITsViewToolkit toolkit) {
        toolkit_ = toolkit;
    }

    public ITsViewToolkit getTsToolkit() {
        return toolkit_;
    }

    public void set(Ts benchSa, Ts sa, boolean mul) {
        TsData sdiff = mul
                ? TsData.divide(benchSa.getTsData(), sa.getTsData()).minus(1)
                : TsData.subtract(benchSa.getTsData(), sa.getTsData());

        TsCollection base = Stream.of(TsConverter.toTs(sa), TsConverter.toTs(benchSa)).collect(TsCollection.toTsCollection());
        TsCollection diff = TsCollection.of(OldTsUtil.toTs("Differences", sdiff));
        TsCollection all = Stream.concat(base.stream(), diff.stream()).collect(TsCollection.toTsCollection());
        
        chart_.setTsCollection(base);
        dchart_.setTsCollection(diff);
        grid_.setTsCollection(all);

        HtmlTsDifferenceDocument document = new HtmlTsDifferenceDocument(benchSa, sa, mul);
        Disposables.disposeAndRemoveAll(documentPanel_).add(toolkit_.getHtmlViewer(document));
        chart_.updateUI();
        dchart_.updateUI();
    }
}