/*
 * Copyright 2022 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.sts.ui;

import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicReference;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
public class StructuralModelViewFactory extends SaDocumentViewFactory<StsSpecification, StsDocument> {

    public static final String SELECTION = "Selection",
            STOCHASTIC = "Stochastic series",
            COMPONENTS = "Components",
            STM = "Structural model components",
            MODELBASED = "Model-based tests",
            WKANALYSIS = "WK analysis",
            WK_COMPONENTS = "Components",
            WK_FINALS = "Final estimators",
            WK_PRELIMINARY = "Preliminary estimators",
            WK_ERRORS = "Errors analysis";
    public static final Id DECOMPOSITION_SUMMARY = new LinearId(DECOMPOSITION);
    public static final Id DECOMPOSITION_SERIES = new LinearId(DECOMPOSITION, STOCHASTIC);
    public static final Id DECOMPOSITION_CMPSERIES = new LinearId(DECOMPOSITION, COMPONENTS);
    public static final Id DECOMPOSITION_STM = new LinearId(DECOMPOSITION, STM);
    public static final Id DECOMPOSITION_WK_COMPONENTS = new LinearId(DECOMPOSITION, WKANALYSIS, WK_COMPONENTS);
    public static final Id DECOMPOSITION_WK_FINALS = new LinearId(DECOMPOSITION, WKANALYSIS, WK_FINALS);
    public static final Id DECOMPOSITION_TESTS = new LinearId(DECOMPOSITION, MODELBASED);
    private static final Id MODEL_SELECTION = new LinearId(MODEL, SELECTION);
    private static final Id MODEL_LIKELIHOOD = new LinearId(MODEL, LIKELIHOOD);
    private static final AtomicReference<IProcDocumentViewFactory<StsDocument>> INSTANCE = new AtomicReference(new StructuralModelViewFactory());

    public static IProcDocumentViewFactory<StsDocument> getDefault() {
        return INSTANCE.get();
    }

    public static void setDefault(IProcDocumentViewFactory<StsDocument> factory) {
        INSTANCE.set(factory);
    }

    public static InformationExtractor<StsDocument, WkInformation> wkExtractor() {
        return WkExtractor.INSTANCE;
    }

    public static InformationExtractor<StsDocument, TsData> stmresExtractor() {
        return StsresExtractor.INSTANCE;
    }

    public StructuralModelViewFactory() {
        registerDefault();
        registerFromLookup(StsDocument.class);
    }

    @Override
    public Id getPreferredView() {
        return MAIN_CHARTS_LOW;
    }

    @Deprecated
    public void registerDefault() {
        registerMainViews();
        registerLightPreprocessingViews();
        registerStsViews();
        registerBenchmarkingView();
        registerDiagnosticsViews();
    }

    //<editor-fold defaultstate="collapsed" desc="REGISTER MAIN VIEWS">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 101010)
    public static class MainChartsLowFactory extends SaDocumentViewFactory.MainChartsLowFactory<StsDocument> {

        public MainChartsLowFactory() {
            super(StsDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 101020)
    public static class MainChartsHighFactory extends SaDocumentViewFactory.MainChartsHighFactory<StsDocument> {

        public MainChartsHighFactory() {
            super(StsDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 102000)
    public static class MainTableFactory extends SaDocumentViewFactory.MainTableFactory<StsDocument> {

        public MainTableFactory() {
            super(StsDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER SI VIEW">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 103000)
    public static class MainSiFactory extends SaDocumentViewFactory.MainSiFactory<StsDocument> {

        public MainSiFactory() {
            super(StsDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER LIGHT PREPROCESSING VIEWS">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 200000)
    public static class PreprocessingSummaryFactory extends SaDocumentViewFactory.PreprocessingSummaryFactory<StsDocument> {

        public PreprocessingSummaryFactory() {
            super(StsDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 201000)
    public static class PreprocessingRegsFactory extends SaDocumentViewFactory.PreprocessingRegsFactory<StsDocument> {

        public PreprocessingRegsFactory() {
            super(StsDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 202000)
    public static class PreprocessingDetFactory extends SaDocumentViewFactory.PreprocessingDetFactory<StsDocument> {

        public PreprocessingDetFactory() {
            super(StsDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 203000)
    public static class LikelihoodFactory extends SaDocumentViewFactory.LikelihoodFactory<StsDocument> {

        public LikelihoodFactory() {
            super(StsDocument.class);
            setAsync(true);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER STM">
    @Deprecated
    public void registerStsViews() {
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 301000)
    public static class ModelSelectionFactory extends ItemFactory<StsEstimation> {

        public ModelSelectionFactory() {
            super(MODEL_SELECTION, new DefaultInformationExtractor<StsDocument, StsEstimation>() {
                @Override
                public StsEstimation retrieve(StsDocument source) {
                    return source.getEstimationPart();
                }
            }, new HtmlItemUI<View, StsEstimation>() {
                @Override
                public IHtmlElement getHtmlElement(View host, StsEstimation information) {
                    return new HtmlBsm(information);
                }
            });
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 302000)
    public static class ModelArimaFactory extends ItemFactory<LinkedHashMap<String, IArimaModel>> {

        public ModelArimaFactory() {
            super(MODEL_ARIMA, new DefaultInformationExtractor<StsDocument, LinkedHashMap<String, IArimaModel>>() {
                @Override
                public LinkedHashMap<String, IArimaModel> retrieve(StsDocument source) {
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
            super(MODEL_RES_STATS, new DefaultInformationExtractor<StsDocument, NiidTests>() {
                @Override
                public NiidTests retrieve(StsDocument source) {
                    TsData res = source.getEstimationPart().getResiduals();
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
    public static class StsLikelihoodFactory extends ItemFactory<Functions> {

        public StsLikelihoodFactory() {
            super(MODEL_LIKELIHOOD, LikelihoodExtractor.INSTANCE, new SurfacePlotterUI());
        }

    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 400000)
    public static class DecompositionSummaryFactory extends ItemFactory<UcarimaUI.Information> {

        public DecompositionSummaryFactory() {
            super(DECOMPOSITION_SUMMARY, new DefaultInformationExtractor<StsDocument, UcarimaUI.Information>() {
                @Override
                public UcarimaUI.Information retrieve(StsDocument source) {
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
    public static class LinearizedSeriesFactory extends ItemFactory<CompositeResults> {

        private static String[] generateItems() {
            StringBuilder y = new StringBuilder();
            y.append(DocumentManager.COMPOSITE).append("Series (lin)=,").append(InformationSet.item(GenericSaProcessingFactory.DECOMPOSITION, ModellingDictionary.Y_LIN))
                    .append(',').append(InformationSet.item(GenericSaProcessingFactory.DECOMPOSITION, ModellingDictionary.Y_LIN)).append(SeriesInfo.F_SUFFIX);
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
            return new String[]{y.toString(), sa.toString(), t.toString(), s.toString(), i.toString()};
        }

        public ComponentsSeriesFactory() {
            super(DECOMPOSITION_CMPSERIES, saExtractor(), new GenericTableUI(false, generateItems()));
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 401020)
    public static class StsSeriesFactory extends ItemFactory<CompositeResults> {

        public StsSeriesFactory() {
            super(DECOMPOSITION_STM, saExtractor(), new SaTableUI("level", "slope",
                    "cycle", "noise", "seasonal"));
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
            super(DECOMPOSITION_TESTS, new DefaultInformationExtractor<StsDocument, ModelBasedUI.Information>() {
                @Override
                public ModelBasedUI.Information retrieve(StsDocument source) {
                    ModelBasedUI.Information info = new ModelBasedUI.Information();
                    StsDecomposition rslt = source.getDecompositionPart();
                    info.decomposition = rslt.getComponents();
                    info.ucm = rslt.getUcarimaModel();
                    info.err = source.getEstimationPart().getLikelihood().getSer() * rslt.getResidualsScalingFactor();
                    return info;
                }
            }, new ModelBasedUI());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER BENCHMARKING VIEW">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 500000)
    public static class BenchmarkingFactory extends SaDocumentViewFactory.BenchmarkingFactory<StsDocument> {

        public BenchmarkingFactory() {
            super(StsDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER DIAGNOSTICS VIEWS">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 600000)
    public static class DiagnosticsSummaryFactory extends SaDocumentViewFactory.DiagnosticsSummaryFactory<StsDocument> {

        public DiagnosticsSummaryFactory() {
            super(StsDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 602010)
    public static class DiagnosticsSpectrumResFactory extends SaDocumentViewFactory.DiagnosticsSpectrumResFactory<StsDocument> {

        public DiagnosticsSpectrumResFactory() {
            super(StsDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 602020)
    public static class DiagnosticsSpectrumIFactory extends SaDocumentViewFactory.DiagnosticsSpectrumIFactory<StsDocument> {

        public DiagnosticsSpectrumIFactory() {
            super(StsDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 602030)
    public static class DiagnosticsSpectrumSaFactory extends SaDocumentViewFactory.DiagnosticsSpectrumSaFactory<StsDocument> {

        public DiagnosticsSpectrumSaFactory() {
            super(StsDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER SEASONALITY VIEW">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 601000)
    public static class DiagnosticsSeasonalityFactory extends SaDocumentViewFactory.DiagnosticsSeasonalityFactory<StsDocument> {

        public DiagnosticsSeasonalityFactory() {
            super(StsDocument.class);
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
    public static class DiagnosticsSlidingTdFactory extends SaDocumentViewFactory.DiagnosticsSlidingTdFactory<StsDocument> {

        public DiagnosticsSlidingTdFactory() {
            super(StsDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 603030)
    public static class DiagnosticsSlidingSaFactory extends SaDocumentViewFactory.DiagnosticsSlidingSaFactory<StsDocument> {

        public DiagnosticsSlidingSaFactory() {
            super(StsDocument.class);
        }
    }
    //</editor-fold>

    private static class ItemFactory<I> extends ComposedProcDocumentItemFactory<StsDocument, I> {

        public ItemFactory(Id itemId, InformationExtractor<? super StsDocument, I> informationExtractor, ItemUI<? extends IProcDocumentView<StsDocument>, I> itemUI) {
            super(StsDocument.class, itemId, informationExtractor, itemUI);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="EXTRACTORS IMPL">
    private static class WkExtractor extends DefaultInformationExtractor<StsDocument, WkInformation> {

        private static final WkExtractor INSTANCE = new WkExtractor();

        @Override
        public WkInformation retrieve(StsDocument source) {
            WkInformation information = new WkInformation();
            information.descriptors = Iterables.toArray(SeatsResults.airlineDescriptors, ComponentDescriptor.class);
            information.estimators = source.getDecompositionPart().getWienerKolmogorovEstimators();
            information.frequency = source.getSeries().getFrequency();
            return information;
        }
    };

    private static class StsresExtractor extends DefaultInformationExtractor<StsDocument, TsData> {

        private static final StsresExtractor INSTANCE = new StsresExtractor();

        @Override
        public TsData retrieve(StsDocument source) {
            StsEstimation stm = source.getEstimationPart();
            return stm.getResiduals();
        }
    };

    private static class LikelihoodExtractor extends TsDocumentInformationExtractor<StsDocument, Functions> {

        public static final LikelihoodExtractor INSTANCE = new LikelihoodExtractor();

        @Override
        protected Functions buildInfo(StsDocument source) {
            StsEstimation stm = source.getEstimationPart();
            if (stm == null) {
                return null;
            } else {
                ProxyFunction proxy = new ProxyFunction(stm.likelihoodFunction());
                return Functions.create(proxy, proxy.evaluate(stm.maxLikelihoodFunction().getParameters()));
//                return null;
//                return Functions.create(stm.likelihoodFunction(), stm.maxLikelihoodFunction());
            }
        }
    };

    //</editor-fold>
}

class ProxyFunctionInstance implements IFunctionInstance {

    final ec.demetra.realfunctions.IFunctionPoint pt;

    ProxyFunctionInstance(ec.demetra.realfunctions.IFunctionPoint pt) {
        this.pt = pt;
    }

    @Override
    public IReadDataBlock getParameters() {
        return pt.getParameters();
    }

    @Override
    public double getValue() {
        return pt.getValue();
    }

}

class ProxyFunction implements IFunction {

    final ec.demetra.realfunctions.IFunction df;

    ProxyFunction(ec.demetra.realfunctions.IFunction df) {
        this.df = df;
    }

    @Override
    public IFunctionInstance evaluate(IReadDataBlock parameters) {
        return new ProxyFunctionInstance(df.evaluate(parameters));
    }

    @Override
    public IFunctionDerivatives getDerivatives(IFunctionInstance point) {
        // not used
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IParametersDomain getDomain() {
        // not used
        return new ProxyDomain(df.getDomain());
    }

}

class ProxyDomain implements IParametersDomain {

    final ec.demetra.realfunctions.IParametersDomain domain;

    ProxyDomain(ec.demetra.realfunctions.IParametersDomain domain) {
        this.domain = domain;
    }

    @Override
    public boolean checkBoundaries(IReadDataBlock inparams) {
        return domain.checkBoundaries(inparams);
    }

    @Override
    public double epsilon(IReadDataBlock inparams, int idx) {
        return domain.epsilon(inparams, idx);
    }

    @Override
    public int getDim() {
        return domain.getDim();
    }

    @Override
    public double lbound(int idx) {
        return domain.lbound(idx);
    }

    @Override
    public double ubound(int idx) {
        return domain.ubound(idx);
    }

    @Override
    public ParamValidation validate(IDataBlock ioparams) {
        return ParamValidation.valueOf(domain.validate(ioparams).name());
    }

    @Override
    public String getDescription(int idx) {
        return domain.getDescription(idx);
    }

}
