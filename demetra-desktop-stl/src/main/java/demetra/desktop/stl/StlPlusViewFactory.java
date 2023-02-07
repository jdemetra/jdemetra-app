/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.stl;

import demetra.desktop.TsDynamicProvider;
import demetra.desktop.processing.ui.modelling.ForecastsFactory;
import demetra.desktop.processing.ui.modelling.InputFactory;
import demetra.desktop.processing.ui.modelling.ModelArimaFactory;
import demetra.desktop.processing.ui.modelling.ModelRegressorsFactory;
import demetra.desktop.processing.ui.modelling.NiidTestsFactory;
import demetra.desktop.processing.ui.modelling.OutOfSampleTestFactory;
import demetra.desktop.processing.ui.modelling.RegSarimaViews;
import demetra.desktop.processing.ui.sa.BenchmarkingUI;
import demetra.desktop.processing.ui.sa.SIFactory;
import demetra.desktop.sa.ui.DemetraSaUI;
import demetra.desktop.sa.ui.SaViews;
import demetra.desktop.ui.processing.GenericChartUI;
import demetra.desktop.ui.processing.GenericTableUI;
import demetra.desktop.ui.processing.HtmlItemUI;
import demetra.desktop.ui.processing.IProcDocumentItemFactory;
import demetra.desktop.ui.processing.IProcDocumentViewFactory;
import demetra.desktop.ui.processing.ProcDocumentItemFactory;
import demetra.desktop.ui.processing.ProcDocumentViewFactory;
import demetra.desktop.ui.processing.stats.ResidualsDistUI;
import demetra.desktop.ui.processing.stats.ResidualsUI;
import demetra.desktop.ui.processing.stats.SpectrumUI;
import demetra.html.HtmlElement;
import demetra.html.HtmlElements;
import demetra.html.HtmlHeader;
import demetra.html.core.HtmlDiagnosticsSummary;
import demetra.modelling.ModellingDictionary;
import demetra.processing.ProcDiagnostic;
import demetra.sa.SaDictionaries;
import demetra.sa.SaManager;
import demetra.sa.SaProcessingFactory;
import demetra.sa.StationaryVarianceDecomposition;
import demetra.sa.html.HtmlSeasonalityDiagnostics;
import demetra.sa.html.HtmlStationaryVarianceDecomposition;
import demetra.timeseries.TsData;
import demetra.timeseries.TsDocument;
import demetra.toolkit.dictionaries.RegressionDictionaries;
import demetra.util.Id;
import demetra.util.LinearId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import jdplus.regsarima.regular.RegSarimaModel;
import jdplus.sa.SaBenchmarkingResults;
import jdplus.sa.tests.SeasonalityTests;
import jdplus.stl.StlResults;
import jdplus.stlplus.StlPlusDiagnostics;
import jdplus.stlplus.StlPlusDocument;
import jdplus.stlplus.StlPlusResults;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
public class StlPlusViewFactory extends ProcDocumentViewFactory<StlPlusDocument> {

    public static final String STVAR = "Stationary variance decomposition";
    public static final Id DECOMPOSITION_VAR = new LinearId(SaViews.DECOMPOSITION, STVAR);

    private static final AtomicReference<IProcDocumentViewFactory<StlPlusDocument>> INSTANCE = new AtomicReference();

    public static IProcDocumentViewFactory<StlPlusDocument> getDefault() {
        IProcDocumentViewFactory<StlPlusDocument> fac = INSTANCE.get();
        if (fac == null) {
            fac = new StlPlusViewFactory();
            INSTANCE.lazySet(fac);
        }
        return fac;
    }

    public static void setDefault(IProcDocumentViewFactory<StlPlusDocument> factory) {
        INSTANCE.set(factory);
    }

    public StlPlusViewFactory() {
        registerFromLookup(StlPlusDocument.class);
    }

    @Override
    public Id getPreferredView() {
        return SaViews.MAIN_CHARTS_LOW;
    }

    private final static Function<StlPlusDocument, RegSarimaModel> MODELEXTRACTOR = source -> {
        StlPlusResults tr = source.getResult();
        return tr == null ? null : tr.getPreprocessing();
    };

    private final static Function<StlPlusDocument, StlResults> DECOMPOSITIONEXTRACTOR = source -> {
        StlPlusResults tr = source.getResult();
        return tr == null ? null : tr.getDecomposition();
    };

    private final static Function<StlPlusDocument, StlPlusDiagnostics> DIAGSEXTRACTOR = source -> {
        StlPlusResults tr = source.getResult();
        return tr == null ? null : tr.getDiagnostics();
    };

    private final static Function<StlPlusDocument, TsData> RESEXTRACTOR = MODELEXTRACTOR
            .andThen(regarima -> regarima == null ? null : regarima.fullResiduals());

    private static String generateId(String name, String id) {
        return TsDynamicProvider.CompositeTs.builder()
                .name(name)
                .now(id)
                .build().toString();
    }

    public static String[] lowSeries() {
        return new String[]{
            generateId("Series", SaDictionaries.Y),
            generateId("Seasonally adjusted", SaDictionaries.SA),
            generateId("Trend", SaDictionaries.T)
        };
    }

    public static String[] highSeries() {
        return new String[]{
            generateId("Seasonal", SaDictionaries.S),
            generateId("Calendar effects", ModellingDictionary.CAL),
            generateId("Irregular", SaDictionaries.I)
        };
    }

    public static String[] finalSeries() {
        return new String[]{
            generateId("Series", SaDictionaries.Y),
            generateId("Seasonally adjusted", SaDictionaries.SA),
            generateId("Trend", SaDictionaries.T),
            generateId("Seasonal", SaDictionaries.S),
            generateId("Irregular", SaDictionaries.I)
        };
    }

//<editor-fold defaultstate="collapsed" desc="REGISTER SPEC">
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 100010)
//    public static class SpecFactory extends ProcDocumentItemFactory<FractionalAirlineDocument, HtmlElement> {
//
//        public SpecFactory() {
//            super(StlPlusDocument.class, RegSarimaViews.INPUT_SPEC,
//                    (StlPlusDocument doc) -> {
//                        InformationSet info = FractionalAirlineSpecMapping.write(doc.getSpecification(), true);
//                        return new demetra.html.core.HtmlInformationSet(info);
//                    },
//                    new HtmlItemUI()
//            );
//        }
//
//        @Override
//        public int getPosition() {
//            return 100010;
//        }
//    }
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 1000)
    public static class Input extends InputFactory<StlPlusDocument> {

        public Input() {
            super(StlPlusDocument.class, RegSarimaViews.INPUT_SERIES);
        }

        @Override
        public int getPosition() {
            return 1000;
        }
    }

//</editor-fold>
//
//<editor-fold defaultstate="collapsed" desc="REGISTER SUMMARY">
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 100000 + 1000)
//    public static class SummaryFactory extends ProcDocumentItemFactory<StlPlusDocument, HtmlElement> {
//
//        public SummaryFactory() {
//            super(StlPlusDocument.class, RegSarimaViews.MODEL_SUMMARY,
//                    source -> new HtmlFractionalAirlineModel(source.getResult(), false),
//                    new HtmlItemUI());
//        }
//
//        @Override
//        public int getPosition() {
//            return 101000;
//        }
//    }
//</editor-fold>
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 2000)
    public static class MainLowChart extends ProcDocumentItemFactory<StlPlusDocument, TsDocument> {

        public MainLowChart() {
            super(StlPlusDocument.class, SaViews.MAIN_CHARTS_LOW, s -> s, new GenericChartUI(false, lowSeries()));
        }

        @Override
        public int getPosition() {
            return 2000;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 2100)
    public static class MainHighChart extends ProcDocumentItemFactory<StlPlusDocument, TsDocument> {

        public MainHighChart() {
            super(StlPlusDocument.class, SaViews.MAIN_CHARTS_HIGH, s -> s, new GenericChartUI(false, highSeries()));
        }

        @Override
        public int getPosition() {
            return 2100;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 2200)
    public static class MainTable extends ProcDocumentItemFactory<StlPlusDocument, TsDocument> {

        public MainTable() {
            super(StlPlusDocument.class, SaViews.MAIN_TABLE, s -> s, new GenericTableUI(false, finalSeries()));
        }

        @Override
        public int getPosition() {
            return 2200;
        }

    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 2400)
    public static class MainSiFactory extends SIFactory<StlPlusDocument> {

        public MainSiFactory() {
            super(StlPlusDocument.class, SaViews.MAIN_SI, (StlPlusDocument source) -> {
                StlPlusResults result = source.getResult();
                if (result == null) {
                    return null;
                }
                return result.getDecomposition().asDecomposition();
            });
        }

        @Override
        public int getPosition() {
            return 2400;
        }
    }

//<editor-fold defaultstate="collapsed" desc="PREPROCESSING">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3000)
    public static class SummaryFactory extends ProcDocumentItemFactory<StlPlusDocument, HtmlElement> {

        public SummaryFactory() {

            super(StlPlusDocument.class, SaViews.PREPROCESSING_SUMMARY, MODELEXTRACTOR
                    .andThen(regarima -> regarima == null ? null
                    : new demetra.html.modelling.HtmlRegSarima(regarima, false)),
                    new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 3000;
        }
    }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="PREPROCESSING-FORECASTS">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3110)
    public static class ForecastsTable extends ProcDocumentItemFactory<StlPlusDocument, TsDocument> {

        public ForecastsTable() {
            super(StlPlusDocument.class, SaViews.PREPROCESSING_FCASTS_TABLE, s -> s, new GenericTableUI(false, generateItems()));
        }

        @Override
        public int getPosition() {
            return 3110;
        }

        private static String[] generateItems() {
            return new String[]{RegressionDictionaries.Y_F, RegressionDictionaries.Y_EF};
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3100)
    public static class FCastsFactory extends ForecastsFactory<StlPlusDocument> {

        public FCastsFactory() {
            super(StlPlusDocument.class, SaViews.PREPROCESSING_FCASTS, MODELEXTRACTOR);
        }

        @Override
        public int getPosition() {
            return 3100;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3120)
    public static class FCastsOutFactory extends OutOfSampleTestFactory<StlPlusDocument> {

        public FCastsOutFactory() {
            super(StlPlusDocument.class, SaViews.PREPROCESSING_FCASTS_OUTOFSAMPLE, MODELEXTRACTOR);
        }

        @Override
        public int getPosition() {
            return 3120;
        }
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="PREPROCESSING-DETAILS">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3200)
    public static class ModelRegsFactory extends ModelRegressorsFactory<StlPlusDocument> {

        public ModelRegsFactory() {
            super(StlPlusDocument.class, SaViews.PREPROCESSING_REGS, MODELEXTRACTOR);
        }

        @Override
        public int getPosition() {
            return 3200;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3300)
    public static class ArimaFactory extends ModelArimaFactory {

        public ArimaFactory() {
            super(StlPlusDocument.class, SaViews.PREPROCESSING_ARIMA, MODELEXTRACTOR);
        }

        @Override
        public int getPosition() {
            return 3300;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3400)
    public static class PreprocessingDetFactory extends ProcDocumentItemFactory<StlPlusDocument, TsDocument> {

        public PreprocessingDetFactory() {
            super(StlPlusDocument.class, SaViews.PREPROCESSING_DET, source -> source, new GenericTableUI(false,
                    SaDictionaries.PREPROCESSING, ModellingDictionary.YCAL,
                    SaDictionaries.PREPROCESSING, ModellingDictionary.Y_LIN,
                    SaDictionaries.PREPROCESSING, ModellingDictionary.DET,
                    SaDictionaries.PREPROCESSING, ModellingDictionary.CAL,
                    SaDictionaries.PREPROCESSING, ModellingDictionary.TDE,
                    SaDictionaries.PREPROCESSING, ModellingDictionary.EE,
                    SaDictionaries.PREPROCESSING, ModellingDictionary.OUT,
                    SaDictionaries.PREPROCESSING, ModellingDictionary.FULL_RES));
        }

        @Override
        public int getPosition() {
            return 3400;
        }
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="PREPROCESSING-RESIDUALS">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3500)
    public static class ModelResFactory extends ProcDocumentItemFactory<StlPlusDocument, TsData> {

        public ModelResFactory() {
            super(StlPlusDocument.class, SaViews.PREPROCESSING_RES, RESEXTRACTOR,
                    new ResidualsUI()
            );
        }

        @Override
        public int getPosition() {
            return 3500;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3510)
    public static class ModelResStatsFactory extends NiidTestsFactory<StlPlusDocument> {

        public ModelResStatsFactory() {
            super(StlPlusDocument.class, SaViews.PREPROCESSING_RES_STATS, MODELEXTRACTOR);
        }

        @Override
        public int getPosition() {
            return 3510;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3520)
    public static class ModelResDist extends ProcDocumentItemFactory<StlPlusDocument, TsData> {

        public ModelResDist() {
            super(StlPlusDocument.class, SaViews.PREPROCESSING_RES_DIST,
                    RESEXTRACTOR,
                    new ResidualsDistUI());

        }

        @Override
        public int getPosition() {
            return 3520;
        }
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="REGISTER FORECASTS">
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 200000 + 500)
//    public static class ForecastsTable extends ProcDocumentItemFactory<FractionalAirlineDocument, TsDocument> {
//
//        public ForecastsTable() {
//            super(StlPlusDocument.class, RegSarimaViews.MODEL_FCASTS_TABLE, s -> s, new GenericTableUI(false, generateItems()));
//        }
//
//        @Override
//        public int getPosition() {
//            return 200500;
//        }
//
//        private static String[] generateItems() {
//            return new String[]{ModellingDictionary.Y + SeriesInfo.F_SUFFIX, ModellingDictionary.Y + SeriesInfo.EF_SUFFIX};
//        }
//    }
//
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 200000 + 1000)
//    public static class FCastsFactory extends ForecastsFactory<FractionalAirlineDocument> {
//
//        public FCastsFactory() {
//            super(StlPlusDocument.class, RegSarimaViews.MODEL_FCASTS, MODELEXTRACTOR);
//        }
//
//        @Override
//        public int getPosition() {
//            return 201000;
//        }
//    }
//
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 200000 + 2000)
//    public static class FCastsOutFactory extends OutOfSampleTestFactory<FractionalAirlineDocument> {
//
//        public FCastsOutFactory() {
//            super(StlPlusDocument.class, RegSarimaViews.MODEL_FCASTS_OUTOFSAMPLE, MODELEXTRACTOR);
//        }
//
//        @Override
//        public int getPosition() {
//            return 202000;
//        }
//    }
//</editor-fold>
//
//<editor-fold defaultstate="collapsed" desc="REGISTER MODEL">
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 300000 + 1000)
//    public static class ModelRegsFactory extends ModelRegressorsFactory<FractionalAirlineDocument> {
//
//        public ModelRegsFactory() {
//            super(StlPlusDocument.class, RegSarimaViews.MODEL_REGS, MODELEXTRACTOR);
//        }
//
//        @Override
//        public int getPosition() {
//            return 301000;
//        }
//    }
//
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 300000 + 2000)
//    public static class ArimaFactory extends ModelArimaFactory {
//
//        public ArimaFactory() {
//            super(StlPlusDocument.class, RegSarimaViews.MODEL_ARIMA, MODELEXTRACTOR);
//        }
//
//        @Override
//        public int getPosition() {
//            return 302000;
//        }
//    }
//
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 300000 + 3000)
//    public static class PreprocessingDetFactory extends ProcDocumentItemFactory<FractionalAirlineDocument, StlPlusDocument> {
//
//        public PreprocessingDetFactory() {
//            super(StlPlusDocument.class, RegSarimaViews.MODEL_DET,
//                    source -> source, new GenericTableUI(false,
//                            ModellingDictionary.Y_LIN, ModellingDictionary.DET,
//                            ModellingDictionary.CAL, ModellingDictionary.TDE, ModellingDictionary.EE,
//                            ModellingDictionary.OUT, ModellingDictionary.FULL_RES));
//        }
//
//        @Override
//        public int getPosition() {
//            return 303000;
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="REGISTER RESIDUALS">
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 400000 + 1000)
//    public static class ModelResFactory extends ProcDocumentItemFactory<FractionalAirlineDocument, TsData> {
//
//        public ModelResFactory() {
//            super(StlPlusDocument.class, RegSarimaViews.MODEL_RES, RESEXTRACTOR,
//                    new ResidualsUI()
//            );
//        }
//
//        @Override
//        public int getPosition() {
//            return 401000;
//        }
//    }
//
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 400000 + 2000)
//    public static class ModelResStatsFactory extends NiidTestsFactory<FractionalAirlineDocument> {
//
//        public ModelResStatsFactory() {
//            super(StlPlusDocument.class, RegSarimaViews.MODEL_RES_STATS, MODELEXTRACTOR);
//        }
//
//        @Override
//        public int getPosition() {
//            return 402000;
//        }
//    }
//
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 400000 + 3000)
//    public static class ModelResDist extends ProcDocumentItemFactory<StlPlusDocument, DoubleSeq> {
//
//        public ModelResDist() {
//            super(StlPlusDocument.class, RegSarimaViews.MODEL_RES_DIST, RESEXTRACTOR,
//                    new DistributionUI());
//
//        }
//
//        @Override
//        public int getPosition() {
//            return 403000;
//        }
//    }
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 400000 + 4000)
//    public static class ModelResSpectrum extends ProcDocumentItemFactory<StlPlusDocument, DoubleSeq> {
//
//        public ModelResSpectrum() {
//            super(StlPlusDocument.class, RegSarimaViews.MODEL_RES_SPECTRUM, RESEXTRACTOR,
//                    new PeriodogramUI());
//
//        }
//
//        @Override
//        public int getPosition() {
//            return 404000;
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="REGISTER DETAILS">
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 500000)
//    public static class LFactory extends LikelihoodFactory<FractionalAirlineDocument> {
//
//        public LFactory() {
//            super(StlPlusDocument.class, RegSarimaViews.MODEL_LIKELIHOOD, MODELEXTRACTOR);
//            setAsync(true);
//        }
//
//        @Override
//        public int getPosition() {
//            return 500000;
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="BENCHMARKING">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 4900)
    public static class BenchmarkingFactory extends ProcDocumentItemFactory<StlPlusDocument, BenchmarkingUI.Input> {

        public BenchmarkingFactory() {
            super(StlPlusDocument.class, SaViews.BENCHMARKING_SUMMARY, (StlPlusDocument doc) -> {
                StlPlusResults rslt = doc.getResult();
                if (rslt == null) {
                    return null;
                }
                SaBenchmarkingResults benchmarking = rslt.getBenchmarking();
                if (benchmarking == null) {
                    return null;
                }
                boolean mul = rslt.getFinals().getMode().isMultiplicative();
                return new BenchmarkingUI.Input(mul, benchmarking);
            }, new BenchmarkingUI());
        }

        @Override
        public int getPosition() {
            return 4900;
        }

    }
//</editor-fold>

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 4910)
    public static class StationaryVarianceDecompositionFactory extends ProcDocumentItemFactory<StlPlusDocument, HtmlElement> {

        public StationaryVarianceDecompositionFactory() {
            super(StlPlusDocument.class, DECOMPOSITION_VAR, DIAGSEXTRACTOR.andThen(
                    (StlPlusDiagnostics diags) -> {
                        StationaryVarianceDecomposition decomp = diags.getVarianceDecomposition();
                        if (decomp == null) {
                            return null;
                        }
                        return new HtmlStationaryVarianceDecomposition(decomp);
                    }),
                    new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 4910;
        }
    }

//<editor-fold defaultstate="collapsed" desc="DIAGNOSTICS">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5000)
    public static class DiagnosticsSummaryFactory extends ProcDocumentItemFactory<StlPlusDocument, HtmlElement> {

        public DiagnosticsSummaryFactory() {
            super(StlPlusDocument.class, SaViews.DIAGNOSTICS_SUMMARY, (StlPlusDocument doc) -> {
                StlPlusResults rslt = doc.getResult();
                if (rslt == null) {
                    return null;
                }
                SaProcessingFactory factory = SaManager.factoryFor(doc.getSpecification());
                List<ProcDiagnostic> diags = new ArrayList<>();
                factory.fillDiagnostics(diags, rslt);
                return new HtmlDiagnosticsSummary(diags);
            }, new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 5000;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5010)
    public static class OriginalSeasonalityFactory extends ProcDocumentItemFactory<StlPlusDocument, HtmlElement> {

        public OriginalSeasonalityFactory() {
            super(StlPlusDocument.class, SaViews.DIAGNOSTICS_OSEASONALITY, (StlPlusDocument doc) -> {
                StlPlusResults rslt = doc.getResult();
                if (rslt == null) {
                    return null;
                }
                TsData s;
                if (rslt.getPreprocessing() != null) {
                    s = rslt.getPreprocessing().transformedSeries();
                } else {
                    s = rslt.getDecomposition().getSeries();
                }
                if (s == null) {
                    return null;
                }
                return new HtmlElements(new HtmlHeader(1, "Original [transformed] series", true),
                        new HtmlSeasonalityDiagnostics(SeasonalityTests.seasonalityTest(s.getValues(), s.getAnnualFrequency(), 1, true, true), false));

            }, new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 5010;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5020)
    public static class LinSeasonalityFactory extends ProcDocumentItemFactory<StlPlusDocument, HtmlElement> {

        public LinSeasonalityFactory() {
            super(StlPlusDocument.class, SaViews.DIAGNOSTICS_LSEASONALITY, (StlPlusDocument doc) -> {
                StlPlusResults rslt = doc.getResult();
                if (rslt == null || rslt.getPreprocessing() == null) {
                    return null;
                }
                TsData s = rslt.getPreprocessing().linearizedSeries();
                if (s == null) {
                    return null;
                }
                return new HtmlElements(new HtmlHeader(1, "Linearized series", true),
                        new HtmlSeasonalityDiagnostics(SeasonalityTests.seasonalityTest(s.getValues(), s.getAnnualFrequency(), 1, true, true), false));

            }, new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 5020;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5030)
    public static class ResSeasonalityFactory extends ProcDocumentItemFactory<StlPlusDocument, HtmlElement> {

        public ResSeasonalityFactory() {
            super(StlPlusDocument.class, SaViews.DIAGNOSTICS_RSEASONALITY, (StlPlusDocument doc) -> {
                StlPlusResults rslt = doc.getResult();
                if (rslt == null || rslt.getPreprocessing() == null) {
                    return null;
                }
                TsData s = rslt.getPreprocessing().fullResiduals();
                if (s == null) {
                    return null;
                }
                return new HtmlElements(new HtmlHeader(1, "Full residuals", true),
                        new HtmlSeasonalityDiagnostics(SeasonalityTests.seasonalityTest(s.getValues(), s.getAnnualFrequency(), 0, false, true), true));

            }, new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 5030;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5040)
    public static class SaSeasonalityFactory extends ProcDocumentItemFactory<StlPlusDocument, HtmlElement> {

        public SaSeasonalityFactory() {
            super(StlPlusDocument.class, SaViews.DIAGNOSTICS_SASEASONALITY, (StlPlusDocument doc) -> {
                StlPlusResults rslt = doc.getResult();
                if (rslt == null) {
                    return null;
                }
                TsData s = rslt.getDecomposition().getSa();
                if (s == null) {
                    return null;
                }
                if (rslt.getDecomposition().isMultiplicative()) {
                    s = s.log();
                }
                return new HtmlElements(new HtmlHeader(1, "[Linearized] seasonally adjusted series", true),
                        new HtmlSeasonalityDiagnostics(SeasonalityTests.seasonalityTest(s.getValues(), s.getAnnualFrequency(), 1, true, true), true));

            }, new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 5030;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5050)
    public static class IrrSeasonalityFactory extends ProcDocumentItemFactory<StlPlusDocument, HtmlElement> {

        public IrrSeasonalityFactory() {
            super(StlPlusDocument.class, SaViews.DIAGNOSTICS_ISEASONALITY, (StlPlusDocument doc) -> {
                StlPlusResults rslt = doc.getResult();
                if (rslt == null) {
                    return null;
                }
                TsData s = rslt.getDecomposition().getIrregular();
                if (s == null) {
                    return null;
                }
                if (rslt.getDecomposition().isMultiplicative()) {
                    s = s.log();
                }
                return new HtmlElements(new HtmlHeader(1, "[Linearized] irregular component", true),
                        new HtmlSeasonalityDiagnostics(SeasonalityTests.seasonalityTest(s.getValues(), s.getAnnualFrequency(), 0, false, true), true));

            }, new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 5050;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5060)
    public static class LastResSeasonalityFactory extends ProcDocumentItemFactory<StlPlusDocument, HtmlElement> {

        public LastResSeasonalityFactory() {
            super(StlPlusDocument.class, SaViews.DIAGNOSTICS_LASTRSEASONALITY, (StlPlusDocument doc) -> {
                StlPlusResults rslt = doc.getResult();
                if (rslt == null || rslt.getPreprocessing() == null) {
                    return null;
                }
                TsData s = rslt.getPreprocessing().fullResiduals();
                if (s == null) {
                    return null;
                }
                StringBuilder header = new StringBuilder().append("Full residuals");
                int ny = DemetraSaUI.get().getSeasonalityLength();
                if (ny > 0) {
                    s = s.drop(Math.max(0, s.length() - s.getAnnualFrequency() * ny), 0);
                    header.append(" (last ").append(ny).append(" years)");
                }
                return new HtmlElements(new HtmlHeader(1, header.toString(), true),
                        new HtmlSeasonalityDiagnostics(SeasonalityTests.seasonalityTest(s.getValues(), s.getAnnualFrequency(), 0, false, true), true));

            }, new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 5060;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5070)
    public static class LastSaSeasonalityFactory extends ProcDocumentItemFactory<StlPlusDocument, HtmlElement> {

        public LastSaSeasonalityFactory() {
            super(StlPlusDocument.class, SaViews.DIAGNOSTICS_LASTSASEASONALITY, (StlPlusDocument doc) -> {
                StlPlusResults rslt = doc.getResult();
                if (rslt == null) {
                    return null;
                }
                TsData s = rslt.getDecomposition().getSa();
                if (s == null) {
                    return null;
                }
                StringBuilder header = new StringBuilder().append("[Linearized] seasonally adjusted series");
                int ny = DemetraSaUI.get().getSeasonalityLength();
                if (ny > 0) {
                    s = s.drop(Math.max(0, s.length() - s.getAnnualFrequency() * ny - 1), 0);
                    header.append(" (last ").append(ny).append(" years)");
                }
                if (rslt.getDecomposition().isMultiplicative()) {
                    s = s.log();
                }
                return new HtmlElements(new HtmlHeader(1, header.toString(), true),
                        new HtmlSeasonalityDiagnostics(SeasonalityTests.seasonalityTest(s.getValues(), s.getAnnualFrequency(), 1, true, true), true));

            }, new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 5070;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5080)
    public static class LastIrrSeasonalityFactory extends ProcDocumentItemFactory<StlPlusDocument, HtmlElement> {

        public LastIrrSeasonalityFactory() {
            super(StlPlusDocument.class, SaViews.DIAGNOSTICS_LASTISEASONALITY, (StlPlusDocument doc) -> {
                StlPlusResults rslt = doc.getResult();
                if (rslt == null) {
                    return null;
                }
                TsData s = rslt.getDecomposition().getIrregular();
                if (s == null) {
                    return null;
                }
                StringBuilder header = new StringBuilder().append("[Linearized] irregular component");
                int ny = DemetraSaUI.get().getSeasonalityLength();
                if (ny > 0) {
                    s = s.drop(Math.max(0, s.length() - s.getAnnualFrequency() * ny), 0);
                    header.append(" (last ").append(ny).append(" years)");
                }
                if (rslt.getDecomposition().isMultiplicative()) {
                    s = s.log();
                }
                return new HtmlElements(new HtmlHeader(1, header.toString(), true),
                        new HtmlSeasonalityDiagnostics(SeasonalityTests.seasonalityTest(s.getValues(), s.getAnnualFrequency(), 0, false, true), true));

            }, new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 5080;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5310)
    public static class ModelResSpectrum extends ProcDocumentItemFactory<StlPlusDocument, TsData> {

        public ModelResSpectrum() {
            super(StlPlusDocument.class, SaViews.DIAGNOSTICS_SPECTRUM_RES,
                    RESEXTRACTOR.andThen(
                            (TsData s) -> {
                                if (s == null) {
                                    return null;
                                }
                                int ny = DemetraSaUI.get().getSpectralLastYears();
                                if (ny > 0) {
                                    s = s.drop(Math.max(0, s.length() - s.getAnnualFrequency() * ny), 0);
                                }
                                return s;
                            }
                    ),
                    new SpectrumUI(true));

        }

        @Override
        public int getPosition() {
            return 5310;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5320)
    public static class DiagnosticsSpectrumIFactory extends ProcDocumentItemFactory<StlPlusDocument, TsData> {

        public DiagnosticsSpectrumIFactory() {
            super(StlPlusDocument.class, SaViews.DIAGNOSTICS_SPECTRUM_I,
                    DECOMPOSITIONEXTRACTOR.andThen(
                            (StlResults stl) -> {
                                if (stl == null) {
                                    return null;
                                }
                                TsData s = stl.getIrregular();
                                if (s == null) {
                                    return null;
                                }
                                int ny = DemetraSaUI.get().getSpectralLastYears();
                                if (ny > 0) {
                                    s = s.drop(Math.max(0, s.length() - s.getAnnualFrequency() * ny), 0);
                                }
                                return stl.isMultiplicative()? s.log() : s;
                            }
                    ),
                    new SpectrumUI(false));
        }

        @Override
        public int getPosition() {
            return 5320;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5330)
    public static class DiagnosticsSpectrumSaFactory extends ProcDocumentItemFactory<StlPlusDocument, TsData> {

        public DiagnosticsSpectrumSaFactory() {
            super(StlPlusDocument.class, SaViews.DIAGNOSTICS_SPECTRUM_SA,
                    DECOMPOSITIONEXTRACTOR.andThen(
                            (StlResults stl) -> {
                                if (stl == null) {
                                    return null;
                                }
                                TsData s = stl.getSa();
                                if (s == null) {
                                    return null;
                                }
                                s = s.delta(1);
                                int ny = DemetraSaUI.get().getSpectralLastYears();
                                if (ny > 0) {
                                    s = s.drop(Math.max(0, s.length() - s.getAnnualFrequency() * ny), 0);
                                }
                                return s;
                            }
                    ),
                    new SpectrumUI(false));
        }

        @Override
        public int getPosition() {
            return 5330;
        }
    }

//</editor-fold>
}
