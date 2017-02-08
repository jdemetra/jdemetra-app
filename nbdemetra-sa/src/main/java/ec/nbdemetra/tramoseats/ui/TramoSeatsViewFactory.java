/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats.ui;

import com.google.common.collect.Iterables;
import ec.nbdemetra.sa.DiagnosticsMatrixView;
import ec.satoolkit.ComponentDescriptor;
import ec.satoolkit.GenericSaProcessingFactory;
import ec.satoolkit.diagnostics.SignificantSeasonalityTest;
import ec.satoolkit.diagnostics.StationaryVarianceDecomposition;
import ec.satoolkit.seats.SeatsResults;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.tss.documents.DocumentManager;
import ec.tss.html.IHtmlElement;
import ec.tss.html.implementation.HtmlModelBasedRevisionsAnalysis;
import ec.tss.html.implementation.HtmlSignificantSeasons;
import ec.tss.html.implementation.HtmlStationaryVarianceDecomposition;
import ec.tss.html.implementation.HtmlTramoSeatsGrowthRates;
import ec.tss.sa.documents.TramoSeatsDocument;
import ec.tstoolkit.algorithm.CompositeResults;
import ec.tstoolkit.arima.ArimaModel;
import ec.tstoolkit.arima.IArimaModel;
import ec.tstoolkit.modelling.ComponentInformation;
import ec.tstoolkit.modelling.ComponentType;
import ec.tstoolkit.modelling.ModellingDictionary;
import ec.tstoolkit.modelling.SeriesInfo;
import ec.tstoolkit.modelling.arima.PreprocessingModel;
import ec.tstoolkit.sarima.SarimaModel;
import ec.tstoolkit.timeseries.analysis.SlidingSpans;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.ucarima.UcarimaModel;
import ec.tstoolkit.utilities.*;
import ec.ui.view.tsprocessing.ArimaUI;
import ec.ui.view.tsprocessing.ComposedProcDocumentItemFactory;
import ec.ui.view.tsprocessing.DefaultItemUI;
import ec.ui.view.tsprocessing.EstimationUI;
import ec.ui.view.tsprocessing.GenericTableUI;
import ec.ui.view.tsprocessing.HtmlItemUI;
import ec.ui.view.tsprocessing.IProcDocumentView;
import ec.ui.view.tsprocessing.IProcDocumentViewFactory;
import ec.ui.view.tsprocessing.ItemUI;
import ec.ui.view.tsprocessing.PooledItemUI;
import ec.ui.view.tsprocessing.ProcDocumentItemFactory;
import ec.ui.view.tsprocessing.SlidingSpansDetailUI;
import ec.ui.view.tsprocessing.SlidingSpansUI;
import ec.ui.view.tsprocessing.UcarimaUI;
import ec.ui.view.tsprocessing.WkComponentsUI;
import ec.ui.view.tsprocessing.WkFinalEstimatorsUI;
import ec.ui.view.tsprocessing.WkInformation;
import ec.ui.view.tsprocessing.sa.ModelBasedUI;
import ec.ui.view.tsprocessing.sa.SaDocumentViewFactory;
import static ec.ui.view.tsprocessing.sa.SaDocumentViewFactory.saExtractor;
import static ec.ui.view.tsprocessing.sa.SaDocumentViewFactory.ssExtractor;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JComponent;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
public class TramoSeatsViewFactory extends SaDocumentViewFactory<TramoSeatsSpecification, TramoSeatsDocument> {

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
    public static final Id DECOMPOSITION_SUMMARY = new LinearId(DECOMPOSITION);
    public static final Id DECOMPOSITION_STOCH_TREND = new LinearId(DECOMPOSITION, STOCHASTIC, STOCHASTIC_TREND);
    public static final Id DECOMPOSITION_STOCH_SEAS = new LinearId(DECOMPOSITION, STOCHASTIC, STOCHASTIC_SEAS);
    public static final Id DECOMPOSITION_STOCH_SA = new LinearId(DECOMPOSITION, STOCHASTIC, STOCHASTIC_SA);
    public static final Id DECOMPOSITION_STOCH_IRR = new LinearId(DECOMPOSITION, STOCHASTIC, STOCHASTIC_IRR);
    public static final Id DECOMPOSITION_SERIES = new LinearId(DECOMPOSITION, STOCHASTIC);
    public static final Id DECOMPOSITION_CMPSERIES = new LinearId(DECOMPOSITION, COMPONENTS);
    public static final Id DECOMPOSITION_WK_COMPONENTS = new LinearId(DECOMPOSITION, WKANALYSIS, WK_COMPONENTS);
    public static final Id DECOMPOSITION_WK_FINALS = new LinearId(DECOMPOSITION, WKANALYSIS, WK_FINALS);
    public static final Id DECOMPOSITION_ERRORS = new LinearId(DECOMPOSITION, WK_ERRORS);
    public static final Id DECOMPOSITION_RATES = new LinearId(DECOMPOSITION, WK_RATES);
    public static final Id DECOMPOSITION_TESTS = new LinearId(DECOMPOSITION, MODELBASED);
    public static final Id DECOMPOSITION_VAR = new LinearId(DECOMPOSITION, STVAR);
    public static final Id DECOMPOSITION_SIGSEAS = new LinearId(DECOMPOSITION, SIGSEAS);
    private static final AtomicReference<IProcDocumentViewFactory<TramoSeatsDocument>> INSTANCE = new AtomicReference(new TramoSeatsViewFactory());

    public static IProcDocumentViewFactory<TramoSeatsDocument> getDefault() {
        return INSTANCE.get();
    }

    public static void setDefault(IProcDocumentViewFactory<TramoSeatsDocument> factory) {
        INSTANCE.set(factory);
    }

    public static InformationExtractor<TramoSeatsDocument, WkInformation> wkExtractor() {
        return WkExtractor.INSTANCE;
    }

    public static InformationExtractor<TramoSeatsDocument, EstimationUI.Information> cmpExtractor(ComponentType type) {
        return new CmpExtractor(type);
    }

    public TramoSeatsViewFactory() {
        registerDefault();
        registerFromLookup(TramoSeatsDocument.class);
    }

    @Override
    public Id getPreferredView() {
        return MAIN_SUMMARY;
    }

    @Deprecated
    public void registerDefault() {
        registerSpec();
        registerSummary();
        registerMainViews();
        registerPreprocessingViews();
        registerSeatsViews();
        registerBenchmarkingView();
        registerDiagnosticsViews();
    }

    //<editor-fold defaultstate="collapsed" desc="REGISTER SPEC">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000)
    public static class SpecAllFactory extends SaDocumentViewFactory.SpecAllFactory<TramoSeatsDocument> {

        public SpecAllFactory() {
            super(TramoSeatsDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100010)
    public static class InputFactory extends SaDocumentViewFactory.InputFactory<TramoSeatsDocument> {

        public InputFactory() {
            super(TramoSeatsDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER SUMMARY">
    @Deprecated
    public void registerSummary() {
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 200000)
    public static class MainSummaryFactory extends ItemFactory<TramoSeatsDocument> {

        public MainSummaryFactory() {
            super(MAIN_SUMMARY, new DefaultInformationExtractor<TramoSeatsDocument, TramoSeatsDocument>() {
                @Override
                public TramoSeatsDocument retrieve(TramoSeatsDocument source) {
                    return source;
                }
            }, new PooledItemUI<View, TramoSeatsDocument, TramoSeatsSummary>(TramoSeatsSummary.class) {
                @Override
                protected void init(TramoSeatsSummary c, View host, TramoSeatsDocument information) {
                    c.setTsToolkit(host.getToolkit());
                    c.set(information);
                }
            });
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER MAIN VIEWS">
    // provide regitration of main components
    @Deprecated
    public void registerMainViews() {
        registerSiView();
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 201010)
    public static class MainChartsLowFactory extends SaDocumentViewFactory.MainChartsLowFactory<TramoSeatsDocument> {

        public MainChartsLowFactory() {
            super(TramoSeatsDocument.class, GenericSaProcessingFactory.FINAL);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 201020)
    public static class MainChartsHighFactory extends SaDocumentViewFactory.MainChartsHighFactory<TramoSeatsDocument> {

        public MainChartsHighFactory() {
            super(TramoSeatsDocument.class, GenericSaProcessingFactory.FINAL);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 202000)
    public static class MainTableFactory extends SaDocumentViewFactory.MainTableFactory<TramoSeatsDocument> {

        public MainTableFactory() {
            super(TramoSeatsDocument.class, GenericSaProcessingFactory.FINAL);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 203000)
    public static class MainSiFactory extends SaDocumentViewFactory.MainSiFactory<TramoSeatsDocument> {

        public MainSiFactory() {
            super(TramoSeatsDocument.class);
        }
    }

//    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 204000)
    public static class ProcessingLogFactory extends SaDocumentViewFactory.ProcessingLogFactory<TramoSeatsDocument> {

        public ProcessingLogFactory() {
            super(TramoSeatsDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER PREPROCESSING VIEWS">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000)
    public static class PreprocessingSummaryFactory extends SaDocumentViewFactory.PreprocessingSummaryFactory<TramoSeatsDocument> {

        public PreprocessingSummaryFactory() {
            super(TramoSeatsDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 302000)
    public static class PreprocessingRegsFactory extends SaDocumentViewFactory.PreprocessingRegsFactory<TramoSeatsDocument> {

        public PreprocessingRegsFactory() {
            super(TramoSeatsDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 304000)
    public static class PreprocessingDetFactory extends SaDocumentViewFactory.PreprocessingDetFactory<TramoSeatsDocument> {

        public PreprocessingDetFactory() {
            super(TramoSeatsDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 301000)
    public static class PreprocessingFCastsFactory extends SaDocumentViewFactory.PreprocessingFCastsFactory<TramoSeatsDocument> {

        public PreprocessingFCastsFactory() {
            super(TramoSeatsDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 301005)
    public static class PreprocessingFCastsTableFactory extends SaDocumentViewFactory.PreprocessingFCastsTableFactory<TramoSeatsDocument> {

        public PreprocessingFCastsTableFactory() {
            super(TramoSeatsDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 301010)
    public static class PreprocessingFCastsOutFactory extends SaDocumentViewFactory.PreprocessingFCastsOutFactory<TramoSeatsDocument> {

        public PreprocessingFCastsOutFactory() {
            super(TramoSeatsDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 305000)
    public static class PreprocessingResFactory extends SaDocumentViewFactory.PreprocessingResFactory<TramoSeatsDocument> {

        public PreprocessingResFactory() {
            super(TramoSeatsDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 305010)
    public static class PreprocessingResStatsFactory extends SaDocumentViewFactory.PreprocessingResStatsFactory<TramoSeatsDocument> {

        public PreprocessingResStatsFactory() {
            super(TramoSeatsDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 305020)
    public static class PreprocessingResDistFactory extends SaDocumentViewFactory.PreprocessingResDistFactory<TramoSeatsDocument> {

        public PreprocessingResDistFactory() {
            super(TramoSeatsDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 306000)
    public static class LikelihoodFactory extends SaDocumentViewFactory.LikelihoodFactory<TramoSeatsDocument> {

        public LikelihoodFactory() {
            super(TramoSeatsDocument.class);
            setAsync(true);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER ARIMA">
    @Deprecated
    @Override
    public void registerArimaView() {
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 303000)
    public static class PreprocessingArimaFactory extends ItemFactory<LinkedHashMap<String, IArimaModel>> {

        public PreprocessingArimaFactory() {
            super(PREPROCESSING_ARIMA, new DefaultInformationExtractor<TramoSeatsDocument, LinkedHashMap<String, IArimaModel>>() {
                @Override
                public LinkedHashMap<String, IArimaModel> retrieve(TramoSeatsDocument source) {
                    LinkedHashMap<String, IArimaModel> models = new LinkedHashMap<>();
                    PreprocessingModel pm = source.getPreprocessingPart();
                    SarimaModel tmodel = null;
                    if (pm != null) {
                        tmodel = pm.estimation.getArima();
                        models.put("Tramo model", tmodel);
                    }
                    SeatsResults seats = source.getDecompositionPart();
                    if (seats != null) {
                        IArimaModel smodel = seats.getModel().getArima();
                        if (tmodel == null || !ArimaModel.same(tmodel, smodel, 1e-4)) {
                            models.put("Seats model", smodel);
                        }
                    }
                    return models;
                }
            }, new ArimaUI());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER SEATS">
    @Deprecated
    public void registerSeatsViews() {
        //register(DECOMPOSITION_STOCH_SA, new CmpExtractor(ComponentType.SeasonallyAdjusted), new EstimationUI());
        //register(DECOMPOSITION_STOCH_IRR, new CmpExtractor(ComponentType.Irregular), new EstimationUI());
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 400000)
    public static class DecompositionSummaryFactory extends ItemFactory<UcarimaUI.Information> {

        public DecompositionSummaryFactory() {
            super(DECOMPOSITION_SUMMARY, new DefaultInformationExtractor<TramoSeatsDocument, UcarimaUI.Information>() {
                @Override
                public UcarimaUI.Information retrieve(TramoSeatsDocument source) {
                    SeatsResults seats = source.getDecompositionPart();
                    if (seats == null) {
                        return null;
                    }
                    UcarimaModel ucm = seats.getUcarimaModel();
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
    public static class LinearizedSeriesFactory extends ItemFactory<CompositeResults> {

        private static String[] generateItems() {
            StringBuilder y = new StringBuilder();
            y.append(DocumentManager.COMPOSITE).append("Series (lin)=,").append(ModellingDictionary.Y_LIN)
                    .append(',').append(ModellingDictionary.Y_LIN).append(SeriesInfo.F_SUFFIX);
            StringBuilder t = new StringBuilder();
            t.append(DocumentManager.COMPOSITE).append("Trend (lin)=,").append(ModellingDictionary.T_LIN)
                    .append(',').append(ModellingDictionary.T_LIN).append(SeriesInfo.F_SUFFIX);
            StringBuilder sa = new StringBuilder();
            sa.append(DocumentManager.COMPOSITE).append("Seasonally adjusted (lin)=,").append(ModellingDictionary.SA_LIN)
                    .append(',').append(ModellingDictionary.SA_LIN).append(SeriesInfo.F_SUFFIX);
            StringBuilder s = new StringBuilder();
            s.append(DocumentManager.COMPOSITE).append("Seasonal (lin)=,").append(ModellingDictionary.S_LIN)
                    .append(',').append(ModellingDictionary.S_LIN).append(SeriesInfo.F_SUFFIX);
            StringBuilder i = new StringBuilder();
            i.append(DocumentManager.COMPOSITE).append("Irregular (lin)=,").append(ModellingDictionary.I_LIN)
                    .append(',').append(ModellingDictionary.I_LIN).append(SeriesInfo.F_SUFFIX);
            StringBuilder te = new StringBuilder();
            te.append(DocumentManager.COMPOSITE).append("Trend (stde lin)=,")
                    .append(ModellingDictionary.T_LIN).append(SeriesInfo.E_SUFFIX)
                    .append(',').append(ModellingDictionary.T_LIN).append(SeriesInfo.EF_SUFFIX);
            StringBuilder sae = new StringBuilder();
            sae.append(DocumentManager.COMPOSITE).append("Seasonally adjusted (stde lin)=,")
                    .append(ModellingDictionary.SA_LIN).append(SeriesInfo.E_SUFFIX)
                    .append(',').append(ModellingDictionary.SA_LIN).append(SeriesInfo.EF_SUFFIX);
            StringBuilder se = new StringBuilder();
            se.append(DocumentManager.COMPOSITE).append("Seasonal (stde lin)=,")
                    .append(ModellingDictionary.S_LIN).append(SeriesInfo.E_SUFFIX)
                    .append(',').append(ModellingDictionary.S_LIN).append(SeriesInfo.EF_SUFFIX);
            StringBuilder ie = new StringBuilder();
            ie.append(DocumentManager.COMPOSITE).append("Irregular (stde lin)=,")
                    .append(ModellingDictionary.I_LIN).append(SeriesInfo.E_SUFFIX)
                    .append(',').append(ModellingDictionary.I_LIN).append(SeriesInfo.EF_SUFFIX);
            return new String[]{y.toString(), sa.toString(), t.toString(), s.toString(), i.toString()/*, ye.toString()*/, sae.toString(), te.toString(), se.toString(), ie.toString()};
        }

        public LinearizedSeriesFactory() {
            super(DECOMPOSITION_SERIES, saExtractor(), new GenericTableUI(false, generateItems()));
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 401010)
    public static class ComponentsSeriesFactory extends ItemFactory<CompositeResults> {

        private static String[] generateItems() {
            StringBuilder y = new StringBuilder();
            y.append(DocumentManager.COMPOSITE).append("Series (cmp)=,").append(ModellingDictionary.Y_CMP)
                    .append(',').append(ModellingDictionary.Y_CMP).append(SeriesInfo.F_SUFFIX);
            StringBuilder t = new StringBuilder();
            t.append(DocumentManager.COMPOSITE).append("Trend (cmp)=,").append(ModellingDictionary.T_CMP)
                    .append(',').append(ModellingDictionary.T_CMP).append(SeriesInfo.F_SUFFIX);
            StringBuilder sa = new StringBuilder();
            sa.append(DocumentManager.COMPOSITE).append("Seasonally adjusted (cmp)=,").append(ModellingDictionary.SA_CMP)
                    .append(',').append(ModellingDictionary.SA_CMP).append(SeriesInfo.F_SUFFIX);
            StringBuilder s = new StringBuilder();
            s.append(DocumentManager.COMPOSITE).append("Seasonal (cmp)=,").append(ModellingDictionary.S_CMP)
                    .append(',').append(ModellingDictionary.S_CMP).append(SeriesInfo.F_SUFFIX);
            StringBuilder i = new StringBuilder();
            i.append(DocumentManager.COMPOSITE).append("Irregular (cmp)=,").append(ModellingDictionary.I_CMP)
                    .append(',').append(ModellingDictionary.I_CMP).append(SeriesInfo.F_SUFFIX);
            StringBuilder te = new StringBuilder();
            te.append(DocumentManager.COMPOSITE).append("Trend stdev(cmp)=,").append(ModellingDictionary.T_CMP).append(SeriesInfo.E_SUFFIX)
                    .append(',').append(ModellingDictionary.T_CMP).append(SeriesInfo.EF_SUFFIX);
            StringBuilder sae = new StringBuilder();
            sae.append(DocumentManager.COMPOSITE).append("Seasonally adjusted stdev(cmp)=,").append(ModellingDictionary.SA_CMP).append(SeriesInfo.E_SUFFIX)
                    .append(',').append(ModellingDictionary.SA_CMP).append(SeriesInfo.EF_SUFFIX);
            StringBuilder se = new StringBuilder();
            se.append(DocumentManager.COMPOSITE).append("Seasonal stdev (cmp)=,").append(ModellingDictionary.S_CMP).append(SeriesInfo.E_SUFFIX)
                    .append(',').append(ModellingDictionary.S_CMP).append(SeriesInfo.EF_SUFFIX);
            StringBuilder ie = new StringBuilder();
            ie.append(DocumentManager.COMPOSITE).append("Irregular stdev(cmp)=,").append(ModellingDictionary.I_CMP).append(SeriesInfo.E_SUFFIX)
                    .append(',').append(ModellingDictionary.I_CMP).append(SeriesInfo.EF_SUFFIX);
            return new String[]{y.toString(), sa.toString(), t.toString(), s.toString(), i.toString(), sae.toString(), te.toString(), se.toString(), ie.toString()};
        }

        public ComponentsSeriesFactory() {
            super(DECOMPOSITION_CMPSERIES, saExtractor(), new GenericTableUI(false, generateItems()));
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 402010)
    public static class DecompositionWkComponentsFactory extends ItemFactory<WkInformation> {

        public DecompositionWkComponentsFactory() {
            super(DECOMPOSITION_WK_COMPONENTS, wkExtractor(), new WkComponentsUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 401010)
    public static class DecompositionStochTrendFactory extends ItemFactory<EstimationUI.Information> {

        public DecompositionStochTrendFactory() {
            super(DECOMPOSITION_STOCH_TREND, cmpExtractor(ComponentType.Trend), new EstimationUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 401020)
    public static class DecompositionStochSeasFactory extends ItemFactory<EstimationUI.Information> {

        public DecompositionStochSeasFactory() {
            super(DECOMPOSITION_STOCH_SEAS, cmpExtractor(ComponentType.Seasonal), new EstimationUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 402020)
    public static class DecompositionWkFinalsFactory extends ItemFactory<WkInformation> {

        public DecompositionWkFinalsFactory() {
            super(DECOMPOSITION_WK_FINALS, wkExtractor(), new WkFinalEstimatorsUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 402030)
    public static class DecompositionWkErrorsFactory extends ItemFactory<WkInformation> {

        public DecompositionWkErrorsFactory() {
            super(DECOMPOSITION_ERRORS, wkExtractor(), new WkErrorsUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 402050)
    public static class DecompositionGrowthFactory extends ItemFactory<CompositeResults> {

        public DecompositionGrowthFactory() {
            super(DECOMPOSITION_RATES, new DefaultInformationExtractor<TramoSeatsDocument, CompositeResults>() {
                @Override
                public CompositeResults retrieve(TramoSeatsDocument source) {
                    CompositeResults results = source.getResults();
                    if (!results.isSuccessful()) {
                        return null;
                    } else {
                        return results;
                    }
                }
            }, new GrowthRatesUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 403000)
    public static class DecompositionTestsFactory extends ItemFactory<ModelBasedUI.Information> {

        public DecompositionTestsFactory() {
            super(DECOMPOSITION_TESTS, new DefaultInformationExtractor<TramoSeatsDocument, ModelBasedUI.Information>() {
                @Override
                public ModelBasedUI.Information retrieve(TramoSeatsDocument source) {
                    SeatsResults rslt = source.getDecompositionPart();
                    if (rslt == null) {
                        return null;
                    }
                    ModelBasedUI.Information info = new ModelBasedUI.Information();
                    info.decomposition = rslt.getComponents();
                    info.ucm = rslt.getUcarimaModel();
                    info.err = rslt.getModel().getSer();
                    return info;
                }
            }, new ModelBasedUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 403010)
    public static class StationaryVarianceDecompositionFactory extends ItemFactory<StationaryVarianceDecomposition> {

        public StationaryVarianceDecompositionFactory() {
            super(DECOMPOSITION_VAR, stvarExtractor(), new StvarUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 403005)
    public static class SignificantSeasonalityFactory extends ItemFactory<CompositeResults> {

        public SignificantSeasonalityFactory() {
            super(DECOMPOSITION_SIGSEAS, saExtractor(), new SigSeasUI());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER BENCHMARKING VIEW">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 500000)
    public static class BenchmarkingFactory extends SaDocumentViewFactory.BenchmarkingFactory<TramoSeatsDocument> {

        public BenchmarkingFactory() {
            super(TramoSeatsDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER DIAGNOSTICS VIEWS">
    private static class DiagnosticsMatrixExtractor extends DefaultInformationExtractor<TramoSeatsDocument, Map<String, CompositeResults>> {

        public static final DiagnosticsMatrixExtractor INSTANCE = new DiagnosticsMatrixExtractor();

        @Override
        public Map<String, CompositeResults> retrieve(TramoSeatsDocument source) {
            if (source.getInput() == null) {
                return null;
            }

            Map<String, CompositeResults> results = new LinkedHashMap<>();
            TramoSeatsSpecification currentSpec = source.getSpecification();
            results.put("[C] " + currentSpec.toString(), source.getResults());

            for (TramoSeatsSpecification spec : TramoSeatsSpecification.allSpecifications()) {
                if (!spec.equals(currentSpec)) {
                    source.setSpecification(spec);
                    source.clear();
                    results.put(spec.toString(), source.getResults());
                }
            }

            return results;
        }
    };

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 600001)
    public static class DiagnosticsMatrixFactory extends ItemFactory<Map<String, CompositeResults>> {

        public DiagnosticsMatrixFactory() {
            super(DIAGNOSTICS_MATRIX, DiagnosticsMatrixExtractor.INSTANCE, new DefaultItemUI<IProcDocumentView<TramoSeatsDocument>, Map<String, CompositeResults>>() {
                @Override
                public JComponent getView(IProcDocumentView<TramoSeatsDocument> host, Map<String, CompositeResults> information) {
                    return new DiagnosticsMatrixView(information);
                }

            });
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 600000)
    public static class DiagnosticsSummaryFactory extends SaDocumentViewFactory.DiagnosticsSummaryFactory<TramoSeatsDocument> {

        public DiagnosticsSummaryFactory() {
            super(TramoSeatsDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 602010)
    public static class DiagnosticsSpectrumResFactory extends SaDocumentViewFactory.DiagnosticsSpectrumResFactory<TramoSeatsDocument> {

        public DiagnosticsSpectrumResFactory() {
            super(TramoSeatsDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 602020)
    public static class DiagnosticsSpectrumIFactory extends SaDocumentViewFactory.DiagnosticsSpectrumIFactory<TramoSeatsDocument> {

        public DiagnosticsSpectrumIFactory() {
            super(TramoSeatsDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 602030)
    public static class DiagnosticsSpectrumSaFactory extends SaDocumentViewFactory.DiagnosticsSpectrumSaFactory<TramoSeatsDocument> {

        public DiagnosticsSpectrumSaFactory() {
            super(TramoSeatsDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER REVISION HISTORY VIEW">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 604010)
    public static class RevisionHistorySaFactory extends SaDocumentViewFactory.DiagnosticsRevisionSaFactory<TramoSeatsDocument> {

        public RevisionHistorySaFactory() {
            super(TramoSeatsDocument.class);
            setAsync(true);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 604020)
    public static class RevisionHistoryTrendFactory extends SaDocumentViewFactory.DiagnosticsRevisionTrendFactory<TramoSeatsDocument> {

        public RevisionHistoryTrendFactory() {
            super(TramoSeatsDocument.class);
            setAsync(true);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 604030)
    public static class RevisionHistorySaChangesFactory extends SaDocumentViewFactory.DiagnosticsRevisionSaChangesFactory<TramoSeatsDocument> {

        public RevisionHistorySaChangesFactory() {
            super(TramoSeatsDocument.class);
            setAsync(true);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 604040)
    public static class RevisionHistoryTrendChangesFactory extends SaDocumentViewFactory.DiagnosticsRevisionTrendChangesFactory<TramoSeatsDocument> {

        public RevisionHistoryTrendChangesFactory() {
            super(TramoSeatsDocument.class);
            setAsync(true);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER STABILITY VIEWS">    
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 604010)
    public static class StabilityTDFactory extends SaDocumentViewFactory.StabilityTDFactory<TramoSeatsDocument> {

        public StabilityTDFactory() {
            super(TramoSeatsDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 605020)
    public static class StabilityEasterFactory extends SaDocumentViewFactory.StabilityEasterFactory<TramoSeatsDocument> {

        public StabilityEasterFactory() {
            super(TramoSeatsDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 605030)
    public static class StabilityArimaFactory extends SaDocumentViewFactory.StabilityArimaFactory<TramoSeatsDocument> {

        public StabilityArimaFactory() {
            super(TramoSeatsDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER SEASONALITY VIEW">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 600010)
    public static class OriginalSeasonalityFactory extends SaDocumentViewFactory.SeasonalityTestsFactory<TramoSeatsDocument> {

        public OriginalSeasonalityFactory() {
            super(TramoSeatsDocument.class, DIAGNOSTICS_OSEASONALITY, SaDocumentViewFactory.oseasExtractor(), "Original series", false);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 600020)
    public static class LinearSeasonalityFactory extends SaDocumentViewFactory.SeasonalityTestsFactory<TramoSeatsDocument> {

        public LinearSeasonalityFactory() {
            super(TramoSeatsDocument.class, DIAGNOSTICS_LSEASONALITY, SaDocumentViewFactory.lseasExtractor(), "Linearized series", false);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 600030)
    public static class ResSeasonalityFactory extends SaDocumentViewFactory.SeasonalityTestsFactory<TramoSeatsDocument> {

        public ResSeasonalityFactory() {
            super(TramoSeatsDocument.class, DIAGNOSTICS_RSEASONALITY, SaDocumentViewFactory.rseasExtractor(), "Full residuals", true);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 600040)
    public static class DiagnosticsSeasonalityFactory extends SaDocumentViewFactory.DiagnosticsSeasonalityFactory<TramoSeatsDocument> {

        public DiagnosticsSeasonalityFactory() {
            super(TramoSeatsDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 600050)
    public static class SaSeasonalityFactory extends SaDocumentViewFactory.SeasonalityTestsFactory<TramoSeatsDocument> {

        public SaSeasonalityFactory() {
            super(TramoSeatsDocument.class, DIAGNOSTICS_SASEASONALITY, SaDocumentViewFactory.saseasExtractor(), "Seasonally adjusted series (stochastic)", true);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 600060)
    public static class IrrSeasonalityFactory extends SaDocumentViewFactory.SeasonalityTestsFactory<TramoSeatsDocument> {

        public IrrSeasonalityFactory() {
            super(TramoSeatsDocument.class, DIAGNOSTICS_ISEASONALITY, SaDocumentViewFactory.iseasExtractor(), "Irregular series (stochastic)", true);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 600070)
    public static class LastResSeasonalityFactory extends SaDocumentViewFactory.SeasonalityTestsFactory<TramoSeatsDocument> {

        public LastResSeasonalityFactory() {
            super(TramoSeatsDocument.class, DIAGNOSTICS_LASTRSEASONALITY, SaDocumentViewFactory.lastrseasExtractor(), "Residuals (last periods)", true);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 600080)
    public static class LastSaSeasonalityFactory extends SaDocumentViewFactory.SeasonalityTestsFactory<TramoSeatsDocument> {

        public LastSaSeasonalityFactory() {
            super(TramoSeatsDocument.class, DIAGNOSTICS_LASTSASEASONALITY, SaDocumentViewFactory.lastsaseasExtractor(), "Seasonally adjusted series (last periods)", true);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 600090)
    public static class LastIrrSeasonalityFactory extends SaDocumentViewFactory.SeasonalityTestsFactory<TramoSeatsDocument> {

        public LastIrrSeasonalityFactory() {
            super(TramoSeatsDocument.class, DIAGNOSTICS_LASTISEASONALITY, SaDocumentViewFactory.lastiseasExtractor(), "Irregular series (last periods)", true);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 600100)
    public static class ResidualSeasonalityFactory extends SaDocumentViewFactory.ResidualSeasonalityFactory<TramoSeatsDocument> {

        public ResidualSeasonalityFactory() {
            super(TramoSeatsDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER SLIDING SPANS">
    @Override
    public void registerSlidingSpansView() {
        super.registerSlidingSpansView();
    }

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
    public static class DiagnosticsSlidingTdFactory extends SaDocumentViewFactory.DiagnosticsSlidingTdFactory<TramoSeatsDocument> {

        public DiagnosticsSlidingTdFactory() {
            super(TramoSeatsDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 603030)
    public static class DiagnosticsSlidingSaFactory extends SaDocumentViewFactory.DiagnosticsSlidingSaFactory<TramoSeatsDocument> {

        public DiagnosticsSlidingSaFactory() {
            super(TramoSeatsDocument.class);
        }
    }
    //</editor-fold>

    private static class ItemFactory<I> extends ComposedProcDocumentItemFactory<TramoSeatsDocument, I> {

        public ItemFactory(Id itemId, InformationExtractor<? super TramoSeatsDocument, I> informationExtractor, ItemUI<? extends IProcDocumentView<TramoSeatsDocument>, I> itemUI) {
            super(TramoSeatsDocument.class, itemId, informationExtractor, itemUI);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="EXTRACTORS IMPL">
    private static class CmpExtractor implements InformationExtractor<TramoSeatsDocument, EstimationUI.Information> {

        final ComponentType type_;

        private CmpExtractor(ComponentType type) {
            type_ = type;
        }

        @Override
        public EstimationUI.Information retrieve(TramoSeatsDocument source) {
            SeatsResults seats = source.getDecompositionPart();
            if (seats == null) {
                return null;
            }
            TsData s = seats.getComponents().getSeries(type_, ComponentInformation.Value);
            if (s == null) {
                return new EstimationUI.Information(null, null, null, null);
            }
            TsData es = seats.getComponents().getSeries(type_, ComponentInformation.Stdev);
//            TsPeriodSelector sel = new TsPeriodSelector();
//            sel.last(2 * s.getFrequency().intValue());
            TsData fs = seats.getComponents().getSeries(type_, ComponentInformation.Forecast);
            TsData efs = seats.getComponents().getSeries(type_, ComponentInformation.StdevForecast);
            Date x = s.getLastPeriod().lastday().getTime();
            s = s.update(fs);
            es = es.update(efs);
//           s = s.select(sel).update(fs);
//            es = es.select(sel).update(efs);
            EstimationUI.Information rslt
                    = new EstimationUI.Information(s, null, s.minus(es.times(1.96)), s.plus(es.times(1.96)));
            rslt.markers = new Date[]{x};
            return rslt;
        }

        @Override
        public void flush(TramoSeatsDocument source) {
        }
    }

    private static class WkExtractor extends DefaultInformationExtractor<TramoSeatsDocument, WkInformation> {

        private static final WkExtractor INSTANCE = new WkExtractor();

        @Override
        public WkInformation retrieve(TramoSeatsDocument source) {
            SeatsResults seats = source.getDecompositionPart();
            if (seats == null) {
                return null;
            }
            WkInformation information = new WkInformation();
            information.descriptors = Iterables.toArray(SeatsResults.descriptors, ComponentDescriptor.class);
            information.estimators = seats.getWienerKolmogorovEstimators();
            information.frequency = source.getSeries().getFrequency();
            return information;
        }
    };
    //</editor-fold>

    public static class WkErrorsUI<V extends IProcDocumentView<?>> extends HtmlItemUI<V, WkInformation> {

        @Override
        protected IHtmlElement getHtmlElement(V host, WkInformation information) {
            return new HtmlModelBasedRevisionsAnalysis(information.frequency.intValue(), information.estimators,
                    information.descriptors);
        }
    }

    public static class StvarUI<V extends IProcDocumentView<?>> extends HtmlItemUI<V, StationaryVarianceDecomposition> {

        @Override
        protected IHtmlElement getHtmlElement(V host, StationaryVarianceDecomposition information) {
            return new HtmlStationaryVarianceDecomposition(information);
        }
    }

    public static class SigSeasUI<V extends IProcDocumentView<?>> extends HtmlItemUI<V, CompositeResults> {

        @Override
        protected IHtmlElement getHtmlElement(V host, CompositeResults information) {
            int[] p99 = SignificantSeasonalityTest.test(information, .01);
            int[] p95 = SignificantSeasonalityTest.test(information, .05);
            return new HtmlSignificantSeasons(p99, p95);
        }
    }

    public static class GrowthRatesUI<V extends IProcDocumentView<?>> extends HtmlItemUI<V, CompositeResults> {

        @Override
        protected IHtmlElement getHtmlElement(V host, CompositeResults information) {
            return new HtmlTramoSeatsGrowthRates(information);
        }
    }
}
