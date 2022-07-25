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
package demetra.desktop.benchmarking.ui;

import java.util.Collections;
import java.util.List;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
public class MultiCholetteViewFactory extends ProcDocumentViewFactory<MultiCholetteDocument> {

    // Items 
    public static final String DIFF = "Differences", CHART = "Chart", TABLE = "Table", PERIODOGRAM = "Periodogram", STATS = "Statistics";
    public static final LinearId DIFF_STATS = new LinearId(DIFF, STATS), DIFF_CHART = new LinearId(DIFF, CHART), DIFF_TABLE = new LinearId(DIFF, TABLE), DIFF_PERIODOGRAM = new LinearId(DIFF, PERIODOGRAM);
    public static final String DETAILS = "Details", INPUT="Input", BENCH = "Benchmarked series";
    public static final LinearId ALL_INPUT = new LinearId(DETAILS, INPUT), ALL_BENCH = new LinearId(DETAILS, BENCH);
    public static final LinearId MAIN_DETAILS = new LinearId(DETAILS);
    private static final IProcDocumentViewFactory<MultiCholetteDocument> INSTANCE = new MultiCholetteViewFactory();

    public static IProcDocumentViewFactory<MultiCholetteDocument> getDefault() {
        return INSTANCE;
    }

    private MultiCholetteViewFactory() {
        registerFromLookup(MultiCholetteDocument.class);
    }

    @Override
    public Id getPreferredView() {
        return ALL_BENCH;
    }

    public static class AllBenchExtractor extends DocumentInformationExtractor<MultiCholetteDocument, TsCollection> {

        public static final AllBenchExtractor INSTANCE = new AllBenchExtractor();

        @Override
        protected TsCollection buildInfo(MultiCholetteDocument source) {
            MultiBenchmarkingResults results = source.getResults();
            List<String> items = results.getBenchmarkedItems();
            Collections.sort(items);
            TsCollection bench = TsFactory.instance.createTsCollection();
            for (String s : items) {
                Ts ts = DocumentManager.instance.getTs(source, s);
                bench.quietAdd(ts);
            }
            return bench;
        }
    }

    public static class AllInputExtractor extends DocumentInformationExtractor<MultiCholetteDocument, TsCollection> {

        public static final AllInputExtractor INSTANCE = new AllInputExtractor();

        @Override
        protected TsCollection buildInfo(MultiCholetteDocument source) {
            MultiBenchmarkingResults results = source.getResults();
            List<String> items = results.getInputItems();
            Collections.sort(items);
            TsCollection bench = TsFactory.instance.createTsCollection();
            for (String s : items) {
                Ts ts = DocumentManager.instance.getTs(source, s);
                bench.quietAdd(ts);
            }
            return bench;
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000 + 1000)
    public static final class AllInputFactory extends ItemFactory<TsCollection> {

        public AllInputFactory() {
            super(ALL_INPUT, AllInputExtractor.INSTANCE, new DefaultTableUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000 + 2000)
    public static final class AllBenchFactory extends ItemFactory<TsCollection> {

        public AllBenchFactory() {
            super(ALL_BENCH, AllBenchExtractor.INSTANCE, new DefaultTableUI());
        }
    }

    private static class ItemFactory<I> extends ComposedProcDocumentItemFactory<MultiCholetteDocument, I> {

        protected ItemFactory(Id itemId, InformationExtractor<? super MultiCholetteDocument, I> informationExtractor, ItemUI<? extends IProcDocumentView<MultiCholetteDocument>, I> itemUI) {
            super(MultiCholetteDocument.class, itemId, informationExtractor, itemUI);
        }
    }
}
