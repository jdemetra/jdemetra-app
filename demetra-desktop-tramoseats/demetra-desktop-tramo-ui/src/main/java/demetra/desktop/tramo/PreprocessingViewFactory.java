/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramo;


import demetra.desktop.ui.chart3d.functions.SurfacePlotterUI.Functions;
import demetra.desktop.ui.modelling.EstimationUI;
import demetra.desktop.ui.modelling.OutOfSampleTestUI;
import demetra.desktop.ui.modelling.PreprocessingUI;
import demetra.desktop.ui.processing.ComposedProcDocumentItemFactory;
import demetra.desktop.ui.processing.DefaultItemUI;
import demetra.desktop.ui.processing.HtmlItemUI;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.desktop.ui.processing.ItemUI;
import demetra.desktop.ui.processing.ProcDocumentViewFactory;
import demetra.desktop.ui.processing.TsViewToolkit;
import demetra.html.HtmlElement;
import demetra.html.core.HtmlInformationSet;
import demetra.information.InformationSet;
import demetra.processing.ProcSpecification;
import demetra.timeseries.TimeSelector;
import demetra.timeseries.Ts;
import demetra.timeseries.TsData;
import demetra.timeseries.TsDocument;
import demetra.util.Id;
import demetra.util.LinearId;
import java.util.Collections;
import java.util.function.Function;
import javax.swing.JComponent;
import jdplus.regarima.tests.OneStepAheadForecastingTest;
import jdplus.regsarima.RegSarimaComputer;
import jdplus.regsarima.regular.RegSarimaModel;

/**
 *
 * @author Jean Palate
 */
public abstract class PreprocessingViewFactory<S extends ProcSpecification, D extends TsDocument<S, RegSarimaModel>> extends ProcDocumentViewFactory<D> {

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

    @Override
    public Id getPreferredView() {
        return MODEL_SUMMARY;
    }

    //<editor-fold defaultstate="collapsed" desc="REGISTER SUMMARY">
    protected abstract static class InputFactory<D extends TsDocument<? extends ProcSpecification, RegSarimaModel>>
            extends ItemFactory<D, Ts> {

        protected InputFactory(Class<D> documentType) {
            super(documentType, INPUT_SERIES, INPUTEXTRACTOR, new DefaultItemUI<IProcDocumentView<D>, Ts>() {
                @Override
                public JComponent getView(IProcDocumentView<D> host, Ts information) {
                    return TsViewToolkit.getGrid(information == null ? null : Collections.singleton(information)); 
                }
            });
        }
    }
//
    protected abstract static class SpecAllFactory<D extends TsDocument<? extends ProcSpecification, RegSarimaModel>>
            extends ItemFactory<D, InformationSet> {

        protected SpecAllFactory(Class<D> documentType, Function<D, InformationSet> infoExtractor) {
            super(documentType, INPUT_SPEC, infoExtractor, new HtmlItemUI<IProcDocumentView<D>, InformationSet>() {
                @Override
                protected HtmlElement getHtmlElement(IProcDocumentView<D> host, InformationSet information) {
                    return new HtmlInformationSet(information);
                }
            });
        }
    }
//
//    protected abstract static class LikelihoodFactory<D extends TsDocument<? extends ProcSpecification, RegSarimaModel>>
//            extends ItemFactory<D, Functions> {
//
//        protected LikelihoodFactory(Class<D> documentType) {
//            super(documentType, MODEL_LIKELIHOOD, LikelihoodExtractor.INSTANCE, new SurfacePlotterUI());
//        }
//    }
//
    protected abstract static class SummaryFactory<D extends TsDocument<? extends ProcSpecification, RegSarimaModel>>
            extends ItemFactory<D, RegSarimaModel> {

        protected SummaryFactory(Class<D> documentType) {
            super(documentType, MODEL_SUMMARY, PMEXTRACTOR, new PreprocessingUI());
        }
    }
//
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="REGISTER FORECASTS">
    protected abstract static class ModelFCastsFactory<D extends TsDocument<? extends ProcSpecification, RegSarimaModel>>
            extends ItemFactory<D, EstimationUI.Information> {

        protected ModelFCastsFactory(Class<D> documentType) {
            super(documentType, MODEL_FCASTS, (D source)-> {
                    RegSarimaModel model = source.getResult();
                    TsData orig = model.getDescription().getSeries();
                    TimeSelector sel = TimeSelector.last(3 * orig.getAnnualFrequency());
                    TsData f=model.getData("y_f", TsData.class), ef= model.getData("y_ef", TsData.class);
                    if (f == null)
                        return null;
                    else
                        return new EstimationUI.Information(orig.select(sel),f, ef, 1.96);
                }
           , new EstimationUI());
        }
    }
//
//    protected abstract static class PreprocessingFCastsTableFactory<D extends TsDocument<? extends ProcSpecification, RegSarimaModel>>
//            extends ItemFactory<D, RegSarimaModel> {
//
//        private static String[] generateItems() {
//            return new String[]{ModellingDictionary.Y + SeriesInfo.F_SUFFIX, ModellingDictionary.Y + SeriesInfo.EF_SUFFIX};
//        }
//
//        protected PreprocessingFCastsTableFactory(Class<D> documentType) {
//            super(documentType, MODEL_FCASTS_TABLE, PmExtractor.INSTANCE, new GenericTableUI(false, generateItems()));
//        }
//    }
//
    protected abstract static class ModelFCastsOutFactory<D extends TsDocument<? extends ProcSpecification, RegSarimaModel>>
            extends ItemFactory<D, OneStepAheadForecastingTest> {

        protected ModelFCastsOutFactory(Class<D> documentType) {
            super(documentType, MODEL_FCASTS_OUTOFSAMPLE, (D source)->{
                     RegSarimaModel model = source.getResult();
                    int lback;
                    int freq = model.getDescription().getSeries().getAnnualFrequency();
                    switch (freq) {
                        case 12:
                            lback = 18;
                            break;
                        case 6:
                            lback = 9;
                            break;
                        case 4:
                            lback = 6;
                            break;
                        default:
                            lback = 5;
                            break;
                    }
                    RegSarimaComputer processor = RegSarimaComputer.builder().build();
                    OneStepAheadForecastingTest test =  OneStepAheadForecastingTest.of(model.regarima(), processor, lback);
                    return test;
            }, new OutOfSampleTestUI());
        }
    }
//</editor-fold>
//
//<editor-fold defaultstate="collapsed" desc="REGISTER MODEL">
//    public abstract static class ModelRegsFactory<D extends TsDocument<? extends ProcSpecification, RegSarimaModel>>
//            extends ItemFactory<D, RegSarimaModel> {
//
//        protected ModelRegsFactory(Class<D> documentType) {
//            super(documentType, MODEL_REGS, PmExtractor.INSTANCE, new RegressorsUI());
//        }
//    }
//
//    public abstract static class ModelArimaFactory<D extends TsDocument<? extends ProcSpecification, RegSarimaModel>>
//            extends ItemFactory<D, LinkedHashMap<String, IArimaModel>> {
//
//        protected ModelArimaFactory(Class<D> documentType) {
//            super(documentType, MODEL_ARIMA, new DefaultInformationExtractor<D, LinkedHashMap<String, IArimaModel>>() {
//                @Override
//                public LinkedHashMap<String, IArimaModel> retrieve(D source) {
//                    LinkedHashMap<String, IArimaModel> models = new LinkedHashMap<>();
//                    RegSarimaModel pm = source.getResults();
//                    if (pm == null) {
//                        return null;
//                    }
//                    SarimaModel model = pm.estimation.getArima();
//                    models.put("Arima model", model);
//                    return models;
//                }
//            }, new ArimaUI());
//        }
//    }
//</editor-fold>
//
//<editor-fold defaultstate="collapsed" desc="REGISTER RESIDUALS">
//    protected abstract static class ModelResFactory<D extends TsDocument<? extends ProcSpecification, RegSarimaModel>>
//            extends ItemFactory<D, TsData> {
//
//        protected ModelResFactory(Class<D> documentType) {
//            super(documentType, MODEL_RES, ResExtractor.INSTANCE, new ResidualsUI());
//        }
//    }
//
//    protected abstract static class ModelResStatsFactory<D extends TsDocument<? extends ProcSpecification, RegSarimaModel>>
//            extends ItemFactory<D, NiidTests> {
//
//        protected ModelResStatsFactory(Class<D> documentType) {
//            super(documentType, MODEL_RES_STATS, new DefaultInformationExtractor<D, NiidTests>() {
//                @Override
//                public NiidTests retrieve(D source) {
//                    RegSarimaModel rslt = source.getResults();
//                    TsData res = rslt.getFullResiduals();
//                    return new NiidTests(res, res.getFrequency().intValue(),
//                            rslt.description.getArimaComponent().getFreeParametersCount(), true);
//                }
//            }, new ResidualsStatsUI());
//        }
//    }
//
//    protected abstract static class ModelResDist<D extends TsDocument<? extends ProcSpecification, RegSarimaModel>>
//            extends ItemFactory<D, TsData> {
//
//        protected ModelResDist(Class<D> documentType) {
//            super(documentType, MODEL_RES_DIST, ResExtractor.INSTANCE, new ResidualsDistUI());
//        }
//    }
//
//    protected abstract static class ModelResSpectrum<D extends TsDocument<? extends ProcSpecification, RegSarimaModel>>
//            extends ItemFactory<D, TsData> {
//
//        protected ModelResSpectrum(Class<D> documentType) {
//            super(documentType, MODEL_RES_SPECTRUM, ResExtractor.INSTANCE, new SpectrumUI(true));
//        }
//    }
//</editor-fold>
//
//<editor-fold defaultstate="collapsed" desc="REGISTER DETAILS">
//</editor-fold>
    private abstract static class ItemFactory<D extends TsDocument<? extends ProcSpecification, RegSarimaModel>, I> extends ComposedProcDocumentItemFactory<D, I> {

        public ItemFactory(Class<D> documentType, Id itemId, Function<? super D, I> informationExtractor, ItemUI<? extends IProcDocumentView<D>, I> itemUI) {
            super(documentType, itemId, informationExtractor, itemUI);
        }
    }
//
    static final Function<TsDocument, TsDocument> DUMMYEXTRACTOR=source->source;
    static final Function<TsDocument, Ts> INPUTEXTRACTOR=source->source.getInput();
    static final Function<TsDocument<? extends ProcSpecification, RegSarimaModel>, RegSarimaModel> PMEXTRACTOR=source->source.getResult();
    static final Function<TsDocument<? extends ProcSpecification, RegSarimaModel>, TsData> RESEXTRACTOR=source->source.getResult().fullResiduals();
    static final Function<TsDocument<? extends ProcSpecification, RegSarimaModel>, ProcSpecification> SPECEXTRACTOR=source->source.getSpecification();
//
//    protected abstract static class PreprocessingDetFactory<D extends TsDocument<? extends ProcSpecification, RegSarimaModel>>
//            extends ItemFactory<D, RegSarimaModel> {
//
//        protected PreprocessingDetFactory(Class<D> documentType) {
//            super(documentType, MODEL_DET, PMEXTRACTOR, new SaTableUI(ModellingDictionary.getDeterministicSeries(), null));
//        }
//    }
//
//     private static class LikelihoodExtractor extends TsDocumentInformationExtractor<TsDocument<? extends ProcSpecification, RegSarimaModel>, Functions> {
//
//        public static final LikelihoodExtractor INSTANCE = new LikelihoodExtractor();
//
//        @Override
//        protected Functions buildInfo(TsDocument<? extends ProcSpecification, RegSarimaModel> source) {
//            RegSarimaModel model = source.getResults();
//            if (model == null) {
//                return null;
//            } else {
//                return Functions.create(model.likelihoodFunction(), model.maxLikelihoodFunction());
//            }
//        }
//    }

}
