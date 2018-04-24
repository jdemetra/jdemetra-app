/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.ui.view.tsprocessing.sa;

import ec.nbdemetra.ui.DemetraUI;
import ec.nbdemetra.ui.chart3d.functions.SurfacePlotterUI;
import ec.nbdemetra.ui.chart3d.functions.SurfacePlotterUI.Functions;
import ec.satoolkit.DecompositionMode;
import ec.satoolkit.GenericSaProcessingFactory;
import ec.satoolkit.ISaSpecification;
import ec.satoolkit.ISeriesDecomposition;
import ec.satoolkit.diagnostics.StationaryVarianceDecomposition;
import ec.tss.documents.DocumentManager;
import ec.tss.documents.TsDocumentProcessing;
import ec.tss.html.IHtmlElement;
import ec.tss.html.implementation.HtmlInformationSet;
import ec.tss.sa.EstimationPolicyType;
import ec.tss.sa.SaManager;
import ec.tss.sa.documents.SaDocument;
import ec.tss.sa.documents.SaDocumentProcessing;
import ec.tstoolkit.algorithm.CompositeResults;
import ec.tstoolkit.arima.IArimaModel;
import ec.tstoolkit.information.InformationSet;
import ec.tstoolkit.modelling.ComponentInformation;
import ec.tstoolkit.modelling.ComponentType;
import ec.tstoolkit.modelling.ModellingDictionary;
import ec.tstoolkit.modelling.SeriesInfo;
import ec.tstoolkit.modelling.arima.PreprocessingModel;
import ec.tstoolkit.modelling.arima.diagnostics.IOneStepAheadForecastingTest;
import ec.tstoolkit.modelling.arima.diagnostics.OneStepAheadForecastingTest;
import ec.tstoolkit.sarima.SarimaModel;
import ec.tstoolkit.stats.NiidTests;
import ec.tstoolkit.timeseries.TsPeriodSelector;
import ec.tstoolkit.timeseries.analysis.DiagnosticInfo;
import ec.tstoolkit.timeseries.analysis.MovingProcessing;
import ec.tstoolkit.timeseries.analysis.RevisionHistory;
import ec.tstoolkit.timeseries.analysis.SlidingSpans;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import static ec.tstoolkit.timeseries.simplets.TsFrequency.BiMonthly;
import static ec.tstoolkit.timeseries.simplets.TsFrequency.Monthly;
import static ec.tstoolkit.timeseries.simplets.TsFrequency.Quarterly;
import ec.tstoolkit.utilities.DefaultInformationExtractor;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.InformationExtractor;
import ec.tstoolkit.utilities.Jdk6;
import ec.tstoolkit.utilities.LinearId;
import ec.ui.view.tsprocessing.ArimaUI;
import ec.ui.view.tsprocessing.BenchmarkingUI;
import ec.ui.view.tsprocessing.ChartUI;
import ec.ui.view.tsprocessing.ComposedProcDocumentItemFactory;
import ec.ui.view.tsprocessing.DefaultItemUI;
import ec.ui.view.tsprocessing.DiagnosticsUI;
import ec.ui.view.tsprocessing.EstimationUI;
import ec.ui.view.tsprocessing.GenericTableUI;
import ec.ui.view.tsprocessing.HtmlItemUI;
import ec.ui.view.tsprocessing.IProcDocumentView;
import ec.ui.view.tsprocessing.ItemUI;
import ec.ui.view.tsprocessing.OutOfSampleTestUI;
import ec.ui.view.tsprocessing.PreprocessingUI;
import ec.ui.view.tsprocessing.ProcDocumentViewFactory;
import ec.ui.view.tsprocessing.ProcessingLogUI;
import ec.ui.view.tsprocessing.RegressorsUI;
import ec.ui.view.tsprocessing.ResidualsDistUI;
import ec.ui.view.tsprocessing.ResidualsStatsUI;
import ec.ui.view.tsprocessing.ResidualsUI;
import ec.ui.view.tsprocessing.RevisionHistoryUI;
import ec.ui.view.tsprocessing.SlidingSpansDetailUI;
import ec.ui.view.tsprocessing.SpectrumUI;
import ec.ui.view.tsprocessing.StabilityUI;
import ec.ui.view.tsprocessing.TsDocumentInformationExtractor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate, Philippe Charles, Mats Maggi
 * @param <S> Specification
 * @param <D> Document
 */
public abstract class SaDocumentViewFactory<S extends ISaSpecification, D extends SaDocument<S>> extends ProcDocumentViewFactory<D> {
    // main nodes

    public static final String INPUT = "Input", SPEC = "Specifications", SERIES = "Series",
            MAIN = "Main results",
            PRELIMINARY = "Preliminary tests",
            PREPROCESSING = "Pre-processing",
            MODEL = "Model",
            DECOMPOSITION = "Decomposition",
            BENCHMARKING = "Benchmarking",
            DIAGNOSTICS = "Diagnostics",
            LIKELIHOOD = "Likelihood";
    // main results sub-nodes
    public static final String CHART = "Chart", CHARTS = "Charts",
            TABLE = "Table",
            SI_RATIO = "S-I ratio",
            SA_TREND = "Sa, trend",
            C_S_I = "Cal., sea., irr.",
            LOG = "Processing log";
    public static final String PROCESSING = "Processing",
            FCASTS = "Forecasts",
            OSAMPLE = "Out-of-sample test",
            DETAILS = "Details",
            SUMMARY = "Summary",
            STEPS = "Steps",
            PREADJUSTMENT = "Pre-adjustment series",
            ARIMA = "Arima",
            REGRESSORS = "Regressors",
            RESIDUALS = "Residuals",
            STATS = "Statistics",
            DISTRIBUTION = "Distribution";
    // diagnostics sub-nodes
    public static final String SEASONALITY = "Seasonality tests",
            OSEASONALITY = "Original (transformed) series",
            RSEASONALITY = "Full residuals",
            LASTRSEASONALITY = "Residuals (last periods)",
            LSEASONALITY = "Linearized series",
            COMBINED = "Combined test",
            RESIDUAL = "Residual seasonality",
            TRANSFORMATION = "Transformation",
            SPECTRAL = "Spectral analysis",
            REVISIONS = "Revisions analysis",
            SLIDINGSPANS = "Sliding spans",
            STABILITY = "Model stability",
            IRREGULAR = "Irregular",
            SA_ST = "Sa series (stationary)",
            SASERIES = "SA series",
            SASERIES_CHANGES = "SA changes",
            LASTIRREGULAR = "Irregular (last periods)",
            LASTSASERIES = "SA series (last periods)",
            TREND = "Trend",
            SEASONAL = "Seasonal",
            TRADINGDAYS = "Trading days",
            SACHANGES = "SA changes",
            TRENDCHANGES = "Trend changes",
            REVISION = "Revisions history",
            EASTER = "Easter",
            MATRIX = "Matrix";
    public static final Id INPUT_SPEC = new LinearId(INPUT, SPEC),
            INPUT_SERIES = new LinearId(INPUT, SERIES),
            MAIN_SUMMARY = new LinearId(MAIN),
            MODEL_SUMMARY = new LinearId(MODEL),
            MODEL_FCASTS = new LinearId(MODEL, FCASTS),
            MODEL_FCASTS_OUTOFSAMPLE = new LinearId(MODEL, FCASTS, OSAMPLE),
            MODEL_REGS = new LinearId(MODEL, REGRESSORS),
            MODEL_ARIMA = new LinearId(MODEL, ARIMA),
            MODEL_RES = new LinearId(MODEL, RESIDUALS),
            MODEL_RES_STATS = new LinearId(MODEL, RESIDUALS, STATS),
            MODEL_RES_DIST = new LinearId(MODEL, RESIDUALS, DISTRIBUTION),
            MODEL_RES_SPECTRUM = new LinearId(MODEL, RESIDUALS, SPECTRAL),
            MAIN_CHART = new LinearId(MAIN, CHART),
            MAIN_CHARTS_LOW = new LinearId(MAIN, CHARTS, SA_TREND),
            MAIN_CHARTS_HIGH = new LinearId(MAIN, CHARTS, C_S_I),
            MAIN_TABLE = new LinearId(MAIN, TABLE),
            MAIN_SI = new LinearId(MAIN, SI_RATIO),
            MAIN_LOG = new LinearId(MAIN, LOG),
            PREPROCESSING_SUMMARY = new LinearId(PREPROCESSING),
            PREPROCESSING_FCASTS = new LinearId(PREPROCESSING, FCASTS),
            PREPROCESSING_FCASTS_TABLE = new LinearId(PREPROCESSING, FCASTS, TABLE),
            PREPROCESSING_FCASTS_OUTOFSAMPLE = new LinearId(PREPROCESSING, FCASTS, OSAMPLE),
            PREPROCESSING_DETAILS = new LinearId(PREPROCESSING, DETAILS),
            PREPROCESSING_REGS = new LinearId(PREPROCESSING, REGRESSORS),
            PREPROCESSING_ARIMA = new LinearId(PREPROCESSING, ARIMA),
            PREPROCESSING_DET = new LinearId(PREPROCESSING, PREADJUSTMENT),
            PREPROCESSING_RES = new LinearId(PREPROCESSING, RESIDUALS),
            PREPROCESSING_RES_STATS = new LinearId(PREPROCESSING, RESIDUALS, STATS),
            PREPROCESSING_RES_DIST = new LinearId(PREPROCESSING, RESIDUALS, DISTRIBUTION),
            PREPROCESSING_LIKELIHOOD = new LinearId(PREPROCESSING, LIKELIHOOD),
            DIAGNOSTICS_SUMMARY = new LinearId(DIAGNOSTICS),
            DIAGNOSTICS_SEASONALITY = new LinearId(DIAGNOSTICS, SEASONALITY, COMBINED),
            DIAGNOSTICS_OSEASONALITY = new LinearId(DIAGNOSTICS, SEASONALITY, OSEASONALITY),
            DIAGNOSTICS_LSEASONALITY = new LinearId(DIAGNOSTICS, SEASONALITY, LSEASONALITY),
            DIAGNOSTICS_RSEASONALITY = new LinearId(DIAGNOSTICS, SEASONALITY, RSEASONALITY),
            DIAGNOSTICS_SASEASONALITY = new LinearId(DIAGNOSTICS, SEASONALITY, SASERIES),
            DIAGNOSTICS_LASTRSEASONALITY = new LinearId(DIAGNOSTICS, SEASONALITY, LASTRSEASONALITY),
            DIAGNOSTICS_LASTSASEASONALITY = new LinearId(DIAGNOSTICS, SEASONALITY, LASTSASERIES),
            DIAGNOSTICS_RESIDUALSEASONALITY = new LinearId(DIAGNOSTICS, SEASONALITY, RESIDUAL),
            DIAGNOSTICS_ISEASONALITY = new LinearId(DIAGNOSTICS, SEASONALITY, IRREGULAR),
            DIAGNOSTICS_LASTISEASONALITY = new LinearId(DIAGNOSTICS, SEASONALITY, LASTIRREGULAR),
            DIAGNOSTICS_SPECTRUM_RES = new LinearId(DIAGNOSTICS, SPECTRAL, RESIDUALS),
            DIAGNOSTICS_SPECTRUM_I = new LinearId(DIAGNOSTICS, SPECTRAL, IRREGULAR),
            DIAGNOSTICS_SPECTRUM_SA = new LinearId(DIAGNOSTICS, SPECTRAL, SA_ST),
            DIAGNOSTICS_SLIDING_SUMMARY = new LinearId(DIAGNOSTICS, SLIDINGSPANS),
            DIAGNOSTICS_SLIDING_SEAS = new LinearId(DIAGNOSTICS, SLIDINGSPANS, SEASONAL),
            DIAGNOSTICS_SLIDING_TD = new LinearId(DIAGNOSTICS, SLIDINGSPANS, TRADINGDAYS),
            DIAGNOSTICS_SLIDING_SA = new LinearId(DIAGNOSTICS, SLIDINGSPANS, SACHANGES),
            DIAGNOSTICS_REVISION_SA = new LinearId(DIAGNOSTICS, REVISION, SASERIES),
            DIAGNOSTICS_REVISION_TREND = new LinearId(DIAGNOSTICS, REVISION, TREND),
            DIAGNOSTICS_REVISION_SA_CHANGES = new LinearId(DIAGNOSTICS, REVISION, SACHANGES),
            DIAGNOSTICS_REVISION_TREND_CHANGES = new LinearId(DIAGNOSTICS, REVISION, TRENDCHANGES),
            DIAGNOSTICS_STABILITY_TD = new LinearId(DIAGNOSTICS, STABILITY, TRADINGDAYS),
            DIAGNOSTICS_STABILITY_EASTER = new LinearId(DIAGNOSTICS, STABILITY, EASTER),
            DIAGNOSTICS_STABILITY_ARIMA = new LinearId(DIAGNOSTICS, STABILITY, ARIMA),
            DIAGNOSTICS_MATRIX = new LinearId(DIAGNOSTICS, MATRIX);

    public static InformationExtractor<SaDocument<? extends ISaSpecification>, ISaSpecification> specExtractor() {
        return SpecExtractor.INSTANCE;
    }

    public static InformationExtractor<SaDocument<? extends ISaSpecification>, CompositeResults> saExtractor() {
        return SaExtractor.INSTANCE;
    }

    public static InformationExtractor<SaDocument<? extends ISaSpecification>, PreprocessingModel> pmExtractor() {
        return PmExtractor.INSTANCE;
    }

    public static InformationExtractor<SaDocument<? extends ISaSpecification>, TsData> resExtractor() {
        return ResExtractor.INSTANCE;
    }

    public static InformationExtractor<SaDocument<? extends ISaSpecification>, SlidingSpans> ssExtractor() {
        return SsExtractor.INSTANCE;
    }

    public static InformationExtractor<SaDocument<? extends ISaSpecification>, SeasonalityTestUI2.Information> oseasExtractor() {
        return SeasTestExtractor.OINSTANCE;
    }

    public static InformationExtractor<SaDocument<? extends ISaSpecification>, SeasonalityTestUI2.Information> lseasExtractor() {
        return SeasTestExtractor.LINSTANCE;
    }

    public static InformationExtractor<SaDocument<? extends ISaSpecification>, SeasonalityTestUI2.Information> rseasExtractor() {
        return SeasTestExtractor.RINSTANCE;
    }

    public static InformationExtractor<SaDocument<? extends ISaSpecification>, SeasonalityTestUI2.Information> saseasExtractor() {
        return SeasTestExtractor.SAINSTANCE;
    }

    public static InformationExtractor<SaDocument<? extends ISaSpecification>, SeasonalityTestUI2.Information> iseasExtractor() {
        return SeasTestExtractor.IINSTANCE;
    }

    public static InformationExtractor<SaDocument<? extends ISaSpecification>, SeasonalityTestUI2.Information> lastrseasExtractor() {
        return SeasTestExtractor.LASTRINSTANCE;
    }

    public static InformationExtractor<SaDocument<? extends ISaSpecification>, SeasonalityTestUI2.Information> lastsaseasExtractor() {
        return SeasTestExtractor.LASTSAINSTANCE;
    }

    public static InformationExtractor<SaDocument<? extends ISaSpecification>, SeasonalityTestUI2.Information> lastiseasExtractor() {
        return SeasTestExtractor.LASTIINSTANCE;
    }

    public static InformationExtractor<SaDocument<? extends ISaSpecification>, RevisionHistory> historyExtractor() {
        return RevisionExtractor.INSTANCE;
    }

    public static InformationExtractor<SaDocument<? extends ISaSpecification>, Functions> likelihoodExtractor() {
        return LikelihoodExtractor.INSTANCE;
    }

    public static InformationExtractor<SaDocument<? extends ISaSpecification>, StationaryVarianceDecomposition> stvarExtractor() {
        return StvarExtractor.INSTANCE;
    }

    //<editor-fold defaultstate="collapsed" desc="REGISTER SPEC">
    protected static class SpecAllFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, ISaSpecification> {

        protected SpecAllFactory(Class<D> documentType) {
            super(documentType, INPUT_SPEC, specExtractor(), new HtmlItemUI<IProcDocumentView<D>, ISaSpecification>() {
                @Override
                protected IHtmlElement getHtmlElement(IProcDocumentView<D> host, ISaSpecification information) {
                    return new HtmlInformationSet(information.write(true));
                }
            });
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER MAIN VIEWS">
    // provide regitration of main components

//    protected static class MainChartFactory<D extends SaDocument<? extends ISaSpecification>>
//            extends ItemFactory<D, CompositeResults> {
//
//        protected MainChartFactory(Class<D> documentType) {
//            super(documentType, MAIN_CHART, SaExtractor.INSTANCE, new ChartUI(ModellingDictionary.Y, ModellingDictionary.T, ModellingDictionary.SA));
//        }
//    }
    protected static class MainChartsLowFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, CompositeResults> {

        private static String[] generateItems(String prefix) {
            String sy=InformationSet.concatenate(prefix,ModellingDictionary.Y);
            String st=InformationSet.concatenate(prefix,ModellingDictionary.T);
            String ssa=InformationSet.concatenate(prefix,ModellingDictionary.SA);
            StringBuilder y = new StringBuilder();
            y.append(DocumentManager.COMPOSITE).append("Series=")
//                    .append(sy).append(SeriesInfo.B_SUFFIX)
                    .append(',').append(sy)
                    .append(',').append(sy).append(SeriesInfo.F_SUFFIX);
            StringBuilder t = new StringBuilder();
            t.append(DocumentManager.COMPOSITE).append("Trend=")
                    .append(',').append(st)
                    .append(',').append(st).append(SeriesInfo.F_SUFFIX);
            StringBuilder sa = new StringBuilder();
            sa.append(DocumentManager.COMPOSITE).append("Seasonally adjusted=")
                    .append(',').append(ssa)
                    .append(',').append(ssa).append(SeriesInfo.F_SUFFIX);
            return new String[]{y.toString(), t.toString(), sa.toString()};
        }
        protected MainChartsLowFactory(Class<D> documentType) {
            this(documentType, null);
        }

        protected MainChartsLowFactory(Class<D> documentType, String prefix) {
            super(documentType, MAIN_CHARTS_LOW, saExtractor(), new ChartUI(generateItems(prefix)));
        }
    }

    protected static class MainChartsHighFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, CompositeResults> {

        private static String[] generateItems(String prefix) {
            StringBuilder cal = new StringBuilder();
            cal.append(DocumentManager.COMPOSITE).append("Calendar effects=,").append(ModellingDictionary.CAL)
                    .append(',').append(ModellingDictionary.CAL).append(SeriesInfo.F_SUFFIX);
            StringBuilder s = new StringBuilder();
            s.append(DocumentManager.COMPOSITE).append("Seas (component)=,").append(ModellingDictionary.S_CMP)
                    .append(',').append(ModellingDictionary.S_CMP).append(SeriesInfo.F_SUFFIX);
            String si=InformationSet.concatenate(prefix,ModellingDictionary.I);
            StringBuilder i = new StringBuilder();
            i.append(DocumentManager.COMPOSITE).append("Irregular=")
                    .append(',').append(si)
                    .append(',').append(si).append(SeriesInfo.F_SUFFIX);
            return new String[]{cal.toString(), s.toString(), i.toString()};
        }

        protected MainChartsHighFactory(Class<D> documentType) {
            this(documentType, null);
        }
        
        protected MainChartsHighFactory(Class<D> documentType, String prefix) {
            super(documentType, MAIN_CHARTS_HIGH, saExtractor(), new ChartUI(generateItems(prefix)));
        }
    }

    protected static class MainTableFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, CompositeResults> {

        private static String[] generateItems(String prefix) {
            String sy=InformationSet.concatenate(prefix,ModellingDictionary.Y);
            String st=InformationSet.concatenate(prefix,ModellingDictionary.T);
            String ssa=InformationSet.concatenate(prefix,ModellingDictionary.SA);
            String ss=InformationSet.concatenate(prefix,ModellingDictionary.S);
            String si=InformationSet.concatenate(prefix,ModellingDictionary.I);
            StringBuilder y = new StringBuilder();
            y.append(DocumentManager.COMPOSITE).append("Series=")
//                    .append(sy).append(SeriesInfo.B_SUFFIX)
                    .append(',').append(sy)
                    .append(',').append(sy).append(SeriesInfo.F_SUFFIX);
            StringBuilder t = new StringBuilder();
            t.append(DocumentManager.COMPOSITE).append("Trend=")
                    .append(',').append(st)
                    .append(',').append(st).append(SeriesInfo.F_SUFFIX);
            StringBuilder sa = new StringBuilder();
            sa.append(DocumentManager.COMPOSITE).append("Seasonally adjusted=")
                    .append(',').append(ssa)
                    .append(',').append(ssa).append(SeriesInfo.F_SUFFIX);
            StringBuilder s = new StringBuilder();
            s.append(DocumentManager.COMPOSITE).append("Seasonal=")
                    .append(',').append(ss)
                    .append(',').append(ss).append(SeriesInfo.F_SUFFIX);
            StringBuilder i = new StringBuilder();
            i.append(DocumentManager.COMPOSITE).append("Irregular=")
                    .append(',').append(si)
                    .append(',').append(si).append(SeriesInfo.F_SUFFIX);
            return new String[]{y.toString(), sa.toString(), t.toString(), s.toString(), i.toString()};
        }

        protected MainTableFactory(Class<D> documentType) {
            this(documentType, null);
        }

        protected MainTableFactory(Class<D> documentType, String prefix) {
            super(documentType, MAIN_TABLE, saExtractor(), new GenericTableUI(false, generateItems(prefix)));
//            super(documentType, MAIN_TABLE, saExtractor(), new SaTableUI(ModellingDictionary.getFinalSeries(), null));
        }
    }

    protected static class InputFactory<D extends SaDocument<? extends ISaSpecification>>
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

    protected static class SeasonalityTestsFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, SeasonalityTestUI2.Information> {

        protected SeasonalityTestsFactory(Class<D> documentType, Id id, InformationExtractor<SaDocument<? extends ISaSpecification>, SeasonalityTestUI2.Information> extractor, String header, boolean control) {
            super(documentType, id, extractor, new SeasonalityTestUI2(header, control));
        }
    }

    protected static class ProcessingLogFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, CompositeResults> {

        protected ProcessingLogFactory(Class<D> documentType) {
            super(documentType, MAIN_LOG, saExtractor(), new ProcessingLogUI());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER SI VIEW">
    protected static class MainSiFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, TsData[]> {

        protected MainSiFactory(Class<D> documentType) {
            super(documentType, MAIN_SI, new DefaultInformationExtractor<D, TsData[]>() {
                @Override
                public TsData[] retrieve(D source) {
                    CompositeResults rslt = source.getResults();
                    TsData seas = rslt.getData(ModellingDictionary.S_CMP, TsData.class);
                    TsData i = rslt.getData(ModellingDictionary.I_CMP, TsData.class);
                    if (seas == null && i == null) {
                        return null;
                    }
                    TsData si;
                    if (source.getFinalDecomposition().getMode().isMultiplicative()) {
                        si = TsData.multiply(seas, i);
                        if (seas == null) {
                            seas = new TsData(i.getDomain(), 1);
                        }
                    } else {
                        si = TsData.add(seas, i);
                        if (seas == null) {
                            seas = new TsData(i.getDomain(), 0);
                        }
                    }
                    return new TsData[]{seas, si};
                }
            }, new SiRatioUI());
        }
    }

    protected static class LogFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, CompositeResults> {

        protected LogFactory(Class<D> documentType) {
            super(documentType, MAIN_LOG, saExtractor(), new SiRatioUI());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER ARIMA VIEW">
    protected static class PreprocessingArimaFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, LinkedHashMap<String, IArimaModel>> {

        protected PreprocessingArimaFactory(Class<D> documentType) {
            super(documentType, PREPROCESSING_ARIMA, new DefaultInformationExtractor<D, LinkedHashMap<String, IArimaModel>>() {
                @Override
                public LinkedHashMap<String, IArimaModel> retrieve(D source) {
                    LinkedHashMap<String, IArimaModel> models = new LinkedHashMap<>();
                    PreprocessingModel preprocessingPart = source.getPreprocessingPart();
                    if (preprocessingPart != null) {
                        SarimaModel tmodel = preprocessingPart.estimation.getArima();
                        models.put("RegArima model", tmodel);
                    }
                    return models;
                }
            }, new ArimaUI());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER LIGHT PREPROCESSING VIEWS">
    protected static class PreprocessingSummaryFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, PreprocessingModel> {

        protected PreprocessingSummaryFactory(Class<D> documentType) {
            super(documentType, PREPROCESSING_SUMMARY, pmExtractor(), new PreprocessingUI());
        }
    }

    protected static class PreprocessingRegsFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, PreprocessingModel> {

        protected PreprocessingRegsFactory(Class<D> documentType) {
            super(documentType, PREPROCESSING_REGS, pmExtractor(), new RegressorsUI());
        }
    }

    static String[] generateFullItems(List<SeriesInfo> infos) {
        List<String> names = new ArrayList<>();
        for (SeriesInfo info : infos) {
            if (info.info == ComponentInformation.Value) {
                StringBuilder y = new StringBuilder();
                y.append(DocumentManager.COMPOSITE).append(info.name).append("=,").append(info.name)
                        .append(',').append(info.name).append(SeriesInfo.F_SUFFIX);
                names.add(y.toString());
            }
        }
        return Jdk6.Collections.toArray(names, String.class);
    }

    protected static class PreprocessingDetFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, CompositeResults> {

        protected PreprocessingDetFactory(Class<D> documentType) {
            super(documentType, PREPROCESSING_DET, saExtractor(),
                    new GenericTableUI(false,
                            generateFullItems(ModellingDictionary.getDeterministicSeries())));
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER PREPROCESSING VIEWS">
    protected static class PreprocessingFCastsFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, EstimationUI.Information> {

        protected PreprocessingFCastsFactory(Class<D> documentType) {
            super(documentType, PREPROCESSING_FCASTS, new DefaultInformationExtractor<D, EstimationUI.Information>() {
                @Override
                public EstimationUI.Information retrieve(D source) {
                    PreprocessingModel model = source.getPreprocessingPart();
                    if (model == null) {
                        return null;
                    }
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

    protected static class PreprocessingFCastsTableFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, CompositeResults> {

        private static String[] generateItems() {
            return new String[]{ModellingDictionary.Y + SeriesInfo.F_SUFFIX, ModellingDictionary.Y + SeriesInfo.EF_SUFFIX};
        }

        protected PreprocessingFCastsTableFactory(Class<D> documentType) {
            super(documentType, PREPROCESSING_FCASTS_TABLE, saExtractor(), new GenericTableUI(false, generateItems()));
        }
    }

    protected static class PreprocessingFCastsOutFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, IOneStepAheadForecastingTest> {

        protected PreprocessingFCastsOutFactory(Class<D> documentType) {
            super(documentType, PREPROCESSING_FCASTS_OUTOFSAMPLE, new DefaultInformationExtractor<D, IOneStepAheadForecastingTest>() {
                @Override
                public IOneStepAheadForecastingTest retrieve(D source) {
                    PreprocessingModel model = source.getPreprocessingPart();
                    if (model == null) {
                        return null;
                    }
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
                    test.test(model.estimation.getRegArima());
                    //                test.getProcessor().setPrecision(source.getSpecification().getRegArimaSpecification()
                    //                        .getEstimate().getTol());
                    return test;
                }
            }, new OutOfSampleTestUI());
        }
    }

    protected static class PreprocessingResFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, TsData> {

        protected PreprocessingResFactory(Class<D> documentType) {
            super(documentType, PREPROCESSING_RES, resExtractor(), new ResidualsUI());
        }
    }

    protected static class PreprocessingResStatsFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, NiidTests> {

        protected PreprocessingResStatsFactory(Class<D> documentType) {
            super(documentType, PREPROCESSING_RES_STATS, new DefaultInformationExtractor<D, NiidTests>() {
                @Override
                public NiidTests retrieve(D source) {
                    PreprocessingModel pp = source.getPreprocessingPart();
                    if (pp == null) {
                        return null;
                    }
                    TsData res = pp.getFullResiduals();
                    return new NiidTests(res, res.getFrequency().intValue(),
                            pp.description.getArimaComponent().getFreeParametersCount(), true);
                }
            }, new ResidualsStatsUI());
        }
    }

    protected static class PreprocessingResDistFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, TsData> {

        protected PreprocessingResDistFactory(Class<D> documentType) {
            super(documentType, PREPROCESSING_RES_DIST, resExtractor(), new ResidualsDistUI());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER BENCHMARKING VIEW">
    protected static class BenchmarkingFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, Boolean> {

        protected BenchmarkingFactory(Class<D> documentType) {
            super(documentType, new LinearId(BENCHMARKING), new DefaultInformationExtractor<D, Boolean>() {
                @Override
                public Boolean retrieve(D source) {
                    if (source.getBenchmarking() == null) {
                        return null;
                    }
                    return source.getFinalDecomposition().getMode() != DecompositionMode.Additive;
                }
            }, new BenchmarkingUI());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER DIAGNOSTICS VIEWS">
    protected static class DiagnosticsSummaryFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, CompositeResults> {

        protected DiagnosticsSummaryFactory(Class<D> documentType) {
            super(documentType, DIAGNOSTICS_SUMMARY, saExtractor(), new DiagnosticsUI());
        }
    }

    protected static class DiagnosticsSpectrumResFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, TsData> {

        protected DiagnosticsSpectrumResFactory(Class<D> documentType) {
            super(documentType, DIAGNOSTICS_SPECTRUM_RES, resExtractor(), new SpectrumUI(true));
        }
    }

    protected static class DiagnosticsSpectrumIFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, TsData> {

        protected DiagnosticsSpectrumIFactory(Class<D> documentType) {
            super(documentType, DIAGNOSTICS_SPECTRUM_I, new DefaultInformationExtractor<D, TsData>() {
                @Override
                public TsData retrieve(D source) {
                    ISeriesDecomposition finals = source.getFinalDecomposition();
                    if (finals == null) {
                        return null;
                    }
                    return finals.getSeries(ComponentType.Irregular, ComponentInformation.Value);
                }
            }, new SpectrumUI(false));
        }
    }

    protected static class DiagnosticsSpectrumSaFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, TsData> {

        protected DiagnosticsSpectrumSaFactory(Class<D> documentType) {
            super(documentType, DIAGNOSTICS_SPECTRUM_SA, new DefaultInformationExtractor<D, TsData>() {
                @Override
                public TsData retrieve(D source) {
                    ISeriesDecomposition finals = source.getFinalDecomposition();
                    if (finals == null) {
                        return null;
                    }
                    TsData s = finals.getSeries(ComponentType.SeasonallyAdjusted, ComponentInformation.Value);
                    //                if (s != null) {
                    //                    int lag = s.getFrequency() == TsFrequency.Monthly ? 3 : 1;
                    //                    s = s.delta(lag);
                    //                }
                    return s.delta(1);
                }
            }, new SpectrumUI(false));
        }
    }

    protected static class DiagnosticsRevisionSaFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, RevisionHistory> {

        protected DiagnosticsRevisionSaFactory(Class<D> documentType) {
            super(documentType, DIAGNOSTICS_REVISION_SA, RevisionExtractor.INSTANCE, new RevisionHistoryUI("sa", DiagnosticInfo.RelativeDifference));
        }
    }

    protected static class DiagnosticsRevisionTrendFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, RevisionHistory> {

        protected DiagnosticsRevisionTrendFactory(Class<D> documentType) {
            super(documentType, DIAGNOSTICS_REVISION_TREND, RevisionExtractor.INSTANCE, new RevisionHistoryUI("t", DiagnosticInfo.RelativeDifference));
        }
    }

    protected static class DiagnosticsRevisionSaChangesFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, RevisionHistory> {

        protected DiagnosticsRevisionSaChangesFactory(Class<D> documentType) {
            super(documentType, DIAGNOSTICS_REVISION_SA_CHANGES, RevisionExtractor.INSTANCE, new RevisionHistoryUI("sa", DiagnosticInfo.PeriodToPeriodGrowthDifference));
        }
    }

    protected static class DiagnosticsRevisionTrendChangesFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, RevisionHistory> {

        protected DiagnosticsRevisionTrendChangesFactory(Class<D> documentType) {
            super(documentType, DIAGNOSTICS_REVISION_TREND_CHANGES, RevisionExtractor.INSTANCE, new RevisionHistoryUI("t", DiagnosticInfo.PeriodToPeriodGrowthDifference));
        }
    }

    protected static class StabilityTDFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, MovingProcessing> {

        private static final String[] Items = new String[]{
            "regression.td(1)",
            "regression.td(2)",
            "regression.td(3)",
            "regression.td(4)",
            "regression.td(5)",
            "regression.td(6)"
        };

        protected StabilityTDFactory(Class<D> documentType) {
            super(documentType, DIAGNOSTICS_STABILITY_TD, MovingProcessingExtractor.INSTANCE, new StabilityUI(TRADINGDAYS, Items));
        }
    }

    protected static class StabilityEasterFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, MovingProcessing> {

        private static final String[] Items = new String[]{
            "regression.easter"
        };

        protected StabilityEasterFactory(Class<D> documentType) {
            super(documentType, DIAGNOSTICS_STABILITY_EASTER, MovingProcessingExtractor.INSTANCE, new StabilityUI(EASTER, Items));
        }
    }

    protected static class StabilityArimaFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, MovingProcessing> {

        private static final String[] Items = new String[]{
            "arima.phi(1)", "arima.phi(2)", "arima.phi(3)", "arima.th(1)", "arima.th(2)", "arima.th(3)",
            "arima.bphi(1)", "arima.bth(1)"
        };

        protected StabilityArimaFactory(Class<D> documentType) {
            super(documentType, DIAGNOSTICS_STABILITY_ARIMA, MovingProcessingExtractor.INSTANCE, new StabilityUI(ARIMA, Items));
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER LIKELIHOOD VIEW">
    protected static class LikelihoodFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, Functions> {

        protected LikelihoodFactory(Class<D> documentType) {
            super(documentType, PREPROCESSING_LIKELIHOOD, LikelihoodExtractor.INSTANCE, new SurfacePlotterUI());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER SEASONALITY VIEW">
    protected static class ResidualSeasonalityFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, TsData> {

        protected ResidualSeasonalityFactory(Class<D> documentType) {
            super(documentType, DIAGNOSTICS_RESIDUALSEASONALITY, new DefaultInformationExtractor<D, TsData>() {
                @Override
                public TsData retrieve(D source) {
                    CompositeResults rslt = source.getResults();
                    return rslt.getData(ModellingDictionary.SA, TsData.class);
                }
            }, new ResidualSeasonalityTestUI());
        }
    }

    protected static class DiagnosticsSeasonalityFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, SeasonalityTestUI.Information> {

        protected DiagnosticsSeasonalityFactory(Class<D> documentType) {
            super(documentType, DIAGNOSTICS_SEASONALITY, new DefaultInformationExtractor<D, SeasonalityTestUI.Information>() {
                @Override
                public SeasonalityTestUI.Information retrieve(D source) {
                    CompositeResults rslt = source.getResults();
                    ISeriesDecomposition finals = source.getFinalDecomposition();
                    if (finals == null) {
                        return null;
                    }
                    TsData seas = rslt.getData(ModellingDictionary.S_CMP, TsData.class);
                    TsData i = rslt.getData(ModellingDictionary.I_CMP, TsData.class);
                    TsData si;
                    boolean mul = finals.getMode().isMultiplicative();
                    if (mul) {
                        si = TsData.multiply(seas, i);
                    } else {
                        si = TsData.add(seas, i);
                    }
                    SeasonalityTestUI.Information info = new SeasonalityTestUI.Information();
                    info.si = si;
                    info.mul = mul;
                    return info;
                }
            }, new SeasonalityTestUI());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER SLIDING SPANS VIEW">
    protected static class DiagnosticsSlidingTdFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, SlidingSpans> {

        protected DiagnosticsSlidingTdFactory(Class<D> documentType) {
            super(documentType, DIAGNOSTICS_SLIDING_TD, ssExtractor(), new SlidingSpansDetailUI(ModellingDictionary.TDE));
        }
    }

    protected static class DiagnosticsSlidingSaFactory<D extends SaDocument<? extends ISaSpecification>>
            extends ItemFactory<D, SlidingSpans> {

        protected DiagnosticsSlidingSaFactory(Class<D> documentType) {
            super(documentType, DIAGNOSTICS_SLIDING_SA, ssExtractor(), new SlidingSpansDetailUI(ModellingDictionary.SA));
        }
    }
    //</editor-fold>

    public static class ItemFactory<D extends SaDocument<? extends ISaSpecification>, I> extends ComposedProcDocumentItemFactory<D, I> {

        protected ItemFactory(Class<D> documentType, Id itemId, InformationExtractor<? super D, I> informationExtractor, ItemUI<? extends IProcDocumentView<D>, I> itemUI) {
            super(documentType, itemId, informationExtractor, itemUI);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="EXTRACTORS IMPL">
    private static class SpecExtractor extends DefaultInformationExtractor<SaDocument<? extends ISaSpecification>, ISaSpecification> {

        private static final SpecExtractor INSTANCE = new SpecExtractor();

        @Override
        public ISaSpecification retrieve(SaDocument<? extends ISaSpecification> source) {
            return source.getSpecification();
        }
    };

    private static class SaExtractor extends DefaultInformationExtractor<SaDocument<? extends ISaSpecification>, CompositeResults> {

        private static final SaExtractor INSTANCE = new SaExtractor();

        @Override
        public CompositeResults retrieve(SaDocument<? extends ISaSpecification> source) {
            return source.getResults();
        }
    };

    private static class PmExtractor extends DefaultInformationExtractor<SaDocument<? extends ISaSpecification>, PreprocessingModel> {

        private static final PmExtractor INSTANCE = new PmExtractor();

        @Override
        public PreprocessingModel retrieve(SaDocument<? extends ISaSpecification> source) {
            return source.getPreprocessingPart();
        }
    };

    private static class ResExtractor extends DefaultInformationExtractor<SaDocument<? extends ISaSpecification>, TsData> {

        private static final ResExtractor INSTANCE = new ResExtractor();

        @Override
        public TsData retrieve(SaDocument<? extends ISaSpecification> source) {
            PreprocessingModel preprocessing = source.getPreprocessingPart();
            return preprocessing == null ? null : preprocessing.getFullResiduals();
        }
    };

    private static class SsExtractor extends TsDocumentInformationExtractor<SaDocument<? extends ISaSpecification>, SlidingSpans> {

        private static final SsExtractor INSTANCE = new SsExtractor();

        @Override
        protected SlidingSpans buildInfo(SaDocument<? extends ISaSpecification> source) {
            if (!source.getResults().isSuccessful()) {
                return null;
            }
            TsData s = source.getSeries();
            TsDomain domain = s.getDomain();
            SaDocument<? extends ISaSpecification> cl = SaManager.instance.refreshDocument(source, domain, EstimationPolicyType.FreeParameters, false);
            return new SlidingSpans(new TsDocumentProcessing(cl), domain);
        }
    };

    private static class RevisionExtractor extends TsDocumentInformationExtractor<SaDocument<? extends ISaSpecification>, RevisionHistory> {

        private final DemetraUI demetraUI = DemetraUI.getDefault();
        private static final RevisionExtractor INSTANCE = new RevisionExtractor();

        @Override
        protected RevisionHistory buildInfo(SaDocument<? extends ISaSpecification> source) {
            if (!source.getResults().isSuccessful()) {
                return null;
            }
            SaDocumentProcessing process = new SaDocumentProcessing(source, demetraUI.getEstimationPolicyType());
            TsDomain d = source.getPreprocessingPart().description.getEstimationDomain();
            process.process(d);
            RevisionHistory history = new RevisionHistory(process, d);
            return history;
        }
    };

    private static class LikelihoodExtractor extends TsDocumentInformationExtractor<SaDocument<? extends ISaSpecification>, Functions> {

        public static final LikelihoodExtractor INSTANCE = new LikelihoodExtractor();

        @Override
        protected Functions buildInfo(SaDocument<? extends ISaSpecification> source) {
            PreprocessingModel preprocessingPart = source.getPreprocessingPart();
            if (preprocessingPart == null) {
                return null;
            } else {
                return Functions.create(preprocessingPart.likelihoodFunction(), source.getPreprocessingPart().maxLikelihoodFunction());
            }
        }
    };

    private static class StvarExtractor extends DefaultInformationExtractor<SaDocument<? extends ISaSpecification>, StationaryVarianceDecomposition> {

        private static final StvarExtractor INSTANCE = new StvarExtractor();

        @Override
        public StationaryVarianceDecomposition retrieve(SaDocument source) {
            StationaryVarianceDecomposition decomp = new StationaryVarianceDecomposition();
            if (decomp.process(source.getResults())) {
                return decomp;
            } else {
                return null;
            }
        }
    };

    private static class MovingProcessingExtractor extends TsDocumentInformationExtractor<SaDocument<? extends ISaSpecification>, MovingProcessing> {

        private final DemetraUI demetraUI = DemetraUI.getDefault();
        private static final MovingProcessingExtractor INSTANCE = new MovingProcessingExtractor();

        @Override
        protected MovingProcessing buildInfo(SaDocument<? extends ISaSpecification> source) {
            SaDocumentProcessing process = new SaDocumentProcessing(source, demetraUI.getEstimationPolicyType());
            PreprocessingModel preprocessingPart = source.getPreprocessingPart();
            TsDomain d;
            if (preprocessingPart != null) {
                d = preprocessingPart.description.getEstimationDomain();
            } else {
                d = source.getSeries().getDomain();
            }
            process.process(d);
            MovingProcessing mm = new MovingProcessing(process, d);
            mm.setWindowLength(demetraUI.getStabilityLength() * mm.getWindowIncrement());

            return mm;
        }
    };

    private static class SeasTestExtractor extends TsDocumentInformationExtractor<SaDocument<? extends ISaSpecification>, SeasonalityTestUI2.Information> {

        private static final SeasTestExtractor OINSTANCE = new SeasTestExtractor(ModellingDictionary.Y);
        private static final SeasTestExtractor LINSTANCE = new SeasTestExtractor(ModellingDictionary.L);
        private static final SeasTestExtractor RINSTANCE = new SeasTestExtractor(ModellingDictionary.FULL_RES);
        private static final SeasTestExtractor SAINSTANCE = new SeasTestExtractor(ModellingDictionary.SA_CMP);
        private static final SeasTestExtractor IINSTANCE = new SeasTestExtractor(ModellingDictionary.I_CMP);
        private static final SeasTestExtractor LASTRINSTANCE = new SeasTestExtractor(ModellingDictionary.FULL_RES, 10);
        private static final SeasTestExtractor LASTSAINSTANCE = new SeasTestExtractor(ModellingDictionary.SA_CMP, 10);
        private static final SeasTestExtractor LASTIINSTANCE = new SeasTestExtractor(ModellingDictionary.I_CMP, 10);

        private final String name;
        private final int nlast;

        protected SeasTestExtractor(String name) {
            this.name = name;
            this.nlast = 0;
        }

        protected SeasTestExtractor(String name, int nlast) {
            this.name = name;
            this.nlast = nlast;
        }

        @Override
        protected SeasonalityTestUI2.Information buildInfo(SaDocument<? extends ISaSpecification> source) {

            SeasonalityTestUI2.Information info = new SeasonalityTestUI2.Information();
            info.mean = true;
            switch (name) {
                case ModellingDictionary.FULL_RES:
                    info.del = 0;
                    info.mean = false;
                    break;
                case ModellingDictionary.I_CMP:
                    info.del = 0;
                    info.mean = true;
                    break;
                default:
                    info.del = 1;
                    info.mean = true;
                    break;
            }
            PreprocessingModel preprocessingPart = source.getPreprocessingPart();
            ISeriesDecomposition finals = source.getFinalDecomposition();
            if (preprocessingPart != null) {
                switch (name) {
                    case ModellingDictionary.Y:
                        info.s = preprocessingPart.description.transformedOriginal();
                        info.mul = false;
                        break;
                    case ModellingDictionary.FULL_RES:
                        info.s = preprocessingPart.getFullResiduals();
                        info.mul = false;
                        break;
                    default:
                        info.s = source.getResults().getData(name, TsData.class);
                        info.mul = preprocessingPart.isMultiplicative();
                        break;
                }
            } else {
                info.s = source.getResults().getData(name, TsData.class);
                info.mul = finals != null ? finals.getMode().isMultiplicative() : false;
            }
            if (info.s == null) {
                return null;
            }
            if (nlast != 0) {
                TsPeriodSelector selector = new TsPeriodSelector();
                selector.last(nlast * info.s.getFrequency().intValue() + info.del);
                info.s = info.s.select(selector);
            }
            if (info.s.getLength() < 4 * info.s.getFrequency().intValue()) {
                return null;
            }
            return info;
        }
    };
    //</editor-fold>
}
