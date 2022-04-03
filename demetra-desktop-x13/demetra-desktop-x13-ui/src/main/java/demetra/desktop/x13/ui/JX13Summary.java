/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.x13.ui;

import demetra.desktop.TsDynamicProvider;
import demetra.desktop.components.JTsChart;
import demetra.desktop.components.parts.HasTsCollection.TsUpdateMode;
import demetra.desktop.design.SwingComponent;
import demetra.desktop.interfaces.Disposable;
import demetra.desktop.processing.ui.sa.JSIView;
import demetra.desktop.ui.Disposables;
import demetra.desktop.ui.processing.TsViewToolkit;
import demetra.desktop.util.NbComponents;
import demetra.modelling.SeriesInfo;
import demetra.sa.DecompositionMode;
import demetra.sa.SaDictionaries;
import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsData;
import demetra.timeseries.TsFactory;
import demetra.timeseries.TsInformationType;
import demetra.util.MultiLineNameUtil;
import demetra.x13.html.HtmlX13Summary;
import java.awt.*;
import java.util.Arrays;
import javax.swing.*;
import jdplus.x11.X11Results;
import jdplus.x13.X13Document;
import jdplus.x13.X13Results;

/**
 * @author Kristof Bayens
 */
@SwingComponent
public final class JX13Summary extends JComponent implements Disposable {

    private final Box document_;
    private final JTsChart chart_;
    private final JSIView siPanel_;
    private X13Document doc_;

    public JX13Summary() {
        setLayout(new BorderLayout());

        this.chart_ = new JTsChart();
        chart_.setTsUpdateMode(TsUpdateMode.None);
        this.siPanel_ = new JSIView();

        JSplitPane split1 = NbComponents.newJSplitPane(JSplitPane.HORIZONTAL_SPLIT, chart_, siPanel_);
        split1.setDividerLocation(0.6);
        split1.setResizeWeight(.5);

        this.document_ = Box.createHorizontalBox();

        JSplitPane split2 = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, document_, split1);
        split2.setDividerLocation(0.5);
        split2.setResizeWeight(.5);

        add(split2, BorderLayout.CENTER);
    }

    public void set(X13Document doc) {
        this.doc_ = doc;
        if (doc == null) {
            return;
        }
        X13Results results = doc.getResult();
        if (results == null) {
            return;
        }

        HtmlX13Summary summary = new HtmlX13Summary(MultiLineNameUtil.join(doc.getInput().getName()), results);
        Disposables.disposeAndRemoveAll(document_).add(TsViewToolkit.getHtmlViewer(summary));

        String[] lowSeries = lowSeries();
        chart_.setTsCollection(
                Arrays.stream(lowSeries).map(s->getMainSeries(s)).collect(TsCollection.toTsCollection())
        );

        X11Results x11 = doc.getResult().getDecomposition();
        if (x11 != null) {
            TsData si = results.getDecomposition().getD8();
            TsData seas = results.getDecomposition().getD10();

            if (x11.getMode() == DecompositionMode.LogAdditive) {
                si = si.exp();
            }

            siPanel_.setSiData(seas, si);
        } else {
            siPanel_.reset();
        }
    }

    private Ts getMainSeries(String str) {
        return TsFactory.getDefault().makeTs(TsDynamicProvider.monikerOf(doc_, str), TsInformationType.All);
    }

    private static String generateId(String name, String id){
        return TsDynamicProvider.CompositeTs.builder()
                .name(name)
                .back(id+SeriesInfo.B_SUFFIX)
                .now(id)
                .fore(id+SeriesInfo.F_SUFFIX)
                .build().toString();
    }
    
    public static String[] lowSeries(){
        return new String[]{
            generateId("Series", SaDictionaries.Y),
            generateId("Seasonally adjusted", SaDictionaries.SA),
            generateId("Trend", SaDictionaries.T)
        };
    }

    @Override
    public void dispose() {
        doc_ = null;
        Disposables.disposeAndRemoveAll(document_);
    }
}
