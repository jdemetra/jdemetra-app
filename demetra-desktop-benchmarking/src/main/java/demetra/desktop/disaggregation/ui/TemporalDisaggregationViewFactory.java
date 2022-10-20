/*
 * Copyright 2022 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
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
import demetra.desktop.ui.processing.HtmlItemUI;
import demetra.desktop.ui.processing.IProcDocumentItemFactory;
import demetra.desktop.ui.processing.IProcDocumentViewFactory;
import demetra.desktop.ui.processing.ProcDocumentItemFactory;
import demetra.desktop.ui.processing.ProcDocumentViewFactory;
import demetra.desktop.ui.processing.StaticChartUI;
import demetra.desktop.ui.processing.StaticGrowthChartUI;
import demetra.desktop.ui.processing.stats.ResidualsDistUI;
import demetra.desktop.ui.processing.stats.ResidualsUI;
import demetra.html.HtmlElement;
import demetra.tempdisagg.univariate.TemporalDisaggregationDictionaries;
import demetra.timeseries.Ts;
import demetra.timeseries.TsData;
import demetra.timeseries.TsUnit;
import demetra.util.Id;
import demetra.util.LinearId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import jdplus.stats.tests.NiidTests;
import jdplus.tempdisagg.univariate.TemporalDisaggregationDocument;
import jdplus.tempdisagg.univariate.TemporalDisaggregationResults;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean
 */
public class TemporalDisaggregationViewFactory extends ProcDocumentViewFactory<TemporalDisaggregationDocument> {

    public static final String PREVIEW = "Preview",
            MAIN = "Main results",
            GROWTHCHART = "Growth chart",
            CHART = "Chart",
            TABLE = "Table",
            DECOMPOSITION = "Decomposition",
            DIAGNOSTICS = "Diagnostics",
            MODEL = "Model",
            ANALYSIS = "Analysis",
            OSAMPLE = "Out-of-sample test",
            DETAILS = "Details",
            SUMMARY = "Summary",
            REGRESSORS = "Regressors",
            RESIDUALS = "Residuals",
            STATS = "Statistics",
            DISTRIBUTION = "Distribution",
            LIKELIHOOD = "Likelihood";

    public static final Id MODEL_RES = new LinearId(MODEL, RESIDUALS),
            MODEL_RES_STATS = new LinearId(MODEL, RESIDUALS, STATS),
            MODEL_RES_DIST = new LinearId(MODEL, RESIDUALS, DISTRIBUTION),
            PREVIEW_CHART = new LinearId(PREVIEW, CHART),
            PREVIEW_GROWTHCHART = new LinearId(PREVIEW, GROWTHCHART),
            ANALYSIS_OSAMPLE = new LinearId(ANALYSIS, OSAMPLE),
            MAIN_CHART = new LinearId(MAIN, CHART),
            MAIN_DECOMPOSITION = new LinearId(MAIN, DECOMPOSITION),
            MAIN_TABLE = new LinearId(MAIN, TABLE),
            LIKELIHOOD_FN = new LinearId(MODEL, LIKELIHOOD);

    public static final Id MODEL_SUMMARY = new LinearId(MODEL, SUMMARY);

    private static final AtomicReference<IProcDocumentViewFactory<TemporalDisaggregationDocument>> INSTANCE = new AtomicReference();

    public static void setDefault(IProcDocumentViewFactory<TemporalDisaggregationDocument> factory) {
        INSTANCE.set(factory);
    }

    public static IProcDocumentViewFactory<TemporalDisaggregationDocument> getDefault() {
        IProcDocumentViewFactory<TemporalDisaggregationDocument> fac = INSTANCE.get();
        if (fac == null) {
            fac = new TemporalDisaggregationViewFactory();
            INSTANCE.lazySet(fac);
        }
        return fac;
    }

    public TemporalDisaggregationViewFactory() {
        registerFromLookup(TemporalDisaggregationDocument.class);
    }

    @Override
    public Id getPreferredView() {
        return MODEL_SUMMARY;
    }

    //<editor-fold defaultstate="collapsed" desc="REGISTER MAIN">
    // provide regitration of main components
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 1010)
    public static class PreviewChartFactory extends ProcDocumentItemFactory<TemporalDisaggregationDocument, List<Ts>> {

        public PreviewChartFactory() {
            super(TemporalDisaggregationDocument.class,
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
    public static class PreviewGrowthChartFactory extends ProcDocumentItemFactory<TemporalDisaggregationDocument, List<Ts>> {

        public PreviewGrowthChartFactory() {
            super(TemporalDisaggregationDocument.class,
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
    public static class MainChartFactory extends ProcDocumentItemFactory<TemporalDisaggregationDocument, TemporalDisaggregationDocument> {

        public MainChartFactory() {
            super(TemporalDisaggregationDocument.class, MAIN_CHART, s->s, new GenericChartUI(true, TemporalDisaggregationDictionaries.DISAGG,
                    TemporalDisaggregationDictionaries.LDISAGG, TemporalDisaggregationDictionaries.UDISAGG));
        }

        @Override
        public int getPosition() {
            return 1030;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 1040)
    public static class MainTableFactory extends ProcDocumentItemFactory<TemporalDisaggregationDocument, TemporalDisaggregationDocument> {

        public MainTableFactory() {
            super(TemporalDisaggregationDocument.class, MAIN_TABLE, s->s, new GenericTableUI(true, TemporalDisaggregationDictionaries.DISAGG,
                    TemporalDisaggregationDictionaries.EDISAGG));
        }

        @Override
        public int getPosition() {
            return 1040;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 1050)
    public static class MainDecompositionFactory extends ProcDocumentItemFactory<TemporalDisaggregationDocument, TemporalDisaggregationDocument> {

        public MainDecompositionFactory() {
            super(TemporalDisaggregationDocument.class, MAIN_DECOMPOSITION, s->s, new GenericChartUI(true, TemporalDisaggregationDictionaries.DISAGG,
                    TemporalDisaggregationDictionaries.REGEFFECT, TemporalDisaggregationDictionaries.SMOOTHINGEFFECT));
        }

        @Override
        public int getPosition() {
            return 1050;
        }
    }

//    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000 + 5000)
//    public static class MainDecompositionFactory extends ItemFactory<DisaggregationResults> {
//
//        public MainDecompositionFactory() {
//            super(MAIN_DECOMPOSITION, DisaggExtractor.INSTANCE, new GenericChartUI(true, DisaggregationResults.DISAGGREGATION, DisaggregationResults.REGEFFECT, DisaggregationResults.SMOOTHING));
//        }
//    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="REGISTER SUMMARY">
    @Deprecated
    protected void registerSummary() {
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 200000 + 1000)
    public static class ModelSummaryFactory extends ProcDocumentItemFactory<TemporalDisaggregationDocument, HtmlElement> {

        public ModelSummaryFactory() {
            super(TemporalDisaggregationDocument.class,
                    MODEL_SUMMARY,
                    s -> new ModelSummary(s),
                    new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 2010;
        }

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER RESIDUALS">
    @Deprecated
    protected void registerResiduals() {
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3010)
    public static class ModelResFactory extends ProcDocumentItemFactory<TemporalDisaggregationDocument, TsData> {

        public ModelResFactory() {
            super(TemporalDisaggregationDocument.class, MODEL_RES, RESEXTRACTOR, new ResidualsUI());
        }

        @Override
        public int getPosition() {
            return 3010;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3020)
    public static class ModelResStatsFactory extends ProcDocumentItemFactory<TemporalDisaggregationDocument, HtmlElement> {

        public ModelResStatsFactory() {
            super(TemporalDisaggregationDocument.class,
                    MODEL_RES_STATS,
                    s -> {
                        TemporalDisaggregationResults result = s.getResult();
                        if (result == null) {
                            return null;
                        }
                        return new demetra.html.stats.HtmlNiidTest(result.getResidualsDiagnostics().getNiid());
                    },
                    new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 3020;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 3030)
    public static class ModelResDistFactory extends ProcDocumentItemFactory<TemporalDisaggregationDocument, TsData> {

        public ModelResDistFactory() {
            super(TemporalDisaggregationDocument.class,
                    MODEL_RES_DIST,
                    RESEXTRACTOR,
                    new ResidualsDistUI());
        }

        @Override
        public int getPosition() {
            return 3030;
        }
    }
    //</editor-fold>

    private final static Function<TemporalDisaggregationDocument, TemporalDisaggregationResults> RSLTEXTRACTOR = source -> {
        return source.getResult();
    };

    private final static Function<TemporalDisaggregationDocument, TsData> RESEXTRACTOR = source -> {
        TemporalDisaggregationResults result = source.getResult();
        return result == null ? null : result.getResidualsDiagnostics().getFullResiduals();
    };

//  private final static Function<TemporalDisaggregationDocument, TsData> LLEXTRACTOR = source -> {
//        TemporalDisaggregationResults result = source.getResult();
//        return result == null ? null : result.;
//    };
//        @ServiceProvider(service = IProcDocumentItemFactory.class, position = 306000)
//    public static class LikelihoodFactory extends ItemFactory<Functions> {
//
//        public LikelihoodFactory() {
//            super(LIKELIHOOD_FN, LikelihoodExtractor.INSTANCE, new SurfacePlotterUI());
//            setAsync(true);
//        }
//  }
}
