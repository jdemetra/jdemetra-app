/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.composite;

import demetra.bridge.TsConverter;
import demetra.ui.OldTsUtil;
import ec.satoolkit.DecompositionMode;
import ec.satoolkit.diagnostics.IBTest;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.html.IHtmlElement;
import ec.tss.html.implementation.HtmlAnova;
import ec.tss.html.implementation.HtmlDescriptiveStatistics;
import ec.tss.sa.composite.MultiSaDocument;
import ec.tss.sa.composite.MultiSaProcessingFactory;
import ec.tss.sa.composite.MultiSaSpecification;
import ec.tstoolkit.algorithm.IProcResults;
import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.information.InformationSet;
import ec.tstoolkit.modelling.ModellingDictionary;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.utilities.DefaultInformationExtractor;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.InformationExtractor;
import ec.tstoolkit.utilities.LinearId;
import ec.ui.view.tsprocessing.ComposedProcDocumentItemFactory;
import ec.ui.view.tsprocessing.DefaultTableUI;
import ec.ui.view.tsprocessing.GenericChartUI;
import ec.ui.view.tsprocessing.GenericTableUI;
import ec.ui.view.tsprocessing.HtmlItemUI;
import ec.ui.view.tsprocessing.IProcDocumentView;
import ec.ui.view.tsprocessing.IProcDocumentViewFactory;
import ec.ui.view.tsprocessing.ProcDocumentViewFactory;
import ec.ui.view.tsprocessing.SpectrumUI;
import ec.ui.view.tsprocessing.sa.SeasonalityTestUI2;
import ec.ui.view.tsprocessing.DocumentInformationExtractor;
import ec.ui.view.tsprocessing.ItemUI;
import ec.ui.view.tsprocessing.ProcDocumentItemFactory;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
public class DirectIndirectViewFactory extends ProcDocumentViewFactory<MultiSaDocument> {

    //<editor-fold defaultstate="collapsed" desc="CUSTOM EXTRACTORS">
    public static class TsDataExtractor extends DocumentInformationExtractor<MultiSaDocument, TsData> {

        private final String id;

        public TsDataExtractor(String id) {
            this.id = id;
        }

        @Override
        protected TsData buildInfo(MultiSaDocument source) {
            IProcResults rslts = source.getResults();
            return rslts != null ? rslts.getData(id, TsData.class) : null;
        }
    };

    public static class AllSaExtractor extends DocumentInformationExtractor<MultiSaDocument, demetra.timeseries.TsCollection> {

        public static final AllSaExtractor INSTANCE = new AllSaExtractor();

        @Override
        protected demetra.timeseries.TsCollection buildInfo(MultiSaDocument source) {
            List<demetra.timeseries.Ts> result = new ArrayList<>();
            IProcResults results = source.getResults();
            TsCollection input = source.getInput();
            int icmp = 0;
            for (Ts s : input) {
                String name = s.getName() + " (sa)";
                String id = InformationSet.item(MultiSaSpecification.COMPONENT + (icmp++), ModellingDictionary.SA);
                TsData sa = results.getData(id, TsData.class);
                result.add(OldTsUtil.toTs(name, sa));
            }
            return demetra.timeseries.TsCollection.of(result);
        }
    }

    public static class AllBenchSaExtractor extends DocumentInformationExtractor<MultiSaDocument, demetra.timeseries.TsCollection> {

        public static final AllBenchSaExtractor INSTANCE = new AllBenchSaExtractor();

        @Override
        protected demetra.timeseries.TsCollection buildInfo(MultiSaDocument source) {
            List<demetra.timeseries.Ts> result = new ArrayList<>();
            IProcResults results = source.getResults().get(MultiSaProcessingFactory.BENCHMARKING);
            TsCollection input = source.getInput();
            int icmp = 0;
            for (Ts s : input) {
                String name = s.getName() + " (benchmarked sa)";
                String id = MultiSaSpecification.COMPONENT + (icmp++);
                TsData sa = results.getData(id, TsData.class);
                result.add(OldTsUtil.toTs(name, sa));
            }
            return demetra.timeseries.TsCollection.of(result);
        }
    }

    public static class SeasonalityExtractor extends DefaultInformationExtractor<MultiSaDocument, SeasonalityTestUI2.Information> {

        private final String id;

        public SeasonalityExtractor(String id) {
            this.id = id;
        }

        @Override
        public SeasonalityTestUI2.Information retrieve(MultiSaDocument source) {
            SeasonalityTestUI2.Information info = new SeasonalityTestUI2.Information();
            IProcResults rslts = source.getResults();
            //            if (rslts == null) {
            //                return null;
            //            }
            TsData s = rslts.getData(id, TsData.class);
            if (s == null) {
                return null;
            }
            info.s = s;
            info.del = 1;
            info.mul = false;
            DecompositionMode mode = source.getResults().getData(InformationSet.item(MultiSaSpecification.DIRECT, ModellingDictionary.MODE), DecompositionMode.class);
            if (mode != null) {
                info.mul = mode.isMultiplicative();
            }
            return info;
        }
    };

    private static class ResultExtractor extends DefaultInformationExtractor<MultiSaDocument, IProcResults> {

        public static final ResultExtractor INSTANCE = new ResultExtractor();

        @Override
        public IProcResults retrieve(MultiSaDocument source) {
            return source.getResults();
        }
    };
    //</editor-fold>
    // Items 
    public static final String IBTEST = "IB-Test";
    public static final LinearId IB_TEST = new LinearId(IBTEST);
    public static final String DIFF = "Differences", CHART = "Chart", TABLE = "Table", PERIODOGRAM = "Periodogram", STATS = "Statistics";
    public static final String SEAS_TEST = "SeasonalityTests", DIRECT_SA = "Direct sa", INDIRECT_SA = "Indirect sa";
    public static final LinearId DIFF_STATS = new LinearId(DIFF, STATS), DIFF_CHART = new LinearId(DIFF, CHART), DIFF_TABLE = new LinearId(DIFF, TABLE), DIFF_PERIODOGRAM = new LinearId(DIFF, PERIODOGRAM);
    public static final LinearId SEAS_DIRECT = new LinearId(SEAS_TEST, DIRECT_SA), SEAS_INDIRECT = new LinearId(SEAS_TEST, INDIRECT_SA);
    public static final String DETAILS = "Details", SA = "Sa series", BENCH_SA = "Benchmarked Sa series";
    public static final LinearId ALL_SA = new LinearId(DETAILS, SA), ALL_BENCH_SA = new LinearId(DETAILS, BENCH_SA);
    public static final LinearId MAIN_DETAILS = new LinearId(DETAILS);
    public static final LinearId MAIN_CHART = new LinearId(SA);
    private static final String DSA = InformationSet.item(MultiSaSpecification.DIRECT, ModellingDictionary.SA);
    private static final String ISA = InformationSet.item(MultiSaSpecification.INDIRECT, ModellingDictionary.SA);
    private static final String BSA = InformationSet.item(MultiSaProcessingFactory.BENCHMARKING, ModellingDictionary.SA);
    private static final IProcDocumentViewFactory<MultiSaDocument> INSTANCE = new DirectIndirectViewFactory();

    public static IProcDocumentViewFactory<MultiSaDocument> getDefault() {
        return INSTANCE;
    }

    public DirectIndirectViewFactory() {
        registerFromLookup(MultiSaDocument.class);
    }

    @Override
    public Id getPreferredView() {
        return MAIN_CHART;
    }

    //<editor-fold defaultstate="collapsed" desc="REGISTER MAIN">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 200000 + 1000)
    public static class MainChartFactory extends ItemFactory<IProcResults> {

        public MainChartFactory() {
            super(MAIN_CHART, ResultExtractor.INSTANCE, new GenericChartUI(true, DSA, ISA, BSA));
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER IB_TEST">
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 100000 + 1000)
    public static class IBTestFactory extends ItemFactory<IProcResults> {

        public IBTestFactory() {
            super(IB_TEST, ResultExtractor.INSTANCE, new HtmlItemUI<IProcDocumentView<MultiSaDocument>, IProcResults>() {
                @Override
                protected IHtmlElement getHtmlElement(IProcDocumentView<MultiSaDocument> host, IProcResults information) {
                    TsCollection input = host.getDocument().getInput();
                    IBTest ibtest = new IBTest();
                    if (!ibtest.process(input.getAllData())) {
                        return null;
                    }
                    return new HtmlAnova(ibtest.anova(), new String[]{"periods", "years", "series"});
                }
            });
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER DIFFERENCES">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 400000 + 1000)
    public static class DiffStatsFactory extends ItemFactory<TsData> {

        public DiffStatsFactory() {
            super(DIFF_STATS, new TsDataExtractor(MultiSaProcessingFactory.DIFFERENCES), new HtmlItemUI<IProcDocumentView<MultiSaDocument>, TsData>() {
                @Override
                protected IHtmlElement getHtmlElement(IProcDocumentView<MultiSaDocument> host, TsData information) {
                    DecompositionMode mode = host.getDocument().getResults().getData(InformationSet.item(MultiSaSpecification.DIRECT, ModellingDictionary.MODE), DecompositionMode.class);
                    String txt = "Differences between the direct and the indirect SA series";
                    if (mode != null && mode.isMultiplicative()) {
                        txt = "Relative differences(%) between the direct and the indirect SA series";
                    }
                    return new HtmlDescriptiveStatistics(new DescriptiveStatistics(information), txt, true);
                }
            });
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 400000 + 2000)
    public static class DiffChartFactory extends ItemFactory<IProcResults> {

        public DiffChartFactory() {
            super(DIFF_CHART, ResultExtractor.INSTANCE, new GenericChartUI(true, MultiSaProcessingFactory.DIFFERENCES));
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 400000 + 3000)
    public static class DiffTableFactory extends ItemFactory<IProcResults> {

        public DiffTableFactory() {
            super(DIFF_TABLE, ResultExtractor.INSTANCE, new GenericTableUI(true, MultiSaProcessingFactory.DIFFERENCES));
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 400000 + 4000)
    public static class DiffPeriodogramFactory extends ItemFactory<TsData> {

        public DiffPeriodogramFactory() {
            super(DIFF_PERIODOGRAM, new TsDataExtractor(MultiSaProcessingFactory.DIFFERENCES), new SpectrumUI(false));
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER SEASONALITY_TESTS">
    private static final String HEADER_SEAS_DIRECT = "Seasonality tests on the direct sa series";
    private static final String HEADER_SEAS_INDIRECT = "Seasonality tests on the indirect sa series";

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 1000)
    public static class SeasDirectFactory extends ItemFactory<SeasonalityTestUI2.Information> {

        public SeasDirectFactory() {
            super(SEAS_DIRECT, new SeasonalityExtractor(InformationSet.item(MultiSaSpecification.DIRECT, ModellingDictionary.SA)), new SeasonalityTestUI2(HEADER_SEAS_DIRECT, true));
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 2000)
    public static class SeasIndirectFactory extends ItemFactory<SeasonalityTestUI2.Information> {

        public SeasIndirectFactory() {
            super(SEAS_INDIRECT, new SeasonalityExtractor(InformationSet.item(MultiSaSpecification.INDIRECT, ModellingDictionary.SA)), new SeasonalityTestUI2(HEADER_SEAS_INDIRECT, true));
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER DETAILS">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 400000 + 1000)
    public static class MainDetailsFactory extends ItemFactory<IProcResults> {

        public MainDetailsFactory() {
            super(MAIN_DETAILS, ResultExtractor.INSTANCE, new HtmlItemUI<IProcDocumentView<MultiSaDocument>, IProcResults>() {
                @Override
                protected IHtmlElement getHtmlElement(IProcDocumentView<MultiSaDocument> host, IProcResults information) {
                    TsCollection input = host.getDocument().getInput();
                    TsCollection sa = TsConverter.fromTsCollection(AllSaExtractor.INSTANCE.retrieve(host.getDocument()));
                    TsCollection bsa = TsConverter.fromTsCollection(AllBenchSaExtractor.INSTANCE.retrieve(host.getDocument()));
                    return new DirectIndirectSummary(input, sa, bsa, host.getDocument().getSpecification());
                }
            });
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 400000 + 2000)
    public static class AllSaFactory extends ItemFactory<demetra.timeseries.TsCollection> {

        public AllSaFactory() {
            super(ALL_SA, AllSaExtractor.INSTANCE, new DefaultTableUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 400000 + 3000)
    public static class AllBenchSaFactory extends ItemFactory<demetra.timeseries.TsCollection> {

        public AllBenchSaFactory() {
            super(ALL_BENCH_SA, AllBenchSaExtractor.INSTANCE, new DefaultTableUI());
        }
    }
    //</editor-fold>

    private static class ItemFactory<I> extends ComposedProcDocumentItemFactory<MultiSaDocument, I> {

        public ItemFactory(Id itemId, InformationExtractor<? super MultiSaDocument, I> informationExtractor, ItemUI<? extends IProcDocumentView<MultiSaDocument>, I> itemUI) {
            super(MultiSaDocument.class, itemId, informationExtractor, itemUI);
        }
    }
}
