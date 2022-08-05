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

import demetra.benchmarking.BenchmarkingDictionaries;
import demetra.desktop.ui.processing.GenericChartUI;
import demetra.desktop.ui.processing.GenericTableUI;
import demetra.desktop.ui.processing.IProcDocumentItemFactory;
import demetra.desktop.ui.processing.IProcDocumentViewFactory;
import demetra.desktop.ui.processing.ProcDocumentItemFactory;
import demetra.desktop.ui.processing.ProcDocumentViewFactory;
import demetra.util.Id;
import java.util.concurrent.atomic.AtomicReference;
import jdplus.benchmarking.univariate.CholetteDocument;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
public class CholetteViewFactory extends ProcDocumentViewFactory<CholetteDocument> {


     private static final AtomicReference<IProcDocumentViewFactory<CholetteDocument>> INSTANCE = new AtomicReference();

    public CholetteViewFactory() {
        registerFromLookup(CholetteDocument.class);
    }

    public static IProcDocumentViewFactory<CholetteDocument> getDefault() {
        IProcDocumentViewFactory<CholetteDocument> fac = INSTANCE.get();
        if (fac == null) {
            fac = new CholetteViewFactory();
            INSTANCE.lazySet(fac);
        }
        return fac;
    }

    public static void setDefault(IProcDocumentViewFactory<CholetteDocument> factory) {
        INSTANCE.set(factory);
    }

    @Override
    public Id getPreferredView() {
        return BenchmarkingViewFactory.RESULTS_MAIN; //To change body of generated methods, choose Tools | Templates.
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 1000)
    public static class InputDataFactory extends ProcDocumentItemFactory<CholetteDocument, CholetteDocument> {

        public InputDataFactory() {
            super(CholetteDocument.class, 
                    BenchmarkingViewFactory.INPUT_DATA, 
                    s-> s, 
                    new GenericTableUI(true, new String[]{ BenchmarkingDictionaries.ORIGINAL, BenchmarkingDictionaries.TARGET}));
        }

        @Override
        public int getPosition() {
            return 1000;
        }
    }
    
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 1010)
    public static class BIRatioFactory extends ProcDocumentItemFactory<CholetteDocument, CholetteDocument> {

        public BIRatioFactory() {
            super(CholetteDocument.class, 
                    BenchmarkingViewFactory.INPUT_BI, 
                    s-> s, 
                    new GenericChartUI(true, new String[]{ BenchmarkingDictionaries.BIRATIO}));
        }

        @Override
        public int getPosition() {
            return 1010;
        }
    }
    
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 2000)
    public static class MainChartFactory extends ProcDocumentItemFactory<CholetteDocument, CholetteDocument> {

        public MainChartFactory() {
            super(CholetteDocument.class, 
                    BenchmarkingViewFactory.RESULTS_MAIN, 
                    s-> s, 
                    new GenericChartUI(true, new String[]{ BenchmarkingDictionaries.ORIGINAL, BenchmarkingDictionaries.BENCHMARKED}));
        }

        @Override
        public int getPosition() {
            return 2000;
        }
    }

}
