/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.advanced.ui;

import com.google.common.collect.Iterables;
import ec.ui.view.tsprocessing.ChartUI;
import ec.ui.view.tsprocessing.WkFinalEstimatorsUI;
import ec.ui.view.tsprocessing.WkInformation;
import ec.ui.view.tsprocessing.WkComponentsUI;
import ec.ui.view.tsprocessing.ArimaUI;
import ec.satoolkit.ComponentDescriptor;
import ec.satoolkit.seats.SeatsResults;
import ec.satoolkit.special.MixedAirlineResults;
import ec.satoolkit.special.MixedAirlineSpecification;
import ec.tss.html.IHtmlElement;
import ec.tss.html.implementation.HtmlMixedAirline;
import ec.tss.sa.documents.MixedAirlineDocument;
import ec.tstoolkit.algorithm.CompositeResults;
import ec.tstoolkit.arima.IArimaModel;
import ec.tstoolkit.modelling.ModellingDictionary;
import ec.tstoolkit.sarima.SarimaModel;
import ec.tstoolkit.stats.NiidTests;
import ec.tstoolkit.timeseries.analysis.SlidingSpans;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.utilities.*;
import ec.ui.view.tsprocessing.*;
import ec.ui.view.tsprocessing.sa.SaDocumentViewFactory;
import static ec.ui.view.tsprocessing.sa.SaDocumentViewFactory.MAIN_CHARTS_LOW;
import static ec.ui.view.tsprocessing.sa.SaDocumentViewFactory.ssExtractor;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicReference;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author pcuser
 */
public class MixedAirlineViewFactory extends SaDocumentViewFactory<MixedAirlineSpecification, MixedAirlineDocument> {

    public static final String SELECTION = "Selection", NOISE = "Noise", WK = "Wiener-Kolmogorov analysis";
    public static final String COMPONENTS = "Components";
    public static final String FINALS = "Final estimators";
    private static final Id MODEL_SELECTION = new LinearId(MODEL, SELECTION);
    private static final Id MODEL_NOISE = new LinearId(MODEL, NOISE);
    private static final Id MODEL_WK_COMPONENTS = new LinearId(MODEL, WK, COMPONENTS);
    private static final Id MODEL_WK_FINALS = new LinearId(MODEL, WK, FINALS);
    private static final AtomicReference<IProcDocumentViewFactory<MixedAirlineDocument>> INSTANCE = new AtomicReference(new MixedAirlineViewFactory());

    public static IProcDocumentViewFactory<MixedAirlineDocument> getDefault() {
        return INSTANCE.get();
    }

    public static void setDefault(IProcDocumentViewFactory<MixedAirlineDocument> factory) {
        INSTANCE.set(factory);
    }

    public static InformationExtractor<MixedAirlineDocument, WkInformation> wkExtractor() {
        return WkExtractor.INSTANCE;
    }

    public static InformationExtractor<MixedAirlineDocument, TsData> maresExtractor() {
        return MaresExtractor.INSTANCE;
    }

    public MixedAirlineViewFactory() {
        registerDefault();
        registerFromLookup(MixedAirlineDocument.class);
    }

    @Override
    public Id getPreferredView() {
        return MAIN_CHARTS_LOW;
    }

    @Deprecated
    public void registerDefault() {
        registerMainViews();
        registerLightPreprocessingViews();
        registerMixedAirlineViews();
        registerBenchmarkingView();
        registerDiagnosticsViews();
    }

    //<editor-fold defaultstate="collapsed" desc="REGISTER MAIN VIEWS">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000 + 1000)
    public static class MainChartsLowFactory extends SaDocumentViewFactory.MainChartsLowFactory<MixedAirlineDocument> {

        public MainChartsLowFactory() {
            super(MixedAirlineDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000 + 2000)
    public static class MainChartsHighFactory extends SaDocumentViewFactory.MainChartsHighFactory<MixedAirlineDocument> {

        public MainChartsHighFactory() {
            super(MixedAirlineDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000 + 3000)
    public static class MainTableFactory extends SaDocumentViewFactory.MainTableFactory<MixedAirlineDocument> {

        public MainTableFactory() {
            super(MixedAirlineDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER SI VIEW">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000 + 4000)
    public static class MainSiFactory extends SaDocumentViewFactory.MainSiFactory<MixedAirlineDocument> {

        public MainSiFactory() {
            super(MixedAirlineDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER LIGHT PREPROCESSING VIEWS">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 200000 + 1000)
    public static class PreprocessingSummaryFactory extends SaDocumentViewFactory.PreprocessingSummaryFactory<MixedAirlineDocument> {

        public PreprocessingSummaryFactory() {
            super(MixedAirlineDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 200000 + 2000)
    public static class PreprocessingRegsFactory extends SaDocumentViewFactory.PreprocessingRegsFactory<MixedAirlineDocument> {

        public PreprocessingRegsFactory() {
            super(MixedAirlineDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 200000 + 3000)
    public static class PreprocessingDetFactory extends SaDocumentViewFactory.PreprocessingDetFactory<MixedAirlineDocument> {

        public PreprocessingDetFactory() {
            super(MixedAirlineDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER MIXED AIRLINE">
    @Deprecated
    public void registerMixedAirlineViews() {
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 1000)
    public static class ModelSelectionFactory extends ItemFactory<MixedAirlineResults> {

        public ModelSelectionFactory() {
            super(MODEL_SELECTION, new DefaultInformationExtractor<MixedAirlineDocument, MixedAirlineResults>() {
                @Override
                public MixedAirlineResults retrieve(MixedAirlineDocument source) {
                    return source.getDecompositionPart();
                }
            }, new HtmlItemUI<View, MixedAirlineResults>() {
                @Override
                public IHtmlElement getHtmlElement(View host, MixedAirlineResults information) {
                    return new HtmlMixedAirline(information.getAllModels(), information.getBestModelPosition());
                }
            });
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 2000)
    public static class ModelNoiseFactory extends ItemFactory<CompositeResults> {

        public ModelNoiseFactory() {
            super(MODEL_NOISE, new DefaultInformationExtractor<MixedAirlineDocument, CompositeResults>() {
                @Override
                public CompositeResults retrieve(MixedAirlineDocument source) {
                    return source.getResults();
                }
            }, new ChartUI(MixedAirlineResults.NOISE_DATA, MixedAirlineResults.IRREGULAR));
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 3000)
    public static class ModelArimaFactory extends ItemFactory<LinkedHashMap<String, IArimaModel>> {

        public ModelArimaFactory() {
            super(MODEL_ARIMA, new DefaultInformationExtractor<MixedAirlineDocument, LinkedHashMap<String, IArimaModel>>() {
                @Override
                public LinkedHashMap<String, IArimaModel> retrieve(MixedAirlineDocument source) {
                    LinkedHashMap<String, IArimaModel> models = new LinkedHashMap<>();
                    if (source.getPreprocessingPart() != null) {
                        SarimaModel tmodel = source.getPreprocessingPart().estimation.getRegArima().getArima();
                        models.put("Preprocessing model", tmodel);
                    }
                    SarimaModel mmodel = source.getDecompositionPart().getBestModel().model.getAirline();
                    models.put("Final model", mmodel);
                    return models;
                }
            }, new ArimaUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 4000)
    public static class ModelWkComponentsFactory extends ItemFactory<WkInformation> {

        public ModelWkComponentsFactory() {
            super(MODEL_WK_COMPONENTS, wkExtractor(), new WkComponentsUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 5000)
    public static class ModelWkFinalsFactory extends ItemFactory<WkInformation> {

        public ModelWkFinalsFactory() {
            super(MODEL_WK_FINALS, wkExtractor(), new WkFinalEstimatorsUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 2500)
    public static class ModelResFactory extends ItemFactory<TsData> {

        public ModelResFactory() {
            super(MODEL_RES, maresExtractor(), new ResidualsUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 2600)
    public static class ModelResStatsFactory extends ItemFactory<NiidTests> {

        public ModelResStatsFactory() {
            super(MODEL_RES_STATS, new DefaultInformationExtractor<MixedAirlineDocument, NiidTests>() {
                @Override
                public NiidTests retrieve(MixedAirlineDocument source) {
                    TsData res = source.getDecompositionPart().getResiduals();
                    int np = source.getPreprocessingPart() != null
                            ? source.getPreprocessingPart().description.getArimaComponent().getFreeParametersCount()
                            : 0;
                    return new NiidTests(res.getValues(), res.getFrequency().intValue(), np, true);
                }
            }, new ResidualsStatsUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 2700)
    public static class ModelResDistFactory extends ItemFactory<TsData> {

        public ModelResDistFactory() {
            super(MODEL_RES_DIST, maresExtractor(), new ResidualsDistUI());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER BENCHMARKING VIEW">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 400000 + 1000)
    public static class BenchmarkingFactory extends SaDocumentViewFactory.BenchmarkingFactory<MixedAirlineDocument> {

        public BenchmarkingFactory() {
            super(MixedAirlineDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER DIAGNOSTICS VIEWS">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 500000 + 1000)
    public static class DiagnosticsSummaryFactory extends SaDocumentViewFactory.DiagnosticsSummaryFactory<MixedAirlineDocument> {

        public DiagnosticsSummaryFactory() {
            super(MixedAirlineDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 500000 + 2000)
    public static class DiagnosticsSpectrumResFactory extends SaDocumentViewFactory.DiagnosticsSpectrumResFactory<MixedAirlineDocument> {

        public DiagnosticsSpectrumResFactory() {
            super(MixedAirlineDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 500000 + 3000)
    public static class DiagnosticsSpectrumIFactory extends SaDocumentViewFactory.DiagnosticsSpectrumIFactory<MixedAirlineDocument> {

        public DiagnosticsSpectrumIFactory() {
            super(MixedAirlineDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 500000 + 4000)
    public static class DiagnosticsSpectrumSaFactory extends SaDocumentViewFactory.DiagnosticsSpectrumSaFactory<MixedAirlineDocument> {

        public DiagnosticsSpectrumSaFactory() {
            super(MixedAirlineDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER SEASONALITY VIEW">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 500000 + 5000)
    public static class DiagnosticsSeasonalityFactory extends SaDocumentViewFactory.DiagnosticsSeasonalityFactory<MixedAirlineDocument> {

        public DiagnosticsSeasonalityFactory() {
            super(MixedAirlineDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER SLIDING SPANS">
    //@ServiceProvider(service = ProcDocumentItemFactory.class)
    public static class DiagnosticsSlidingSummaryFactory extends ItemFactory<SlidingSpans> {

        public DiagnosticsSlidingSummaryFactory() {
            super(DIAGNOSTICS_SLIDING_SUMMARY, ssExtractor(), new SlidingSpansUI(ModellingDictionary.S, ModellingDictionary.SI_CMP));
        }
    }

    //@ServiceProvider(service = ProcDocumentItemFactory.class)
    public static class DiagnosticsSlidingSeasFactory extends ItemFactory<SlidingSpans> {

        public DiagnosticsSlidingSeasFactory() {
            super(DIAGNOSTICS_SLIDING_SEAS, ssExtractor(), new SlidingSpansDetailUI(ModellingDictionary.S_CMP));
        }
    }

    //@ServiceProvider(service = ProcDocumentItemFactory.class)
    public static class DiagnosticsSlidingTdFactory extends SaDocumentViewFactory.DiagnosticsSlidingTdFactory<MixedAirlineDocument> {

        public DiagnosticsSlidingTdFactory() {
            super(MixedAirlineDocument.class);
        }
    }

    //@ServiceProvider(service = ProcDocumentItemFactory.class)
    public static class DiagnosticsSlidingSaFactory extends SaDocumentViewFactory.DiagnosticsSlidingSaFactory<MixedAirlineDocument> {

        public DiagnosticsSlidingSaFactory() {
            super(MixedAirlineDocument.class);
        }
    }
    //</editor-fold>

    private static class ItemFactory<I> extends ComposedProcDocumentItemFactory<MixedAirlineDocument, I> {

        public ItemFactory(Id itemId, InformationExtractor<? super MixedAirlineDocument, I> informationExtractor, ItemUI<? extends IProcDocumentView<MixedAirlineDocument>, I> itemUI) {
            super(MixedAirlineDocument.class, itemId, informationExtractor, itemUI);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="EXTRACTORS IMPL">
    private static class WkExtractor extends DefaultInformationExtractor<MixedAirlineDocument, WkInformation> {

        private static final WkExtractor INSTANCE = new WkExtractor();

        @Override
        public WkInformation retrieve(MixedAirlineDocument source) {
            WkInformation information = new WkInformation();
            information.descriptors = Iterables.toArray(SeatsResults.descriptors, ComponentDescriptor.class);
            information.estimators = source.getDecompositionPart().getWienerKolmogorovEstimators();
            information.frequency = source.getSeries().getFrequency();
            return information;
        }
    };

    private static class MaresExtractor extends DefaultInformationExtractor<MixedAirlineDocument, TsData> {

        private static final MaresExtractor INSTANCE = new MaresExtractor();

        @Override
        public TsData retrieve(MixedAirlineDocument source) {
            return source.getDecompositionPart().getResiduals();
        }
    };
    //</editor-fold>
}
