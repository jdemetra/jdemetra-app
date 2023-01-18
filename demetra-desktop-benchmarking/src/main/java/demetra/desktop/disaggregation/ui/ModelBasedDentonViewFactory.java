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
package demetra.desktop.disaggregation.ui;

import demetra.data.AggregationType;
import demetra.desktop.ui.processing.GenericChartUI;
import demetra.desktop.ui.processing.GenericTableUI;
import demetra.desktop.ui.processing.IProcDocumentItemFactory;
import demetra.desktop.ui.processing.IProcDocumentViewFactory;
import demetra.desktop.ui.processing.ProcDocumentItemFactory;
import demetra.desktop.ui.processing.ProcDocumentViewFactory;
import demetra.desktop.ui.processing.StaticChartUI;
import demetra.desktop.ui.processing.StaticGrowthChartUI;
import demetra.tempdisagg.univariate.TemporalDisaggregationDictionaries;
import demetra.timeseries.Ts;
import demetra.timeseries.TsData;
import demetra.timeseries.TsUnit;
import demetra.util.Id;
import demetra.util.LinearId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import jdplus.tempdisagg.univariate.ModelBasedDentonDocument;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
public class ModelBasedDentonViewFactory extends ProcDocumentViewFactory<ModelBasedDentonDocument> {

    public static final String PREVIEW = "Preview",
            MAIN = "Main results",
            GROWTHCHART = "Growth chart",
            CHART = "Chart",
            TABLE = "Table",
            INPUT = "Input",
            RESULTS = "Results",
            RESIDUALS = "Residuals",
            STATS = "Statistics",
            DISTRIBUTION = "Distribution",
            BIRATIO = "BI-ratio",
            MODEL = "Model",
            ANALYSIS = "Analysis",
            DATA = "Data";

    public static final Id INPUT_DATA = new LinearId(INPUT, DATA),
            INPUT_BI = new LinearId(INPUT, BIRATIO),
            PREVIEW_CHART = new LinearId(PREVIEW, CHART),
            PREVIEW_GROWTHCHART = new LinearId(PREVIEW, GROWTHCHART),
            MAIN_CHART = new LinearId(MAIN, CHART),
            MAIN_TABLE = new LinearId(MAIN, TABLE),
            BI_CHART = new LinearId(BIRATIO, CHART),
            BI_TABLE = new LinearId(BIRATIO, TABLE),
            MODEL_RES = new LinearId(MODEL, RESIDUALS),
            MODEL_RES_STATS = new LinearId(MODEL, RESIDUALS, STATS),
            MODEL_RES_DIST = new LinearId(MODEL, RESIDUALS, DISTRIBUTION);

    private static final AtomicReference<IProcDocumentViewFactory<ModelBasedDentonDocument>> INSTANCE = new AtomicReference();

    public ModelBasedDentonViewFactory() {
        registerFromLookup(ModelBasedDentonDocument.class);
    }

    public static IProcDocumentViewFactory<ModelBasedDentonDocument> getDefault() {
        IProcDocumentViewFactory<ModelBasedDentonDocument> fac = INSTANCE.get();
        if (fac == null) {
            fac = new ModelBasedDentonViewFactory();
            INSTANCE.lazySet(fac);
        }
        return fac;
    }

    public static void setDefault(IProcDocumentViewFactory<ModelBasedDentonDocument> factory) {
        INSTANCE.set(factory);
    }

    @Override
    public Id getPreferredView() {
        return MAIN_CHART; //To change body of generated methods, choose Tools | Templates.
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 1000)
    public static class InputDataFactory extends ProcDocumentItemFactory<ModelBasedDentonDocument, ModelBasedDentonDocument> {

        public InputDataFactory() {
            super(ModelBasedDentonDocument.class,
                    INPUT_DATA,
                    s -> s,
                    new GenericTableUI(true, new String[]{TemporalDisaggregationDictionaries.TARGET, TemporalDisaggregationDictionaries.INDICATOR}));
        }

        @Override
        public int getPosition() {
            return 1000;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 1010)
    public static class PreviewChartFactory extends ProcDocumentItemFactory<ModelBasedDentonDocument, List<Ts>> {

        public PreviewChartFactory() {
            super(ModelBasedDentonDocument.class,
                    PREVIEW_CHART,
                    s -> s.getInput(),
                    new StaticChartUI(StaticChartUI.Transformation.INDEX));
        }

        @Override
        public int getPosition() {
            return 1010;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 1020)
    public static class PreviewGrowthChartFactory extends ProcDocumentItemFactory<ModelBasedDentonDocument, List<Ts>> {

        public PreviewGrowthChartFactory() {
            super(ModelBasedDentonDocument.class,
                    PREVIEW_GROWTHCHART,
                    s -> {
                        List<Ts> ts = s.getInput();
                        AggregationType aggregationType = s.getSpecification().getAggregationType();
                        TsUnit unit = ts.get(0).getData().getDomain().getTsUnit();
                        List<Ts> items = new ArrayList<>();
                        items.add(ts.get(0));
                        for (int i = 1; i < ts.size(); ++i) {
                            Ts cur = ts.get(i);
                            TsData data = cur.getData();
                            data = data.aggregate(unit, aggregationType, true);
                            items.add(Ts.of(cur.getName(), data));
                        }
                        return items;
                    },
                    new StaticGrowthChartUI()
            );
        }

        @Override
        public int getPosition() {
            return 1020;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 1030)
    public static class BIRatioFactory extends ProcDocumentItemFactory<ModelBasedDentonDocument, ModelBasedDentonDocument> {

        public BIRatioFactory() {
            super(ModelBasedDentonDocument.class,
                    INPUT_BI,
                    s -> s,
                    new GenericChartUI(true, new String[]{TemporalDisaggregationDictionaries.LFBIRATIO}));
        }

        @Override
        public int getPosition() {
            return 1030;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 2000)
    public static class MainChartFactory extends ProcDocumentItemFactory<ModelBasedDentonDocument, ModelBasedDentonDocument> {

        public MainChartFactory() {
            super(ModelBasedDentonDocument.class,
                    MAIN_CHART,
                    s -> s,
                    new GenericChartUI(true,
                            TemporalDisaggregationDictionaries.DISAGG,
                            TemporalDisaggregationDictionaries.LDISAGG,
                            TemporalDisaggregationDictionaries.UDISAGG));
        }

        @Override
        public int getPosition() {
            return 2000;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 2010)
    public static class MainTableFactory extends ProcDocumentItemFactory<ModelBasedDentonDocument, ModelBasedDentonDocument> {

        public MainTableFactory() {
            super(ModelBasedDentonDocument.class, MAIN_TABLE, s -> s, new GenericTableUI(true, TemporalDisaggregationDictionaries.DISAGG,
                    TemporalDisaggregationDictionaries.EDISAGG));
        }

        @Override
        public int getPosition() {
            return 2010;
        }
    }

   @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3000)
    public static class BiChartFactory extends ProcDocumentItemFactory<ModelBasedDentonDocument, ModelBasedDentonDocument> {

        public BiChartFactory() {
            super(ModelBasedDentonDocument.class,
                    BI_CHART,
                    s -> s,
                    new GenericChartUI(true,
                            TemporalDisaggregationDictionaries.BIRATIO,
                            TemporalDisaggregationDictionaries.LBIRATIO,
                            TemporalDisaggregationDictionaries.UBIRATIO));
        }

        @Override
        public int getPosition() {
            return 3000;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3010)
    public static class BiTableFactory extends ProcDocumentItemFactory<ModelBasedDentonDocument, ModelBasedDentonDocument> {

        public BiTableFactory() {
            super(ModelBasedDentonDocument.class, BI_TABLE, s -> s, new GenericTableUI(true, TemporalDisaggregationDictionaries.BIRATIO,
                    TemporalDisaggregationDictionaries.EBIRATIO));
        }

        @Override
        public int getPosition() {
            return 3010;
        }
    }
}
