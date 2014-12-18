/*
 * Copyright 2013-2014 National Bank of Belgium
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
package ec.nbdemetra.sa.advanced.ui;

import ec.satoolkit.seats.SeatsResults;
import ec.tss.documents.DocumentManager;
import ec.tss.html.IHtmlElement;
import ec.tss.sa.documents.MixedFrequenciesArimaDocument;
import ec.tss.sa.documents.TramoSeatsDocument;
import ec.tstoolkit.algorithm.CompositeResults;
import ec.tstoolkit.arima.special.mixedfrequencies.MixedFrequenciesModelDecomposition;
import ec.tstoolkit.arima.special.mixedfrequencies.MixedFrequenciesModelEstimation;
import ec.tstoolkit.modelling.ComponentInformation;
import ec.tstoolkit.modelling.ComponentType;
import ec.tstoolkit.modelling.ModellingDictionary;
import ec.tstoolkit.modelling.SeriesInfo;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.ucarima.UcarimaModel;
import ec.tstoolkit.utilities.DefaultInformationExtractor;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.InformationExtractor;
import ec.tstoolkit.utilities.LinearId;
import ec.ui.view.tsprocessing.ChartUI;
import ec.ui.view.tsprocessing.ComposedProcDocumentItemFactory;
import ec.ui.view.tsprocessing.EstimationUI;
import ec.ui.view.tsprocessing.GenericTableUI;
import ec.ui.view.tsprocessing.HtmlItemUI;
import ec.ui.view.tsprocessing.IProcDocumentView;
import ec.ui.view.tsprocessing.IProcDocumentViewFactory;
import ec.ui.view.tsprocessing.ItemUI;
import ec.ui.view.tsprocessing.ProcDocumentItemFactory;
import ec.ui.view.tsprocessing.ProcDocumentViewFactory;
import ec.ui.view.tsprocessing.UcarimaUI;
import ec.ui.view.tsprocessing.sa.SaDocumentViewFactory;
import static ec.ui.view.tsprocessing.sa.SaDocumentViewFactory.DECOMPOSITION;
import static ec.ui.view.tsprocessing.sa.SaDocumentViewFactory.MAIN_CHARTS_HIGH;
import static ec.ui.view.tsprocessing.sa.SaDocumentViewFactory.MAIN_CHARTS_LOW;
import static ec.ui.view.tsprocessing.sa.SaDocumentViewFactory.MAIN_TABLE;
import static ec.ui.view.tsprocessing.sa.SaDocumentViewFactory.saExtractor;
import ec.ui.view.tsprocessing.sa.SaTableUI;
import java.util.Date;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
public class MixedFrequenciesArimaViewFactory extends ProcDocumentViewFactory<MixedFrequenciesArimaDocument> {

    public static final String INTERPOLATION = "Interpolation";
    public static final Id MAIN_INTERPOLATION = new LinearId(SaDocumentViewFactory.MAIN, INTERPOLATION);
    public static final String STOCHASTIC = "Stochastic series",
            STOCHASTIC_TREND = "Trend",
            STOCHASTIC_SA = "SA Series",
            STOCHASTIC_SEAS = "Seasonal",
            STOCHASTIC_IRR = "Irregular",
            MODELBASED = "Model-based tests",
            WKANALYSIS = "WK analysis",
            WK_COMPONENTS = "Components",
            WK_FINALS = "Final estimators",
            WK_PRELIMINARY = "Preliminary estimators",
            WK_ERRORS = "Errors analysis",
            WK_RATES = "Growth rates";
    public static final Id DECOMPOSITION_SUMMARY = new LinearId(DECOMPOSITION);
    public static final Id DECOMPOSITION_STOCH_TREND = new LinearId(DECOMPOSITION, STOCHASTIC, STOCHASTIC_TREND);
    public static final Id DECOMPOSITION_STOCH_SEAS = new LinearId(DECOMPOSITION, STOCHASTIC, STOCHASTIC_SEAS);
    public static final Id DECOMPOSITION_STOCH_SA = new LinearId(DECOMPOSITION, STOCHASTIC, STOCHASTIC_SA);
    public static final Id DECOMPOSITION_STOCH_IRR = new LinearId(DECOMPOSITION, STOCHASTIC, STOCHASTIC_IRR);
    public static final Id DECOMPOSITION_SERIES = new LinearId(DECOMPOSITION, STOCHASTIC);
    public static final Id DECOMPOSITION_WK_COMPONENTS = new LinearId(DECOMPOSITION, WKANALYSIS, WK_COMPONENTS);
    public static final Id DECOMPOSITION_WK_FINALS = new LinearId(DECOMPOSITION, WKANALYSIS, WK_FINALS);
    public static final Id DECOMPOSITION_ERRORS = new LinearId(DECOMPOSITION, WK_ERRORS);
    public static final Id DECOMPOSITION_RATES = new LinearId(DECOMPOSITION, WK_RATES);
    public static final Id DECOMPOSITION_TESTS = new LinearId(DECOMPOSITION, MODELBASED);

    private static final IProcDocumentViewFactory<MixedFrequenciesArimaDocument> INSTANCE = new MixedFrequenciesArimaViewFactory();

    public static IProcDocumentViewFactory<MixedFrequenciesArimaDocument> getDefault() {
        return INSTANCE;
    }

    private MixedFrequenciesArimaViewFactory() {
        registerFromLookup(MixedFrequenciesArimaDocument.class);
    }

    @Override
    public Id getPreferredView() {
        return SaDocumentViewFactory.MODEL_SUMMARY; //To change body of generated methods, choose Tools | Templates.
    }

    private static class ModelExtractor extends DefaultInformationExtractor<MixedFrequenciesArimaDocument, MixedFrequenciesModelEstimation> {

        private static final ModelExtractor INSTANCE = new ModelExtractor();

        @Override
        public MixedFrequenciesModelEstimation retrieve(MixedFrequenciesArimaDocument source) {
            return source.getPreprocessingPart();
        }
    };

    static class SaExtractor extends DefaultInformationExtractor<MixedFrequenciesArimaDocument, CompositeResults> {

        static final SaExtractor INSTANCE = new SaExtractor();

        @Override
        public CompositeResults retrieve(MixedFrequenciesArimaDocument source) {
            return source.getResults();
        }
    };

    static class decompExtractor extends DefaultInformationExtractor<MixedFrequenciesArimaDocument, MixedFrequenciesModelDecomposition> {

        static final SaExtractor INSTANCE = new SaExtractor();

        @Override
        public MixedFrequenciesModelDecomposition retrieve(MixedFrequenciesArimaDocument source) {
            return source.getDecompositionPart();
        }
    };

    private static class ItemFactory<I> extends ComposedProcDocumentItemFactory<MixedFrequenciesArimaDocument, I> {

        public ItemFactory(Id itemId, InformationExtractor<? super MixedFrequenciesArimaDocument, I> informationExtractor, ItemUI<? extends IProcDocumentView<MixedFrequenciesArimaDocument>, I> itemUI) {
            super(MixedFrequenciesArimaDocument.class, itemId, informationExtractor, itemUI);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 200000 + 1000)
    public static class ModelSummaryFactory extends ItemFactory<MixedFrequenciesArimaDocument> {

        public ModelSummaryFactory() {
            super(SaDocumentViewFactory.MODEL_SUMMARY, new DefaultInformationExtractor<MixedFrequenciesArimaDocument, MixedFrequenciesArimaDocument>() {
                @Override
                public MixedFrequenciesArimaDocument retrieve(MixedFrequenciesArimaDocument source) {
                    return source;
                }
            }, new HtmlItemUI<View, MixedFrequenciesArimaDocument>() {
                @Override
                protected IHtmlElement getHtmlElement(View host, MixedFrequenciesArimaDocument information) {
                    return new MixedFrequenciesArimaModelSummary(information);
                }
            });
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 200000 + 2000)
    public static class ModelRegsFactory extends ItemFactory<MixedFrequenciesModelEstimation> {

        public ModelRegsFactory() {
            super(SaDocumentViewFactory.MODEL_REGS, new DefaultInformationExtractor<MixedFrequenciesArimaDocument, MixedFrequenciesModelEstimation>() {
                @Override
                public MixedFrequenciesModelEstimation retrieve(MixedFrequenciesArimaDocument source) {
                    return source.getPreprocessingPart();
                }
            }, new SaTableUI(ModellingDictionary.getDeterministicSeries(), null));
        }
    }

    public static InformationExtractor<MixedFrequenciesArimaDocument, MixedFrequenciesModelEstimation> modelExtractor() {
        return ModelExtractor.INSTANCE;
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000 + 1000)
    public static class MainChartsLowFactory
            extends ItemFactory<CompositeResults> {

        private static String[] generateItems() {
            StringBuilder y = new StringBuilder();
            y.append(DocumentManager.COMPOSITE).append("Series=,").append(ModellingDictionary.Y)
                    .append(',').append(ModellingDictionary.Y).append(SeriesInfo.F_SUFFIX);
            StringBuilder t = new StringBuilder();
            t.append(DocumentManager.COMPOSITE).append("Trend=,").append(ModellingDictionary.T)
                    .append(',').append(ModellingDictionary.T).append(SeriesInfo.F_SUFFIX);
            StringBuilder sa = new StringBuilder();
            sa.append(DocumentManager.COMPOSITE).append("Seasonally adjusted=,").append(ModellingDictionary.SA)
                    .append(',').append(ModellingDictionary.SA).append(SeriesInfo.F_SUFFIX);
            return new String[]{y.toString(), t.toString(), sa.toString()};
        }

        public MainChartsLowFactory() {
            super(MAIN_CHARTS_LOW, SaExtractor.INSTANCE, new ChartUI(generateItems()));
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000 + 2000)
    public static class MainChartsHighFactory extends ItemFactory<CompositeResults> {

        private static String[] generateItems() {
            StringBuilder cal = new StringBuilder();
            cal.append(DocumentManager.COMPOSITE).append("Calendar effects=,").append(ModellingDictionary.CAL)
                    .append(',').append(ModellingDictionary.CAL).append(SeriesInfo.F_SUFFIX);
            StringBuilder s = new StringBuilder();
            s.append(DocumentManager.COMPOSITE).append("Seas (linearized)=,").append(ModellingDictionary.S_CMP)
                    .append(',').append(ModellingDictionary.S_CMP).append(SeriesInfo.F_SUFFIX);
            StringBuilder i = new StringBuilder();
            i.append(DocumentManager.COMPOSITE).append("Irregular=,").append(ModellingDictionary.I)
                    .append(',').append(ModellingDictionary.I).append(SeriesInfo.F_SUFFIX);
            return new String[]{cal.toString(), s.toString(), i.toString()};
        }

        public MainChartsHighFactory() {
            super(MAIN_CHARTS_HIGH, SaExtractor.INSTANCE, new ChartUI(generateItems()));
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000 + 3000)
    public static class MainTableFactory extends ItemFactory<CompositeResults> {

        private static String[] generateItems() {
            StringBuilder y = new StringBuilder();
            y.append(DocumentManager.COMPOSITE).append("Series=,").append(ModellingDictionary.Y)
                    .append(',').append(ModellingDictionary.Y).append(SeriesInfo.F_SUFFIX);
            StringBuilder t = new StringBuilder();
            t.append(DocumentManager.COMPOSITE).append("Trend=,").append(ModellingDictionary.T)
                    .append(',').append(ModellingDictionary.T).append(SeriesInfo.F_SUFFIX);
            StringBuilder sa = new StringBuilder();
            sa.append(DocumentManager.COMPOSITE).append("Seasonally adjusted=,").append(ModellingDictionary.SA)
                    .append(',').append(ModellingDictionary.SA).append(SeriesInfo.F_SUFFIX);
            StringBuilder s = new StringBuilder();
            s.append(DocumentManager.COMPOSITE).append("Seasonal=,").append(ModellingDictionary.S)
                    .append(',').append(ModellingDictionary.S).append(SeriesInfo.F_SUFFIX);
            StringBuilder i = new StringBuilder();
            i.append(DocumentManager.COMPOSITE).append("Irregular=,").append(ModellingDictionary.I)
                    .append(',').append(ModellingDictionary.I).append(SeriesInfo.F_SUFFIX);
            return new String[]{y.toString(), sa.toString(), t.toString(), s.toString(), i.toString()};
        }

        public MainTableFactory() {
            super(MAIN_TABLE, SaExtractor.INSTANCE, new GenericTableUI(false, generateItems()));
//            super(documentType, MAIN_TABLE, saExtractor(), new SaTableUI(ModellingDictionary.getFinalSeries(), null));
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 10000 + 1000)
    public static class InterpolationTableFactory extends ItemFactory<MixedFrequenciesModelEstimation> {

        private static String[] generateItems() {
            StringBuilder y = new StringBuilder();
            y.append(DocumentManager.COMPOSITE).append("Interpolated series=,").append(MixedFrequenciesModelEstimation.YC).append(',');
            StringBuilder ye = new StringBuilder();
            ye.append(DocumentManager.COMPOSITE).append("Interpolation errors=,").append(MixedFrequenciesModelEstimation.YC_E).append(',');
            StringBuilder yl = new StringBuilder();
            yl.append(DocumentManager.COMPOSITE).append("Interpolated series (log)=,").append(MixedFrequenciesModelEstimation.YC_L).append(',');
            StringBuilder yle = new StringBuilder();
            yle.append(DocumentManager.COMPOSITE).append("Interpolation errors (log)=,").append(MixedFrequenciesModelEstimation.YC_L_E).append(',');
            return new String[]{y.toString(), ye.toString(), yl.toString(), yle.toString()};
        }

        public InterpolationTableFactory() {
            super(MAIN_INTERPOLATION, modelExtractor(), new GenericTableUI(false, generateItems()));
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 400000)
    public static class DecompositionSummaryFactory extends ItemFactory<UcarimaUI.Information> {

        public DecompositionSummaryFactory() {
            super(DECOMPOSITION_SUMMARY, new DefaultInformationExtractor<MixedFrequenciesArimaDocument, UcarimaUI.Information>() {
                @Override
                public UcarimaUI.Information retrieve(MixedFrequenciesArimaDocument source) {
                    UcarimaModel ucm = source.getDecompositionPart().getUcarimaModel();
                    UcarimaUI.Information info = new UcarimaUI.Information();
                    info.model = ucm.getModel();
                    info.names = SeatsResults.getComponentsName(ucm);
                    info.cmps = SeatsResults.getComponents(ucm);
                    return info;
                }
            }, new UcarimaUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 401000)
    public static class DecompositionSeriesFactory extends ItemFactory<CompositeResults> {

//        public DecompositionSeriesFactory() {
//            super(DECOMPOSITION_SERIES, new DefaultInformationExtractor<TramoSeatsDocument, List<NamedObject<TsData>>>() {
//                @Override
//                public List<NamedObject<TsData>> retrieve(TramoSeatsDocument source) {
//                    ArrayList<NamedObject<TsData>> items = new ArrayList<>();
//                    ISeriesDecomposition cmps = source.getDecompositionPart().getComponents();
//                    TsData t = cmps.getSeries(ComponentType.Trend, ComponentInformation.Value);
//                    if (t != null) {
//                        items.add(new NamedObject<>("trend", t));
//                    }
//                    TsData sa = cmps.getSeries(ComponentType.SeasonallyAdjusted, ComponentInformation.Value);
//                    if (sa != null) {
//                        items.add(new NamedObject<>("sa", sa));
//                    }
//                    TsData s = cmps.getSeries(ComponentType.Seasonal, ComponentInformation.Value);
//                    if (s != null) {
//                        items.add(new NamedObject<>("seas", s));
//                    }
//                    TsData i = cmps.getSeries(ComponentType.Irregular, ComponentInformation.Value);
//                    if (i != null) {
//                        items.add(new NamedObject<>("iregular", i));
//                    }
//                    return items;
//                }
//            }, new TableUI());
//        }
        private static String[] generateItems() {
            StringBuilder y = new StringBuilder();
            y.append(DocumentManager.COMPOSITE).append("Series (lin)=,").append(ModellingDictionary.Y_CMP)
                    .append(',').append(ModellingDictionary.Y_CMP).append(SeriesInfo.F_SUFFIX);
            StringBuilder t = new StringBuilder();
            t.append(DocumentManager.COMPOSITE).append("Trend (lin)=,").append(ModellingDictionary.T_CMP)
                    .append(',').append(ModellingDictionary.T_CMP).append(SeriesInfo.F_SUFFIX);
            StringBuilder sa = new StringBuilder();
            sa.append(DocumentManager.COMPOSITE).append("Seasonally adjusted (lin)=,").append(ModellingDictionary.SA_CMP)
                    .append(',').append(ModellingDictionary.SA_CMP).append(SeriesInfo.F_SUFFIX);
            StringBuilder s = new StringBuilder();
            s.append(DocumentManager.COMPOSITE).append("Seasonal (lin)=,").append(ModellingDictionary.S_CMP)
                    .append(',').append(ModellingDictionary.S_CMP).append(SeriesInfo.F_SUFFIX);
            StringBuilder i = new StringBuilder();
            i.append(DocumentManager.COMPOSITE).append("Irregular (lin)=,").append(ModellingDictionary.I_CMP)
                    .append(',').append(ModellingDictionary.I_CMP).append(SeriesInfo.F_SUFFIX);
//            StringBuilder ye = new StringBuilder();
//            ye.append(DocumentManager.COMPOSITE).append("Series (stde lin)=,")
//                    .append(ModellingDictionary.Y_CMP).append(SeriesInfo.E_SUFFIX)
//                    .append(',').append(ModellingDictionary.Y_CMP).append(SeriesInfo.EF_SUFFIX);
            StringBuilder te = new StringBuilder();
            te.append(DocumentManager.COMPOSITE).append("Trend (stde lin)=,")
                    .append(ModellingDictionary.T_CMP).append(SeriesInfo.E_SUFFIX)
                    .append(',').append(ModellingDictionary.T_CMP).append(SeriesInfo.EF_SUFFIX);
            StringBuilder sae = new StringBuilder();
            sae.append(DocumentManager.COMPOSITE).append("Seasonally adjusted (stde lin)=,")
                    .append(ModellingDictionary.SA_CMP).append(SeriesInfo.E_SUFFIX)
                    .append(',').append(ModellingDictionary.SA_CMP).append(SeriesInfo.EF_SUFFIX);
            StringBuilder se = new StringBuilder();
            se.append(DocumentManager.COMPOSITE).append("Seasonal (stde lin)=,")
                    .append(ModellingDictionary.S_CMP).append(SeriesInfo.E_SUFFIX)
                    .append(',').append(ModellingDictionary.S_CMP).append(SeriesInfo.EF_SUFFIX);
            StringBuilder ie = new StringBuilder();
            ie.append(DocumentManager.COMPOSITE).append("Irregular (stde lin)=,")
                    .append(ModellingDictionary.I_CMP).append(SeriesInfo.E_SUFFIX)
                    .append(',').append(ModellingDictionary.I_CMP).append(SeriesInfo.EF_SUFFIX);
            return new String[]{y.toString(), sa.toString(), t.toString(), s.toString(), i.toString()/*, ye.toString()*/, sae.toString(), te.toString(), se.toString(), ie.toString()};
        }

        public DecompositionSeriesFactory() {
            super(DECOMPOSITION_SERIES, SaExtractor.INSTANCE, new GenericTableUI(false, generateItems()));
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 401010)
    public static class DecompositionStochTrendFactory extends ItemFactory<EstimationUI.Information> {

        public DecompositionStochTrendFactory() {
            super(DECOMPOSITION_STOCH_TREND, cmpExtractor(ComponentType.Trend), new EstimationUI());
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 401020)
    public static class DecompositionStochSeasFactory extends ItemFactory<EstimationUI.Information> {

        public DecompositionStochSeasFactory() {
            super(DECOMPOSITION_STOCH_SEAS, cmpExtractor(ComponentType.Seasonal), new EstimationUI());
        }
    }

    public static InformationExtractor<MixedFrequenciesArimaDocument, EstimationUI.Information> cmpExtractor(ComponentType type) {
        return new CmpExtractor(type);
    }

    private static class CmpExtractor implements InformationExtractor<MixedFrequenciesArimaDocument, EstimationUI.Information> {

        final ComponentType type_;

        private CmpExtractor(ComponentType type) {
            type_ = type;
        }

        @Override
        public EstimationUI.Information retrieve(MixedFrequenciesArimaDocument source) {
            MixedFrequenciesModelDecomposition decomp = source.getDecompositionPart();
            TsData s = decomp.getDecomposition().getSeries(type_, ComponentInformation.Value);
            if (s == null) {
                return new EstimationUI.Information(null, null, null, null);
            }
            TsData es = decomp.getDecomposition().getSeries(type_, ComponentInformation.Stdev);
            TsData fs = decomp.getDecomposition().getSeries(type_, ComponentInformation.Forecast);
            TsData efs = decomp.getDecomposition().getSeries(type_, ComponentInformation.StdevForecast);
            Date x = s.getLastPeriod().lastday().getTime();
            s = s.update(fs);
            es = es.update(efs);
            EstimationUI.Information rslt
                    = new EstimationUI.Information(s, null, s.minus(es.times(1.96)), s.plus(es.times(1.96)));
            rslt.markers = new Date[]{x};
            return rslt;
        }

        @Override
        public void flush(MixedFrequenciesArimaDocument source) {
        }
    }

}
