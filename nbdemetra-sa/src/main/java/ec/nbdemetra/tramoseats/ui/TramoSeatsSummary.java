/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats.ui;

import demetra.bridge.TsConverter;
import demetra.timeseries.TsCollection;
import demetra.ui.components.HasTsCollection.TsUpdateMode;
import demetra.ui.util.NbComponents;
import ec.satoolkit.ComponentDescriptor;
import ec.satoolkit.seats.SeatsResults;
import ec.tss.documents.DocumentManager;
import ec.tss.html.implementation.HtmlTramoSeatsSummary;
import ec.tss.sa.documents.TramoSeatsDocument;
import ec.tss.tsproviders.utils.MultiLineNameUtil;
import ec.tstoolkit.arima.ArimaModel;
import ec.tstoolkit.modelling.ModellingDictionary;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.ucarima.UcarimaModel;
import ec.ui.Disposables;
import ec.ui.interfaces.IDisposable;
import demetra.ui.components.JTsChart;
import ec.ui.view.SIView;
import ec.ui.view.tsprocessing.ITsViewToolkit;
import ec.ui.view.tsprocessing.TsViewToolkit;
import java.awt.BorderLayout;
import javax.swing.*;

/**
 *
 * @author Kristof Bayens
 */
public class TramoSeatsSummary extends JComponent implements IDisposable {

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
            c[i] = components[i].name;
        }
        c[n] = components[4].name;
        return c;
    }

    protected ArimaModel[] getComponents(UcarimaModel ucm) {
        int n = ucm.getComponentsCount();
        ArimaModel[] models = new ArimaModel[n + 1];
        for (int i = 0; i < n; ++i) {
            models[i] = components[i].signal ? ucm.getComponent(components[i].cmp)
                    : ucm.getComplement(components[i].cmp);
        }
        models[n] = ucm.getComponent(components[4].cmp);

        return models;
    }
    private ITsViewToolkit toolkit_ = TsViewToolkit.getInstance();
    private final Box document_;
    private final JTsChart chart_;
    private final SIView siPanel_;
    private TramoSeatsDocument doc_;

    public TramoSeatsSummary() {
        setLayout(new BorderLayout());

        chart_ = new JTsChart();
        chart_.setTsUpdateMode(TsUpdateMode.None);
        siPanel_ = new SIView();

        JSplitPane split1 = NbComponents.newJSplitPane(JSplitPane.HORIZONTAL_SPLIT, chart_, siPanel_);
        split1.setDividerLocation(0.6);
        split1.setResizeWeight(.5);

        document_ = Box.createHorizontalBox();

        JSplitPane split2 = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, document_, split1);
        split2.setDividerLocation(0.5);
        split2.setResizeWeight(.5);

        add(split2, BorderLayout.CENTER);
    }

    public void setTsToolkit(ITsViewToolkit toolkit) {
        toolkit_ = toolkit;
    }

    public ITsViewToolkit getTsToolkit() {
        return toolkit_;
    }

    public void set(TramoSeatsDocument doc) {
        doc_ = doc;

        if (doc_ == null || doc_.getResults() == null) {
            return;
        }

        SeatsResults seats = doc_.getDecompositionPart();
        HtmlTramoSeatsSummary document;
        if (seats == null) {
            document = new HtmlTramoSeatsSummary(MultiLineNameUtil.join(doc_.getInput().getName()), doc_.getResults(), null, null, null);
        } else {
            UcarimaModel ucm = seats.getUcarimaModel();
            document = new HtmlTramoSeatsSummary(MultiLineNameUtil.join(doc_.getInput().getName()), doc_.getResults(), getComponentsName(ucm), getComponents(ucm), null);
        }
        Disposables.disposeAndRemoveAll(document_).add(toolkit_.getHtmlViewer(document));

        chart_.setTsCollection(
                TsCollection
                        .builder()
                        .data(getMainSeries(ModellingDictionary.Y))
                        .data(getMainSeries(ModellingDictionary.T))
                        .data(getMainSeries(ModellingDictionary.SA))
                        .build()
        );

        if (seats != null) {
            TsData seas = doc_.getResults().getData(ModellingDictionary.S_CMP, TsData.class);
            TsData irr = doc_.getResults().getData(ModellingDictionary.I_CMP, TsData.class);
            siPanel_.setData(seas, irr, doc_.getFinalDecomposition().getMode());
        } else {
            siPanel_.reset();
        }
    }

    private demetra.timeseries.Ts getMainSeries(String str) {
        return TsConverter.toTs(DocumentManager.instance.getTs(doc_, str));
    }

    @Override
    public void dispose() {
        doc_ = null;
        Disposables.disposeAndRemoveAll(document_);
    }
}
