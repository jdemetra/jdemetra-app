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
package ec.nbdemetra.disaggregation.ui;

import ec.nbdemetra.ui.chart3d.functions.SurfacePlotterUI;
import ec.nbdemetra.ui.chart3d.functions.SurfacePlotterUI.Functions;
import ec.tss.disaggregation.documents.DisaggregationResults;
import ec.tss.disaggregation.documents.TsDisaggregationModelDocument;
import ec.tss.html.IHtmlElement;
import ec.tstoolkit.maths.realfunctions.IFunction;
import ec.tstoolkit.maths.realfunctions.IFunctionInstance;
import ec.tstoolkit.stats.NiidTests;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.utilities.DefaultInformationExtractor;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.InformationExtractor;
import ec.tstoolkit.utilities.LinearId;
import ec.ui.view.tsprocessing.ComposedProcDocumentItemFactory;
import ec.ui.view.tsprocessing.DocumentInformationExtractor;
import ec.ui.view.tsprocessing.GenericChartUI;
import ec.ui.view.tsprocessing.GenericTableUI;
import ec.ui.view.tsprocessing.HtmlItemUI;
import ec.ui.view.tsprocessing.IProcDocumentView;
import ec.ui.view.tsprocessing.IProcDocumentViewFactory;
import ec.ui.view.tsprocessing.ItemUI;
import ec.ui.view.tsprocessing.ProcDocumentItemFactory;
import ec.ui.view.tsprocessing.ProcDocumentViewFactory;
import ec.ui.view.tsprocessing.ResidualsDistUI;
import ec.ui.view.tsprocessing.ResidualsStatsUI;
import ec.ui.view.tsprocessing.ResidualsUI;
import ec.ui.view.tsprocessing.TsDocumentInformationExtractor;
import java.util.concurrent.atomic.AtomicReference;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean
 */
public class TsDisaggregationViewFactory extends ProcDocumentViewFactory<TsDisaggregationModelDocument> {

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
            LIKELIHOOD="Likelihood";
    public static final Id MODEL_RES = new LinearId(MODEL, RESIDUALS),
            MODEL_RES_STATS = new LinearId(MODEL, RESIDUALS, STATS),
            MODEL_RES_DIST = new LinearId(MODEL, RESIDUALS, DISTRIBUTION),
            PREVIEW_CHART = new LinearId(PREVIEW, CHART),
            PREVIEW_GROWTHCHART = new LinearId(PREVIEW, GROWTHCHART),
            ANALYSIS_OSAMPLE = new LinearId(ANALYSIS, OSAMPLE),
            MAIN_CHART = new LinearId(MAIN, CHART),
            MAIN_DECOMPOSITION = new LinearId(MAIN, DECOMPOSITION),
            MAIN_TABLE = new LinearId(MAIN, TABLE),
            LIKELIHOOD_FN = new LinearId(MODEL, LIKELIHOOD)
;

    public static final Id MODEL_SUMMARY = new LinearId(MODEL, SUMMARY);
    private static final AtomicReference<IProcDocumentViewFactory<TsDisaggregationModelDocument>> INSTANCE = new AtomicReference(new TsDisaggregationViewFactory());

    public static IProcDocumentViewFactory<TsDisaggregationModelDocument> getDefault() {
        return INSTANCE.get();
    }

    public static void setDefault(IProcDocumentViewFactory<TsDisaggregationModelDocument> factory) {
        INSTANCE.set(factory);
    }

    public TsDisaggregationViewFactory() {
        registerDefault();
        registerFromLookup(TsDisaggregationModelDocument.class);
    }

    @Override
    public Id getPreferredView() {
        return MODEL_SUMMARY;
    }

    @Deprecated
    private void registerDefault() {
        registerMainViews();
        registerSummary();
        registerResiduals();
    }

    //<editor-fold defaultstate="collapsed" desc="REGISTER MAIN">
    // provide regitration of main components
    @Deprecated
    protected void registerMainViews() {
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000 + 1000)
    public static class PreviewChartFactory extends ItemFactory<DisaggregationResults> {

        public PreviewChartFactory() {
            super(PREVIEW_CHART, DisaggExtractor.INSTANCE, new IndexChartPreview());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000 + 2000)
    public static class PreviewGrowthChartFactory extends ItemFactory<DisaggregationResults> {

        public PreviewGrowthChartFactory() {
            super(PREVIEW_GROWTHCHART, DisaggExtractor.INSTANCE, new GrowthChartPreview());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000 + 3000)
    public static class MainChartFactory extends ItemFactory<DisaggregationResults> {

        public MainChartFactory() {
            super(MAIN_CHART, DisaggExtractor.INSTANCE, new GenericChartUI(true, DisaggregationResults.DISAGGREGATION, DisaggregationResults.LDISAGGREGATION, DisaggregationResults.UDISAGGREGATION));
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000 + 4000)
    public static class MainTableFactory extends ItemFactory<DisaggregationResults> {

        public MainTableFactory() {
            super(MAIN_TABLE, DisaggExtractor.INSTANCE, new GenericTableUI(true, DisaggregationResults.DISAGGREGATION, DisaggregationResults.EDISAGGREGATION));
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000 + 5000)
    public static class MainDecompositionFactory extends ItemFactory<DisaggregationResults> {

        public MainDecompositionFactory() {
            super(MAIN_DECOMPOSITION, DisaggExtractor.INSTANCE, new GenericChartUI(true, DisaggregationResults.DISAGGREGATION, DisaggregationResults.REGEFFECT, DisaggregationResults.SMOOTHING));
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER SUMMARY">
    @Deprecated
    protected void registerSummary() {
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 200000 + 1000)
    public static class ModelSummaryFactory extends ItemFactory<TsDisaggregationModelDocument> {

        public ModelSummaryFactory() {
            super(MODEL_SUMMARY, new DefaultInformationExtractor<TsDisaggregationModelDocument, TsDisaggregationModelDocument>() {
                @Override
                public TsDisaggregationModelDocument retrieve(TsDisaggregationModelDocument source) {
                    return source;
                }
            }, new HtmlItemUI<View, TsDisaggregationModelDocument>() {
                @Override
                protected IHtmlElement getHtmlElement(View host, TsDisaggregationModelDocument information) {
                    return new ModelSummary(information);
                }
            });
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER RESIDUALS">
    @Deprecated
    protected void registerResiduals() {
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 1000)
    public static class ModelResFactory extends ItemFactory<TsData> {

        public ModelResFactory() {
            super(MODEL_RES, ResExtractor.INSTANCE, new ResidualsUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 2000)
    public static class ModelResStatsFactory extends ItemFactory<NiidTests> {

        public ModelResStatsFactory() {
            super(MODEL_RES_STATS, new DefaultInformationExtractor<TsDisaggregationModelDocument, NiidTests>() {
                @Override
                public NiidTests retrieve(TsDisaggregationModelDocument source) {
                    TsData res = source.getResults().getData(DisaggregationResults.RESIDUALS, TsData.class);
                    return new NiidTests(res.getValues(), res.getFrequency().intValue(),
                            source.getSpecification().getModel().getParametersCount(), res.getFrequency() != TsFrequency.Yearly);
                }
            }, new ResidualsStatsUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 3000)
    public static class ModelResDistFactory extends ItemFactory<TsData> {

        public ModelResDistFactory() {
            super(MODEL_RES_DIST, ResExtractor.INSTANCE, new ResidualsDistUI());
        }
    }
    //</editor-fold>

    private static class ItemFactory<I> extends ComposedProcDocumentItemFactory<TsDisaggregationModelDocument, I> {

        public ItemFactory(Id itemId, InformationExtractor<? super TsDisaggregationModelDocument, I> informationExtractor, ItemUI<? extends IProcDocumentView<TsDisaggregationModelDocument>, I> itemUI) {
            super(TsDisaggregationModelDocument.class, itemId, informationExtractor, itemUI);
        }
    }

    private static class ResExtractor extends DefaultInformationExtractor<TsDisaggregationModelDocument, TsData> {

        static final ResExtractor INSTANCE = new ResExtractor();

        @Override
        public TsData retrieve(TsDisaggregationModelDocument source) {
            return source.getResults().getData(DisaggregationResults.RESIDUALS, TsData.class);
        }
    };

    private static class DisaggExtractor extends DefaultInformationExtractor<TsDisaggregationModelDocument, DisaggregationResults> {

        static final DisaggExtractor INSTANCE = new DisaggExtractor();

        @Override
        public DisaggregationResults retrieve(TsDisaggregationModelDocument source) {
            return source.getResults();
        }
    };

    private static class LikelihoodExtractor extends DocumentInformationExtractor<TsDisaggregationModelDocument, Functions> {

        public static final LikelihoodExtractor INSTANCE = new LikelihoodExtractor();

//        @Override
//        protected Functions buildInfo(SaDocument<? extends ISaSpecification> source) {
//            PreprocessingModel preprocessingPart = source.getPreprocessingPart();
//            if (preprocessingPart == null) {
//                return null;
//            } else {
//                return Functions.create(preprocessingPart.likelihoodFunction(), source.getPreprocessingPart().maxLikelihoodFunction());
//            }
//        }
//
        @Override
        protected Functions buildInfo(TsDisaggregationModelDocument source) {
            DisaggregationResults results = source.getResults();
            if (results == null) {
                return null;
            }
            IFunction fn = results.getEstimationFunction();
            IFunctionInstance min = results.getMin();
            if (min == null)
                return null;
            return Functions.create(fn, min, 2);
        }
    };

        @ServiceProvider(service = ProcDocumentItemFactory.class, position = 306000)
    public static class LikelihoodFactory extends ItemFactory<Functions> {

        public LikelihoodFactory() {
            super(LIKELIHOOD_FN, LikelihoodExtractor.INSTANCE, new SurfacePlotterUI());
            setAsync(true);
        }
    }

}
