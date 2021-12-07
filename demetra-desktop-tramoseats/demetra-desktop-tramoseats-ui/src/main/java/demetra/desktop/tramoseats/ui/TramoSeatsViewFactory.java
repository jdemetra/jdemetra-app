/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramoseats.ui;

import demetra.desktop.modelling.ForecastsFactory;
import demetra.desktop.modelling.ForecastsTableFactory;
import demetra.desktop.modelling.InputFactory;
import demetra.desktop.modelling.NiidTestsFactory;
import demetra.desktop.modelling.PreprocessingViews;
import demetra.desktop.ui.processing.GenericTableUI;
import demetra.desktop.ui.processing.HtmlItemUI;
import demetra.desktop.ui.processing.IProcDocumentItemFactory;
import demetra.desktop.ui.processing.IProcDocumentViewFactory;
import demetra.desktop.ui.processing.ProcDocumentItemFactory;
import demetra.desktop.ui.processing.ProcDocumentViewFactory;
import demetra.desktop.ui.processing.stats.ResidualsDistUI;
import demetra.desktop.ui.processing.stats.ResidualsUI;
import demetra.desktop.ui.processing.stats.SpectrumUI;
import demetra.html.HtmlElement;
import demetra.information.InformationSet;
import demetra.modelling.ModellingDictionary;
import demetra.timeseries.TsData;
import demetra.tramoseats.io.information.TramoSeatsSpecMapping;
import demetra.util.Id;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import jdplus.regsarima.regular.RegSarimaModel;
import jdplus.tramoseats.TramoSeatsResults;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
public class TramoSeatsViewFactory extends ProcDocumentViewFactory<TramoSeatsDocument> {

    private final static Function<TramoSeatsDocument, RegSarimaModel> MODELEXTRACTOR =  source ->{
                TramoSeatsResults tr = source.getResult();
                return tr == null ? null : tr.getPreprocessing();
                    };

    private final static Function<TramoSeatsDocument, TsData> RESEXTRACTOR = MODELEXTRACTOR
            .andThen(regarima ->regarima.fullResiduals());

    public static IProcDocumentViewFactory<TramoSeatsDocument> getDefault() {
        return INSTANCE.get();
    }

    public static void setDefault(IProcDocumentViewFactory<TramoSeatsDocument> factory) {
        INSTANCE.set(factory);
    }

    public TramoSeatsViewFactory() {
        registerFromLookup(TramoSeatsDocument.class);
    }

    @Override
    public Id getPreferredView() {
        return PreprocessingViews.MODEL_SUMMARY;
    }

//<editor-fold defaultstate="collapsed" desc="REGISTER SPEC">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 100010)
    public static class SpecFactory extends ProcDocumentItemFactory<TramoSeatsDocument, HtmlElement> {

        public SpecFactory() {
            super(TramoSeatsDocument.class, PreprocessingViews.INPUT_SPEC,
                    (TramoSeatsDocument doc) -> {
                        InformationSet info = TramoSeatsSpecMapping.write(doc.getSpecification(), true);
                        return new demetra.html.core.HtmlInformationSet(info);
                    },
                    new HtmlItemUI()
            );
        }

        @Override
        public int getPosition() {
            return 100010;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 100000)
    public static class Input extends InputFactory<TramoSeatsDocument> {

        public Input() {
            super(TramoSeatsDocument.class, PreprocessingViews.INPUT_SERIES);
        }

        @Override
        public int getPosition() {
            return 100000;
        }
    }

//</editor-fold>
//
//<editor-fold defaultstate="collapsed" desc="REGISTER SUMMARY">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 100000 + 1000)
    public static class SummaryFactory extends ProcDocumentItemFactory<TramoSeatsDocument, HtmlElement> {

        public SummaryFactory() {
            
            super(TramoSeatsDocument.class, PreprocessingViews.MODEL_SUMMARY, MODELEXTRACTOR
                    .andThen(regarima->regarima == null ? null :
                            new demetra.html.modelling.HtmlRegArima(regarima, false)),
                    new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 101000;
        }
    }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="REGISTER FORECASTS">
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 200000 + 500)
//    public static class ForecastsTable extends ForecastsTableFactory<TramoSeatsDocument> {
//
//        public ForecastsTable() {
//            super(TramoSeatsDocument.class, PreprocessingViews.MODEL_FCASTS_TABLE);
//        }
//
//        @Override
//        public int getPosition() {
//            return 200500;
//        }
//    }
//
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 200000 + 1000)
    public static class FCastsFactory extends ForecastsFactory<TramoSeatsDocument> {

        public FCastsFactory() {
            super(TramoSeatsDocument.class, PreprocessingViews.MODEL_FCASTS, MODELEXTRACTOR);
        }

        @Override
        public int getPosition() {
            return 201000;
        }
    }

//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 200000 + 2000)
//    public static class FCastsOutFactory extends OutOfSampleTestFactory<TramoSeatsDocument> {
//
//        public FCastsOutFactory() {
//            super(TramoSeatsDocument.class, PreprocessingViews.MODEL_FCASTS_OUTOFSAMPLE);
//        }
//
//        @Override
//        public int getPosition() {
//            return 202000;
//        }
//    }
//</editor-fold>
//
//<editor-fold defaultstate="collapsed" desc="REGISTER MODEL">
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 300000 + 1000)
//    public static class ModelRegsFactory extends ModelRegressorsFactory<TramoSeatsDocument> {
//
//        public ModelRegsFactory() {
//            super(TramoSeatsDocument.class, PreprocessingViews.MODEL_REGS);
//        }
//
//        @Override
//        public int getPosition() {
//            return 301000;
//        }
//    }
//
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 300000 + 2000)
//    public static class ArimaFactory extends ModelArimaFactory {
//
//        public ArimaFactory() {
//            super(TramoSeatsDocument.class, PreprocessingViews.MODEL_ARIMA);
//        }
//
//        @Override
//        public int getPosition() {
//            return 302000;
//        }
//    }
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 300000 + 3000)
//    public static class PreprocessingDetFactory extends ProcDocumentItemFactory<TramoSeatsDocument, RegSarimaModel> {
//
//        public PreprocessingDetFactory() {
//            super(TramoSeatsDocument.class, PreprocessingViews.MODEL_DET,
//                    source->source.getResult(), new GenericTableUI(false, 
//                            ModellingDictionary.Y_LIN, ModellingDictionary.DET, 
//                    ModellingDictionary.CAL, ModellingDictionary.TDE, ModellingDictionary.EE, 
//                    ModellingDictionary.OUT, ModellingDictionary.FULL_RES));
//        }
//
//        @Override
//        public int getPosition() {
//            return 303000;
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="REGISTER RESIDUALS">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 400000 + 1000)
    public static class ModelResFactory extends ProcDocumentItemFactory<TramoSeatsDocument, TsData> {

        public ModelResFactory() {
            super(TramoSeatsDocument.class, PreprocessingViews.MODEL_RES, RESEXTRACTOR,
                    new ResidualsUI()
            );
        }

        @Override
        public int getPosition() {
            return 401000;
        }
    }

//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 400000 + 2000)
//    public static class ModelResStatsFactory extends NiidTestsFactory<TramoSeatsDocument> {
//
//        public ModelResStatsFactory() {
//            super(TramoSeatsDocument.class, PreprocessingViews.MODEL_RES_STATS);
//        }
//
//        @Override
//        public int getPosition() {
//            return 402000;
//        }
//    }
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 400000 + 3000)
    public static class ModelResDist extends ProcDocumentItemFactory<TramoSeatsDocument, TsData> {

        public ModelResDist() {
            super(TramoSeatsDocument.class, PreprocessingViews.MODEL_RES_DIST,
                    RESEXTRACTOR,
                    new ResidualsDistUI());

        }

        @Override
        public int getPosition() {
            return 403000;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 400000 + 4000)
    public static class ModelResSpectrum extends ProcDocumentItemFactory<TramoSeatsDocument, TsData> {

        public ModelResSpectrum() {
            super(TramoSeatsDocument.class, PreprocessingViews.MODEL_RES_SPECTRUM,
                    RESEXTRACTOR,
                    new SpectrumUI(true));

        }

        @Override
        public int getPosition() {
            return 404000;
        }
    }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="REGISTER DETAILS">
//    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 500000)
//    public static class LFactory extends LikelihoodFactory<TramoSeatsDocument> {
//
//        public LFactory() {
//            super(TramoSeatsDocument.class, PreprocessingViews.MODEL_LIKELIHOOD);
//            setAsync(true);
//        }
//
//        @Override
//        public int getPosition() {
//            return 500000;
//        }
//    }
//</editor-fold>
    private static final AtomicReference<IProcDocumentViewFactory<TramoSeatsDocument>> INSTANCE = new AtomicReference(new TramoSeatsViewFactory());

}
