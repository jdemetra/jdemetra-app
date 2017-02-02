/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13.ui;

import ec.nbdemetra.sa.DiagnosticsMatrixView;
import ec.satoolkit.DecompositionMode;
import ec.satoolkit.ISeriesDecomposition;
import ec.satoolkit.x11.DefaultSeasonalFilteringStrategy;
import ec.satoolkit.x11.Mstatistics;
import ec.satoolkit.x11.X11Kernel;
import ec.satoolkit.x11.X11Results;
import ec.satoolkit.x13.X13Specification;
import ec.tss.html.AbstractHtmlElement;
import ec.tss.html.HtmlStream;
import ec.tss.html.HtmlTag;
import ec.tss.html.IHtmlElement;
import ec.tss.html.implementation.HtmlMstatistics;
import ec.tss.html.implementation.HtmlX11Diagnostics;
import ec.tss.sa.documents.X13Document;
import ec.tstoolkit.algorithm.CompositeResults;
import ec.tstoolkit.timeseries.analysis.SlidingSpans;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.utilities.DefaultInformationExtractor;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.InformationExtractor;
import ec.tstoolkit.utilities.LinearId;
import ec.ui.view.tsprocessing.ComposedProcDocumentItemFactory;
import ec.ui.view.tsprocessing.DefaultItemUI;
import ec.ui.view.tsprocessing.HtmlItemUI;
import ec.ui.view.tsprocessing.IProcDocumentView;
import ec.ui.view.tsprocessing.IProcDocumentViewFactory;
import ec.ui.view.tsprocessing.ItemUI;
import ec.ui.view.tsprocessing.PooledItemUI;
import ec.ui.view.tsprocessing.ProcDocumentItemFactory;
import ec.ui.view.tsprocessing.SlidingSpansDetailUI;
import ec.ui.view.tsprocessing.SlidingSpansUI;
import ec.ui.view.tsprocessing.TsDocumentInformationExtractor;
import ec.ui.view.tsprocessing.sa.SaDocumentViewFactory;
import static ec.ui.view.tsprocessing.sa.SaDocumentViewFactory.MAIN_SUMMARY;
import static ec.ui.view.tsprocessing.sa.SaDocumentViewFactory.saExtractor;
import static ec.ui.view.tsprocessing.sa.SaDocumentViewFactory.ssExtractor;
import ec.ui.view.tsprocessing.sa.SaTableUI;
import ec.ui.view.tsprocessing.sa.SeasonalityTestUI;
import ec.ui.view.tsprocessing.sa.SiRatioUI;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JComponent;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
public class X13ViewFactory extends SaDocumentViewFactory<X13Specification, X13Document> {

    //RegArima nodes
    public static final String X11 = "Decomposition (X11)";
    // X11 nodes
    public static final String A = "A-Table", B = "B-Table",
            C = "C-Table", D = "D-Table", E = "E-Table", M = "Quality measures", FINALFILTERS = "Final filters";
    public static final Id M_STATISTICS_SUMMARY = new LinearId(X11, M, SUMMARY),
            M_STATISTICS_DETAILS = new LinearId(X11, M, DETAILS),
            X11_FILTERS = new LinearId(X11, FINALFILTERS),
            A_TABLES = new LinearId(X11, A),
            B_TABLES = new LinearId(X11, B),
            C_TABLES = new LinearId(X11, C),
            D_TABLES = new LinearId(X11, D),
            E_TABLES = new LinearId(X11, E);
    private static final AtomicReference<IProcDocumentViewFactory<X13Document>> INSTANCE = new AtomicReference(new X13ViewFactory());

    public static IProcDocumentViewFactory<X13Document> getDefault() {
        return INSTANCE.get();
    }

    public static void setDefault(IProcDocumentViewFactory<X13Document> factory) {
        INSTANCE.set(factory);
    }

    public X13ViewFactory() {
        registerDefault();
        registerFromLookup(X13Document.class);
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
        registerX11Views();
        registerBenchmarkingView();
        registerDiagnosticsViews();
    }

    //<editor-fold defaultstate="collapsed" desc="REGISTER SPEC">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000)
    public static class SpecAllFactory extends SaDocumentViewFactory.SpecAllFactory<X13Document> {

        public SpecAllFactory() {
            super(X13Document.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100010)
    public static class InputFactory extends SaDocumentViewFactory.InputFactory<X13Document> {

        public InputFactory() {
            super(X13Document.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER SUMMARY">
    @Deprecated
    public void registerSummary() {
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 200000)
    public static class MainSummaryFactory extends ItemFactory<X13Document> {

        public MainSummaryFactory() {
            super(MAIN_SUMMARY, new DefaultInformationExtractor<X13Document, X13Document>() {
                @Override
                public X13Document retrieve(X13Document source) {
                    return source;
                }
            }, new PooledItemUI<View, X13Document, X13Summary>(X13Summary.class) {
                @Override
                protected void init(X13Summary c, View host, X13Document information) {
                    c.setTsToolkit(host.getToolkit());
                    c.set(information);
                }
            });
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER MAIN VIEWS">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 201010)
    public static class MainChartsLowFactory extends SaDocumentViewFactory.MainChartsLowFactory<X13Document> {

        public MainChartsLowFactory() {
            super(X13Document.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 201020)
    public static class MainChartsHighFactory extends SaDocumentViewFactory.MainChartsHighFactory<X13Document> {

        public MainChartsHighFactory() {
            super(X13Document.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 202000)
    public static class MainTableFactory extends SaDocumentViewFactory.MainTableFactory<X13Document> {

        public MainTableFactory() {
            super(X13Document.class);
        }
    }

    @Deprecated
    @Override
    public void registerSiView() {
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 203000)
    public static class MainSiFactory extends ItemFactory<TsData[]> {

        public MainSiFactory() {
            super(MAIN_SI, new DefaultInformationExtractor<X13Document, TsData[]>() {
                @Override
                public TsData[] retrieve(X13Document source) {
                    X11Results rslt = source.getDecompositionPart();
                    if (rslt == null) {
                        return null;
                    }
                    TsData si = rslt.getData("d8", TsData.class);
                    TsData seas = rslt.getData("d10", TsData.class);

                    if (rslt.getSeriesDecomposition().getMode() == DecompositionMode.LogAdditive) {
                        si = si.exp();
                    }
                    return new TsData[]{seas, si};
                }
            }, new SiRatioUI());
        }
    }

//    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 204000)
    public static class ProcessingLogFactory extends SaDocumentViewFactory.ProcessingLogFactory<X13Document> {

        public ProcessingLogFactory() {
            super(X13Document.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER PREPROCESSING VIEWS">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000)
    public static class PreprocessingSummaryFactory extends SaDocumentViewFactory.PreprocessingSummaryFactory<X13Document> {

        public PreprocessingSummaryFactory() {
            super(X13Document.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 302000)
    public static class PreprocessingRegsFactory extends SaDocumentViewFactory.PreprocessingRegsFactory<X13Document> {

        public PreprocessingRegsFactory() {
            super(X13Document.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 304000)
    public static class PreprocessingDetFactory extends SaDocumentViewFactory.PreprocessingDetFactory<X13Document> {

        public PreprocessingDetFactory() {
            super(X13Document.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 301005)
    public static class PreprocessingFCastsTableFactory extends SaDocumentViewFactory.PreprocessingFCastsTableFactory<X13Document> {

        public PreprocessingFCastsTableFactory() {
            super(X13Document.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 301000)
    public static class PreprocessingFCastsFactory extends SaDocumentViewFactory.PreprocessingFCastsFactory<X13Document> {

        public PreprocessingFCastsFactory() {
            super(X13Document.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 301010)
    public static class PreprocessingFCastsOutFactory extends SaDocumentViewFactory.PreprocessingFCastsOutFactory<X13Document> {

        public PreprocessingFCastsOutFactory() {
            super(X13Document.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 305000)
    public static class PreprocessingResFactory extends SaDocumentViewFactory.PreprocessingResFactory<X13Document> {

        public PreprocessingResFactory() {
            super(X13Document.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 305010)
    public static class PreprocessingResStatsFactory extends SaDocumentViewFactory.PreprocessingResStatsFactory<X13Document> {

        public PreprocessingResStatsFactory() {
            super(X13Document.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 305020)
    public static class PreprocessingResDistFactory extends SaDocumentViewFactory.PreprocessingResDistFactory<X13Document> {

        public PreprocessingResDistFactory() {
            super(X13Document.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 306000)
    public static class LikelihoodFactory extends SaDocumentViewFactory.LikelihoodFactory<X13Document> {

        public LikelihoodFactory() {
            super(X13Document.class);
            setAsync(true);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER ARIMA VIEW">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 303000)
    public static class PreprocessingArimaFactory extends SaDocumentViewFactory.PreprocessingArimaFactory<X13Document> {

        public PreprocessingArimaFactory() {
            super(X13Document.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER X11">
    @Deprecated
    public void registerX11Views() {
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 401000)
    public static class ATablesFactory extends ItemFactory<CompositeResults> {

        public ATablesFactory() {
            super(A_TABLES, saExtractor(), new SaTableUI(X11Kernel.ALL_A));
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 402000)
    public static class BTablesFactory extends ItemFactory<CompositeResults> {

        public BTablesFactory() {
            super(B_TABLES, saExtractor(), new SaTableUI(X11Kernel.ALL_B));
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 403000)
    public static class CTablesFactory extends ItemFactory<CompositeResults> {

        public CTablesFactory() {
            super(C_TABLES, saExtractor(), new SaTableUI(X11Kernel.ALL_C));
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 404000)
    public static class DTablesFactory extends ItemFactory<CompositeResults> {

        public DTablesFactory() {
            super(D_TABLES, saExtractor(), new SaTableUI(X11Kernel.ALL_D));
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 405000)
    public static class ETablesFactory extends ItemFactory<CompositeResults> {

        public ETablesFactory() {
            super(E_TABLES, saExtractor(), new SaTableUI(X11Kernel.ALL_E));
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 406000)
    public static class X11FiltersFactory extends ItemFactory<X11Results> {

        public X11FiltersFactory() {
            super(X11_FILTERS, new TsDocumentInformationExtractor<X13Document, X11Results>() {
                @Override
                protected X11Results buildInfo(X13Document source) {
                    return source.getDecompositionPart();
                }
            }, new HtmlItemUI<View, X11Results>() {
                @Override
                protected IHtmlElement getHtmlElement(View host, final X11Results information) {
                    return new AbstractHtmlElement() {
                        @Override
                        public void write(HtmlStream stream) throws IOException {
                            stream.write(HtmlTag.HEADER1, h1, "Final filters").newLine();
                            //stream.open(HtmlTag.DIV, "title", "Final seasonal filter");
                            stream.write("Seasonal filter: " + information.getFinalSeasonalFilter()).newLine();
                            if ("Composite filter".equals(information.getFinalSeasonalFilter())) {
                                DefaultSeasonalFilteringStrategy[] strategies = information.getFinalSeasonalFilterComposit();
                                for (int i = 0; i < strategies.length; i++) {
                                    DefaultSeasonalFilteringStrategy strategy = strategies[i];
                                    stream.write((i + 1) + "&#09;" + strategy.getDescription()).newLine();

                                }
                            }

//stream.close(HtmlTag.DIV).newLine();
                            stream.write("Trend filter: " + information.getFinalTrendFilter()).newLine();
                        }
                    };
                }
            });
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 407010)
    public static class MStatisticsSummaryFactory extends ItemFactory<Mstatistics> {

        public MStatisticsSummaryFactory() {
            super(M_STATISTICS_SUMMARY, new TsDocumentInformationExtractor<X13Document, Mstatistics>() {
                @Override
                protected Mstatistics buildInfo(X13Document source) {
                    return source.getMStatistics();
                }
            }, new HtmlItemUI<View, Mstatistics>() {
                @Override
                protected IHtmlElement getHtmlElement(View host, Mstatistics information) {
                    return new HtmlMstatistics(information);
                }
            });
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 407020)
    public static class MStatisticsDetailsFactory extends ItemFactory<Mstatistics> {

        public MStatisticsDetailsFactory() {
            super(M_STATISTICS_DETAILS, new TsDocumentInformationExtractor<X13Document, Mstatistics>() {
                @Override
                protected Mstatistics buildInfo(X13Document source) {
                    return source.getMStatistics();
                }
            }, new HtmlItemUI<View, Mstatistics>() {
                @Override
                protected IHtmlElement getHtmlElement(View host, Mstatistics information) {
                    return new HtmlX11Diagnostics(information);
                }
            });
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER BENCHMARKING VIEW">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 500000)
    public static class BenchmarkingFactory extends SaDocumentViewFactory.BenchmarkingFactory<X13Document> {

        public BenchmarkingFactory() {
            super(X13Document.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER DIAGNOSTICS VIEWS">
    private static class DiagnosticsMatrixExtractor extends DefaultInformationExtractor<X13Document, Map<String, CompositeResults>> {

        public static final DiagnosticsMatrixExtractor INSTANCE = new DiagnosticsMatrixExtractor();

        @Override
        public Map<String, CompositeResults> retrieve(X13Document source) {
            if (source.getInput() == null) {
                return null;
            }

            Map<String, CompositeResults> results = new LinkedHashMap<>();
            X13Specification currentSpec = source.getSpecification();
            results.put("[C] " + currentSpec.toString(), source.getResults());

            for (X13Specification spec : X13Specification.allSpecifications()) {
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
            super(DIAGNOSTICS_MATRIX, DiagnosticsMatrixExtractor.INSTANCE, new DefaultItemUI<IProcDocumentView<X13Document>, Map<String, CompositeResults>>() {
                @Override
                public JComponent getView(IProcDocumentView<X13Document> host, Map<String, CompositeResults> information) {
                    return new DiagnosticsMatrixView(information);
                }

            });
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 600000)
    public static class DiagnosticsSummaryFactory extends SaDocumentViewFactory.DiagnosticsSummaryFactory<X13Document> {

        public DiagnosticsSummaryFactory() {
            super(X13Document.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 602010)
    public static class DiagnosticsSpectrumResFactory extends SaDocumentViewFactory.DiagnosticsSpectrumResFactory<X13Document> {

        public DiagnosticsSpectrumResFactory() {
            super(X13Document.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 602020)
    public static class DiagnosticsSpectrumIFactory extends SaDocumentViewFactory.DiagnosticsSpectrumIFactory<X13Document> {

        public DiagnosticsSpectrumIFactory() {
            super(X13Document.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 602030)
    public static class DiagnosticsSpectrumSaFactory extends SaDocumentViewFactory.DiagnosticsSpectrumSaFactory<X13Document> {

        public DiagnosticsSpectrumSaFactory() {
            super(X13Document.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER SEASONALITY">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 600010)
    public static class OriginalSeasonalityFactory extends SaDocumentViewFactory.SeasonalityTestsFactory<X13Document> {

        public OriginalSeasonalityFactory() {
            super(X13Document.class, DIAGNOSTICS_OSEASONALITY, SaDocumentViewFactory.oseasExtractor(), "Original series", false);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 600020)
    public static class LinearSeasonalityFactory extends SaDocumentViewFactory.SeasonalityTestsFactory<X13Document> {

        public LinearSeasonalityFactory() {
            super(X13Document.class, DIAGNOSTICS_LSEASONALITY, SaDocumentViewFactory.lseasExtractor(), "Linearized series", false);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 600030)
    public static class ResSeasonalityFactory extends SaDocumentViewFactory.SeasonalityTestsFactory<X13Document> {

        public ResSeasonalityFactory() {
            super(X13Document.class, DIAGNOSTICS_RSEASONALITY, SaDocumentViewFactory.rseasExtractor(), "Full residuals", true);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 600040)
    public static class DiagnosticsSeasonalityFactory extends ItemFactory<SeasonalityTestUI.Information> {

        public DiagnosticsSeasonalityFactory() {
            super(DIAGNOSTICS_SEASONALITY, new DefaultInformationExtractor<X13Document, SeasonalityTestUI.Information>() {
                @Override
                public SeasonalityTestUI.Information retrieve(X13Document source) {
                    CompositeResults rslt = source.getResults();
                    ISeriesDecomposition finals = source.getFinalDecomposition();
                    if (finals == null) {
                        return null;
                    }
                    TsData si = rslt.getData("d8", TsData.class);
                    if (si == null) {
                        return null;
                    }
                    DecompositionMode mode = finals.getMode();
                    boolean mul = mode.isMultiplicative();
                    if (mode == DecompositionMode.LogAdditive) {
                        si = si.exp();
                    }
                    SeasonalityTestUI.Information info = new SeasonalityTestUI.Information();
                    info.si = si;
                    info.mul = mul;
                    return info;
                }
            }, new SeasonalityTestUI());
        }

        @ServiceProvider(service = ProcDocumentItemFactory.class, position = 600050)
        public static class SaSeasonalityFactory extends SaDocumentViewFactory.SeasonalityTestsFactory<X13Document> {

            public SaSeasonalityFactory() {
                super(X13Document.class, DIAGNOSTICS_SASEASONALITY, SaDocumentViewFactory.saseasExtractor(), "SA series (d11)", true);
            }
        }

        @ServiceProvider(service = ProcDocumentItemFactory.class, position = 600060)
        public static class IrrSeasonalityFactory extends SaDocumentViewFactory.SeasonalityTestsFactory<X13Document> {

            public IrrSeasonalityFactory() {
                super(X13Document.class, DIAGNOSTICS_ISEASONALITY, SaDocumentViewFactory.iseasExtractor(), "Irregular (d12)", true);
            }
        }

        @ServiceProvider(service = ProcDocumentItemFactory.class, position = 600070)
        public static class LastResSeasonalityFactory extends SaDocumentViewFactory.SeasonalityTestsFactory<X13Document> {

            public LastResSeasonalityFactory() {
                super(X13Document.class, DIAGNOSTICS_LASTRSEASONALITY, SaDocumentViewFactory.lastrseasExtractor(), "Residuals (last periods)", true);
            }
        }

        @ServiceProvider(service = ProcDocumentItemFactory.class, position = 600080)
        public static class LastSaSeasonalityFactory extends SaDocumentViewFactory.SeasonalityTestsFactory<X13Document> {

            public LastSaSeasonalityFactory() {
                super(X13Document.class, DIAGNOSTICS_LASTSASEASONALITY, SaDocumentViewFactory.lastsaseasExtractor(), "Seasonally adjusted series (last periods)", true);
            }
        }

        @ServiceProvider(service = ProcDocumentItemFactory.class, position = 600090)
        public static class LastIrrSeasonalityFactory extends SaDocumentViewFactory.SeasonalityTestsFactory<X13Document> {

            public LastIrrSeasonalityFactory() {
                super(X13Document.class, DIAGNOSTICS_LASTISEASONALITY, SaDocumentViewFactory.lastiseasExtractor(), "Irregular series (last periods)", true);
            }
        }

        @ServiceProvider(service = ProcDocumentItemFactory.class, position = 600100)
        public static class ResidualSeasonalityFactory extends SaDocumentViewFactory.ResidualSeasonalityFactory<X13Document> {

            public ResidualSeasonalityFactory() {
                super(X13Document.class);
            }
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
            super(DIAGNOSTICS_SLIDING_SUMMARY, ssExtractor(), new SlidingSpansUI(X11Kernel.D10, X11Kernel.D8));
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 603010)
    public static class DiagnosticsSlidingSeasFactory extends ItemFactory<SlidingSpans> {

        public DiagnosticsSlidingSeasFactory() {
            super(DIAGNOSTICS_SLIDING_SEAS, ssExtractor(), new SlidingSpansDetailUI(X11Kernel.D10));
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 603020)
    public static class DiagnosticsSlidingTdFactory extends SaDocumentViewFactory.DiagnosticsSlidingTdFactory<X13Document> {

        public DiagnosticsSlidingTdFactory() {
            super(X13Document.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 603030)
    public static class DiagnosticsSlidingSaFactory extends SaDocumentViewFactory.DiagnosticsSlidingSaFactory<X13Document> {

        public DiagnosticsSlidingSaFactory() {
            super(X13Document.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER REVISION HISTORY VIEW">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 604010)
    public static class RevisionHistorySaFactory extends SaDocumentViewFactory.DiagnosticsRevisionSaFactory<X13Document> {

        public RevisionHistorySaFactory() {
            super(X13Document.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 604020)
    public static class RevisionHistoryTrendFactory extends SaDocumentViewFactory.DiagnosticsRevisionTrendFactory<X13Document> {

        public RevisionHistoryTrendFactory() {
            super(X13Document.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 604030)
    public static class RevisionHistorySaChangesFactory extends SaDocumentViewFactory.DiagnosticsRevisionSaChangesFactory<X13Document> {

        public RevisionHistorySaChangesFactory() {
            super(X13Document.class);
            setAsync(true);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 604040)
    public static class RevisionHistoryTrendChangesFactory extends SaDocumentViewFactory.DiagnosticsRevisionTrendChangesFactory<X13Document> {

        public RevisionHistoryTrendChangesFactory() {
            super(X13Document.class);
            setAsync(true);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER STABILITY VIEWS">    
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 604010)
    public static class StabilityTDFactory extends SaDocumentViewFactory.StabilityTDFactory<X13Document> {

        public StabilityTDFactory() {
            super(X13Document.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 605020)
    public static class StabilityEasterFactory extends SaDocumentViewFactory.StabilityEasterFactory<X13Document> {

        public StabilityEasterFactory() {
            super(X13Document.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 605030)
    public static class StabilityArimaFactory extends SaDocumentViewFactory.StabilityArimaFactory<X13Document> {

        public StabilityArimaFactory() {
            super(X13Document.class);
        }
    }
    //</editor-fold>

    private static class ItemFactory<I> extends ComposedProcDocumentItemFactory<X13Document, I> {

        public ItemFactory(Id itemId, InformationExtractor<? super X13Document, I> informationExtractor, ItemUI<? extends IProcDocumentView<X13Document>, I> itemUI) {
            super(X13Document.class, itemId, informationExtractor, itemUI);
        }
    }
}
