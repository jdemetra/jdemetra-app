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
import demetra.desktop.sa.ui.DemetraSaUI;
import demetra.desktop.sa.ui.SaViews;
import demetra.desktop.sa.ui.WkComponentsUI;
import demetra.desktop.sa.ui.WkInformation;
import demetra.desktop.sa.ui.WkFinalEstimatorsUI;
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
import demetra.html.HtmlElements;
import demetra.html.HtmlFragment;
import demetra.html.HtmlHeader;
import demetra.html.core.HtmlDiagnosticsSummary;
import demetra.information.InformationSet;
import demetra.modelling.ComponentInformation;
import demetra.modelling.ModellingDictionary;
import demetra.modelling.SeriesInfo;
import demetra.processing.ProcDiagnostic;
import demetra.sa.ComponentDescriptor;
import demetra.sa.ComponentType;
import demetra.sa.SaDictionaries;
import demetra.sa.SaManager;
import demetra.sa.SaProcessingFactory;
import demetra.sa.SeriesDecomposition;
import demetra.sa.StationaryVarianceDecomposition;
import demetra.sa.html.HtmlSeasonalityDiagnostics;
import demetra.sa.html.HtmlSignificantSeasons;
import demetra.sa.html.HtmlStationaryVarianceDecomposition;
import demetra.timeseries.TsData;
import demetra.timeseries.TsDocument;
import demetra.toolkit.dictionaries.Dictionary;
import demetra.toolkit.dictionaries.RegressionDictionaries;
import demetra.tramoseats.io.html.HtmlModelBasedRevisionsAnalysis;
import demetra.tramoseats.io.html.HtmlSeatsGrowthRates;
import demetra.tramoseats.io.html.HtmlWienerKolmogorovDiagnostics;
import demetra.tramoseats.io.information.TramoSeatsSpecMapping;
import demetra.util.Id;
import demetra.util.LinearId;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import jdplus.regsarima.regular.RegSarimaModel;
import jdplus.sa.diagnostics.SignificantSeasonalityTest;
import jdplus.sa.tests.SeasonalityTests;
import jdplus.seats.SeatsResults;
import jdplus.tramoseats.TramoSeatsDiagnostics;
import jdplus.tramoseats.TramoSeatsResults;
import jdplus.tramoseats.TramoSeatsDocument;
import jdplus.ucarima.UcarimaModel;
import jdplus.ucarima.WienerKolmogorovDiagnostics;
import jdplus.ucarima.WienerKolmogorovEstimators;
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

    private final static Function<TramoSeatsDocument, TramoSeatsDiagnostics> DIAGSEXTRACTOR = source -> {
        TramoSeatsResults tr = source.getResult();
        return tr == null ? null : tr.getDiagnostics();
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
                    = new EstimationUI.Information(s, null, s.fastFn(es, (a, b) -> a + b * 1.96), s.fastFn(es, (a, b) -> a - b * 1.96));
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

//<editor-fold defaultstate="collapsed" desc="SEATS">
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
                                SeatsResults.getComponents(ucm),
                                SeatsResults.getComponentsName(ucm));
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

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 4210)
    public static class DecompositionWkComponentsFactory extends ProcDocumentItemFactory<TramoSeatsDocument, WkInformation> {

        public DecompositionWkComponentsFactory() {
            super(TramoSeatsDocument.class, DECOMPOSITION_WK_COMPONENTS, DECOMPOSITIONEXTRACTOR.andThen(
                    (SeatsResults seats) -> {
                        if (seats == null) {
                            return null;
                        }
                        ComponentDescriptor[] descriptors = SeatsResults.descriptors.toArray(ComponentDescriptor[]::new);
                        WienerKolmogorovEstimators estimators = new WienerKolmogorovEstimators(seats.getUcarimaModel());
                        int period = seats.getOriginalModel().getPeriod();
                        return new WkInformation(estimators, descriptors, period);
                    }),
                     new WkComponentsUI());
        }

        @Override
        public int getPosition() {
            return 4210;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 4220)
    public static class DecompositionWkFinalFactory extends ProcDocumentItemFactory<TramoSeatsDocument, WkInformation> {

        public DecompositionWkFinalFactory() {
            super(TramoSeatsDocument.class, DECOMPOSITION_WK_FINALS, DECOMPOSITIONEXTRACTOR.andThen(
                    (SeatsResults seats) -> {
                        if (seats == null) {
                            return null;
                        }
                        ComponentDescriptor[] descriptors = SeatsResults.descriptors.toArray(ComponentDescriptor[]::new);
                        WienerKolmogorovEstimators estimators = new WienerKolmogorovEstimators(seats.getUcarimaModel());
                        int period = seats.getOriginalModel().getPeriod();
                        return new WkInformation(estimators, descriptors, period);
                    }),
                     new WkFinalEstimatorsUI());
        }

        @Override
        public int getPosition() {
            return 4220;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 4300)
    public static class DecompositionWkErrorsFactory extends ProcDocumentItemFactory<TramoSeatsDocument, HtmlElement> {

        public DecompositionWkErrorsFactory() {
            super(TramoSeatsDocument.class, DECOMPOSITION_ERRORS, DECOMPOSITIONEXTRACTOR.andThen(
                    (SeatsResults seats) -> {
                        if (seats == null) {
                            return null;
                        }
                        try {
                            ComponentDescriptor[] descriptors = SeatsResults.airlineDescriptors.toArray(ComponentDescriptor[]::new);
                            WienerKolmogorovEstimators estimators = new WienerKolmogorovEstimators(seats.getCompactUcarimaModel());
                            int period = seats.getOriginalModel().getPeriod();
                            return new HtmlModelBasedRevisionsAnalysis(period, estimators, descriptors);
                        } catch (Exception err) {
                        }
                        return new HtmlFragment("Unable to compute model-based diagnostics");
                    }
            ), new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 4300;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 4310)
    public static class DecompositionGrowthFactory extends ProcDocumentItemFactory<TramoSeatsDocument, HtmlElement> {

        public DecompositionGrowthFactory() {
            super(TramoSeatsDocument.class, DECOMPOSITION_RATES, DECOMPOSITIONEXTRACTOR.andThen(
                    (SeatsResults seats) -> {
                        if (seats == null) {
                            return null;
                        }
                        return new HtmlSeatsGrowthRates(seats);
                    }
            ), new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 4310;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 4320)
    public static class DecompositionTestsFactory extends ProcDocumentItemFactory<TramoSeatsDocument, HtmlElement> {

        public DecompositionTestsFactory() {
            super(TramoSeatsDocument.class, DECOMPOSITION_TESTS, DECOMPOSITIONEXTRACTOR.andThen(
                    (SeatsResults seats) -> {
                        if (seats == null) {
                            return null;
                        }
                        try {
                            SeriesDecomposition decomposition = seats.getInitialComponents();
                            UcarimaModel ucm = seats.getCompactUcarimaModel();
                            String[] desc = new String[]{"Trend", "Seasonally adjusted", "Seasonal", "Irregular"};
                            int[] cmps = new int[]{1, -2, 2, 3};
                            boolean[] signals = new boolean[]{true, false, true, true};
                            double err = Math.sqrt(seats.getInnovationVariance());
                            TsData t = decomposition.getSeries(ComponentType.Trend, ComponentInformation.Value);
                            TsData s = decomposition.getSeries(ComponentType.Seasonal, ComponentInformation.Value);
                            TsData i = decomposition.getSeries(ComponentType.Irregular, ComponentInformation.Value);
                            TsData sa = decomposition.getSeries(ComponentType.SeasonallyAdjusted, ComponentInformation.Value);

                            double[][] data = new double[][]{
                                t.getValues().toArray(),
                                sa == null ? null : sa.getValues().toArray(),
                                s == null ? null : s.getValues().toArray(),
                                i == null ? null : i.getValues().toArray()
                            };
                            WienerKolmogorovDiagnostics diags = WienerKolmogorovDiagnostics.make(ucm, err, data, cmps);
                            if (diags != null) {
                                return new HtmlWienerKolmogorovDiagnostics(diags, desc, signals, t.getAnnualFrequency());
                            }
                        } catch (Exception err) {
                        }
                        return new HtmlFragment("Unable to compute model-based diagnostics");
                    }), new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 4320;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 4900)
    public static class SignificantSeasonalityFactory extends ProcDocumentItemFactory<TramoSeatsDocument, HtmlElement> {

        public SignificantSeasonalityFactory() {
            super(TramoSeatsDocument.class, DECOMPOSITION_SIGSEAS, DECOMPOSITIONEXTRACTOR.andThen(
                    (SeatsResults seats) -> {
                        if (seats == null) {
                            return null;
                        }
                        TsData s = seats.getInitialComponents().getSeries(ComponentType.Seasonal, ComponentInformation.Value);
                        TsData es = seats.getInitialComponents().getSeries(ComponentType.Seasonal, ComponentInformation.Stdev);
                        TsData fs = seats.getInitialComponents().getSeries(ComponentType.Seasonal, ComponentInformation.Forecast);
                        TsData fes = seats.getInitialComponents().getSeries(ComponentType.Seasonal, ComponentInformation.StdevForecast);
                        int[] test99 = SignificantSeasonalityTest.test(s, es, fs, fes, 0.01);
                        int[] test95 = SignificantSeasonalityTest.test(s, es, fs, fes, 0.05);
                        return new HtmlSignificantSeasons(test99, test95);
                    }
            ), new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 4900;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 4910)
    public static class StationaryVarianceDecompositionFactory extends ProcDocumentItemFactory<TramoSeatsDocument, HtmlElement> {

        public StationaryVarianceDecompositionFactory() {
            super(TramoSeatsDocument.class, DECOMPOSITION_VAR, DIAGSEXTRACTOR.andThen(
                    (TramoSeatsDiagnostics diags) -> {
                        StationaryVarianceDecomposition decomp = diags.getVarianceDecomposition();
                        if (decomp == null) {
                            return null;
                        }
                        return new HtmlStationaryVarianceDecomposition(decomp);
                    }),
                    new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 4910;
        }
    }
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

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5010)
    public static class OriginalSeasonalityFactory extends ProcDocumentItemFactory<TramoSeatsDocument, HtmlElement> {

        public OriginalSeasonalityFactory() {
            super(TramoSeatsDocument.class, SaViews.DIAGNOSTICS_OSEASONALITY, (TramoSeatsDocument doc) -> {
                TramoSeatsResults rslt = doc.getResult();
                if (rslt == null) {
                    return null;
                }
                TsData s = rslt.getPreprocessing().transformedSeries();
                if (s == null) {
                    return null;
                }
                return new HtmlElements(new HtmlHeader(1, "Original [transformed] series", true),
                        new HtmlSeasonalityDiagnostics(SeasonalityTests.seasonalityTest(s.getValues(), s.getAnnualFrequency(), 1, true, true), false));

            }, new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 5010;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5020)
    public static class LinSeasonalityFactory extends ProcDocumentItemFactory<TramoSeatsDocument, HtmlElement> {

        public LinSeasonalityFactory() {
            super(TramoSeatsDocument.class, SaViews.DIAGNOSTICS_LSEASONALITY, (TramoSeatsDocument doc) -> {
                TramoSeatsResults rslt = doc.getResult();
                if (rslt == null) {
                    return null;
                }
                TsData s = rslt.getPreprocessing().linearizedSeries();
                if (s == null) {
                    return null;
                }
                return new HtmlElements(new HtmlHeader(1, "Linearized series", true),
                        new HtmlSeasonalityDiagnostics(SeasonalityTests.seasonalityTest(s.getValues(), s.getAnnualFrequency(), 1, true, true), false));

            }, new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 5020;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5030)
    public static class ResSeasonalityFactory extends ProcDocumentItemFactory<TramoSeatsDocument, HtmlElement> {

        public ResSeasonalityFactory() {
            super(TramoSeatsDocument.class, SaViews.DIAGNOSTICS_RSEASONALITY, (TramoSeatsDocument doc) -> {
                TramoSeatsResults rslt = doc.getResult();
                if (rslt == null) {
                    return null;
                }
                TsData s = rslt.getPreprocessing().fullResiduals();
                if (s == null) {
                    return null;
                }
                return new HtmlElements(new HtmlHeader(1, "Full residuals", true),
                        new HtmlSeasonalityDiagnostics(SeasonalityTests.seasonalityTest(s.getValues(), s.getAnnualFrequency(), 0, false, true), true));

            }, new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 5030;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5040)
    public static class SaSeasonalityFactory extends ProcDocumentItemFactory<TramoSeatsDocument, HtmlElement> {

        public SaSeasonalityFactory() {
            super(TramoSeatsDocument.class, SaViews.DIAGNOSTICS_SASEASONALITY, (TramoSeatsDocument doc) -> {
                TramoSeatsResults rslt = doc.getResult();
                if (rslt == null) {
                    return null;
                }
                TsData s = rslt.getDecomposition().getInitialComponents().getSeries(ComponentType.SeasonallyAdjusted, ComponentInformation.Value);
                if (s == null) {
                    return null;
                }
                return new HtmlElements(new HtmlHeader(1, "[Linearized] seasonally adjusted series", true),
                        new HtmlSeasonalityDiagnostics(SeasonalityTests.seasonalityTest(s.getValues(), s.getAnnualFrequency(), 1, true, true), true));

            }, new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 5030;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5050)
    public static class IrrSeasonalityFactory extends ProcDocumentItemFactory<TramoSeatsDocument, HtmlElement> {

        public IrrSeasonalityFactory() {
            super(TramoSeatsDocument.class, SaViews.DIAGNOSTICS_ISEASONALITY, (TramoSeatsDocument doc) -> {
                TramoSeatsResults rslt = doc.getResult();
                if (rslt == null) {
                    return null;
                }
                TsData s = rslt.getDecomposition().getInitialComponents().getSeries(ComponentType.Irregular, ComponentInformation.Value);
                if (s == null) {
                    return null;
                }
                return new HtmlElements(new HtmlHeader(1, "[Linearized] irregular component", true),
                        new HtmlSeasonalityDiagnostics(SeasonalityTests.seasonalityTest(s.getValues(), s.getAnnualFrequency(), 0, false, true), true));

            }, new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 5050;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5060)
    public static class LastResSeasonalityFactory extends ProcDocumentItemFactory<TramoSeatsDocument, HtmlElement> {

        public LastResSeasonalityFactory() {
            super(TramoSeatsDocument.class, SaViews.DIAGNOSTICS_LASTRSEASONALITY, (TramoSeatsDocument doc) -> {
                TramoSeatsResults rslt = doc.getResult();
                if (rslt == null) {
                    return null;
                }
                TsData s = rslt.getPreprocessing().fullResiduals();
                if (s == null) {
                    return null;
                }
                StringBuilder header = new StringBuilder().append("Full residuals");
                int ny = DemetraSaUI.get().getSeasonalityLength();
                if (ny > 0) {
                    s = s.drop(Math.max(0, s.length() - s.getAnnualFrequency() * ny), 0);
                    header.append(" (last ").append(ny).append(" years)");
                }
                return new HtmlElements(new HtmlHeader(1, header.toString(), true),
                        new HtmlSeasonalityDiagnostics(SeasonalityTests.seasonalityTest(s.getValues(), s.getAnnualFrequency(), 0, false, true), true));

            }, new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 5060;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5070)
    public static class LastSaSeasonalityFactory extends ProcDocumentItemFactory<TramoSeatsDocument, HtmlElement> {

        public LastSaSeasonalityFactory() {
            super(TramoSeatsDocument.class, SaViews.DIAGNOSTICS_LASTSASEASONALITY, (TramoSeatsDocument doc) -> {
                TramoSeatsResults rslt = doc.getResult();
                if (rslt == null) {
                    return null;
                }
                TsData s = rslt.getDecomposition().getInitialComponents().getSeries(ComponentType.SeasonallyAdjusted, ComponentInformation.Value);
                if (s == null) {
                    return null;
                }
                StringBuilder header = new StringBuilder().append("[Linearized] seasonally adjusted series");
                int ny = DemetraSaUI.get().getSeasonalityLength();
                if (ny > 0) {
                    s = s.drop(Math.max(0, s.length() - s.getAnnualFrequency() * ny - 1), 0);
                    header.append(" (last ").append(ny).append(" years)");
                }
                return new HtmlElements(new HtmlHeader(1, header.toString(), true),
                        new HtmlSeasonalityDiagnostics(SeasonalityTests.seasonalityTest(s.getValues(), s.getAnnualFrequency(), 1, true, true), true));

            }, new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 5070;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5080)
    public static class LastIrrSeasonalityFactory extends ProcDocumentItemFactory<TramoSeatsDocument, HtmlElement> {

        public LastIrrSeasonalityFactory() {
            super(TramoSeatsDocument.class, SaViews.DIAGNOSTICS_LASTISEASONALITY, (TramoSeatsDocument doc) -> {
                TramoSeatsResults rslt = doc.getResult();
                if (rslt == null) {
                    return null;
                }
                TsData s = rslt.getDecomposition().getInitialComponents().getSeries(ComponentType.Irregular, ComponentInformation.Value);
                if (s == null) {
                    return null;
                }
                StringBuilder header = new StringBuilder().append("[Linearized] irregular component");
                int ny = DemetraSaUI.get().getSeasonalityLength();
                if (ny > 0) {
                    s = s.drop(Math.max(0, s.length() - s.getAnnualFrequency() * ny), 0);
                    header.append(" (last ").append(ny).append(" years)");
                }
                return new HtmlElements(new HtmlHeader(1, header.toString(), true),
                        new HtmlSeasonalityDiagnostics(SeasonalityTests.seasonalityTest(s.getValues(), s.getAnnualFrequency(), 0, false, true), true));

            }, new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 5080;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5310)
    public static class ModelResSpectrum extends ProcDocumentItemFactory<TramoSeatsDocument, TsData> {

        public ModelResSpectrum() {
            super(TramoSeatsDocument.class, SaViews.DIAGNOSTICS_SPECTRUM_RES,
                    RESEXTRACTOR.andThen(
                            (TsData s) -> {
                                if (s == null) {
                                    return null;
                                }
                                int ny = DemetraSaUI.get().getSpectralLastYears();
                                if (ny > 0) {
                                    s = s.drop(Math.max(0, s.length() - s.getAnnualFrequency() * ny), 0);
                                }
                                return s;
                            }
                    ),
                    new SpectrumUI(true));

        }

        @Override
        public int getPosition() {
            return 5310;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5320)
    public static class DiagnosticsSpectrumIFactory extends ProcDocumentItemFactory<TramoSeatsDocument, TsData> {

        public DiagnosticsSpectrumIFactory() {
            super(TramoSeatsDocument.class, SaViews.DIAGNOSTICS_SPECTRUM_I,
                    DECOMPOSITIONEXTRACTOR.andThen(
                            (SeatsResults seats) -> {
                                if (seats == null) {
                                    return null;
                                }
                                TsData s = seats.getInitialComponents().getSeries(ComponentType.Irregular, ComponentInformation.Value);
                                if (s == null) {
                                    return null;
                                }
                                int ny = DemetraSaUI.get().getSpectralLastYears();
                                if (ny > 0) {
                                    s = s.drop(Math.max(0, s.length() - s.getAnnualFrequency() * ny), 0);
                                }
                                return s;
                            }
                    ),
                    new SpectrumUI(false));
        }

        @Override
        public int getPosition() {
            return 5320;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5330)
    public static class DiagnosticsSpectrumSaFactory extends ProcDocumentItemFactory<TramoSeatsDocument, TsData> {

        public DiagnosticsSpectrumSaFactory() {
            super(TramoSeatsDocument.class, SaViews.DIAGNOSTICS_SPECTRUM_SA,
                    DECOMPOSITIONEXTRACTOR.andThen(
                            (SeatsResults seats) -> {
                                if (seats == null) {
                                    return null;
                                }
                                TsData s = seats.getInitialComponents().getSeries(ComponentType.SeasonallyAdjusted, ComponentInformation.Value);
                                if (s == null) {
                                    return null;
                                }
                                s = s.delta(1);
                                int ny = DemetraSaUI.get().getSpectralLastYears();
                                if (ny > 0) {
                                    s = s.drop(Math.max(0, s.length() - s.getAnnualFrequency() * ny), 0);
                                }
                                return s;
                            }
                    ),
                    new SpectrumUI(false));
        }

        @Override
        public int getPosition() {
            return 5330;
        }
    }
    
    
//</editor-fold>
}
