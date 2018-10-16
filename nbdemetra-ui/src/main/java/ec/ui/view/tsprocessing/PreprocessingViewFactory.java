/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.nbdemetra.ui.chart3d.functions.SurfacePlotterUI;
import ec.nbdemetra.ui.chart3d.functions.SurfacePlotterUI.Functions;
import ec.tss.documents.TsDocument;
import ec.tss.html.IHtmlElement;
import ec.tss.html.implementation.HtmlInformationSet;
import ec.tstoolkit.algorithm.IProcSpecification;
import ec.tstoolkit.arima.IArimaModel;
import ec.tstoolkit.modelling.ModellingDictionary;
import ec.tstoolkit.modelling.SeriesInfo;
import ec.tstoolkit.modelling.arima.PreprocessingModel;
import ec.tstoolkit.modelling.arima.diagnostics.IOneStepAheadForecastingTest;
import ec.tstoolkit.modelling.arima.diagnostics.OneStepAheadForecastingTest;
import ec.tstoolkit.sarima.SarimaModel;
import ec.tstoolkit.stats.NiidTests;
import ec.tstoolkit.timeseries.TsPeriodSelector;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.utilities.DefaultInformationExtractor;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.InformationExtractor;
import ec.tstoolkit.utilities.LinearId;
import ec.ui.view.tsprocessing.sa.SaTableUI;
import java.util.LinkedHashMap;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate
 */
public abstract class PreprocessingViewFactory<S extends IProcSpecification, D extends TsDocument<S, PreprocessingModel>> extends ProcDocumentViewFactory<D> {

    public static final String INPUT = "Input", SPEC = "Specifications", SERIES = "Series",
            MAIN = "Main results",
            PREPROCESSING = "Pre-processing",
            MODEL = "Model",
            DECOMPOSITION = "Decomposition",
            BENCHMARKING = "Benchmarking",
            DIAGNOSTICS = "Diagnostics";
    public static final String PROCESSING = "Processing",
            FCASTS = "Forecasts",
            OSAMPLE = "Out-of-sample test",
            DETAILS = "Details",
            TABLE = "Table",
            STEPS = "Steps",
            PREADJUSTMENT = "Pre-adjustment series",
            ARIMA = "Arima",
            REGRESSORS = "Regressors",
            RESIDUALS = "Residuals",
            STATS = "Statistics",
            DISTRIBUTION = "Distribution",
            SPECTRAL = "Spectral analysis",
            REVISIONS = "Revisions analysis",
            SLIDINGSPANS = "Sliding spans",
            STABILITY = "Model stability",
            LIKELIHOOD = "Likelihood";
    public static final Id INPUT_SPEC = new LinearId(INPUT, SPEC),
            INPUT_SERIES = new LinearId(INPUT, SERIES), MODEL_SUMMARY = new LinearId(MODEL),
            MODEL_DET = new LinearId(MODEL, PREADJUSTMENT),
            MODEL_FCASTS = new LinearId(MODEL, FCASTS),
            MODEL_FCASTS_TABLE = new LinearId(MODEL, FCASTS, TABLE),
            MODEL_FCASTS_OUTOFSAMPLE = new LinearId(MODEL, FCASTS, OSAMPLE),
            MODEL_REGS = new LinearId(MODEL, REGRESSORS),
            MODEL_ARIMA = new LinearId(MODEL, ARIMA),
            MODEL_RES = new LinearId(MODEL, RESIDUALS),
            MODEL_RES_STATS = new LinearId(MODEL, RESIDUALS, STATS),
            MODEL_RES_DIST = new LinearId(MODEL, RESIDUALS, DISTRIBUTION),
            MODEL_RES_SPECTRUM = new LinearId(MODEL, RESIDUALS, SPECTRAL),
            MODEL_LIKELIHOOD = new LinearId(MODEL, LIKELIHOOD),
            PROCESSING_DETAILS = new LinearId(PROCESSING, DETAILS),
            PROCESSING_STEPS = new LinearId(PROCESSING, STEPS),
            PREPROCESSING_SUMMARY = new LinearId(PREPROCESSING),
            PREPROCESSING_FCASTS = new LinearId(PREPROCESSING, FCASTS),
            PREPROCESSING_FCASTS_OUTOFSAMPLE = new LinearId(PREPROCESSING, FCASTS, OSAMPLE),
            PREPROCESSING_DETAILS = new LinearId(PREPROCESSING, DETAILS),
            PREPROCESSING_REGS = new LinearId(PREPROCESSING, REGRESSORS),
            PREPROCESSING_ARIMA = new LinearId(PREPROCESSING, ARIMA),
            PREPROCESSING_DET = new LinearId(PREPROCESSING, PREADJUSTMENT),
            PREPROCESSING_RES = new LinearId(PREPROCESSING, RESIDUALS),
            PREPROCESSING_RES_STATS = new LinearId(PREPROCESSING, RESIDUALS, STATS),
            PREPROCESSING_RES_DIST = new LinearId(PREPROCESSING, RESIDUALS, DISTRIBUTION);

    @Deprecated
    public void registerDefault() {
        registerSummary();
        registerForecasts();
        registerModel();
        registerResiduals();
        registerDetails();
    }

    @Override
    public Id getPreferredView() {
        return MODEL_SUMMARY;
    }

    //<editor-fold defaultstate="collapsed" desc="REGISTER SUMMARY">
    @Deprecated
    public void registerSummary() {
    }

    protected static class InputFactory<D extends TsDocument<? extends IProcSpecification, PreprocessingModel>>
            extends ItemFactory<D, D> {

        protected InputFactory(Class<D> documentType) {
            super(documentType, INPUT_SERIES, new ProcDocumentViewFactory.DoNothingExtractor<>(), new DefaultItemUI<IProcDocumentView<D>, D>() {
                @Override
                public JComponent getView(IProcDocumentView<D> host, D information) {
                    return host.getToolkit().getGrid(information.getInput()); //To change body of generated methods, choose Tools | Templates.
                }
            });
        }
    }

    protected static class SpecAllFactory<D extends TsDocument<? extends IProcSpecification, PreprocessingModel>>
            extends ItemFactory<D, IProcSpecification> {

        protected SpecAllFactory(Class<D> documentType) {
            super(documentType, INPUT_SPEC, specExtractor(), new HtmlItemUI<IProcDocumentView<D>, IProcSpecification>() {
                @Override
                protected IHtmlElement getHtmlElement(IProcDocumentView<D> host, IProcSpecification information) {
                    return new HtmlInformationSet(information.write(true));
                }
            });
        }
    }

    protected static class LikelihoodFactory<D extends TsDocument<? extends IProcSpecification, PreprocessingModel>>
            extends ItemFactory<D, Functions> {

        protected LikelihoodFactory(Class<D> documentType) {
            super(documentType, MODEL_LIKELIHOOD, LikelihoodExtractor.INSTANCE, new SurfacePlotterUI());
        }
    }

    protected static class SummaryFactory<D extends TsDocument<? extends IProcSpecification, PreprocessingModel>>
            extends ItemFactory<D, PreprocessingModel> {

        protected SummaryFactory(Class<D> documentType) {
            super(documentType, MODEL_SUMMARY, PmExtractor.INSTANCE, new PreprocessingUI());
        }
    }

    public static InformationExtractor<TsDocument<? extends IProcSpecification, PreprocessingModel>, IProcSpecification> specExtractor() {
        return SpecExtractor.INSTANCE;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="REGISTER FORECASTS">
    @Deprecated
    public void registerForecasts() {
    }

    protected static class ModelFCastsFactory<D extends TsDocument<? extends IProcSpecification, PreprocessingModel>>
            extends ItemFactory<D, EstimationUI.Information> {

        protected ModelFCastsFactory(Class<D> documentType) {
            super(documentType, MODEL_FCASTS, new DefaultInformationExtractor<D, EstimationUI.Information>() {
                @Override
                public EstimationUI.Information retrieve(D source) {
                    PreprocessingModel model = source.getResults();
                    TsData orig = model.description.getOriginal();
                    TsPeriodSelector sel = new TsPeriodSelector();
                    sel.last(3 * orig.getFrequency().intValue());
                    return new EstimationUI.Information(orig.select(sel),
                            model.getData("y_f", TsData.class),
                            model.getData("y_ef", TsData.class),
                            1.96);
                }
            }, new EstimationUI());
        }
    }

    protected static class PreprocessingFCastsTableFactory<D extends TsDocument<? extends IProcSpecification, PreprocessingModel>>
            extends ItemFactory<D, PreprocessingModel> {

        private static String[] generateItems() {
            return new String[]{ModellingDictionary.Y + SeriesInfo.F_SUFFIX, ModellingDictionary.Y + SeriesInfo.EF_SUFFIX};
        }

        protected PreprocessingFCastsTableFactory(Class<D> documentType) {
            super(documentType, MODEL_FCASTS_TABLE, PmExtractor.INSTANCE, new GenericTableUI(false, generateItems()));
        }
    }

    protected static class ModelFCastsOutFactory<D extends TsDocument<? extends IProcSpecification, PreprocessingModel>>
            extends ItemFactory<D, IOneStepAheadForecastingTest> {

        protected ModelFCastsOutFactory(Class<D> documentType) {
            super(documentType, MODEL_FCASTS_OUTOFSAMPLE, new DefaultInformationExtractor<D, IOneStepAheadForecastingTest>() {
                @Override
                public IOneStepAheadForecastingTest retrieve(D source) {
                    PreprocessingModel model = source.getResults();
                    int lback;
                    TsFrequency freq = model.description.getSeriesDomain().getFrequency();
                    switch (freq) {
                        case Monthly:
                            lback = 18;
                            break;
                        case Quarterly:
                            lback = 6;
                            break;
                        case BiMonthly:
                            lback = 9;
                            break;
                        default:
                            lback = 5;
                            break;
                    }
                    OneStepAheadForecastingTest test = new OneStepAheadForecastingTest(lback);
                    if (test.test(model.estimation.getRegArima())) {
                        return test;
                    } else {
                        return null;
                    }
                }
            }, new OutOfSampleTestUI());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER MODEL">
    @Deprecated
    public void registerModel() {
    }

    public static class ModelRegsFactory<D extends TsDocument<? extends IProcSpecification, PreprocessingModel>>
            extends ItemFactory<D, PreprocessingModel> {

        protected ModelRegsFactory(Class<D> documentType) {
            super(documentType, MODEL_REGS, PmExtractor.INSTANCE, new RegressorsUI());
        }
    }

    public static class ModelArimaFactory<D extends TsDocument<? extends IProcSpecification, PreprocessingModel>>
            extends ItemFactory<D, LinkedHashMap<String, IArimaModel>> {

        protected ModelArimaFactory(Class<D> documentType) {
            super(documentType, MODEL_ARIMA, new DefaultInformationExtractor<D, LinkedHashMap<String, IArimaModel>>() {
                @Override
                public LinkedHashMap<String, IArimaModel> retrieve(D source) {
                    LinkedHashMap<String, IArimaModel> models = new LinkedHashMap<>();
                    PreprocessingModel pm = source.getResults();
                    if (pm == null) {
                        return null;
                    }
                    SarimaModel model = pm.estimation.getArima();
                    models.put("Arima model", model);
                    return models;
                }
            }, new ArimaUI());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER RESIDUALS">
    @Deprecated
    public void registerResiduals() {
    }

    protected static class ModelResFactory<D extends TsDocument<? extends IProcSpecification, PreprocessingModel>>
            extends ItemFactory<D, TsData> {

        protected ModelResFactory(Class<D> documentType) {
            super(documentType, MODEL_RES, ResExtractor.INSTANCE, new ResidualsUI());
        }
    }

    protected static class ModelResStatsFactory<D extends TsDocument<? extends IProcSpecification, PreprocessingModel>>
            extends ItemFactory<D, NiidTests> {

        protected ModelResStatsFactory(Class<D> documentType) {
            super(documentType, MODEL_RES_STATS, new DefaultInformationExtractor<D, NiidTests>() {
                @Override
                public NiidTests retrieve(D source) {
                    PreprocessingModel rslt = source.getResults();
                    TsData res = rslt.getFullResiduals();
                    return new NiidTests(res, res.getFrequency().intValue(),
                            rslt.description.getArimaComponent().getFreeParametersCount(), true);
                }
            }, new ResidualsStatsUI());
        }
    }

    protected static class ModelResDist<D extends TsDocument<? extends IProcSpecification, PreprocessingModel>>
            extends ItemFactory<D, TsData> {

        protected ModelResDist(Class<D> documentType) {
            super(documentType, MODEL_RES_DIST, ResExtractor.INSTANCE, new ResidualsDistUI());
        }
    }

    protected static class ModelResSpectrum<D extends TsDocument<? extends IProcSpecification, PreprocessingModel>>
            extends ItemFactory<D, TsData> {

        protected ModelResSpectrum(Class<D> documentType) {
            super(documentType, MODEL_RES_SPECTRUM, ResExtractor.INSTANCE, new SpectrumUI(true));
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER DETAILS">
    @Deprecated
    public void registerDetails() {
    }
    //</editor-fold>

    private static class ItemFactory<D extends TsDocument<? extends IProcSpecification, PreprocessingModel>, I> extends ComposedProcDocumentItemFactory<D, I> {

        public ItemFactory(Class<D> documentType, Id itemId, InformationExtractor<? super D, I> informationExtractor, ItemUI<? extends IProcDocumentView<D>, I> itemUI) {
            super(documentType, itemId, informationExtractor, itemUI);
        }
    }

    protected static class PmExtractor extends DefaultInformationExtractor<TsDocument<? extends IProcSpecification, PreprocessingModel>, PreprocessingModel> {

        static final PmExtractor INSTANCE = new PmExtractor();

        @Override
        public PreprocessingModel retrieve(TsDocument<? extends IProcSpecification, PreprocessingModel> source) {
            return source.getResults();
        }
    }

    protected static class ResExtractor extends DefaultInformationExtractor<TsDocument<? extends IProcSpecification, PreprocessingModel>, TsData> {

        static final ResExtractor INSTANCE = new ResExtractor();

        @Override
        public TsData retrieve(TsDocument<? extends IProcSpecification, PreprocessingModel> source) {
            return source.getResults().getFullResiduals();
        }
    }

    protected static class PreprocessingDetFactory<D extends TsDocument<? extends IProcSpecification, PreprocessingModel>>
            extends ItemFactory<D, PreprocessingModel> {

        protected PreprocessingDetFactory(Class<D> documentType) {
            super(documentType, MODEL_DET, PmExtractor.INSTANCE, new SaTableUI(ModellingDictionary.getDeterministicSeries(), null));
        }
    }

    private static class SpecExtractor extends DefaultInformationExtractor<TsDocument<? extends IProcSpecification, PreprocessingModel>, IProcSpecification> {

        private static final SpecExtractor INSTANCE = new SpecExtractor();

        @Override
        public IProcSpecification retrieve(TsDocument<? extends IProcSpecification, PreprocessingModel> source) {
            return source.getSpecification();
        }
    };

    private static class LikelihoodExtractor extends TsDocumentInformationExtractor<TsDocument<? extends IProcSpecification, PreprocessingModel>, Functions> {

        public static final LikelihoodExtractor INSTANCE = new LikelihoodExtractor();

        @Override
        protected Functions buildInfo(TsDocument<? extends IProcSpecification, PreprocessingModel> source) {
            PreprocessingModel model = source.getResults();
            if (model == null) {
                return null;
            } else {
                return Functions.create(model.likelihoodFunction(), model.maxLikelihoodFunction());
            }
        }
    };

    public static InformationExtractor<TsDocument<? extends IProcSpecification, PreprocessingModel>, PreprocessingModel> pmExtractor() {
        return PmExtractor.INSTANCE;
    }

    public static InformationExtractor<TsDocument<? extends IProcSpecification, PreprocessingModel>, TsData> resExtractor() {
        return ResExtractor.INSTANCE;
    }

}
