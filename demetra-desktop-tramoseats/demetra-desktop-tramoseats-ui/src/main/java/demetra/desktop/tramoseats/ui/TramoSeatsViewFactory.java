/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramoseats.ui;

import demetra.desktop.TsDynamicProvider;
import demetra.desktop.processing.ui.modelling.ForecastsFactory;
import demetra.desktop.processing.ui.modelling.InputFactory;
import demetra.desktop.processing.ui.modelling.LikelihoodFactory;
import demetra.desktop.processing.ui.modelling.ModelArimaFactory;
import demetra.desktop.processing.ui.modelling.ModelRegressorsFactory;
import demetra.desktop.processing.ui.modelling.NiidTestsFactory;
import demetra.desktop.processing.ui.modelling.OutOfSampleTestFactory;
import demetra.desktop.processing.ui.sa.SIFactory;
import demetra.desktop.sa.ui.SaViews;
import demetra.desktop.ui.processing.GenericChartUI;
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
import demetra.information.BasicInformationExtractor;
import demetra.information.InformationSet;
import demetra.modelling.ModellingDictionary;
import demetra.modelling.SeriesInfo;
import demetra.sa.SaDictionary;
import demetra.timeseries.TsData;
import demetra.timeseries.TsDocument;
import demetra.tramoseats.io.information.TramoSeatsSpecMapping;
import demetra.util.Id;
import demetra.util.LinearId;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import jdplus.regsarima.regular.RegSarimaModel;
import jdplus.tramoseats.TramoSeatsResults;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
public class TramoSeatsViewFactory extends ProcDocumentViewFactory<TramoSeatsDocument> {

    //Seats nodes
    public static final String STOCHASTIC = "Stochastic series", COMPONENTS = "Components",
            STOCHASTIC_TREND = "Trend",
            STOCHASTIC_SA = "SA Series",
            STOCHASTIC_SEAS = "Seasonal",
            STOCHASTIC_IRR = "Irregular",
            MODELBASED = "Model-based tests",
            WKANALYSIS = "WK analysis",
            WK_COMPONENTS = "Components",
            WK_FINALS = "Final estimators",
            WK_PRELIMINARY = "Preliminary estimators",
            WK_ERRORS = "Errors analysis",
            WK_RATES = "Growth rates",
            SIGSEAS = "Significant seasonality",
            STVAR = "Stationary variance decomposition";

    public static final Id DECOMPOSITION_SUMMARY = new LinearId(SaViews.DECOMPOSITION);
    public static final Id DECOMPOSITION_STOCH_TREND = new LinearId(SaViews.DECOMPOSITION, STOCHASTIC, STOCHASTIC_TREND);
    public static final Id DECOMPOSITION_STOCH_SEAS = new LinearId(SaViews.DECOMPOSITION, STOCHASTIC, STOCHASTIC_SEAS);
    public static final Id DECOMPOSITION_STOCH_SA = new LinearId(SaViews.DECOMPOSITION, STOCHASTIC, STOCHASTIC_SA);
    public static final Id DECOMPOSITION_STOCH_IRR = new LinearId(SaViews.DECOMPOSITION, STOCHASTIC, STOCHASTIC_IRR);
    public static final Id DECOMPOSITION_SERIES = new LinearId(SaViews.DECOMPOSITION, STOCHASTIC);
    public static final Id DECOMPOSITION_CMPSERIES = new LinearId(SaViews.DECOMPOSITION, COMPONENTS);
    public static final Id DECOMPOSITION_WK_COMPONENTS = new LinearId(SaViews.DECOMPOSITION, WKANALYSIS, WK_COMPONENTS);
    public static final Id DECOMPOSITION_WK_FINALS = new LinearId(SaViews.DECOMPOSITION, WKANALYSIS, WK_FINALS);
    public static final Id DECOMPOSITION_ERRORS = new LinearId(SaViews.DECOMPOSITION, WK_ERRORS);
    public static final Id DECOMPOSITION_RATES = new LinearId(SaViews.DECOMPOSITION, WK_RATES);
    public static final Id DECOMPOSITION_TESTS = new LinearId(SaViews.DECOMPOSITION, MODELBASED);
    public static final Id DECOMPOSITION_VAR = new LinearId(SaViews.DECOMPOSITION, STVAR);
    public static final Id DECOMPOSITION_SIGSEAS = new LinearId(SaViews.DECOMPOSITION, SIGSEAS);

    private static final AtomicReference<IProcDocumentViewFactory<TramoSeatsDocument>> INSTANCE = new AtomicReference();

    private final static Function<TramoSeatsDocument, RegSarimaModel> MODELEXTRACTOR = source -> {
        TramoSeatsResults tr = source.getResult();
        return tr == null ? null : tr.getPreprocessing();
    };

    private final static Function<TramoSeatsDocument, TsData> RESEXTRACTOR = MODELEXTRACTOR
            .andThen(regarima -> regarima.fullResiduals());

    public static IProcDocumentViewFactory<TramoSeatsDocument> getDefault() {
        IProcDocumentViewFactory<TramoSeatsDocument> fac = INSTANCE.get();
        if (fac == null) {
            fac = new TramoSeatsViewFactory();
            INSTANCE.lazySet(fac);
        }
        return fac;
    }

    public static void setDefault(IProcDocumentViewFactory<TramoSeatsDocument> factory) {
        INSTANCE.set(factory);
    }

    public TramoSeatsViewFactory() {
        registerFromLookup(TramoSeatsDocument.class);
    }

    @Override
    public Id getPreferredView() {
        return SaViews.PREPROCESSING_SUMMARY;
    }

//<editor-fold defaultstate="collapsed" desc="INPUT">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 1010)
    public static class SpecFactory extends ProcDocumentItemFactory<TramoSeatsDocument, HtmlElement> {

        public SpecFactory() {
            super(TramoSeatsDocument.class, SaViews.INPUT_SPEC,
                    (TramoSeatsDocument doc) -> {
                        InformationSet info = TramoSeatsSpecMapping.write(doc.getSpecification(), true);
                        return new demetra.html.core.HtmlInformationSet(info);
                    },
                    new HtmlItemUI()
            );
        }

        @Override
        public int getPosition() {
            return 1010;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 1000)
    public static class Input extends InputFactory<TramoSeatsDocument> {

        public Input() {
            super(TramoSeatsDocument.class, SaViews.INPUT_SERIES);
        }

        @Override
        public int getPosition() {
            return 1000;
        }
    }
//</editor-fold>
    
    private static String generateId(String name, String id){
        return TsDynamicProvider.CompositeTs.builder()
                .name(name)
                .back(id+SeriesInfo.B_SUFFIX)
                .now(id)
                .fore(id+SeriesInfo.F_SUFFIX)
                .build().toString();
    }

    private static String generateStdErrorId(String name, String id){
        return TsDynamicProvider.CompositeTs.builder()
                .name(name)
                .back(id+SeriesInfo.EB_SUFFIX)
                .now(id+SeriesInfo.E_SUFFIX)
                .fore(id+SeriesInfo.EF_SUFFIX)
                .build().toString();
    }
    
    public static String[] lowSeries(){
        return new String[]{
            generateId("Series", SaDictionary.Y),
            generateId("Seasonally adjusted", SaDictionary.SA),
            generateId("Trend", SaDictionary.T)
        };
    }
    
    public static String[] highSeries(){
        return new String[]{
            generateId("Seasonal (component)", BasicInformationExtractor.concatenate(SaDictionary.DECOMPOSITION,SaDictionary.S_CMP)),
            generateId("Calendar effects", ModellingDictionary.CAL),
            generateId("Irregular", SaDictionary.I)
        };
    }
    
    public static String[] finalSeries(){
        return new String[]{
            generateId("Series", SaDictionary.Y),
            generateId("Seasonally adjusted", SaDictionary.SA),
            generateId("Trend", SaDictionary.T),
            generateId("Seasonal", SaDictionary.S),
            generateId("Irregular", SaDictionary.I)
        };
    }
    
//    //<editor-fold defaultstate="collapsed" desc="MAIN">
//    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 200000)
//    public static class MainSummaryFactory extends ItemFactory<TramoSeatsDocument> {
//
//        public MainSummaryFactory() {
//            super(MAIN_SUMMARY, new DefaultInformationExtractor<TramoSeatsDocument, TramoSeatsDocument>() {
//                @Override
//                public TramoSeatsDocument retrieve(TramoSeatsDocument source) {
//                    return source;
//                }
//            }, new PooledItemUI<View, TramoSeatsDocument, JTramoSeatsSummary>(JTramoSeatsSummary.class) {
//                @Override
//                protected void init(JTramoSeatsSummary c, View host, TramoSeatsDocument information) {
//                    c.set(information);
//                }
//            });
//        }
//
//        @Override
//        public int getPosition() {
//            return 200000;
//        }
//    }
//    //</editor-fold>
//
//    public String[] generateItems(String prefix) {
//        StringBuilder cal = new StringBuilder();
//        cal.append(TsDynamicProvider.COMPOSITE).append("Calendar effects=,").append(ModellingDictionary.CAL)
//                .append(',').append(ModellingDictionary.CAL).append(SeriesInfo.F_SUFFIX);
//        String ss = BasicInformationExtractor.concatenate(prefix, SaDictionary.S_CMP);
//        StringBuilder s = new StringBuilder();
//        s.append(TsDynamicProvider.COMPOSITE).append("Seas (component)=,").append(SaDictionary.S_CMP)
//                .append(',').append(SaDictionary.S_CMP).append(SeriesInfo.F_SUFFIX);
//        String si = BasicInformationExtractor.concatenate(prefix, SaDictionary.I_CMP);
//        StringBuilder i = new StringBuilder();
//        i.append(TsDynamicProvider.COMPOSITE).append("Irregular=")
//                .append(',').append(si)
//                .append(',').append(si).append(SeriesInfo.F_SUFFIX);
//        return new String[]{cal.toString(), s.toString(), i.toString()};
//    }
//
//    //<editor-fold defaultstate="collapsed" desc="REGISTER MAIN VIEWS">
//    // provide regitration of main components
//    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 201010)
//    public static class MainChartsLowFactory extends SaDocumentViewFactory.MainChartsLowFactory<TramoSeatsDocument> {
//
//        public MainChartsLowFactory() {
//            super(TramoSeatsDocument.class, SaDictionary.FINAL);
//        }
//
//        @Override
//        public int getPosition() {
//            return 201010;
//        }
//    }
//
//    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 201020)
//    public static class MainChartsHighFactory extends SaDocumentViewFactory.MainChartsHighFactory<TramoSeatsDocument> {
//
//        public MainChartsHighFactory() {
//            super(TramoSeatsDocument.class, GenericSaProcessingFactory.FINAL);
//        }
//
//        @Override
//        public int getPosition() {
//            return 201020;
//        }
//    }
//
//    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 202000)
//    public static class MainTableFactory extends SaDocumentViewFactory.MainTableFactory<TramoSeatsDocument> {
//
//        public MainTableFactory() {
//            super(TramoSeatsDocument.class, GenericSaProcessingFactory.FINAL);
//        }
//
//        @Override
//        public int getPosition() {
//            return 202000;
//        }
//    }
//
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 2000)
    public static class MainLowChart extends ProcDocumentItemFactory<TramoSeatsDocument, TsDocument> {

        public MainLowChart() {
            super(TramoSeatsDocument.class, SaViews.MAIN_CHARTS_LOW, s -> s, new GenericChartUI(false, lowSeries()));
        }

        @Override
        public int getPosition() {
            return 2000;
        }
    }
    
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 2100)
    public static class MainHighChart extends ProcDocumentItemFactory<TramoSeatsDocument, TsDocument> {

        public MainHighChart() {
            super(TramoSeatsDocument.class, SaViews.MAIN_CHARTS_HIGH, s -> s, new GenericChartUI(false, highSeries()));
        }

        @Override
        public int getPosition() {
            return 2100;
        }
    }
    
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 2200)
    public static class MainTable extends ProcDocumentItemFactory<TramoSeatsDocument, TsDocument> {

        public MainTable() {
            super(TramoSeatsDocument.class, SaViews.MAIN_TABLE, s -> s, new GenericTableUI(false, finalSeries()));
        }

        @Override
        public int getPosition() {
            return 2200;
        }

    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 2300)
    public static class MainSiFactory extends SIFactory<TramoSeatsDocument> {

        public MainSiFactory() {
            super(TramoSeatsDocument.class, SaViews.MAIN_SI, (TramoSeatsDocument source)->{
                TramoSeatsResults result = source.getResult();
                if (result == null)
                    return null;
                return result.getDecomposition().getFinalComponents();
            });
        }

        @Override
        public int getPosition() {
            return 2300;
        }
    }

//<editor-fold defaultstate="collapsed" desc="PREPROCESSING">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3000)
    public static class SummaryFactory extends ProcDocumentItemFactory<TramoSeatsDocument, HtmlElement> {

        public SummaryFactory() {

            super(TramoSeatsDocument.class, SaViews.PREPROCESSING_SUMMARY, MODELEXTRACTOR
                    .andThen(regarima -> regarima == null ? null
                    : new demetra.html.modelling.HtmlRegArima(regarima, false)),
                    new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 3000;
        }
    }

//</editor-fold>
    
//<editor-fold defaultstate="collapsed" desc="PREPROCESSING-FORECASTS">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3110)
    public static class ForecastsTable extends ProcDocumentItemFactory<TramoSeatsDocument, TsDocument> {

        public ForecastsTable() {
            super(TramoSeatsDocument.class, SaViews.PREPROCESSING_FCASTS_TABLE, s -> s, new GenericTableUI(false, generateItems()));
        }

        @Override
        public int getPosition() {
            return 3110;
        }

        private static String[] generateItems() {
            return new String[]{ModellingDictionary.Y + SeriesInfo.F_SUFFIX, ModellingDictionary.Y + SeriesInfo.EF_SUFFIX};
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3100)
    public static class FCastsFactory extends ForecastsFactory<TramoSeatsDocument> {

        public FCastsFactory() {
            super(TramoSeatsDocument.class, SaViews.PREPROCESSING_FCASTS, MODELEXTRACTOR);
        }

        @Override
        public int getPosition() {
            return 3100;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3120)
    public static class FCastsOutFactory extends OutOfSampleTestFactory<TramoSeatsDocument> {

        public FCastsOutFactory() {
            super(TramoSeatsDocument.class, SaViews.PREPROCESSING_FCASTS_OUTOFSAMPLE, MODELEXTRACTOR);
        }

        @Override
        public int getPosition() {
            return 3120;
        }
    }
//</editor-fold>
//
//<editor-fold defaultstate="collapsed" desc="PREPROCESSING-DETAILS">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3200)
    public static class ModelRegsFactory extends ModelRegressorsFactory<TramoSeatsDocument> {

        public ModelRegsFactory() {
            super(TramoSeatsDocument.class, SaViews.PREPROCESSING_REGS, MODELEXTRACTOR);
        }

        @Override
        public int getPosition() {
            return 3200;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3300)
    public static class ArimaFactory extends ModelArimaFactory {

        public ArimaFactory() {
            super(TramoSeatsDocument.class, SaViews.PREPROCESSING_ARIMA, MODELEXTRACTOR);
        }

        @Override
        public int getPosition() {
            return 3300;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3400)
    public static class PreprocessingDetFactory extends ProcDocumentItemFactory<TramoSeatsDocument, TsDocument> {

        public PreprocessingDetFactory() {
            super(TramoSeatsDocument.class, SaViews.PREPROCESSING_DET, source->source, new GenericTableUI(false,
//                    BasicInformationExtractor.concatenate(SaDictionary.PREPROCESSING, ModellingDictionary.Y_LIN), 
//                    BasicInformationExtractor.concatenate(SaDictionary.PREPROCESSING, ModellingDictionary.DET),
//                    BasicInformationExtractor.concatenate(SaDictionary.PREPROCESSING, ModellingDictionary.CAL), 
//                    BasicInformationExtractor.concatenate(SaDictionary.PREPROCESSING, ModellingDictionary.TDE), 
//                    BasicInformationExtractor.concatenate(SaDictionary.PREPROCESSING, ModellingDictionary.EE),
//                    BasicInformationExtractor.concatenate(SaDictionary.PREPROCESSING, ModellingDictionary.OUT), 
//                    BasicInformationExtractor.concatenate(SaDictionary.PREPROCESSING, ModellingDictionary.FULL_RES)));
                    SaDictionary.PREPROCESSING, ModellingDictionary.Y_LIN, 
                    SaDictionary.PREPROCESSING, ModellingDictionary.DET,
                    SaDictionary.PREPROCESSING, ModellingDictionary.CAL, 
                   SaDictionary.PREPROCESSING, ModellingDictionary.TDE, 
                    SaDictionary.PREPROCESSING, ModellingDictionary.EE,
                    SaDictionary.PREPROCESSING, ModellingDictionary.OUT, 
                    SaDictionary.PREPROCESSING, ModellingDictionary.FULL_RES));
        }

        @Override
        public int getPosition() {
            return 3400;
        }
    }
//</editor-fold>
    
//<editor-fold defaultstate="collapsed" desc="PREPROCESSING-RESIDUALS">

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3500)
    public static class ModelResFactory extends ProcDocumentItemFactory<TramoSeatsDocument, TsData> {

        public ModelResFactory() {
            super(TramoSeatsDocument.class, SaViews.PREPROCESSING_RES, RESEXTRACTOR,
                    new ResidualsUI()
            );
        }

        @Override
        public int getPosition() {
            return 3500;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3510)
    public static class ModelResStatsFactory extends NiidTestsFactory<TramoSeatsDocument> {

        public ModelResStatsFactory() {
            super(TramoSeatsDocument.class, SaViews.PREPROCESSING_RES_STATS, MODELEXTRACTOR);
        }

        @Override
        public int getPosition() {
            return 3510;
        }
    }


    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3520)
    public static class ModelResDist extends ProcDocumentItemFactory<TramoSeatsDocument, TsData> {

        public ModelResDist() {
            super(TramoSeatsDocument.class, SaViews.PREPROCESSING_RES_DIST,
                    RESEXTRACTOR,
                    new ResidualsDistUI());

        }

        @Override
        public int getPosition() {
            return 3520;
        }
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="PREPROCESSING-OTHERS">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3600)
    public static class LFactory extends LikelihoodFactory<TramoSeatsDocument> {

        public LFactory() {
            super(TramoSeatsDocument.class, SaViews.PREPROCESSING_LIKELIHOOD, MODELEXTRACTOR);
            setAsync(true);
        }

        @Override
        public int getPosition() {
            return 3600;
        }
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="DIAGNOSTICS">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5310)
    public static class ModelResSpectrum extends ProcDocumentItemFactory<TramoSeatsDocument, TsData> {

        public ModelResSpectrum() {
            super(TramoSeatsDocument.class, SaViews.DIAGNOSTICS_SPECTRUM_RES,
                    RESEXTRACTOR,
                    new SpectrumUI(true));

        }

        @Override
        public int getPosition() {
            return 5310;
        }
    }
//</editor-fold>

}
