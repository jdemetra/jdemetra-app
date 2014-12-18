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
package ec.nbdemetra.benchmarking.ui;

import ec.tss.disaggregation.documents.BenchmarkingResults;
import ec.tss.disaggregation.documents.CholetteDocument;
import ec.tstoolkit.utilities.DefaultInformationExtractor;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.InformationExtractor;
import ec.tstoolkit.utilities.LinearId;
import ec.ui.view.tsprocessing.ComposedProcDocumentItemFactory;
import ec.ui.view.tsprocessing.GenericChartUI;
import ec.ui.view.tsprocessing.IProcDocumentView;
import ec.ui.view.tsprocessing.IProcDocumentViewFactory;
import ec.ui.view.tsprocessing.ItemUI;
import ec.ui.view.tsprocessing.ProcDocumentItemFactory;
import ec.ui.view.tsprocessing.ProcDocumentViewFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
public class CholetteViewFactory extends ProcDocumentViewFactory<CholetteDocument> {

    public static final String INPUT = "Input", RESULTS = "Results";
    public static final Id RESULTS_MAIN = new LinearId(RESULTS);

    private static final IProcDocumentViewFactory<CholetteDocument> INSTANCE = new CholetteViewFactory();

    public static IProcDocumentViewFactory<CholetteDocument> getDefault() {
        return INSTANCE;
    }

    private CholetteViewFactory() {
        registerFromLookup(CholetteDocument.class);
    }

    @Override
    public Id getPreferredView() {
        return RESULTS_MAIN; //To change body of generated methods, choose Tools | Templates.
    }

    private static class CholetteExtractor extends DefaultInformationExtractor<CholetteDocument, BenchmarkingResults> {

        static final CholetteExtractor INSTANCE = new CholetteExtractor();

        @Override
        public BenchmarkingResults retrieve(CholetteDocument source) {
            return source.getResults();
        }
    };

    private static class ItemFactory<I> extends ComposedProcDocumentItemFactory<CholetteDocument, I> {

        public ItemFactory(Id itemId, InformationExtractor<? super CholetteDocument, I> informationExtractor, ItemUI<? extends IProcDocumentView<CholetteDocument>, I> itemUI) {
            super(CholetteDocument.class, itemId, informationExtractor, itemUI);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000 + 1000)
    public static class MainChartFactory extends ItemFactory<BenchmarkingResults> {

        public MainChartFactory() {
            super(RESULTS_MAIN, CholetteExtractor.INSTANCE, new GenericChartUI(true, BenchmarkingResults.ORIGINAL, BenchmarkingResults.BENCHMARKED));
        }
    }

}
