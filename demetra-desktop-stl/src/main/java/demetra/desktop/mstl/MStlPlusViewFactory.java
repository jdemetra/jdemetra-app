/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.mstl;

import demetra.desktop.TsDynamicProvider;
import demetra.desktop.processing.ui.modelling.InputFactory;
import demetra.desktop.processing.ui.modelling.RegSarimaViews;
import demetra.desktop.sa.ui.SaViews;
import demetra.desktop.mstl.extractors.MStlPlusExtractor;
import demetra.desktop.ui.processing.GenericChartUI;
import demetra.desktop.ui.processing.GenericTableUI;
import demetra.desktop.ui.processing.IProcDocumentItemFactory;
import demetra.desktop.ui.processing.IProcDocumentViewFactory;
import demetra.desktop.ui.processing.ProcDocumentItemFactory;
import demetra.desktop.ui.processing.ProcDocumentViewFactory;
import demetra.sa.SaDictionaries;
import demetra.timeseries.TsDocument;
import demetra.util.Id;
import java.util.concurrent.atomic.AtomicReference;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
public class MStlPlusViewFactory extends ProcDocumentViewFactory<MStlPlusDocument> {

    private static final AtomicReference<IProcDocumentViewFactory<MStlPlusDocument>> INSTANCE = new AtomicReference();

    public static IProcDocumentViewFactory<MStlPlusDocument> getDefault() {
        IProcDocumentViewFactory<MStlPlusDocument> fac = INSTANCE.get();
        if (fac == null) {
            fac = new MStlPlusViewFactory();
            INSTANCE.lazySet(fac);
        }
        return fac;
    }

    public static void setDefault(IProcDocumentViewFactory<MStlPlusDocument> factory) {
        INSTANCE.set(factory);
    }

    public MStlPlusViewFactory() {
        registerFromLookup(MStlPlusDocument.class);
    }

    @Override
    public Id getPreferredView() {
        return SaViews.MAIN_CHARTS_LOW;
    }

    private static String generateId(String name, String id) {
        return TsDynamicProvider.CompositeTs.builder()
                .name(name)
                .now(id)
                .build().toString();
    }

    public static String[] lowSeries() {
        return new String[]{
            generateId("Series", MStlPlusExtractor.Y),
            generateId("Seasonally adjusted", MStlPlusExtractor.SA),
            generateId("Trend", MStlPlusExtractor.T)
        };
    }

    public static String[] highSeries() {
        return new String[]{
            generateId("Yearly seasonal", MStlPlusExtractor.SY),
            generateId("Weekly seasonal", MStlPlusExtractor.SW),
            generateId("Irregular", MStlPlusExtractor.I)
        };
    }

    public static String[] finalSeries() {
        return new String[]{
            generateId("Series", SaDictionaries.Y),
            generateId("Seasonally adjusted", SaDictionaries.SA),
            generateId("Trend", SaDictionaries.T),
            generateId("Seasonal", SaDictionaries.S),
            generateId("Irregular", SaDictionaries.I)
        };
    }

//<editor-fold defaultstate="collapsed" desc="REGISTER SPEC">
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 100010)
//    public static class SpecFactory extends ProcDocumentItemFactory<FractionalAirlineDocument, HtmlElement> {
//
//        public SpecFactory() {
//            super(StlPlusDocument.class, RegSarimaViews.INPUT_SPEC,
//                    (StlPlusDocument doc) -> {
//                        InformationSet info = FractionalAirlineSpecMapping.write(doc.getSpecification(), true);
//                        return new demetra.html.core.HtmlInformationSet(info);
//                    },
//                    new HtmlItemUI()
//            );
//        }
//
//        @Override
//        public int getPosition() {
//            return 100010;
//        }
//    }
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 100000)
    public static class Input extends InputFactory<MStlPlusDocument> {

        public Input() {
            super(MStlPlusDocument.class, RegSarimaViews.INPUT_SERIES);
        }

        @Override
        public int getPosition() {
            return 100000;
        }
    }

//</editor-fold>
//
//<editor-fold defaultstate="collapsed" desc="REGISTER SUMMARY">
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 100000 + 1000)
//    public static class SummaryFactory extends ProcDocumentItemFactory<StlPlusDocument, HtmlElement> {
//
//        public SummaryFactory() {
//            super(StlPlusDocument.class, RegSarimaViews.MODEL_SUMMARY,
//                    source -> new HtmlFractionalAirlineModel(source.getResult(), false),
//                    new HtmlItemUI());
//        }
//
//        @Override
//        public int getPosition() {
//            return 101000;
//        }
//    }
//</editor-fold>
       @ServiceProvider(service = IProcDocumentItemFactory.class, position = 2000)
    public static class MainLowChart extends ProcDocumentItemFactory<MStlPlusDocument, TsDocument> {

        public MainLowChart() {
            super(MStlPlusDocument.class, SaViews.MAIN_CHARTS_LOW, s -> s, new GenericChartUI(false, lowSeries()));
        }

        @Override
        public int getPosition() {
            return 2000;
        }
    }
    
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 2100)
    public static class MainHighChart extends ProcDocumentItemFactory<MStlPlusDocument, TsDocument> {

        public MainHighChart() {
            super(MStlPlusDocument.class, SaViews.MAIN_CHARTS_HIGH, s -> s, new GenericChartUI(false, highSeries()));
        }

        @Override
        public int getPosition() {
            return 2100;
        }
    }
    
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 2200)
    public static class MainTable extends ProcDocumentItemFactory<MStlPlusDocument, TsDocument> {

        public MainTable() {
            super(MStlPlusDocument.class, SaViews.MAIN_TABLE, s -> s, new GenericTableUI(false, finalSeries()));
        }

        @Override
        public int getPosition() {
            return 2200;
        }

    }

//<editor-fold defaultstate="collapsed" desc="REGISTER FORECASTS">
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 200000 + 500)
//    public static class ForecastsTable extends ProcDocumentItemFactory<FractionalAirlineDocument, TsDocument> {
//
//        public ForecastsTable() {
//            super(StlPlusDocument.class, RegSarimaViews.MODEL_FCASTS_TABLE, s -> s, new GenericTableUI(false, generateItems()));
//        }
//
//        @Override
//        public int getPosition() {
//            return 200500;
//        }
//
//        private static String[] generateItems() {
//            return new String[]{ModellingDictionary.Y + SeriesInfo.F_SUFFIX, ModellingDictionary.Y + SeriesInfo.EF_SUFFIX};
//        }
//    }
//
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 200000 + 1000)
//    public static class FCastsFactory extends ForecastsFactory<FractionalAirlineDocument> {
//
//        public FCastsFactory() {
//            super(StlPlusDocument.class, RegSarimaViews.MODEL_FCASTS, MODELEXTRACTOR);
//        }
//
//        @Override
//        public int getPosition() {
//            return 201000;
//        }
//    }
//
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 200000 + 2000)
//    public static class FCastsOutFactory extends OutOfSampleTestFactory<FractionalAirlineDocument> {
//
//        public FCastsOutFactory() {
//            super(StlPlusDocument.class, RegSarimaViews.MODEL_FCASTS_OUTOFSAMPLE, MODELEXTRACTOR);
//        }
//
//        @Override
//        public int getPosition() {
//            return 202000;
//        }
//    }
//</editor-fold>
//
//<editor-fold defaultstate="collapsed" desc="REGISTER MODEL">
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 300000 + 1000)
//    public static class ModelRegsFactory extends ModelRegressorsFactory<FractionalAirlineDocument> {
//
//        public ModelRegsFactory() {
//            super(StlPlusDocument.class, RegSarimaViews.MODEL_REGS, MODELEXTRACTOR);
//        }
//
//        @Override
//        public int getPosition() {
//            return 301000;
//        }
//    }
//
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 300000 + 2000)
//    public static class ArimaFactory extends ModelArimaFactory {
//
//        public ArimaFactory() {
//            super(StlPlusDocument.class, RegSarimaViews.MODEL_ARIMA, MODELEXTRACTOR);
//        }
//
//        @Override
//        public int getPosition() {
//            return 302000;
//        }
//    }
//
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 300000 + 3000)
//    public static class PreprocessingDetFactory extends ProcDocumentItemFactory<FractionalAirlineDocument, StlPlusDocument> {
//
//        public PreprocessingDetFactory() {
//            super(StlPlusDocument.class, RegSarimaViews.MODEL_DET,
//                    source -> source, new GenericTableUI(false,
//                            ModellingDictionary.Y_LIN, ModellingDictionary.DET,
//                            ModellingDictionary.CAL, ModellingDictionary.TDE, ModellingDictionary.EE,
//                            ModellingDictionary.OUT, ModellingDictionary.FULL_RES));
//        }
//
//        @Override
//        public int getPosition() {
//            return 303000;
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="REGISTER RESIDUALS">
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 400000 + 1000)
//    public static class ModelResFactory extends ProcDocumentItemFactory<FractionalAirlineDocument, TsData> {
//
//        public ModelResFactory() {
//            super(StlPlusDocument.class, RegSarimaViews.MODEL_RES, RESEXTRACTOR,
//                    new ResidualsUI()
//            );
//        }
//
//        @Override
//        public int getPosition() {
//            return 401000;
//        }
//    }
//
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 400000 + 2000)
//    public static class ModelResStatsFactory extends NiidTestsFactory<FractionalAirlineDocument> {
//
//        public ModelResStatsFactory() {
//            super(StlPlusDocument.class, RegSarimaViews.MODEL_RES_STATS, MODELEXTRACTOR);
//        }
//
//        @Override
//        public int getPosition() {
//            return 402000;
//        }
//    }
//
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 400000 + 3000)
//    public static class ModelResDist extends ProcDocumentItemFactory<StlPlusDocument, DoubleSeq> {
//
//        public ModelResDist() {
//            super(StlPlusDocument.class, RegSarimaViews.MODEL_RES_DIST, RESEXTRACTOR,
//                    new DistributionUI());
//
//        }
//
//        @Override
//        public int getPosition() {
//            return 403000;
//        }
//    }
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 400000 + 4000)
//    public static class ModelResSpectrum extends ProcDocumentItemFactory<StlPlusDocument, DoubleSeq> {
//
//        public ModelResSpectrum() {
//            super(StlPlusDocument.class, RegSarimaViews.MODEL_RES_SPECTRUM, RESEXTRACTOR,
//                    new PeriodogramUI());
//
//        }
//
//        @Override
//        public int getPosition() {
//            return 404000;
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="REGISTER DETAILS">
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 500000)
//    public static class LFactory extends LikelihoodFactory<FractionalAirlineDocument> {
//
//        public LFactory() {
//            super(StlPlusDocument.class, RegSarimaViews.MODEL_LIKELIHOOD, MODELEXTRACTOR);
//            setAsync(true);
//        }
//
//        @Override
//        public int getPosition() {
//            return 500000;
//        }
//    }
//</editor-fold>
}
