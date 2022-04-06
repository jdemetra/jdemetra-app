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
import demetra.desktop.ui.processing.stats.EstimationUI;
import demetra.desktop.ui.processing.stats.ResidualsDistUI;
import demetra.desktop.ui.processing.stats.ResidualsUI;
import demetra.desktop.ui.processing.stats.SpectrumUI;
import demetra.html.HtmlElement;
import demetra.html.core.HtmlDiagnosticsSummary;
import demetra.information.InformationSet;
import demetra.modelling.ComponentInformation;
import demetra.modelling.ModellingDictionary;
import demetra.modelling.SeriesInfo;
import demetra.processing.ProcDiagnostic;
import demetra.sa.ComponentType;
import demetra.sa.SaDictionaries;
import demetra.sa.SaManager;
import demetra.sa.SaProcessingFactory;
import demetra.timeseries.TsData;
import demetra.timeseries.TsDocument;
import demetra.toolkit.dictionaries.Dictionary;
import demetra.toolkit.dictionaries.RegressionDictionaries;
import demetra.tramoseats.io.information.TramoSeatsSpecMapping;
import demetra.util.Id;
import demetra.util.LinearId;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import jdplus.regsarima.regular.RegSarimaModel;
import jdplus.seats.SeatsResults;
import jdplus.tramoseats.TramoSeatsResults;
import jdplus.tramoseats.TramoSeatsDocument;
import jdplus.ucarima.UcarimaModel;
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

    private final static Function<TramoSeatsDocument, SeatsResults> DECOMPOSITIONEXTRACTOR = source -> {
        TramoSeatsResults tr = source.getResult();
        return tr == null ? null : tr.getDecomposition();
    };

    private final static Function<TramoSeatsDocument, TsData> RESEXTRACTOR = MODELEXTRACTOR
            .andThen(regarima -> regarima.fullResiduals());

    private static Function<SeatsResults, EstimationUI.Information> cmpExtractor(ComponentType type) {

        return (SeatsResults source) -> {
            if (source == null) {
                return null;
            }
            TsData s = source.getInitialComponents().getSeries(type, ComponentInformation.Value);
            if (s == null) {
                return new EstimationUI.Information(null, null, null, null);
            }
            TsData es = source.getInitialComponents().getSeries(type, ComponentInformation.Stdev);
//            TsPeriodSelector sel = new TsPeriodSelector();
//            sel.last(2 * s.getFrequency().intValue());
            TsData fs = source.getInitialComponents().getSeries(type, ComponentInformation.Forecast);
            TsData efs = source.getInitialComponents().getSeries(type, ComponentInformation.StdevForecast);
            LocalDateTime x = s.getDomain().getEndPeriod().start();
            s = TsData.concatenate(s, fs);
            es = TsData.concatenate(es, efs);
//           s = s.select(sel).update(fs);
//            es = es.select(sel).update(efs);
            EstimationUI.Information rslt
                    = new EstimationUI.Information(s, null, s.fastFn(es, (a,b) -> a + b * 1.96), s.fastFn(es, (a, b) -> a - b * 1.96));
            rslt.markers = new LocalDateTime[]{x};
            return rslt;
        };
    }

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
        registerFromLookup(TramoSeatsDocument.class
        );
    }

    @Override
    public Id getPreferredView() {
        return SaViews.MAIN_SUMMARY;

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

    private static String generateId(String name, String id) {
        return TsDynamicProvider.CompositeTs.builder()
                .name(name)
                .back(id + SeriesInfo.B_SUFFIX)
                .now(id)
                .fore(id + SeriesInfo.F_SUFFIX)
                .build().toString();
    }

    private static String generateStdErrorId(String name, String id) {
        return TsDynamicProvider.CompositeTs.builder()
                .name(name)
                .back(id + SeriesInfo.EB_SUFFIX)
                .now(id + SeriesInfo.E_SUFFIX)
                .fore(id + SeriesInfo.EF_SUFFIX)
                .build().toString();
    }

    public static String[] lowSeries() {
        return new String[]{
            generateId("Series", SaDictionaries.Y),
            generateId("Seasonally adjusted", SaDictionaries.SA),
            generateId("Trend", SaDictionaries.T)
        };
    }

    public static String[] highSeries() {
        return new String[]{
            generateId("Seasonal (component)", Dictionary.concatenate(SaDictionaries.DECOMPOSITION, SaDictionaries.S_CMP)),
            generateId("Calendar effects", ModellingDictionary.CAL),
            generateId("Irregular", SaDictionaries.I)
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

    //<editor-fold defaultstate="collapsed" desc="MAIN">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 2000)
    public static class MainSummaryFactory extends ProcDocumentItemFactory<TramoSeatsDocument, TramoSeatsDocument> {

        public MainSummaryFactory() {
            super(TramoSeatsDocument.class, SaViews.MAIN_SUMMARY, s -> s, new TramoSeatsSummary());
        }

        @Override
        public int getPosition() {
            return 2000;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 2100)
    public static class MainLowChart extends ProcDocumentItemFactory<TramoSeatsDocument, TsDocument> {

        public MainLowChart() {
            super(TramoSeatsDocument.class, SaViews.MAIN_CHARTS_LOW, s -> s, new GenericChartUI(false, lowSeries()));
        }

        @Override
        public int getPosition() {
            return 2100;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 2200)
    public static class MainHighChart extends ProcDocumentItemFactory<TramoSeatsDocument, TsDocument> {

        public MainHighChart() {
            super(TramoSeatsDocument.class, SaViews.MAIN_CHARTS_HIGH, s -> s, new GenericChartUI(false, highSeries()));
        }

        @Override
        public int getPosition() {
            return 2200;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 2300)
    public static class MainTable extends ProcDocumentItemFactory<TramoSeatsDocument, TsDocument> {

        public MainTable() {
            super(TramoSeatsDocument.class, SaViews.MAIN_TABLE, s -> s, new GenericTableUI(false, finalSeries()));
        }

        @Override
        public int getPosition() {
            return 2300;
        }

    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 2400)
    public static class MainSiFactory extends SIFactory<TramoSeatsDocument> {

        public MainSiFactory() {
            super(TramoSeatsDocument.class, SaViews.MAIN_SI, (TramoSeatsDocument source) -> {
                TramoSeatsResults result = source.getResult();
                if (result == null) {
                    return null;
                }
                return result.getDecomposition().getFinalComponents();
            });
        }

        @Override
        public int getPosition() {
            return 2400;
        }
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="PREPROCESSING">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3000)
    public static class SummaryFactory extends ProcDocumentItemFactory<TramoSeatsDocument, HtmlElement> {

        public SummaryFactory() {

            super(TramoSeatsDocument.class, SaViews.PREPROCESSING_SUMMARY, MODELEXTRACTOR
                    .andThen(regarima -> regarima == null ? null
                    : new demetra.html.modelling.HtmlRegSarima(regarima, false)),
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
            return new String[]{RegressionDictionaries.Y_F, RegressionDictionaries.Y_EF};
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
            super(TramoSeatsDocument.class, SaViews.PREPROCESSING_DET, source -> source, new GenericTableUI(false,
                    SaDictionaries.PREPROCESSING, ModellingDictionary.Y_LIN,
                    SaDictionaries.PREPROCESSING, ModellingDictionary.DET,
                    SaDictionaries.PREPROCESSING, ModellingDictionary.CAL,
                    SaDictionaries.PREPROCESSING, ModellingDictionary.TDE,
                    SaDictionaries.PREPROCESSING, ModellingDictionary.EE,
                    SaDictionaries.PREPROCESSING, ModellingDictionary.OUT,
                    SaDictionaries.PREPROCESSING, ModellingDictionary.FULL_RES));
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

//<editor-fold defaultstate="collapsed" desc="REGISTER SEATS">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 4000)
    public static class DecompositionSummaryFactory extends ProcDocumentItemFactory<TramoSeatsDocument, HtmlElement> {

        public DecompositionSummaryFactory() {
            super(TramoSeatsDocument.class, DECOMPOSITION_SUMMARY, DECOMPOSITIONEXTRACTOR
                    .andThen((SeatsResults seats) -> {
                        if (seats == null) {
                            return null;
                        }
                        UcarimaModel ucm = seats.getUcarimaModel();
                        return new demetra.html.modelling.HtmlUcarima(ucm.getModel(),
                                Utility.getComponents(ucm),
                                Utility.getComponentsName(ucm));
                    }),
                    new HtmlItemUI());

        }

        @Override
        public int getPosition() {
            return 4000;
        }
    }

    private static String[] linSeries() {
        return new String[]{
            generateId("Series (lin)", Dictionary.concatenate(SaDictionaries.DECOMPOSITION, SaDictionaries.Y_LIN)),
            generateId("Seasonally adjusted (lin)", Dictionary.concatenate(SaDictionaries.DECOMPOSITION, SaDictionaries.SA_LIN)),
            generateId("Trend (lin)", Dictionary.concatenate(SaDictionaries.DECOMPOSITION, SaDictionaries.T_LIN)),
            generateId("Seasonal (lin)", Dictionary.concatenate(SaDictionaries.DECOMPOSITION, SaDictionaries.S_LIN)),
            generateId("Irregular (lin)", Dictionary.concatenate(SaDictionaries.DECOMPOSITION, SaDictionaries.I_LIN)),
            generateId("Series (lin)", Dictionary.concatenate(SaDictionaries.DECOMPOSITION, SaDictionaries.Y_LIN)),
            generateStdErrorId("Seasonally adjusted (stde lin)", Dictionary.concatenate(SaDictionaries.DECOMPOSITION, SaDictionaries.SA_LIN)),
            generateStdErrorId("Trend (stde lin)", Dictionary.concatenate(SaDictionaries.DECOMPOSITION, SaDictionaries.T_LIN)),
            generateStdErrorId("Seasonal (stde lin)", Dictionary.concatenate(SaDictionaries.DECOMPOSITION, SaDictionaries.S_LIN)),
            generateStdErrorId("Irregular (stde lin)", Dictionary.concatenate(SaDictionaries.DECOMPOSITION, SaDictionaries.I_LIN))
        };

    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 4100)
    public static class LinearizedSeriesFactory extends ProcDocumentItemFactory<TramoSeatsDocument, TsDocument> {

        public LinearizedSeriesFactory() {
            super(TramoSeatsDocument.class, DECOMPOSITION_SERIES, s -> s, new GenericTableUI(true, linSeries()));
        }

        @Override
        public int getPosition() {
            return 4100;
        }
    }

    private static String[] cmpSeries() {
        return new String[]{
            generateId("Series (cmp)", Dictionary.concatenate(SaDictionaries.DECOMPOSITION, SaDictionaries.Y_CMP)),
            generateId("Seasonally adjusted (cmp)", Dictionary.concatenate(SaDictionaries.DECOMPOSITION, SaDictionaries.SA_CMP)),
            generateId("Trend (cmp)", Dictionary.concatenate(SaDictionaries.DECOMPOSITION, SaDictionaries.T_CMP)),
            generateId("Seasonal (cmp)", Dictionary.concatenate(SaDictionaries.DECOMPOSITION, SaDictionaries.S_CMP)),
            generateId("Irregular (cmp)", Dictionary.concatenate(SaDictionaries.DECOMPOSITION, SaDictionaries.I_CMP))
        };

    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 4200)
    public static class ComponentsSeriesFactory extends ProcDocumentItemFactory<TramoSeatsDocument, TsDocument> {

        public ComponentsSeriesFactory() {
            super(TramoSeatsDocument.class, DECOMPOSITION_CMPSERIES, s -> s, new GenericTableUI(true, cmpSeries()));
        }

        @Override
        public int getPosition() {
            return 4200;
        }
    }

//    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 4210)
//    public static class DecompositionWkComponentsFactory extends ItemFactory<WkInformation> {
//
//        public DecompositionWkComponentsFactory() {
//            super(DECOMPOSITION_WK_COMPONENTS, wkExtractor(), new WkComponentsUI());
//        }
//
//        @Override
//        public int getPosition() {
//            return 402010;
//        }
//    }
//
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 4110)
    public static class DecompositionStochTrendFactory extends ProcDocumentItemFactory<TramoSeatsDocument, EstimationUI.Information> {

        public DecompositionStochTrendFactory() {
            super(TramoSeatsDocument.class, DECOMPOSITION_STOCH_TREND, DECOMPOSITIONEXTRACTOR.andThen(cmpExtractor(ComponentType.Trend)), new EstimationUI());
        }

        @Override
        public int getPosition() {
            return 4110;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 4120)
    public static class DecompositionStochSeasFactory extends ProcDocumentItemFactory<TramoSeatsDocument, EstimationUI.Information> {

        public DecompositionStochSeasFactory() {
            super(TramoSeatsDocument.class, DECOMPOSITION_STOCH_SEAS, DECOMPOSITIONEXTRACTOR.andThen(cmpExtractor(ComponentType.Seasonal)), new EstimationUI());
        }

        @Override
        public int getPosition() {
            return 4120;
        }
    }

//    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 402020)
//    public static class DecompositionWkFinalsFactory extends ItemFactory<WkInformation> {
//
//        public DecompositionWkFinalsFactory() {
//            super(DECOMPOSITION_WK_FINALS, wkExtractor(), new WkFinalEstimatorsUI());
//        }
//
//        @Override
//        public int getPosition() {
//            return 402020;
//        }
//    }
//
//    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 402030)
//    public static class DecompositionWkErrorsFactory extends ItemFactory<WkInformation> {
//
//        public DecompositionWkErrorsFactory() {
//            super(DECOMPOSITION_ERRORS, wkExtractor(), new WkErrorsUI());
//        }
//
//        @Override
//        public int getPosition() {
//            return 402030;
//        }
//    }
//
//    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 402050)
//    public static class DecompositionGrowthFactory extends ItemFactory<CompositeResults> {
//
//        public DecompositionGrowthFactory() {
//            super(DECOMPOSITION_RATES, new DefaultInformationExtractor<TramoSeatsDocument, CompositeResults>() {
//                @Override
//                public CompositeResults retrieve(TramoSeatsDocument source) {
//                    CompositeResults results = source.getResults();
//                    if (!results.isSuccessful()) {
//                        return null;
//                    } else {
//                        return results;
//                    }
//                }
//            }, new GrowthRatesUI());
//        }
//
//        @Override
//        public int getPosition() {
//            return 402050;
//        }
//    }
//
//    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 403000)
//    public static class DecompositionTestsFactory extends ItemFactory<ModelBasedUI.Information> {
//
//        public DecompositionTestsFactory() {
//            super(DECOMPOSITION_TESTS, new DefaultInformationExtractor<TramoSeatsDocument, ModelBasedUI.Information>() {
//                @Override
//                public ModelBasedUI.Information retrieve(TramoSeatsDocument source) {
//                    SeatsResults rslt = source.getDecompositionPart();
//                    if (rslt == null) {
//                        return null;
//                    }
//                    ModelBasedUI.Information info = new ModelBasedUI.Information();
//                    info.decomposition = rslt.getComponents();
//                    info.ucm = rslt.getUcarimaModel();
//                    info.err = rslt.getModel().getSer();
//                    return info;
//                }
//            }, new ModelBasedUI());
//        }
//
//        @Override
//        public int getPosition() {
//            return 403000;
//        }
//    }
//
//    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 403010)
//    public static class StationaryVarianceDecompositionFactory extends ItemFactory<StationaryVarianceDecomposition> {
//
//        public StationaryVarianceDecompositionFactory() {
//            super(DECOMPOSITION_VAR, stvarExtractor(), new StvarUI());
//        }
//
//        @Override
//        public int getPosition() {
//            return 403010;
//        }
//    }
//
//    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 403005)
//    public static class SignificantSeasonalityFactory extends ItemFactory<CompositeResults> {
//
//        public SignificantSeasonalityFactory() {
//            super(DECOMPOSITION_SIGSEAS, saExtractor(), new SigSeasUI());
//        }
//
//        @Override
//        public int getPosition() {
//            return 403005;
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="DIAGNOSTICS">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5000)
    public static class DiagnosticsSummaryFactory extends ProcDocumentItemFactory<TramoSeatsDocument, HtmlElement> {

        public DiagnosticsSummaryFactory() {
            super(TramoSeatsDocument.class, SaViews.DIAGNOSTICS_SUMMARY, (TramoSeatsDocument doc) -> {
                TramoSeatsResults rslt = doc.getResult();
                if (rslt == null) {
                    return null;
                }
                SaProcessingFactory factory = SaManager.factoryFor(doc.getSpecification());
                List<ProcDiagnostic> diags = new ArrayList<>();
                factory.fillDiagnostics(diags, rslt);
                return new HtmlDiagnosticsSummary(diags);
            }, new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 5000;
        }
    }

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
