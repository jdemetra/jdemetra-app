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

import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
public class DentonViewFactory extends ProcDocumentViewFactory<DentonDocument> {

    public static final String INPUT = "Input", RESULTS = "Results";
    public static final Id RESULTS_MAIN = new LinearId(RESULTS);

    private static final IProcDocumentViewFactory<DentonDocument> INSTANCE = new DentonViewFactory();

    public static IProcDocumentViewFactory<DentonDocument> getDefault() {
        return INSTANCE;
    }

    private DentonViewFactory() {
        registerFromLookup(DentonDocument.class);
    }

    @Override
    public Id getPreferredView() {
        return RESULTS_MAIN; //To change body of generated methods, choose Tools | Templates.
    }

    private static class DentonExtractor extends DefaultInformationExtractor<DentonDocument, BenchmarkingResults> {

        static final DentonExtractor INSTANCE = new DentonExtractor();

        @Override
        public BenchmarkingResults retrieve(DentonDocument source) {
            return source.getResults();
        }
    };

    private static class ItemFactory<I> extends ComposedProcDocumentItemFactory<DentonDocument, I> {

        public ItemFactory(Id itemId, InformationExtractor<? super DentonDocument, I> informationExtractor, ItemUI<? extends IProcDocumentView<DentonDocument>, I> itemUI) {
            super(DentonDocument.class, itemId, informationExtractor, itemUI);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000 + 1000)
    public static class MainChartFactory extends ItemFactory<BenchmarkingResults> {

        public MainChartFactory() {
            super(RESULTS_MAIN, DentonExtractor.INSTANCE, new GenericChartUI(true, BenchmarkingResults.ORIGINAL, BenchmarkingResults.BENCHMARKED));
        }
    }

}
