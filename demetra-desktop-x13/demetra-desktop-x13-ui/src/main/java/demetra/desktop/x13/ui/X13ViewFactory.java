/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.x13.ui;

import demetra.desktop.TsDynamicProvider;
import demetra.desktop.processing.ui.modelling.ForecastsFactory;
import demetra.desktop.processing.ui.modelling.InputFactory;
import demetra.desktop.processing.ui.modelling.LikelihoodFactory;
import demetra.desktop.processing.ui.modelling.ModelArimaFactory;
import demetra.desktop.processing.ui.modelling.ModelRegressorsFactory;
import demetra.desktop.processing.ui.modelling.NiidTestsFactory;
import demetra.desktop.processing.ui.modelling.OutOfSampleTestFactory;
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
import demetra.desktop.ui.processing.stats.RevisionHistoryUI;
import demetra.desktop.ui.processing.stats.SpectrumUI;
import demetra.desktop.ui.processing.stats.StabilityUI;
import demetra.html.AbstractHtmlElement;
import demetra.html.HtmlElement;
import demetra.html.HtmlElements;
import demetra.html.HtmlHeader;
import demetra.html.HtmlStream;
import demetra.html.HtmlTag;
import demetra.html.core.HtmlDiagnosticsSummary;
import demetra.information.BasicInformationExtractor;
import demetra.information.Explorable;
import demetra.information.InformationSet;
import demetra.modelling.ModellingDictionary;
import demetra.modelling.SeriesInfo;
import demetra.processing.ProcDiagnostic;
import demetra.regarima.RegArimaSpec;
import demetra.sa.ComponentType;
import demetra.sa.EstimationPolicyType;
import demetra.sa.SaDictionaries;
import demetra.sa.SaManager;
import demetra.sa.SaProcessingFactory;
import demetra.sa.SeriesDecomposition;
import demetra.sa.html.HtmlSeasonalityDiagnostics;
import demetra.timeseries.TsData;
import demetra.timeseries.TsDocument;
import demetra.timeseries.TsDomain;
import demetra.toolkit.dictionaries.Dictionary;
import demetra.toolkit.dictionaries.RegressionDictionaries;
import demetra.util.Id;
import demetra.util.LinearId;
import demetra.x11.SeasonalFilterOption;
import demetra.x11.X11Dictionaries;
import demetra.x13.X13Dictionaries;
import demetra.x13.X13Spec;
import demetra.x13.io.information.X13SpecMapping;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import jdplus.regsarima.regular.RegSarimaModel;
import jdplus.sa.tests.SeasonalityTests;
import jdplus.timeseries.simplets.analysis.DiagnosticInfo;
import jdplus.timeseries.simplets.analysis.MovingProcessing;
import jdplus.timeseries.simplets.analysis.RevisionHistory;
import jdplus.x11.X11Results;
import jdplus.x13.X13Document;
import jdplus.x13.X13Factory;
import jdplus.x13.X13Kernel;
import jdplus.x13.X13Results;
import jdplus.x13.regarima.RegArimaFactory;
import jdplus.x13.regarima.RegArimaKernel;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
public class X13ViewFactory extends ProcDocumentViewFactory<X13Document> {

    public static final String X11 = "Decomposition (X11)";
    // X11 nodes
    public static final String A = "A-Table", B = "B-Table",
            C = "C-Table", D = "D-Table", E = "E-Table", M = "Quality measures", FINALFILTERS = "Final filters";
    public static final Id M_STATISTICS_SUMMARY = new LinearId(X11, M, SaViews.SUMMARY),
            M_STATISTICS_DETAILS = new LinearId(X11, M, SaViews.DETAILS),
            X11_FILTERS = new LinearId(X11, FINALFILTERS),
            A_TABLES = new LinearId(X11, A),
            B_TABLES = new LinearId(X11, B),
            C_TABLES = new LinearId(X11, C),
            D_TABLES = new LinearId(X11, D),
            E_TABLES = new LinearId(X11, E);

    private static final AtomicReference<IProcDocumentViewFactory<X13Document>> INSTANCE = new AtomicReference();

    private final static Function<X13Document, RegSarimaModel> MODELEXTRACTOR = source -> {
        X13Results tr = source.getResult();
        return tr == null ? null : tr.getPreprocessing();
    };

    private final static Function<X13Document, TsData> RESEXTRACTOR = MODELEXTRACTOR
            .andThen(regarima -> regarima.fullResiduals());

    public static IProcDocumentViewFactory<X13Document> getDefault() {
        IProcDocumentViewFactory<X13Document> fac = INSTANCE.get();
        if (fac == null) {
            fac = new X13ViewFactory();
            INSTANCE.lazySet(fac);
        }
        return fac;
    }

    public static void setDefault(IProcDocumentViewFactory<X13Document> factory) {
        INSTANCE.set(factory);
    }

    public X13ViewFactory() {
        registerFromLookup(X13Document.class);
    }

    @Override
    public Id getPreferredView() {
        return SaViews.MAIN_SUMMARY;
    }

//<editor-fold defaultstate="collapsed" desc="INPUT">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 1010)
    public static class SpecFactory extends ProcDocumentItemFactory<X13Document, HtmlElement> {

        public SpecFactory() {
            super(X13Document.class, SaViews.INPUT_SPEC,
                    (X13Document doc) -> {
                        InformationSet info = X13SpecMapping.write(doc.getSpecification(), true);
                        return new demetra.html.core.HtmlInformationSet(info);
                    },
                    new HtmlItemUI()
            );
        }

        @Override
        public int getPosition() {
            return 1010;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 1000)
    public static class Input extends InputFactory<X13Document> {

        public Input() {
            super(X13Document.class, SaViews.INPUT_SERIES);
        }

        @Override
        public int getPosition() {
            return 1000;
        }
    }
//</editor-fold>

    private static String generateId(String name, String id) {
        return TsDynamicProvider.CompositeTs.builder()
                .name(name)
                .back(id + SeriesInfo.B_SUFFIX)
                .now(id)
                .fore(id + SeriesInfo.F_SUFFIX)
                .build().toString();
    }

    private static String generateStdErrorId(String name, String id) {
        return TsDynamicProvider.CompositeTs.builder()
                .name(name)
                .back(id + SeriesInfo.EB_SUFFIX)
                .now(id + SeriesInfo.E_SUFFIX)
                .fore(id + SeriesInfo.EF_SUFFIX)
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
            generateId("Seasonal (component)", BasicInformationExtractor.concatenate(SaDictionaries.DECOMPOSITION, SaDictionaries.S_CMP)),
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

    //<editor-fold defaultstate="collapsed" desc="MAIN">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 2000)
    public static class MainSummaryFactory extends ProcDocumentItemFactory<X13Document, X13Document> {

        public MainSummaryFactory() {
            super(X13Document.class, SaViews.MAIN_SUMMARY, s->s, new X13Summary());
        }

        @Override
        public int getPosition() {
            return 2000;
        }
    }
    
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 2100)
    public static class MainLowChart extends ProcDocumentItemFactory<X13Document, TsDocument> {

        public MainLowChart() {
            super(X13Document.class, SaViews.MAIN_CHARTS_LOW, s -> s, new GenericChartUI(false, lowSeries()));
        }

        @Override
        public int getPosition() {
            return 2100;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 2200)
    public static class MainHighChart extends ProcDocumentItemFactory<X13Document, TsDocument> {

        public MainHighChart() {
            super(X13Document.class, SaViews.MAIN_CHARTS_HIGH, s -> s, new GenericChartUI(false, highSeries()));
        }

        @Override
        public int getPosition() {
            return 2200;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 2300)
    public static class MainTable extends ProcDocumentItemFactory<X13Document, TsDocument> {

        public MainTable() {
            super(X13Document.class, SaViews.MAIN_TABLE, s -> s, new GenericTableUI(false, finalSeries()));
        }

        @Override
        public int getPosition() {
            return 2300;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 2400)
    public static class MainSiFactory extends SIFactory<X13Document> {

        public MainSiFactory() {
            super(X13Document.class, SaViews.MAIN_SI, (X13Document source) -> {
                X13Results result = source.getResult();
                if (result == null) {
                    return null;
                }
                X11Results x11 = result.getDecomposition();
                return SeriesDecomposition.builder(result.getDecomposition().getMode())
                        .add(x11.getB1(), ComponentType.Series)
                        .add(x11.getD10(), ComponentType.Seasonal)
                        .add(x11.getD11(), ComponentType.SeasonallyAdjusted)
                        .add(x11.getD12(), ComponentType.Trend)
                        .add(x11.getD13(), ComponentType.Irregular)
                        .build();
            });
        }

        @Override
        public int getPosition() {
            return 2400;
        }
    }
   //</editor-fold>

//<editor-fold defaultstate="collapsed" desc="PREPROCESSING">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3000)
    public static class SummaryFactory extends ProcDocumentItemFactory<X13Document, HtmlElement> {

        public SummaryFactory() {

            super(X13Document.class, SaViews.PREPROCESSING_SUMMARY, MODELEXTRACTOR
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
    public static class ForecastsTable extends ProcDocumentItemFactory<X13Document, TsDocument> {

        public ForecastsTable() {
            super(X13Document.class, SaViews.PREPROCESSING_FCASTS_TABLE, s -> s, new GenericTableUI(false, generateItems()));
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
    public static class FCastsFactory extends ForecastsFactory<X13Document> {

        public FCastsFactory() {
            super(X13Document.class, SaViews.PREPROCESSING_FCASTS, MODELEXTRACTOR);
        }

        @Override
        public int getPosition() {
            return 3100;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3120)
    public static class FCastsOutFactory extends OutOfSampleTestFactory<X13Document> {

        public FCastsOutFactory() {
            super(X13Document.class, SaViews.PREPROCESSING_FCASTS_OUTOFSAMPLE, MODELEXTRACTOR);
        }

        @Override
        public int getPosition() {
            return 3120;
        }
    }
//</editor-fold>
//
//<editor-fold defaultstate="collapsed" desc="PREPROCESSING-DETAILS">

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3200)
    public static class ModelRegsFactory extends ModelRegressorsFactory<X13Document> {

        public ModelRegsFactory() {
            super(X13Document.class, SaViews.PREPROCESSING_REGS, MODELEXTRACTOR);
        }

        @Override
        public int getPosition() {
            return 3200;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3300)
    public static class ArimaFactory extends ModelArimaFactory {

        public ArimaFactory() {
            super(X13Document.class, SaViews.PREPROCESSING_ARIMA, MODELEXTRACTOR);
        }

        @Override
        public int getPosition() {
            return 3300;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3400)
    public static class PreprocessingDetFactory extends ProcDocumentItemFactory<X13Document, TsDocument> {

        public PreprocessingDetFactory() {
            super(X13Document.class, SaViews.PREPROCESSING_DET, source -> source, new GenericTableUI(false,
                    //                    BasicInformationExtractor.concatenate(SaDictionaries.PREPROCESSING, ModellingDictionary.Y_LIN), 
                    //                    BasicInformationExtractor.concatenate(SaDictionaries.PREPROCESSING, ModellingDictionary.DET),
                    //                    BasicInformationExtractor.concatenate(SaDictionaries.PREPROCESSING, ModellingDictionary.CAL), 
                    //                    BasicInformationExtractor.concatenate(SaDictionaries.PREPROCESSING, ModellingDictionary.TDE), 
                    //                    BasicInformationExtractor.concatenate(SaDictionaries.PREPROCESSING, ModellingDictionary.EE),
                    //                    BasicInformationExtractor.concatenate(SaDictionaries.PREPROCESSING, ModellingDictionary.OUT), 
                    //                    BasicInformationExtractor.concatenate(SaDictionaries.PREPROCESSING, ModellingDictionary.FULL_RES)));
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
    public static class ModelResFactory extends ProcDocumentItemFactory<X13Document, TsData> {

        public ModelResFactory() {
            super(X13Document.class, SaViews.PREPROCESSING_RES, RESEXTRACTOR,
                    new ResidualsUI()
            );
        }

        @Override
        public int getPosition() {
            return 3500;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3510)
    public static class ModelResStatsFactory extends NiidTestsFactory<X13Document> {

        public ModelResStatsFactory() {
            super(X13Document.class, SaViews.PREPROCESSING_RES_STATS, MODELEXTRACTOR);
        }

        @Override
        public int getPosition() {
            return 3510;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3520)
    public static class ModelResDist extends ProcDocumentItemFactory<X13Document, TsData> {

        public ModelResDist() {
            super(X13Document.class, SaViews.PREPROCESSING_RES_DIST,
                    RESEXTRACTOR,
                    new ResidualsDistUI());

        }

        @Override
        public int getPosition() {
            return 3520;
        }
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="PREPROCESSING-OTHERS">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3600)
    public static class LFactory extends LikelihoodFactory<X13Document> {

        public LFactory() {
            super(X13Document.class, SaViews.PREPROCESSING_LIKELIHOOD, MODELEXTRACTOR);
            setAsync(true);
        }

        @Override
        public int getPosition() {
            return 3600;
        }
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="REGISTER X11">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 4010)
    public static class ATablesFactory extends ProcDocumentItemFactory<X13Document, TsDocument> {

        public ATablesFactory() {
            super(X13Document.class, A_TABLES, source -> source, new GenericTableUI(false,
                    BasicInformationExtractor.prefix(X13Dictionaries.A_TABLE, X13Dictionaries.PREADJUST)));
        }

        @Override
        public int getPosition() {
            return 4010;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 4020)
    public static class BTablesFactory extends ProcDocumentItemFactory<X13Document, TsDocument> {

        public BTablesFactory() {
            super(X13Document.class, B_TABLES, source -> source, new GenericTableUI(false,
                    BasicInformationExtractor.prefix(X11Dictionaries.B_TABLE, X13Dictionaries.X11)));
        }

        @Override
        public int getPosition() {
            return 4020;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 4030)
    public static class CTablesFactory extends ProcDocumentItemFactory<X13Document, TsDocument> {

        public CTablesFactory() {
            super(X13Document.class, C_TABLES, source -> source, new GenericTableUI(false,
                    BasicInformationExtractor.prefix(X11Dictionaries.C_TABLE, X13Dictionaries.X11)));
        }

        @Override
        public int getPosition() {
            return 4030;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 4040)
    public static class DTablesFactory extends ProcDocumentItemFactory<X13Document, TsDocument> {

        static final String[] items = new String[]{
            Dictionary.concatenate(X13Dictionaries.X11, X11Dictionaries.D1),
            Dictionary.concatenate(X13Dictionaries.X11, X11Dictionaries.D4),
            Dictionary.concatenate(X13Dictionaries.X11, X11Dictionaries.D5),
            Dictionary.concatenate(X13Dictionaries.X11, X11Dictionaries.D6),
            Dictionary.concatenate(X13Dictionaries.X11, X11Dictionaries.D7),
            Dictionary.concatenate(X13Dictionaries.X11, X11Dictionaries.D8),
            Dictionary.concatenate(X13Dictionaries.X11, X11Dictionaries.D9),
            Dictionary.concatenate(X13Dictionaries.X11, X11Dictionaries.D10),
            Dictionary.concatenate(X13Dictionaries.X11, X11Dictionaries.D10A),
            Dictionary.concatenate(X13Dictionaries.FINAL, X13Dictionaries.D11),
            Dictionary.concatenate(X13Dictionaries.FINAL, X13Dictionaries.D11A),
            Dictionary.concatenate(X13Dictionaries.FINAL, X13Dictionaries.D12),
            Dictionary.concatenate(X13Dictionaries.FINAL, X13Dictionaries.D12A),
            Dictionary.concatenate(X13Dictionaries.FINAL, X13Dictionaries.D13),
            Dictionary.concatenate(X13Dictionaries.FINAL, X13Dictionaries.D16),
            Dictionary.concatenate(X13Dictionaries.FINAL, X13Dictionaries.D16A),
            Dictionary.concatenate(X13Dictionaries.FINAL, X13Dictionaries.D18),
            Dictionary.concatenate(X13Dictionaries.FINAL, X13Dictionaries.D18A)};

        public DTablesFactory() {
            super(X13Document.class, D_TABLES, source -> source, new GenericTableUI(false, items));
        }

        @Override
        public int getPosition() {
            return 4040;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 4060)
    public static class ETablesFactory extends ProcDocumentItemFactory<X13Document, TsDocument> {

        public ETablesFactory() {
            super(X13Document.class, E_TABLES, source -> source, new GenericTableUI(false,
                    BasicInformationExtractor.prefix(X13Dictionaries.E_TABLE, X13Dictionaries.FINAL)));
        }

        @Override
        public int getPosition() {
            return 4060;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 4070)
    public static class X11FiltersFactory extends ProcDocumentItemFactory<X13Document, HtmlElement> {

        public X11FiltersFactory() {
            super(X13Document.class, X11_FILTERS, (X13Document doc) -> {
                X13Results rslt = doc.getResult();
                if (rslt == null) {
                    return null;
                }
                return new AbstractHtmlElement() {
                    @Override
                    public void write(HtmlStream stream) throws IOException {
                        stream.write(HtmlTag.HEADER1, "Final filters").newLine();
                        //stream.open(HtmlTag.DIV, "title", "Final seasonal filter");
                        SeasonalFilterOption[] sfilters = rslt.getDecomposition().getFinalSeasonalFilter();
                        if (sfilters != null) {
                            if (sfilters.length == 1) {
                                stream.write("Seasonal filter: ").write(sfilters[0].name()).newLine();
                            } else {
                                stream.write("Composite filter: ").newLine();
                                for (int i = 0; i < sfilters.length; i++) {
                                    stream.write((i + 1) + "&#09;" + sfilters[i].name()).newLine();
                                }

                            }
                        }
                        stream.write("Trend filter: " + rslt.getDecomposition().getFinalHendersonFilterLength()).newLine();
                    }
                };

            }, new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 4060;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 4200)
    public static class MStatisticsSummaryFactory extends ProcDocumentItemFactory<X13Document, HtmlElement> {

        public MStatisticsSummaryFactory() {
            super(X13Document.class, M_STATISTICS_SUMMARY, (X13Document doc) -> {
                X13Results rslt = doc.getResult();
                if (rslt == null) {
                    return null;
                }
                return new demetra.x13.html.HtmlMstatistics(rslt.getDiagnostics().getMstatistics());
            }, new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 4200;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 4300)
    public static class MStatisticsDetailsFactory extends ProcDocumentItemFactory<X13Document, HtmlElement> {

        public MStatisticsDetailsFactory() {
            super(X13Document.class, M_STATISTICS_DETAILS, (X13Document doc) -> {
                X13Results rslt = doc.getResult();
                if (rslt == null) {
                    return null;
                }
                return new demetra.x13.html.HtmlX11Diagnostics(rslt.getDiagnostics().getMstatistics());
            }, new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 4300;
        }
    }
//</editor-fold>
    
//<editor-fold defaultstate="collapsed" desc="DIAGNOSTICS">

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5000)
    public static class DiagnosticsSummaryFactory extends ProcDocumentItemFactory<X13Document, HtmlElement> {

        public DiagnosticsSummaryFactory() {
            super(X13Document.class, SaViews.DIAGNOSTICS_SUMMARY, (X13Document doc) -> {
                jdplus.x13.X13Results rslt = doc.getResult();
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
    public static class OriginalSeasonalityFactory extends ProcDocumentItemFactory<X13Document, HtmlElement> {

        public OriginalSeasonalityFactory() {
            super(X13Document.class, SaViews.DIAGNOSTICS_OSEASONALITY, (X13Document doc) -> {
                X13Results rslt = doc.getResult();
                if (rslt == null) {
                    return null;
                }
                TsData s = rslt.getPreprocessing().transformedSeries();
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
    public static class LinSeasonalityFactory extends ProcDocumentItemFactory<X13Document, HtmlElement> {

        public LinSeasonalityFactory() {
            super(X13Document.class, SaViews.DIAGNOSTICS_LSEASONALITY, (X13Document doc) -> {
                X13Results rslt = doc.getResult();
                if (rslt == null) {
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
    public static class ResSeasonalityFactory extends ProcDocumentItemFactory<X13Document, HtmlElement> {

        public ResSeasonalityFactory() {
            super(X13Document.class, SaViews.DIAGNOSTICS_RSEASONALITY, (X13Document doc) -> {
                X13Results rslt = doc.getResult();
                if (rslt == null) {
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
    public static class SaSeasonalityFactory extends ProcDocumentItemFactory<X13Document, HtmlElement> {

        public SaSeasonalityFactory() {
            super(X13Document.class, SaViews.DIAGNOSTICS_SASEASONALITY, (X13Document doc) -> {
                X13Results rslt = doc.getResult();
                if (rslt == null) {
                    return null;
                }
                TsData s = rslt.getDecomposition().getD11();
                if (s == null) {
                    return null;
                }
                if (rslt.getDecomposition().getMode().isMultiplicative()) {
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
    public static class IrrSeasonalityFactory extends ProcDocumentItemFactory<X13Document, HtmlElement> {

        public IrrSeasonalityFactory() {
            super(X13Document.class, SaViews.DIAGNOSTICS_ISEASONALITY, (X13Document doc) -> {
                X13Results rslt = doc.getResult();
                if (rslt == null) {
                    return null;
                }
                TsData s = rslt.getDecomposition().getD13();
                if (s == null) {
                    return null;
                }
                if (rslt.getDecomposition().getMode().isMultiplicative()) {
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
    public static class LastResSeasonalityFactory extends ProcDocumentItemFactory<X13Document, HtmlElement> {

        public LastResSeasonalityFactory() {
            super(X13Document.class, SaViews.DIAGNOSTICS_LASTRSEASONALITY, (X13Document doc) -> {
                X13Results rslt = doc.getResult();
                if (rslt == null) {
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
    public static class LastSaSeasonalityFactory extends ProcDocumentItemFactory<X13Document, HtmlElement> {

        public LastSaSeasonalityFactory() {
            super(X13Document.class, SaViews.DIAGNOSTICS_LASTSASEASONALITY, (X13Document doc) -> {
                X13Results rslt = doc.getResult();
                if (rslt == null) {
                    return null;
                }
                TsData s = rslt.getDecomposition().getD11();
                if (s == null) {
                    return null;
                }
                if (rslt.getDecomposition().getMode().isMultiplicative()) {
                    s = s.log();
                }
                StringBuilder header = new StringBuilder().append("[Linearized] seasonally adjusted series");
                int ny = DemetraSaUI.get().getSeasonalityLength();
                if (ny > 0) {
                    s = s.drop(Math.max(0, s.length() - s.getAnnualFrequency() * ny - 1), 0);
                    header.append(" (last ").append(ny).append(" years)");
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
    public static class LastIrrSeasonalityFactory extends ProcDocumentItemFactory<X13Document, HtmlElement> {

        public LastIrrSeasonalityFactory() {
            super(X13Document.class, SaViews.DIAGNOSTICS_LASTISEASONALITY, (X13Document doc) -> {
                X13Results rslt = doc.getResult();
                if (rslt == null) {
                    return null;
                }
                TsData s = rslt.getDecomposition().getD13();
                if (s == null) {
                    return null;
                }
                if (rslt.getDecomposition().getMode().isMultiplicative()) {
                    s = s.log();
                }
                StringBuilder header = new StringBuilder().append("[Linearized] irregular component");
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
            return 5080;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 5310)
    public static class ModelResSpectrum extends ProcDocumentItemFactory<X13Document, TsData> {

        public ModelResSpectrum() {
            super(X13Document.class, SaViews.DIAGNOSTICS_SPECTRUM_RES,
                    RESEXTRACTOR,
                    new SpectrumUI(true));

        }

        @Override
        public int getPosition() {
            return 5310;
        }
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="REGISTER REVISION HISTORY VIEW">
    
    private static Function<X13Document, RevisionHistoryUI.Information> revisionExtractor(String info, DiagnosticInfo diag){
        return (X13Document source) -> {
                X13Results result = source.getResult();
                if (result == null) {
                    return null;
                }
                TsData input = source.getInput().getData();
                TsDomain domain = input.getDomain();
                X13Spec pspec = X13Factory.INSTANCE.generateSpec(source.getSpecification(), result);
                X13Spec nspec = X13Factory.INSTANCE.refreshSpec(pspec, source.getSpecification(), DemetraSaUI.get().getEstimationPolicyType(), domain);
                X13Kernel kernel = X13Kernel.of(nspec, source.getContext());
                RevisionHistory<Explorable> rh = new RevisionHistory<>(domain, d -> kernel.process(TsData.fitToDomain(input, d), null));
                return new RevisionHistoryUI.Information(info, diag, rh);
        };
    }
    
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 6410)
    public static class RevisionHistorySaFactory extends ProcDocumentItemFactory<X13Document, RevisionHistoryUI.Information> {

        public RevisionHistorySaFactory() {
            super(X13Document.class, SaViews.DIAGNOSTICS_REVISION_SA, revisionExtractor("sa", DiagnosticInfo.RelativeDifference) , new RevisionHistoryUI());
        }

        @Override
        public int getPosition() {
            return 6410;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 6420)
    public static class RevisionHistoryTrendFactory extends ProcDocumentItemFactory<X13Document, RevisionHistoryUI.Information> {

        public RevisionHistoryTrendFactory() {
            super(X13Document.class, SaViews.DIAGNOSTICS_REVISION_TREND, revisionExtractor("t", DiagnosticInfo.RelativeDifference) , new RevisionHistoryUI());
        }

        @Override
        public int getPosition() {
            return 6420;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 6430)
    public static class RevisionHistorySaChangesFactory extends ProcDocumentItemFactory<X13Document, RevisionHistoryUI.Information> {

        public RevisionHistorySaChangesFactory() {
            super(X13Document.class, SaViews.DIAGNOSTICS_REVISION_SA_CHANGES, revisionExtractor("sa", DiagnosticInfo.PeriodToPeriodGrowthDifference) , new RevisionHistoryUI());
        }

        @Override
        public int getPosition() {
            return 6430;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 6440)
    public static class RevisionHistoryTrendChangesFactory extends ProcDocumentItemFactory<X13Document, RevisionHistoryUI.Information> {

        public RevisionHistoryTrendChangesFactory() {
             super(X13Document.class, SaViews.DIAGNOSTICS_REVISION_TREND_CHANGES, revisionExtractor("t", DiagnosticInfo.PeriodToPeriodGrowthDifference) , new RevisionHistoryUI());
        }

        @Override
        public int getPosition() {
            return 6440;
        }
    }
    //</editor-fold>
    
//<editor-fold defaultstate="collapsed" desc="REGISTER STABILITY VIEWS">
    
    private static Function<X13Document, StabilityUI.Information> stabilityExtractor(EstimationPolicyType policy, String[] items, String msg){
        return (X13Document source) -> {
                X13Results result = source.getResult();
                if (result == null) {
                    return null;
                }
                TsData input = source.getInput().getData();
                TsDomain domain = input.getDomain();
                RegArimaSpec pspec = RegArimaFactory.INSTANCE.generateSpec(source.getSpecification().getRegArima(), result.getPreprocessing().getDescription());
                RegArimaSpec nspec = RegArimaFactory.INSTANCE.refreshSpec(pspec, source.getSpecification().getRegArima(), policy, domain);
                RegArimaKernel kernel = RegArimaKernel.of(nspec, source.getContext());
                MovingProcessing<Explorable> mp=new MovingProcessing<>(domain, (TsDomain d)->kernel.process(TsData.fitToDomain(input, d), null));
                mp.setWindowLength(DemetraSaUI.get().getStabilityLength()*input.getAnnualFrequency());
                return new StabilityUI.Information(mp, items, msg);
        };
    }
    
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 6510)
    public static class StabilityTDFactory extends ProcDocumentItemFactory<X13Document, StabilityUI.Information> {

        public StabilityTDFactory() {
            super(X13Document.class, SaViews.DIAGNOSTICS_STABILITY_TD,
                    stabilityExtractor(DemetraSaUI.get().getEstimationPolicyType(), ITEMS, EXCEPTION), new StabilityUI());
        }

        @Override
        public int getPosition() {
            return 6510;
        }
        
        private static final String EXCEPTION="No information available on trading days !";
        private static final String[] ITEMS = new String[]{
            "regression.td(1)",
            "regression.td(2)",
            "regression.td(3)",
            "regression.td(4)",
            "regression.td(5)",
            "regression.td(6)",
            "regression.td(7)"
        };

    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 6520)
    public static class StabilityEasterFactory extends ProcDocumentItemFactory<X13Document, StabilityUI.Information>  {

        public StabilityEasterFactory() {
           super(X13Document.class, SaViews.DIAGNOSTICS_STABILITY_EASTER,                    
                   stabilityExtractor(DemetraSaUI.get().getEstimationPolicyType(), ITEMS, EXCEPTION), new StabilityUI());
        }

        private static final String EXCEPTION="No information available on Easter effects !";
        private static final String[] ITEMS = new String[]{
            "regression.easter"
        };

        @Override
        public int getPosition() {
            return 6520;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 6530)
    public static class StabilityArimaFactory extends ProcDocumentItemFactory<X13Document, StabilityUI.Information>  {

        public StabilityArimaFactory() {
           super(X13Document.class, SaViews.DIAGNOSTICS_STABILITY_ARIMA,
                   stabilityExtractor(EstimationPolicyType.FreeParameters, ITEMS, EXCEPTION), new StabilityUI());
        }
        
        @Override
        public int getPosition() {
            return 6530;
        }
        
        private static final String EXCEPTION="No information available on the ARIMA model !";
        private static final String[] ITEMS = new String[]{
            "arima.phi(1)", "arima.phi(2)", "arima.phi(3)", "arima.phi(4)", 
            "arima.theta(1)", "arima.theta(2)", "arima.theta(3)", "arima.theta(4)",
            "arima.bphi(1)", "arima.btheta(1)"
        };
        
    }
    //</editor-fold>
}
