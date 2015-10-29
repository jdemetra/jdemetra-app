/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.advanced.ui;

/**
 *
 * @author Jean Palate
 */
public class StructuralModelView{ //extends SeasonalAdjustmentView {
//
//    public static final String SELECTION = "Selection", STOCHASTIC = "Stochastic series",
//            MODELBASED = "Model-based tests",
//            WKANALYSIS = "WK analysis",
//            WK_COMPONENTS = "Components",
//            WK_FINALS = "Final estimators",
//            WK_PRELIMINARY = "Preliminary estimators",
//            WK_ERRORS = "Errors analysis";
//    public static final Id DECOMPOSITION_SUMMARY = new LinearId(DECOMPOSITION);
//    public static final Id DECOMPOSITION_SERIES = new LinearId(DECOMPOSITION, STOCHASTIC);
//    public static final Id DECOMPOSITION_WK_COMPONENTS = new LinearId(DECOMPOSITION, WKANALYSIS, WK_COMPONENTS);
//    public static final Id DECOMPOSITION_WK_FINALS = new LinearId(DECOMPOSITION, WKANALYSIS, WK_FINALS);
//    public static final Id DECOMPOSITION_TESTS = new LinearId(DECOMPOSITION, MODELBASED);
//    private static final Id MODEL_SELECTION = new LinearId(MODEL, SELECTION);
//    private static final LinkedHashMap<Id, ItemUI<ITsProcessingView, ?>> uiMap_ =
//            new LinkedHashMap<Id, ItemUI<ITsProcessingView, ?>>();
//    private static final LinkedHashMap<Id, InformationExtractor<StmDocument, ?>> infoMap_ =
//            new LinkedHashMap<Id, InformationExtractor<StmDocument, ?>>();
//
//    public static <D> void register(Id id
//            , InformationExtractor<StmDocument, D> info
//            , ItemUI<ITsProcessingView, D> ui
//            ) {
//        uiMap_.put(id, ui);
//        infoMap_.put(id, info);
//    }
//    
//    public static void unregister(Id id){
//        uiMap_.remove(id);
//        infoMap_.remove(id);
//    }
//
//    public static void unregisterAll(){
//        uiMap_.clear();
//        infoMap_.clear();
//    }
//
//    private static ITsViewToolkit toolkit_;
//    
//    public static void registerToolkit(ITsViewToolkit toolkit){
//        toolkit_=toolkit;
//    }
//
//    static {
//
//        // fill the map...
//        InformationExtractor<StmDocument, CompositeResults> saExtractor = new DefaultInformationExtractor<StmDocument, CompositeResults>() {
//
//            @Override
//            public CompositeResults retrieve(StmDocument source) {
//                return source.getResults();
//            }
//        };
//
//        InformationExtractor<StmDocument, PreprocessingModel> pmExtractor = new DefaultInformationExtractor<StmDocument, PreprocessingModel>() {
//
//            @Override
//            public PreprocessingModel retrieve(StmDocument source) {
//                return source.getPreprocessingPart();
//            }
//        };
//
//        InformationExtractor<StmDocument, TsData> resExtractor = new DefaultInformationExtractor<StmDocument, TsData>() {
//
//            @Override
//            public TsData retrieve(StmDocument source) {
//                return source.getDecompositionPart().getResiduals();
//            }
//        };
//
//        InformationExtractor<StmDocument, WkInformation> wkExtractor = new DefaultInformationExtractor<StmDocument, WkInformation>() {
//
//            @Override
//            public WkInformation retrieve(StmDocument source) {
//                WkInformation information = new WkInformation();
//                information.descriptors = Jdk6.Collections.toArray(SeatsResults.airlineDescriptors, ComponentDescriptor.class);
//                information.estimators = source.getDecompositionPart().getWienerKolmogorovEstimators();
//                information.frequency = source.getSeries().getFrequency();
//                return information;
//            }
//        };
//
//        register(MAIN_CHART, saExtractor, new ChartUI(ModellingDictionary.Y, ModellingDictionary.T, ModellingDictionary.SA));
//        register(MAIN_TABLE, saExtractor, new SaTableUI(ModellingDictionary.getFinalSeries(), null));
//        register(MAIN_SI, new DefaultInformationExtractor<StmDocument, TsData[]>() {
//
//            @Override
//            public TsData[] retrieve(StmDocument source) {
//                CompositeResults rslt = source.getResults();
//                TsData seas = rslt.getData(ModellingDictionary.S_LIN, TsData.class);
//                TsData i = rslt.getData(ModellingDictionary.I_LIN, TsData.class);
//                TsData si = null;
//                if (source.getFinalDecomposition().getMode().isMultiplicative()) {
//                    si = TsData.multiply(seas, i);
//                } else {
//                    si = TsData.add(seas, i);
//                }
//                return new TsData[]{seas, si};
//            }
//        }, new SiRatioUI());
//        register(PREPROCESSING_SUMMARY, pmExtractor, new PreprocessingUI());
//        register(PREPROCESSING_REGS, pmExtractor, new RegressorsUI());
//        register(PREPROCESSING_DET, saExtractor, new SaTableUI(ModellingDictionary.getDeterministicSeries(), null));
//        register(MODEL_SELECTION
//                , new DefaultInformationExtractor<StmDocument, StmResults>() {
//
//            @Override
//            public StmResults retrieve(StmDocument source) {
//                return source.getDecompositionPart();
//            }
//        }
//                , new DefaultItemUI<ITsProcessingView, StmResults>() {
//
//            @Override
//            public JComponent getView(ITsProcessingView host, StmResults information) {
//                HtmlBsm html = new HtmlBsm(information.getModel());
//                return host.getToolkit().getHtmlViewer(html);
//            }
//        }
//                );
//        register(MODEL_ARIMA, new DefaultInformationExtractor<StmDocument, LinkedHashMap<String, IArimaModel>>() {
//
//            @Override
//            public LinkedHashMap<String, IArimaModel> retrieve(StmDocument source) {
//                LinkedHashMap<String, IArimaModel> models = new LinkedHashMap<String, IArimaModel>();
//                SarimaModel tmodel = source.getPreprocessingPart().estimation.getRegArima().getArima();
//                models.put("Preprocessing model", tmodel);
//                ArimaModel mmodel = (ArimaModel) source.getDecompositionPart().getUcarimaModel().getModel();
//                models.put("Reduced model", mmodel);
//                return models;
//            }
//        }, new ArimaUI());
//        register(MODEL_RES, resExtractor, new ResidualsUI());
//        register(MODEL_RES_STATS, new DefaultInformationExtractor<StmDocument, NiidTests>() {
//
//            @Override
//            public NiidTests retrieve(StmDocument source) {
//                 TsData res = source.getDecompositionPart().getResiduals();
//                return new NiidTests(res.getValues(), res.getFrequency().intValue(),
//                        source.getPreprocessingPart().description.getArimaComponent().getFreeParametersCount(), true);
//            }
//        }, new ResidualsStatsUI());
//        register(MODEL_RES_DIST, resExtractor, new ResidualsDistUI());
//        register(DECOMPOSITION_SUMMARY, new DefaultInformationExtractor<StmDocument, UcarimaUI.Information>() {
//
//            @Override
//            public UcarimaUI.Information retrieve(StmDocument source) {
//                UcarimaModel ucm = source.getDecompositionPart().getUcarimaModel();
//                UcarimaUI.Information info = new UcarimaUI.Information();
//                info.model = ucm.getModel();
//                info.names = SeatsResults.getComponentsName(ucm);
//                info.cmps = SeatsResults.getComponents(ucm);
//                return info;
//            }
//        }, new UcarimaUI());
//        register(DECOMPOSITION_SERIES, saExtractor, new SaTableUI(ModellingDictionary.getStochasticSeries(), null));
//        register(DECOMPOSITION_WK_COMPONENTS, wkExtractor, new WkComponentsUI());
//        register(DECOMPOSITION_WK_FINALS, wkExtractor, new WkFinalEstimatorsUI());
//        register(DECOMPOSITION_TESTS, new DefaultInformationExtractor<StmDocument, ModelBasedUI.Information>() {
//
//            @Override
//            public ModelBasedUI.Information retrieve(StmDocument source) {
//                ModelBasedUI.Information info = new ModelBasedUI.Information();
//                StmResults rslt = source.getDecompositionPart();
//                info.decomposition = rslt.getComponents();
//                info.ucm = rslt.getUcarimaModel();
//                info.err = rslt.getLikelihood().getSer() * rslt.getResidualsScalingFactor();
//                return info;
//            }
//        }, new ModelBasedUI());
//        register(DIAGNOSTICS_SUMMARY, saExtractor, new DiagnosticsUI());
//        register(DIAGNOSTICS_SEASONALITY, new DefaultInformationExtractor<StmDocument, SeasonalityTestUI.Information>() {
//
//            @Override
//            public SeasonalityTestUI.Information retrieve(StmDocument source) {
//                CompositeResults rslt = source.getResults();
//                TsData seas = rslt.getData(ModellingDictionary.S_LIN, TsData.class);
//                TsData i = rslt.getData(ModellingDictionary.I_LIN, TsData.class);
//                TsData si = null;
//                boolean mul = source.getFinalDecomposition().getMode().isMultiplicative();
//                if (mul) {
//                    si = TsData.multiply(seas, i);
//                } else {
//                    si = TsData.add(seas, i);
//                }
//                SeasonalityTestUI.Information info = new SeasonalityTestUI.Information();
//                info.sa = rslt.getData(ModellingDictionary.SA_LIN, TsData.class);
//                info.si = si;
//                info.mul = mul;
//                return info;
//            }
//        }, new SeasonalityTestUI());
//
//        register(DIAGNOSTICS_SPECTRUM_RES, resExtractor, new SpectrumUI(true));
//        register(DIAGNOSTICS_SPECTRUM_I, new DefaultInformationExtractor<StmDocument, TsData>() {
//
//            @Override
//            public TsData retrieve(StmDocument source) {
//                return source.getFinalDecomposition().getSeries(ComponentType.Irregular, ComponentInformation.Value);
//            }
//        }, new SpectrumUI(false));
//        register(DIAGNOSTICS_SPECTRUM_SA, new DefaultInformationExtractor<StmDocument, TsData>() {
//
//            @Override
//            public TsData retrieve(StmDocument source) {
//                TsData s = source.getFinalDecomposition().getSeries(ComponentType.SeasonallyAdjusted, ComponentInformation.Value);
//                return s.delta(1);
//            }
//        }, new SpectrumUI(false));
//
//    }
//    private StmDocument doc_;
//
//    public StructuralModelView(StmDocument doc) {
//        doc_ = doc;
//        if (toolkit_ != null)
//            setToolkit(toolkit_);
//    }
//
//    @Override
//    protected ItemUI getUI(Id id) {
//        return (ItemUI<ITsProcessingView, ?>) uiMap_.get(id);
//    }
//
//    @Override
//    protected Object getInformation(Id id) {
//
//        InformationExtractor<StmDocument, ?> extractor = infoMap_.get(id);
//        if (extractor != null) {
//            return extractor.retrieve(doc_);
//        } else {
//            return this;
//        }
//    }
//
//    @Override
//    public List<Id> getItems() {
//        return new ArrayList<Id>(uiMap_.keySet());
//    }
//
//    @Override
//    public Id getPreferredView() {
//        return MAIN_CHART;
//    }
//
//    @Override
//    public TsDocument getTsDocument() {
//        return doc_;
//    }
//
//    @Override
//    public void refresh() {
//        super.refresh();
//        for (InformationExtractor<StmDocument, ?> ex : infoMap_.values()) {
//            if (ex != null) {
//                ex.flush(doc_);
//            }
//        }
//    }
//
//    @Override
//    public void dispose() {
//        super.dispose();
//        for (InformationExtractor<StmDocument, ?> ex : infoMap_.values()) {
//            if (ex != null) {
//                ex.flush(doc_);
//            }
//        }
//    }
}
