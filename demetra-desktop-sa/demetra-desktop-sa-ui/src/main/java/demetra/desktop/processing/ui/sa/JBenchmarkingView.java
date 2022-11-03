/*
 * Copyright 2022 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.processing.ui.sa;

import demetra.desktop.components.JTsChart;
import demetra.desktop.components.JTsGrid;
import demetra.desktop.components.parts.HasTsCollection.TsUpdateMode;
import demetra.desktop.ui.Disposables;
import demetra.desktop.ui.processing.TsViewToolkit;
import demetra.desktop.util.NbComponents;
import demetra.html.stats.HtmlTsDataDifferenceDocument;
import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsData;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JSplitPane;

/**
 *
 * @author palatej
 */
public class JBenchmarkingView extends JComponent {

    private final JTsGrid grid_;
    private final JTsChart chart_;
    private final JTsChart dchart_;
    private final Box documentPanel_;

    public JBenchmarkingView() {
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

    public void set(TsData benchSa, TsData sa, boolean mul) {
        ArrayList<Ts> all = new ArrayList<>();
        all.add(Ts.of("sa", sa));
        all.add(Ts.of("benchmarked-sa", benchSa));
        chart_.setTsCollection(TsCollection.of(all));
        TsData sdiff = mul ? (TsData.divide(benchSa, sa).fn(z->z-1))
                : TsData.subtract(benchSa, sa);
        Ts diff = Ts.of("Differences", sdiff);
        List<Ts> ldiff = Collections.singletonList(diff);
       dchart_.setTsCollection(TsCollection.of(ldiff));
        all.add(diff);
        grid_.setTsCollection(TsCollection.of(all));

        HtmlTsDataDifferenceDocument document = new HtmlTsDataDifferenceDocument(benchSa, sa, mul);
        Disposables.disposeAndRemoveAll(documentPanel_).add(TsViewToolkit.getHtmlViewer(document));
        chart_.updateUI();
        dchart_.updateUI();
    }

}
