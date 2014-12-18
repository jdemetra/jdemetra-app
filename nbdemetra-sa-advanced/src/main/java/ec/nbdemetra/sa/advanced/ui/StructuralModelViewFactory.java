/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.advanced.ui;

import com.google.common.collect.Iterables;
import ec.nbdemetra.ui.chart3d.functions.SurfacePlotterUI;
import ec.nbdemetra.ui.chart3d.functions.SurfacePlotterUI.Functions;
import ec.ui.view.tsprocessing.WkFinalEstimatorsUI;
import ec.ui.view.tsprocessing.WkInformation;
import ec.ui.view.tsprocessing.WkComponentsUI;
import ec.ui.view.tsprocessing.UcarimaUI;
import ec.ui.view.tsprocessing.ArimaUI;
import ec.satoolkit.ComponentDescriptor;
import ec.satoolkit.seats.SeatsResults;
import ec.satoolkit.special.StmResults;
import ec.satoolkit.special.StmSpecification;
import ec.tss.html.IHtmlElement;
import ec.tss.html.implementation.HtmlBsm;
import ec.tss.sa.documents.StmDocument;
import ec.tstoolkit.algorithm.CompositeResults;
import ec.tstoolkit.arima.ArimaModel;
import ec.tstoolkit.arima.IArimaModel;
import ec.tstoolkit.modelling.ModellingDictionary;
import ec.tstoolkit.sarima.SarimaModel;
import ec.tstoolkit.stats.NiidTests;
import ec.tstoolkit.timeseries.analysis.SlidingSpans;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.ucarima.UcarimaModel;
import ec.tstoolkit.utilities.*;
import ec.ui.view.tsprocessing.*;
import ec.ui.view.tsprocessing.sa.ModelBasedUI;
import ec.ui.view.tsprocessing.sa.SaDocumentViewFactory;
import static ec.ui.view.tsprocessing.sa.SaDocumentViewFactory.DIAGNOSTICS_SLIDING_SEAS;
import static ec.ui.view.tsprocessing.sa.SaDocumentViewFactory.DIAGNOSTICS_SLIDING_SUMMARY;
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
public class StructuralModelViewFactory extends SaDocumentViewFactory<StmSpecification, StmDocument> {

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
    public static final Id DECOMPOSITION_TESTS = new LinearId(DECOMPOSITION, MODELBASED);
    private static final Id MODEL_SELECTION = new LinearId(MODEL, SELECTION);
    private static final Id MODEL_LIKELIHOOD = new LinearId(MODEL, LIKELIHOOD);
    private static final AtomicReference<IProcDocumentViewFactory<StmDocument>> INSTANCE = new AtomicReference(new StructuralModelViewFactory());

    public static IProcDocumentViewFactory<StmDocument> getDefault() {
        return INSTANCE.get();
    }

    public static void setDefault(IProcDocumentViewFactory<StmDocument> factory) {
        INSTANCE.set(factory);
    }

    public static InformationExtractor<StmDocument, WkInformation> wkExtractor() {
        return WkExtractor.INSTANCE;
    }

    public static InformationExtractor<StmDocument, TsData> stmresExtractor() {
        return StmresExtractor.INSTANCE;
    }

    public StructuralModelViewFactory() {
        registerDefault();
        registerFromLookup(StmDocument.class);
    }

    @Override
    public Id getPreferredView() {
        return MAIN_CHARTS_LOW;
    }

    @Deprecated
    public void registerDefault() {
        registerMainViews();
        registerLightPreprocessingViews();
        registerStmViews();
        registerBenchmarkingView();
        registerDiagnosticsViews();
    }

    //<editor-fold defaultstate="collapsed" desc="REGISTER MAIN VIEWS">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 101010)
    public static class MainChartsLowFactory extends SaDocumentViewFactory.MainChartsLowFactory<StmDocument> {

        public MainChartsLowFactory() {
            super(StmDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 101020)
    public static class MainChartsHighFactory extends SaDocumentViewFactory.MainChartsHighFactory<StmDocument> {

        public MainChartsHighFactory() {
            super(StmDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 102000)
    public static class MainTableFactory extends SaDocumentViewFactory.MainTableFactory<StmDocument> {

        public MainTableFactory() {
            super(StmDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER SI VIEW">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 103000)
    public static class MainSiFactory extends SaDocumentViewFactory.MainSiFactory<StmDocument> {

        public MainSiFactory() {
            super(StmDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER LIGHT PREPROCESSING VIEWS">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 200000)
    public static class PreprocessingSummaryFactory extends SaDocumentViewFactory.PreprocessingSummaryFactory<StmDocument> {

        public PreprocessingSummaryFactory() {
            super(StmDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 201000)
    public static class PreprocessingRegsFactory extends SaDocumentViewFactory.PreprocessingRegsFactory<StmDocument> {

        public PreprocessingRegsFactory() {
            super(StmDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 202000)
    public static class PreprocessingDetFactory extends SaDocumentViewFactory.PreprocessingDetFactory<StmDocument> {

        public PreprocessingDetFactory() {
            super(StmDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 203000)
    public static class LikelihoodFactory extends SaDocumentViewFactory.LikelihoodFactory<StmDocument> {

        public LikelihoodFactory() {
            super(StmDocument.class);
            setAsync(true);
        }
    }
   //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER STM">
    @Deprecated
    public void registerStmViews() {
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 301000)
    public static class ModelSelectionFactory extends ItemFactory<StmResults> {

        public ModelSelectionFactory() {
            super(MODEL_SELECTION, new DefaultInformationExtractor<StmDocument, StmResults>() {
                @Override
                public StmResults retrieve(StmDocument source) {
                    return source.getDecompositionPart();
                }
            }, new HtmlItemUI<View, StmResults>() {
                @Override
                public IHtmlElement getHtmlElement(View host, StmResults information) {
                    return new HtmlBsm(information);
                }
            });
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 302000)
    public static class ModelArimaFactory extends ItemFactory<LinkedHashMap<String, IArimaModel>> {

        public ModelArimaFactory() {
            super(MODEL_ARIMA, new DefaultInformationExtractor<StmDocument, LinkedHashMap<String, IArimaModel>>() {
                @Override
                public LinkedHashMap<String, IArimaModel> retrieve(StmDocument source) {
                    LinkedHashMap<String, IArimaModel> models = new LinkedHashMap<>();
                    if (source.getPreprocessingPart() != null) {
                        SarimaModel tmodel = source.getPreprocessingPart().estimation.getRegArima().getArima();
                        models.put("Preprocessing model", tmodel);
                    }
                    ArimaModel mmodel = (ArimaModel) source.getDecompositionPart().getUcarimaModel().getModel();
                    models.put("Reduced model", mmodel);
                    return models;
                }
            }, new ArimaUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 303000)
    public static class ModelResFactory extends ItemFactory<TsData> {

        public ModelResFactory() {
            super(MODEL_RES, stmresExtractor(), new ResidualsUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 303010)
    public static class ModelResStatsFactory extends ItemFactory<NiidTests> {

        public ModelResStatsFactory() {
            super(MODEL_RES_STATS, new DefaultInformationExtractor<StmDocument, NiidTests>() {
                @Override
                public NiidTests retrieve(StmDocument source) {
                    TsData res = source.getDecompositionPart().getResiduals();
                    int np = source.getPreprocessingPart() != null
                            ? source.getPreprocessingPart().description.getArimaComponent().getFreeParametersCount()
                            : 0;
                    return new NiidTests(res.getValues(), res.getFrequency().intValue(), np, true);
                }
            }, new ResidualsStatsUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 303020)
    public static class ModelResDistFactory extends ItemFactory<TsData> {

        public ModelResDistFactory() {
            super(MODEL_RES_DIST, stmresExtractor(), new ResidualsDistUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 304000)
    public static class StmLikelihoodFactory extends ItemFactory<Functions> {

        public StmLikelihoodFactory() {
            super(MODEL_LIKELIHOOD, LikelihoodExtractor.INSTANCE, new SurfacePlotterUI());
        }

    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 400000)
    public static class DecompositionSummaryFactory extends ItemFactory<UcarimaUI.Information> {

        public DecompositionSummaryFactory() {
            super(DECOMPOSITION_SUMMARY, new DefaultInformationExtractor<StmDocument, UcarimaUI.Information>() {
                @Override
                public UcarimaUI.Information retrieve(StmDocument source) {
                    UcarimaModel ucm = source.getDecompositionPart().getUcarimaModel();
                    UcarimaUI.Information info = new UcarimaUI.Information();
                    info.model = ucm.getModel();
                    info.names = SeatsResults.getComponentsName(ucm);
                    info.cmps = SeatsResults.getComponents(ucm);
                    return info;
                }
            }, new UcarimaUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 401000)
    public static class DecompositionSeriesFactory extends ItemFactory<CompositeResults> {

        public DecompositionSeriesFactory() {
            super(DECOMPOSITION_SERIES, saExtractor(), new SaTableUI(ModellingDictionary.getStochasticSeries(), null));
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 402010)
    public static class DecompositionWkComponentsFactory extends ItemFactory<WkInformation> {

        public DecompositionWkComponentsFactory() {
            super(DECOMPOSITION_WK_COMPONENTS, wkExtractor(), new WkComponentsUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 402020)
    public static class DecompositionWkFinalsFactory extends ItemFactory<WkInformation> {

        public DecompositionWkFinalsFactory() {
            super(DECOMPOSITION_WK_FINALS, wkExtractor(), new WkFinalEstimatorsUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 403000)
    public static class DecompositionTestsFactory extends ItemFactory<ModelBasedUI.Information> {

        public DecompositionTestsFactory() {
            super(DECOMPOSITION_TESTS, new DefaultInformationExtractor<StmDocument, ModelBasedUI.Information>() {
                @Override
                public ModelBasedUI.Information retrieve(StmDocument source) {
                    ModelBasedUI.Information info = new ModelBasedUI.Information();
                    StmResults rslt = source.getDecompositionPart();
                    info.decomposition = rslt.getComponents();
                    info.ucm = rslt.getUcarimaModel();
                    info.err = rslt.getLikelihood().getSer() * rslt.getResidualsScalingFactor();
                    return info;
                }
            }, new ModelBasedUI());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER BENCHMARKING VIEW">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 500000)
    public static class BenchmarkingFactory extends SaDocumentViewFactory.BenchmarkingFactory<StmDocument> {

        public BenchmarkingFactory() {
            super(StmDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER DIAGNOSTICS VIEWS">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 600000)
    public static class DiagnosticsSummaryFactory extends SaDocumentViewFactory.DiagnosticsSummaryFactory<StmDocument> {

        public DiagnosticsSummaryFactory() {
            super(StmDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 602010)
    public static class DiagnosticsSpectrumResFactory extends SaDocumentViewFactory.DiagnosticsSpectrumResFactory<StmDocument> {

        public DiagnosticsSpectrumResFactory() {
            super(StmDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 602020)
    public static class DiagnosticsSpectrumIFactory extends SaDocumentViewFactory.DiagnosticsSpectrumIFactory<StmDocument> {

        public DiagnosticsSpectrumIFactory() {
            super(StmDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 602030)
    public static class DiagnosticsSpectrumSaFactory extends SaDocumentViewFactory.DiagnosticsSpectrumSaFactory<StmDocument> {

        public DiagnosticsSpectrumSaFactory() {
            super(StmDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER SEASONALITY VIEW">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 601000)
    public static class DiagnosticsSeasonalityFactory extends SaDocumentViewFactory.DiagnosticsSeasonalityFactory<StmDocument> {

        public DiagnosticsSeasonalityFactory() {
            super(StmDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER SLIDING SPANS VIEW">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 603000)
    public static class DiagnosticsSlidingSummaryFactory extends ItemFactory<SlidingSpans> {

        public DiagnosticsSlidingSummaryFactory() {
            super(DIAGNOSTICS_SLIDING_SUMMARY, ssExtractor(), new SlidingSpansUI(ModellingDictionary.S, ModellingDictionary.SI_CMP));
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 603010)
    public static class DiagnosticsSlidingSeasFactory extends ItemFactory<SlidingSpans> {

        public DiagnosticsSlidingSeasFactory() {
            super(DIAGNOSTICS_SLIDING_SEAS, ssExtractor(), new SlidingSpansDetailUI(ModellingDictionary.S_CMP));
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 603020)
    public static class DiagnosticsSlidingTdFactory extends SaDocumentViewFactory.DiagnosticsSlidingTdFactory<StmDocument> {

        public DiagnosticsSlidingTdFactory() {
            super(StmDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 603030)
    public static class DiagnosticsSlidingSaFactory extends SaDocumentViewFactory.DiagnosticsSlidingSaFactory<StmDocument> {

        public DiagnosticsSlidingSaFactory() {
            super(StmDocument.class);
        }
    }
    //</editor-fold>

    private static class ItemFactory<I> extends ComposedProcDocumentItemFactory<StmDocument, I> {

        public ItemFactory(Id itemId, InformationExtractor<? super StmDocument, I> informationExtractor, ItemUI<? extends IProcDocumentView<StmDocument>, I> itemUI) {
            super(StmDocument.class, itemId, informationExtractor, itemUI);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="EXTRACTORS IMPL">
    private static class WkExtractor extends DefaultInformationExtractor<StmDocument, WkInformation> {

        private static final WkExtractor INSTANCE = new WkExtractor();

        @Override
        public WkInformation retrieve(StmDocument source) {
            WkInformation information = new WkInformation();
            information.descriptors = Iterables.toArray(SeatsResults.airlineDescriptors, ComponentDescriptor.class);
            information.estimators = source.getDecompositionPart().getWienerKolmogorovEstimators();
            information.frequency = source.getSeries().getFrequency();
            return information;
        }
    };

    private static class StmresExtractor extends DefaultInformationExtractor<StmDocument, TsData> {

        private static final StmresExtractor INSTANCE = new StmresExtractor();

        @Override
        public TsData retrieve(StmDocument source) {
            return source.getDecompositionPart().getResiduals();
        }
    };

    private static class LikelihoodExtractor extends TsDocumentInformationExtractor<StmDocument, Functions> {

        public static final LikelihoodExtractor INSTANCE = new LikelihoodExtractor();

        @Override
        protected Functions buildInfo(StmDocument source) {
            StmResults stm = source.getDecompositionPart();
            if (stm == null) {
                return null;
            } else {
                return Functions.create(stm.likelihoodFunction(), source.getDecompositionPart().maxLikelihoodFunction());
            }
        }
    };

    //</editor-fold>
}
