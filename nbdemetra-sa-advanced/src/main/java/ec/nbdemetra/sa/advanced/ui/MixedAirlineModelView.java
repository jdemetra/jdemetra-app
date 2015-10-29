/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.advanced.ui;

/**
 *
 * @author Jean Palate
 */
public class MixedAirlineModelView{// extends SeasonalAdjustmentView {
//
//    public static final String SELECTION = "Selection", NOISE = "Noise", WK = "Wiener-Kolmogorov analysis";
//    public static final String COMPONENTS = "Components";
//    public static final String FINALS = "Final estimators";
//    private static final Id MODEL_SELECTION = new LinearId(MODEL, SELECTION);
//    private static final Id MODEL_NOISE = new LinearId(MODEL, NOISE);
//    private static final Id MODEL_WK_COMPONENTS = new LinearId(MODEL, WK, COMPONENTS);
//    private static final Id MODEL_WK_FINALS = new LinearId(MODEL, WK, FINALS);
//    private static final LinkedHashMap<Id, ItemUI<ITsProcessingView, ?>> uiMap_ =
//            new LinkedHashMap<Id, ItemUI<ITsProcessingView, ?>>();
//    private static final LinkedHashMap<Id, InformationExtractor<MixedAirlineDocument, ?>> infoMap_ =
//            new LinkedHashMap<Id, InformationExtractor<MixedAirlineDocument, ?>>();
//
//    public static <D> void register(Id id
//            , InformationExtractor<MixedAirlineDocument, D> info
//            , ItemUI<ITsProcessingView, D> ui) {
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
//    public static void registerToolkit(ITsViewToolkit toolkit) {
//        toolkit_ = toolkit;
//    }
//
//    static {
//
//        // fill the map...
//        InformationExtractor<MixedAirlineDocument, CompositeResults> saExtractor = new DefaultInformationExtractor<MixedAirlineDocument, CompositeResults>() {
//
//            @Override
//            public CompositeResults retrieve(MixedAirlineDocument source) {
//                return source.getResults();
//            }
//        };
//
//        InformationExtractor<MixedAirlineDocument, PreprocessingModel> pmExtractor = new DefaultInformationExtractor<MixedAirlineDocument, PreprocessingModel>() {
//
//            @Override
//            public PreprocessingModel retrieve(MixedAirlineDocument source) {
//                return source.getPreprocessingPart();
//            }
//        };
//
//        InformationExtractor<MixedAirlineDocument, TsData> resExtractor = new DefaultInformationExtractor<MixedAirlineDocument, TsData>() {
//
//            @Override
//            public TsData retrieve(MixedAirlineDocument source) {
//                return source.getDecompositionPart().getResiduals();
//            }
//        };
//
//        InformationExtractor<MixedAirlineDocument, WkInformation> wkExtractor = new DefaultInformationExtractor<MixedAirlineDocument, WkInformation>() {
//
//            @Override
//            public WkInformation retrieve(MixedAirlineDocument source) {
//                WkInformation information = new WkInformation();
//                information.descriptors = Jdk6.Collections.toArray(SeatsResults.descriptors, ComponentDescriptor.class);
//                information.estimators = source.getDecompositionPart().getWienerKolmogorovEstimators();
//                information.frequency = source.getSeries().getFrequency();
//                return information;
//            }
//        };
//
//        register(MAIN_CHART, saExtractor, new ChartUI(ModellingDictionary.Y, ModellingDictionary.T, ModellingDictionary.SA));
//        register(MAIN_TABLE, saExtractor, new SaTableUI(ModellingDictionary.getFinalSeries(), null));
//        register(MAIN_SI, new DefaultInformationExtractor<MixedAirlineDocument, TsData[]>() {
//
//            @Override
//            public TsData[] retrieve(MixedAirlineDocument source) {
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
//                , new DefaultInformationExtractor<MixedAirlineDocument, MixedAirlineResults>() {
//
//            @Override
//            public MixedAirlineResults retrieve(MixedAirlineDocument source) {
//                return source.getDecompositionPart();
//            }
//        }
//                ,new DefaultItemUI<ITsProcessingView, MixedAirlineResults>() {
//
//            @Override
//            public JComponent getView(ITsProcessingView host, MixedAirlineResults information) {
//                HtmlMixedAirline html = new HtmlMixedAirline(information.getAllModels(),
//                        information.getBestModelPosition());
//                return host.getToolkit().getHtmlViewer(html);
//            }
//        });
//        register(MODEL_NOISE, new DefaultInformationExtractor<MixedAirlineDocument, CompositeResults>() {
//
//            @Override
//            public CompositeResults retrieve(MixedAirlineDocument source) {
//                return source.getResults();
//            }
//        }, new ChartUI(MixedAirlineResults.NOISE_DATA, MixedAirlineResults.IRREGULAR));
//        register(MODEL_ARIMA, new DefaultInformationExtractor<MixedAirlineDocument, LinkedHashMap<String, IArimaModel>>() {
//
//            @Override
//            public LinkedHashMap<String, IArimaModel> retrieve(MixedAirlineDocument source) {
//                LinkedHashMap<String, IArimaModel> models = new LinkedHashMap<String, IArimaModel>();
//                SarimaModel tmodel = source.getPreprocessingPart().estimation.getRegArima().getArima();
//                models.put("Preprocessing model", tmodel);
//                SarimaModel mmodel = source.getDecompositionPart().getBestModel().model.getAirline();
//                models.put("Final model", mmodel);
//                return models;
//            }
//        }, new ArimaUI());
//        register(MODEL_WK_COMPONENTS, wkExtractor, new WkComponentsUI());
//        register(MODEL_WK_FINALS, wkExtractor, new WkFinalEstimatorsUI());
//        register(MODEL_RES, resExtractor, new ResidualsUI());
//        register(MODEL_RES_STATS, new DefaultInformationExtractor<MixedAirlineDocument, NiidTests>() {
//
//            @Override
//            public NiidTests retrieve(MixedAirlineDocument source) {
//                TsData res = source.getDecompositionPart().getResiduals();
//                return new NiidTests(res.getValues(), res.getFrequency().intValue(),
//                        source.getPreprocessingPart().description.getArimaComponent().getFreeParametersCount(), true);
//            }
//        }, new ResidualsStatsUI());
//        register(MODEL_RES_DIST, resExtractor, new ResidualsDistUI());
//        register(DIAGNOSTICS_SUMMARY, saExtractor, new DiagnosticsUI());
//        register(DIAGNOSTICS_SEASONALITY, new DefaultInformationExtractor<MixedAirlineDocument, SeasonalityTestUI.Information>() {
//
//            @Override
//            public SeasonalityTestUI.Information retrieve(MixedAirlineDocument source) {
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
//        register(DIAGNOSTICS_SPECTRUM_I, new DefaultInformationExtractor<MixedAirlineDocument, TsData>() {
//
//            @Override
//            public TsData retrieve(MixedAirlineDocument source) {
//                ISeriesDecomposition decomposition=source.getFinalDecomposition();
//                return decomposition.getSeries(ComponentType.Irregular, ComponentInformation.Value);
//            }
//        }, new SpectrumUI(false));
//        register(DIAGNOSTICS_SPECTRUM_SA, new DefaultInformationExtractor<MixedAirlineDocument, TsData>() {
//
//            @Override
//            public TsData retrieve(MixedAirlineDocument source) {
//                ISeriesDecomposition decomposition=source.getFinalDecomposition();
//                TsData s = decomposition.getSeries(ComponentType.SeasonallyAdjusted, ComponentInformation.Value);
//                return s.delta(1);
//            }
//        }, new SpectrumUI(false));
//
//    }
//    private MixedAirlineDocument doc_;
//
//    public MixedAirlineModelView(MixedAirlineDocument doc) {
//        doc_ = doc;
//        if (toolkit_ != null) {
//            setToolkit(toolkit_);
//        }
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
//        InformationExtractor<MixedAirlineDocument, ?> extractor = infoMap_.get(id);
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
//        for (InformationExtractor<MixedAirlineDocument, ?> ex : infoMap_.values()) {
//            if (ex != null) {
//                ex.flush(doc_);
//            }
//        }
//    }
//
//    @Override
//    public void dispose() {
//        super.dispose();
//        for (InformationExtractor<MixedAirlineDocument, ?> ex : infoMap_.values()) {
//            if (ex != null) {
//                ex.flush(doc_);
//            }
//        }
//    }
}
