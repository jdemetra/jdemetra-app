/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.advanced.ui;

import com.google.common.collect.Iterables;
import ec.ui.view.tsprocessing.WkFinalEstimatorsUI;
import ec.ui.view.tsprocessing.WkInformation;
import ec.ui.view.tsprocessing.WkComponentsUI;
import ec.ui.view.tsprocessing.UcarimaUI;
import ec.ui.view.tsprocessing.ArimaUI;
import ec.satoolkit.ComponentDescriptor;
import ec.satoolkit.seats.SeatsResults;
import ec.satoolkit.special.GeneralizedAirlineResults;
import ec.satoolkit.special.GeneralizedAirlineSpecification;
import ec.tss.html.IHtmlElement;
import ec.tss.html.implementation.HtmlGeneralizedAirline;
import ec.tss.sa.documents.GeneralizedAirlineDocument;
import ec.tstoolkit.algorithm.CompositeResults;
import ec.tstoolkit.arima.IArimaModel;
import ec.tstoolkit.arima.estimation.RegArimaEstimation;
import ec.tstoolkit.arima.special.GeneralizedAirlineModel;
import ec.tstoolkit.modelling.ModellingDictionary;
import ec.tstoolkit.sarima.SarimaModel;
import ec.tstoolkit.stats.NiidTests;
import ec.tstoolkit.timeseries.analysis.SlidingSpans;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.ucarima.UcarimaModel;
import ec.tstoolkit.ucarima.WienerKolmogorovEstimators;
import ec.tstoolkit.utilities.*;
import ec.ui.view.tsprocessing.*;
import ec.ui.view.tsprocessing.sa.SaDocumentViewFactory;
import static ec.ui.view.tsprocessing.sa.SaDocumentViewFactory.MAIN_CHARTS_LOW;
import static ec.ui.view.tsprocessing.sa.SaDocumentViewFactory.saExtractor;
import static ec.ui.view.tsprocessing.sa.SaDocumentViewFactory.ssExtractor;
import ec.ui.view.tsprocessing.sa.SaTableUI;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicReference;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author pcuser
 */
public class GeneralizedAirlineViewFactory extends SaDocumentViewFactory<GeneralizedAirlineSpecification, GeneralizedAirlineDocument> {

    public static final String SELECTION = "Selection", STOCHASTIC = "Stochastic series",
            MODELBASED = "Model-based tests",
            WKANALYSIS = "WK analysis",
            WK_COMPONENTS = "Components",
            WK_FINALS = "Final estimators",
            WK_PRELIMINARY = "Preliminary estimators",
            WK_ERRORS = "Errors analysis";
    public static final Id DECOMPOSITION_SUMMARY = new LinearId(DECOMPOSITION);
    public static final Id DECOMPOSITION_SERIES = new LinearId(DECOMPOSITION, STOCHASTIC);
    public static final Id DECOMPOSITION_WK_COMPONENTS = new LinearId(DECOMPOSITION, WKANALYSIS, WK_COMPONENTS);
    public static final Id DECOMPOSITION_WK_FINALS = new LinearId(DECOMPOSITION, WKANALYSIS, WK_FINALS);
    private static final Id MODEL_SELECTION = new LinearId(MODEL, SELECTION);
    private static final AtomicReference<IProcDocumentViewFactory<GeneralizedAirlineDocument>> INSTANCE = new AtomicReference(new GeneralizedAirlineViewFactory());

    public static IProcDocumentViewFactory<GeneralizedAirlineDocument> getDefault() {
        return INSTANCE.get();
    }

    public static void setDefault(IProcDocumentViewFactory<GeneralizedAirlineDocument> factory) {
        INSTANCE.set(factory);
    }

    public static InformationExtractor<GeneralizedAirlineDocument, WkInformation> wkExtractor() {
        return WkExtractor.INSTANCE;
    }

    public static InformationExtractor<GeneralizedAirlineDocument, TsData> garesExtractor() {
        return GaresExtractor.INSTANCE;
    }

    public GeneralizedAirlineViewFactory() {
        registerDefault();
        registerFromLookup(GeneralizedAirlineDocument.class);
    }

    @Override
    public Id getPreferredView() {
        return MAIN_CHARTS_LOW;
    }

    @Deprecated
    public void registerDefault() {
        registerMainViews();
        registerLightPreprocessingViews();
        registerGeneralizedAirlineViews();
        registerBenchmarkingView();
        registerDiagnosticsViews();
    }

    //<editor-fold defaultstate="collapsed" desc="REGISTER MAIN VIEWS">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000 + 1000)
    public static class MainChartsLowFactory extends SaDocumentViewFactory.MainChartsLowFactory<GeneralizedAirlineDocument> {

        public MainChartsLowFactory() {
            super(GeneralizedAirlineDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000 + 2000)
    public static class MainChartsHighFactory extends SaDocumentViewFactory.MainChartsHighFactory<GeneralizedAirlineDocument> {

        public MainChartsHighFactory() {
            super(GeneralizedAirlineDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000 + 3000)
    public static class MainTableFactory extends SaDocumentViewFactory.MainTableFactory<GeneralizedAirlineDocument> {

        public MainTableFactory() {
            super(GeneralizedAirlineDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER SI VIEW">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000 + 4000)
    public static class MainSiFactory extends SaDocumentViewFactory.MainSiFactory<GeneralizedAirlineDocument> {

        public MainSiFactory() {
            super(GeneralizedAirlineDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER LIGHT PREPROCESSING VIEWS">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 200000 + 1000)
    public static class PreprocessingSummaryFactory extends SaDocumentViewFactory.PreprocessingSummaryFactory<GeneralizedAirlineDocument> {

        public PreprocessingSummaryFactory() {
            super(GeneralizedAirlineDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 200000 + 2000)
    public static class PreprocessingRegsFactory extends SaDocumentViewFactory.PreprocessingRegsFactory<GeneralizedAirlineDocument> {

        public PreprocessingRegsFactory() {
            super(GeneralizedAirlineDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 200000 + 3000)
    public static class PreprocessingDetFactory extends SaDocumentViewFactory.PreprocessingDetFactory<GeneralizedAirlineDocument> {

        public PreprocessingDetFactory() {
            super(GeneralizedAirlineDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER GENERALIZES AIRLINE">
    @Deprecated
    public void registerGeneralizedAirlineViews() {
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 1000)
    public static class ModelSelectionFactory extends ItemFactory<GeneralizedAirlineResults> {

        public ModelSelectionFactory() {
            super(MODEL_SELECTION, new DefaultInformationExtractor<GeneralizedAirlineDocument, GeneralizedAirlineResults>() {
                @Override
                public GeneralizedAirlineResults retrieve(GeneralizedAirlineDocument source) {
                    return source.getDecompositionPart();
                }
            }, new HtmlItemUI<View, GeneralizedAirlineResults>() {
                @Override
                public IHtmlElement getHtmlElement(View host, GeneralizedAirlineResults information) {
                    int n = information.getResultsCount();
                    RegArimaEstimation<GeneralizedAirlineModel> best = information.getBestResult();
                    RegArimaEstimation<?>[] models = new RegArimaEstimation<?>[n];
                    int ibest = 0;
                    for (int i = 0; i < n; ++i) {
                        RegArimaEstimation<GeneralizedAirlineModel> cur = information.getResult(i);
                        if (cur == best) {
                            ibest = i;
                        }
                        models[i] = cur;
                    }
                    return new HtmlGeneralizedAirline((RegArimaEstimation<GeneralizedAirlineModel>[]) models, ibest, information.getUcarimaModel() != null);
                }
            });
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 2000)
    public static class ModelArimaFactory extends ItemFactory<LinkedHashMap<String, IArimaModel>> {

        public ModelArimaFactory() {
            super(MODEL_ARIMA, new DefaultInformationExtractor<GeneralizedAirlineDocument, LinkedHashMap<String, IArimaModel>>() {
                @Override
                public LinkedHashMap<String, IArimaModel> retrieve(GeneralizedAirlineDocument source) {
                    LinkedHashMap<String, IArimaModel> models = new LinkedHashMap<>();
                    if (source.getPreprocessingPart() != null) {
                        SarimaModel tmodel = source.getPreprocessingPart().estimation.getRegArima().getArima();
                        models.put("Preprocessing model", tmodel);
                    }
                    GeneralizedAirlineModel mmodel = source.getDecompositionPart().getBestResult().model.getArima();
                    models.put("Generalized airline model", mmodel);
                    return models;
                }
            }, new ArimaUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 3000)
    public static class ModelResFactory extends ItemFactory<TsData> {

        public ModelResFactory() {
            super(MODEL_RES, garesExtractor(), new ResidualsUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 4000)
    public static class ModelResStatsFactory extends ItemFactory<NiidTests> {

        public ModelResStatsFactory() {
            super(MODEL_RES_STATS, new DefaultInformationExtractor<GeneralizedAirlineDocument, NiidTests>() {
                @Override
                public NiidTests retrieve(GeneralizedAirlineDocument source) {
                    TsData res = source.getDecompositionPart().getResiduals();
                    int np = source.getPreprocessingPart() != null
                            ? source.getPreprocessingPart().description.getArimaComponent().getFreeParametersCount()
                            : 0;
                    return new NiidTests(res.getValues(), res.getFrequency().intValue(), np, true);
                }
            }, new ResidualsStatsUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 5000)
    public static class ModelResDistFactory extends ItemFactory<TsData> {

        public ModelResDistFactory() {
            super(MODEL_RES_DIST, garesExtractor(), new ResidualsDistUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 6000)
    public static class DecompositionSummaryFactory extends ItemFactory<UcarimaUI.Information> {

        public DecompositionSummaryFactory() {
            super(DECOMPOSITION_SUMMARY, new DefaultInformationExtractor<GeneralizedAirlineDocument, UcarimaUI.Information>() {
                @Override
                public UcarimaUI.Information retrieve(GeneralizedAirlineDocument source) {
                    GeneralizedAirlineResults ga = source.getDecompositionPart();
                    if (ga == null) {
                        return null;
                    }
                    UcarimaModel ucm = ga.getUcarimaModel();
                    if (ucm == null) {
                        return null;
                    }
                    UcarimaUI.Information info = new UcarimaUI.Information();
                    info.model = ucm.getModel();
                    info.names = SeatsResults.getComponentsName(ucm);
                    info.cmps = SeatsResults.getComponents(ucm);
                    return info;
                }
            }, new UcarimaUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 7000)
    public static class DecomposotionSeriesFactory extends ItemFactory<CompositeResults> {

        public DecomposotionSeriesFactory() {
            super(DECOMPOSITION_SERIES, saExtractor(), new SaTableUI(ModellingDictionary.getStochasticSeries(), null));
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 8000)
    public static class DecompositionWkComponentsFactory extends ItemFactory<WkInformation> {

        public DecompositionWkComponentsFactory() {
            super(DECOMPOSITION_WK_COMPONENTS, wkExtractor(), new WkComponentsUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 9000)
    public static class DecompositionWkFinalsFactory extends ItemFactory<WkInformation> {

        public DecompositionWkFinalsFactory() {
            super(DECOMPOSITION_WK_FINALS, wkExtractor(), new WkFinalEstimatorsUI());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER BENCHMARKING VIEW">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 400000 + 1000)
    public static class BenchmarkingFactory extends SaDocumentViewFactory.BenchmarkingFactory<GeneralizedAirlineDocument> {

        public BenchmarkingFactory() {
            super(GeneralizedAirlineDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER DIAGNOSTICS VIEWS">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 500000 + 1000)
    public static class DiagnosticsSummaryFactory extends SaDocumentViewFactory.DiagnosticsSummaryFactory<GeneralizedAirlineDocument> {

        public DiagnosticsSummaryFactory() {
            super(GeneralizedAirlineDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 500000 + 2000)
    public static class DiagnosticsSpectrumResFactory extends SaDocumentViewFactory.DiagnosticsSpectrumResFactory<GeneralizedAirlineDocument> {

        public DiagnosticsSpectrumResFactory() {
            super(GeneralizedAirlineDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 500000 + 3000)
    public static class DiagnosticsSpectrumIFactory extends SaDocumentViewFactory.DiagnosticsSpectrumIFactory<GeneralizedAirlineDocument> {

        public DiagnosticsSpectrumIFactory() {
            super(GeneralizedAirlineDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 500000 + 4000)
    public static class DiagnosticsSpectrumSaFactory extends SaDocumentViewFactory.DiagnosticsSpectrumSaFactory<GeneralizedAirlineDocument> {

        public DiagnosticsSpectrumSaFactory() {
            super(GeneralizedAirlineDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER SEASONALITY VIEW">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 500000 + 5000)
    public static class DiagnosticsSeasonalityFactory extends SaDocumentViewFactory.DiagnosticsSeasonalityFactory<GeneralizedAirlineDocument> {

        public DiagnosticsSeasonalityFactory() {
            super(GeneralizedAirlineDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER SLIDING SPANS VIEW">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 500000 + 6000)
    public static class DiagnosticsSlidingTdFactory extends SaDocumentViewFactory.DiagnosticsSlidingTdFactory<GeneralizedAirlineDocument> {

        public DiagnosticsSlidingTdFactory() {
            super(GeneralizedAirlineDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 500000 + 7000)
    public static class DiagnosticsSlidingSaFactory extends SaDocumentViewFactory.DiagnosticsSlidingSaFactory<GeneralizedAirlineDocument> {

        public DiagnosticsSlidingSaFactory() {
            super(GeneralizedAirlineDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 500000 + 8000)
    public static class DiagnosticsSlidingSummaryFactory extends ItemFactory<SlidingSpans> {

        public DiagnosticsSlidingSummaryFactory() {
            super(DIAGNOSTICS_SLIDING_SUMMARY, ssExtractor(), new SlidingSpansUI(ModellingDictionary.S, ModellingDictionary.SI_CMP));
        }
    }

    //@ServiceProvider(service = ProcDocumentItemFactory.class, position = 500000 + 9000)
    public static class DiagnosticsSlidingSeasFactory extends ItemFactory<SlidingSpans> {

        public DiagnosticsSlidingSeasFactory() {
            super(DIAGNOSTICS_SLIDING_SEAS, ssExtractor(), new SlidingSpansDetailUI(ModellingDictionary.S_CMP));
        }
    }
    //</editor-fold>

    private static class ItemFactory<I> extends ComposedProcDocumentItemFactory<GeneralizedAirlineDocument, I> {

        public ItemFactory(Id itemId, InformationExtractor<? super GeneralizedAirlineDocument, I> informationExtractor, ItemUI<? extends IProcDocumentView<GeneralizedAirlineDocument>, I> itemUI) {
            super(GeneralizedAirlineDocument.class, itemId, informationExtractor, itemUI);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="EXTRACTORS IMPL">
    private static class WkExtractor extends DefaultInformationExtractor<GeneralizedAirlineDocument, WkInformation> {

        private static final WkExtractor INSTANCE = new WkExtractor();

        @Override
        public WkInformation retrieve(GeneralizedAirlineDocument source) {
            GeneralizedAirlineResults ga = source.getDecompositionPart();
            if (ga == null) {
                return null;
            }
            WienerKolmogorovEstimators wk = ga.getWienerKolmogorovEstimators();
            if (wk == null) {
                return null;
            }
            WkInformation information = new WkInformation();
            information.descriptors = Iterables.toArray(SeatsResults.airlineDescriptors, ComponentDescriptor.class);
            information.estimators = wk;
            information.frequency = source.getSeries().getFrequency();
            return information;
        }
    };

    private static class GaresExtractor extends DefaultInformationExtractor<GeneralizedAirlineDocument, TsData> {

        private static final GaresExtractor INSTANCE = new GaresExtractor();

        @Override
        public TsData retrieve(GeneralizedAirlineDocument source) {
            GeneralizedAirlineResults ga = source.getDecompositionPart();
            if (ga == null) {
                return null;
            }
            return ga.getResiduals();
        }
    };
    //</editor-fold>
}
