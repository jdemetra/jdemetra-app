/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramo.ui;

import demetra.desktop.processing.ui.modelling.ForecastsFactory;
import demetra.desktop.processing.ui.modelling.InputFactory;
import demetra.desktop.processing.ui.modelling.LikelihoodFactory;
import demetra.desktop.processing.ui.modelling.ModelRegressorsFactory;
import demetra.desktop.processing.ui.modelling.ModelArimaFactory;
import demetra.desktop.processing.ui.modelling.NiidTestsFactory;
import demetra.desktop.processing.ui.modelling.OutOfSampleTestFactory;
import demetra.desktop.processing.ui.modelling.RegSarimaViews;
import demetra.desktop.ui.processing.GenericTableUI;
import demetra.desktop.ui.processing.HtmlItemUI;
import demetra.desktop.ui.processing.IProcDocumentItemFactory;
import demetra.desktop.ui.processing.IProcDocumentViewFactory;
import demetra.desktop.ui.processing.ProcDocumentItemFactory;
import demetra.desktop.ui.processing.ProcDocumentViewFactory;
import demetra.desktop.ui.processing.stats.ResidualsDistUI;
import demetra.desktop.ui.processing.stats.ResidualsUI;
import demetra.desktop.ui.processing.stats.SpectrumUI;
import demetra.html.HtmlElement;
import demetra.information.InformationSet;
import demetra.modelling.ModellingDictionary;
import demetra.modelling.SeriesInfo;
import demetra.timeseries.TsData;
import demetra.timeseries.TsDocument;
import demetra.tramoseats.io.information.TramoSpecMapping;
import demetra.util.Id;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import jdplus.regsarima.regular.RegSarimaModel;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
public class TramoViewFactory extends ProcDocumentViewFactory<TramoDocument> {

    private static final AtomicReference<IProcDocumentViewFactory<TramoDocument>> INSTANCE = new AtomicReference();

    private final static Function<TramoDocument, RegSarimaModel> MODELEXTRACTOR = doc -> doc.getResult();
    private final static Function<TramoDocument, TsData> RESEXTRACTOR = doc -> {
        RegSarimaModel result = doc.getResult();
        return result == null ? null : result.fullResiduals();
    };

    public static IProcDocumentViewFactory<TramoDocument> getDefault() {
        IProcDocumentViewFactory<TramoDocument> fac = INSTANCE.get();
        if (fac == null) {
            fac = new TramoViewFactory();
            INSTANCE.lazySet(fac);
        }
        return fac;
    }

    public static void setDefault(IProcDocumentViewFactory<TramoDocument> factory) {
        INSTANCE.set(factory);
    }

    public TramoViewFactory() {
        registerFromLookup(TramoDocument.class);
    }

    @Override
    public Id getPreferredView() {
        return RegSarimaViews.MODEL_SUMMARY;
    }

//<editor-fold defaultstate="collapsed" desc="REGISTER SPEC">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 100010)
    public static class SpecFactory extends ProcDocumentItemFactory<TramoDocument, HtmlElement> {

        public SpecFactory() {
            super(TramoDocument.class, RegSarimaViews.INPUT_SPEC,
                    (TramoDocument doc) -> {
                        InformationSet info = TramoSpecMapping.write(doc.getSpecification(), true);
                        return new demetra.html.core.HtmlInformationSet(info);
                    },
                    new HtmlItemUI()
            );
        }

        @Override
        public int getPosition() {
            return 100010;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 100000)
    public static class Input extends InputFactory<TramoDocument> {

        public Input() {
            super(TramoDocument.class, RegSarimaViews.INPUT_SERIES);
        }

        @Override
        public int getPosition() {
            return 100000;
        }
    }

//</editor-fold>
//
//<editor-fold defaultstate="collapsed" desc="REGISTER SUMMARY">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 100000 + 1000)
    public static class SummaryFactory extends ProcDocumentItemFactory<TramoDocument, HtmlElement> {

        public SummaryFactory() {
            super(TramoDocument.class, RegSarimaViews.MODEL_SUMMARY,
                    source -> new demetra.html.modelling.HtmlRegArima(source.getResult(), false),
                    new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 101000;
        }
    }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="REGISTER FORECASTS">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 200000 + 500)
    public static class ForecastsTable extends ProcDocumentItemFactory<TramoDocument, TsDocument> {

        public ForecastsTable() {
            super(TramoDocument.class, RegSarimaViews.MODEL_FCASTS_TABLE, s -> s, new GenericTableUI(false, generateItems()));
        }

        @Override
        public int getPosition() {
            return 200500;
        }

        private static String[] generateItems() {
            return new String[]{ModellingDictionary.Y + SeriesInfo.F_SUFFIX, ModellingDictionary.Y + SeriesInfo.EF_SUFFIX};
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 200000 + 1000)
    public static class FCastsFactory extends ForecastsFactory<TramoDocument> {

        public FCastsFactory() {
            super(TramoDocument.class, RegSarimaViews.MODEL_FCASTS, MODELEXTRACTOR);
        }

        @Override
        public int getPosition() {
            return 201000;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 200000 + 2000)
    public static class FCastsOutFactory extends OutOfSampleTestFactory<TramoDocument> {

        public FCastsOutFactory() {
            super(TramoDocument.class, RegSarimaViews.MODEL_FCASTS_OUTOFSAMPLE, MODELEXTRACTOR);
        }

        @Override
        public int getPosition() {
            return 202000;
        }
    }

//</editor-fold>
//
//<editor-fold defaultstate="collapsed" desc="REGISTER MODEL">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 300000 + 1000)
    public static class ModelRegsFactory extends ModelRegressorsFactory<TramoDocument> {

        public ModelRegsFactory() {
            super(TramoDocument.class, RegSarimaViews.MODEL_REGS, MODELEXTRACTOR);
        }

        @Override
        public int getPosition() {
            return 301000;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 300000 + 2000)
    public static class ArimaFactory extends ModelArimaFactory {

        public ArimaFactory() {
            super(TramoDocument.class, RegSarimaViews.MODEL_ARIMA, MODELEXTRACTOR);
        }

        @Override
        public int getPosition() {
            return 302000;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 300000 + 3000)
    public static class PreprocessingDetFactory extends ProcDocumentItemFactory<TramoDocument, TramoDocument> {

        public PreprocessingDetFactory() {
            super(TramoDocument.class, RegSarimaViews.MODEL_DET,
                    source -> source, new GenericTableUI(false,
                            ModellingDictionary.Y_LIN, ModellingDictionary.DET,
                            ModellingDictionary.CAL, ModellingDictionary.TDE, ModellingDictionary.EE,
                            ModellingDictionary.OUT, ModellingDictionary.FULL_RES));
        }

        @Override
        public int getPosition() {
            return 303000;
        }
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="REGISTER RESIDUALS">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 400000 + 1000)
    public static class ModelResFactory extends ProcDocumentItemFactory<TramoDocument, TsData> {

        public ModelResFactory() {
            super(TramoDocument.class, RegSarimaViews.MODEL_RES, RESEXTRACTOR,
                    new ResidualsUI()
            );
        }

        @Override
        public int getPosition() {
            return 401000;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 400000 + 2000)
    public static class ModelResStatsFactory extends NiidTestsFactory<TramoDocument> {

        public ModelResStatsFactory() {
            super(TramoDocument.class, RegSarimaViews.MODEL_RES_STATS, MODELEXTRACTOR);
        }

        @Override
        public int getPosition() {
            return 402000;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 400000 + 3000)
    public static class ModelResDist extends ProcDocumentItemFactory<TramoDocument, TsData> {

        public ModelResDist() {
            super(TramoDocument.class, RegSarimaViews.MODEL_RES_DIST, RESEXTRACTOR,
                    new ResidualsDistUI());

        }

        @Override
        public int getPosition() {
            return 403000;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 400000 + 4000)
    public static class ModelResSpectrum extends ProcDocumentItemFactory<TramoDocument, TsData> {

        public ModelResSpectrum() {
            super(TramoDocument.class, RegSarimaViews.MODEL_RES_SPECTRUM, RESEXTRACTOR,
                    new SpectrumUI(true));

        }

        @Override
        public int getPosition() {
            return 404000;
        }
    }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="REGISTER DETAILS">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 500000)
    public static class LFactory extends LikelihoodFactory<TramoDocument> {

        public LFactory() {
            super(TramoDocument.class, RegSarimaViews.MODEL_LIKELIHOOD, MODELEXTRACTOR);
            setAsync(true);
        }

        @Override
        public int getPosition() {
            return 500000;
        }
    }
//</editor-fold>

}
