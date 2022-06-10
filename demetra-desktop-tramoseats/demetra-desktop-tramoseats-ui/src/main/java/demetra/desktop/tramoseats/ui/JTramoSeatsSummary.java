/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramoseats.ui;

import demetra.desktop.TsDynamicProvider;
import demetra.desktop.components.JTsChart;
import demetra.desktop.components.parts.HasTsCollection.TsUpdateMode;
import demetra.desktop.design.SwingComponent;
import demetra.desktop.interfaces.Disposable;
import demetra.desktop.processing.ui.sa.JSIView;
import demetra.desktop.ui.Disposables;
import demetra.desktop.ui.processing.TsViewToolkit;
import demetra.sa.ComponentDescriptor;
import demetra.desktop.util.NbComponents;
import demetra.modelling.SeriesInfo;
import demetra.sa.SaDictionaries;
import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsData;
import demetra.timeseries.TsFactory;
import demetra.timeseries.TsInformationType;
import demetra.toolkit.dictionaries.Dictionary;
import demetra.tramoseats.io.html.HtmlTramoSeatsSummary;
import demetra.util.MultiLineNameUtil;
import java.awt.*;
import java.util.Arrays;
import javax.swing.*;
import jdplus.arima.ArimaModel;
import jdplus.seats.SeatsResults;
import jdplus.tramoseats.TramoSeatsDocument;
import jdplus.ucarima.UcarimaModel;

/**
 * @author Kristof Bayens
 */
@SwingComponent
public final class JTramoSeatsSummary extends JComponent implements Disposable{

    public static final ComponentDescriptor[] components;

    static {
        components = new ComponentDescriptor[5];
        components[0] = new ComponentDescriptor("sa", 1, false, true);
        components[1] = new ComponentDescriptor("trend", 0, true, true);
        components[2] = new ComponentDescriptor("seasonal", 1, true, false);
        components[3] = new ComponentDescriptor("transitory", 2, true, false);
        components[4] = new ComponentDescriptor("irregular", 3, true, false);
    }

    protected String[] getComponentsName(UcarimaModel ucm) {
        int n = ucm.getComponentsCount();
        String[] c = new String[n + 1];
        for (int i = 0; i < n; ++i) {
            c[i] = components[i].getName();
        }
        c[n] = components[4].getName();
        return c;
    }

    protected ArimaModel[] getComponents(UcarimaModel ucm) {
        int n = ucm.getComponentsCount();
        ArimaModel[] models = new ArimaModel[n + 1];
        for (int i = 0; i < n; ++i) {
            models[i] = components[i].isSignal() ? ucm.getComponent(components[i].getComponent())
                    : ucm.getComplement(components[i].getComponent());
        }
        models[n] = ucm.getComponent(components[4].getComponent());

        return models;
    }

    private final Box document_;
    private final JTsChart chart_;
    private final JSIView siPanel_;
    private TramoSeatsDocument doc_;

    public JTramoSeatsSummary() {
        setLayout(new BorderLayout());

        chart_ = new JTsChart();
        chart_.setTsUpdateMode(TsUpdateMode.None);
        siPanel_ = new JSIView();

        JSplitPane split1 = NbComponents.newJSplitPane(JSplitPane.HORIZONTAL_SPLIT, chart_, siPanel_);
        split1.setDividerLocation(0.6);
        split1.setResizeWeight(.5);

        document_ = Box.createHorizontalBox();

        JSplitPane split2 = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, document_, split1);
        split2.setDividerLocation(0.5);
        split2.setResizeWeight(.5);

        add(split2, BorderLayout.CENTER);
    }

    public void set(TramoSeatsDocument doc) {
        doc_ = doc;

        if (doc_ == null || doc_.getResult() == null) {
            return;
        }

        SeatsResults seats = doc_.getResult().getDecomposition();
        HtmlTramoSeatsSummary document;
        if (seats == null) {
            document = new HtmlTramoSeatsSummary(MultiLineNameUtil.join(doc_.getInput().getName()), doc_.getResult(), null, null);
        } else {
            UcarimaModel ucm = seats.getUcarimaModel();
            document = new HtmlTramoSeatsSummary(MultiLineNameUtil.join(doc_.getInput().getName()), doc_.getResult(), getComponentsName(ucm), getComponents(ucm));
        }
        Disposables.disposeAndRemoveAll(document_).add(TsViewToolkit.getHtmlViewer(document));

        String[] lowSeries = lowSeries();
        chart_.setTsCollection(
                Arrays.stream(lowSeries).map(s->getMainSeries(s)).collect(TsCollection.toTsCollection())
        );

        if (seats != null) {
            TsData seas = doc_.getResult().getData(Dictionary.concatenate(SaDictionaries.DECOMPOSITION, SaDictionaries.S_CMP), TsData.class);
            TsData irr = doc_.getResult().getData(Dictionary.concatenate(SaDictionaries.DECOMPOSITION, SaDictionaries.I_CMP), TsData.class);
            siPanel_.setData(seas, irr, doc_.getResult().getFinals().getMode());
        } else {
            siPanel_.reset();
        }
    }

    private Ts getMainSeries(String str) {
        return TsFactory.getDefault().makeTs(TsDynamicProvider.monikerOf(doc_, str), TsInformationType.All);
    }

    @Override
    public void dispose() {
        doc_ = null;
        Disposables.disposeAndRemoveAll(document_);
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
}
