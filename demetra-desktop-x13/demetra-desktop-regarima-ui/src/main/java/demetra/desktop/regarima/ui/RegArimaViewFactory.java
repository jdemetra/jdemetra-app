/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.regarima.ui;

import demetra.desktop.modelling.ForecastsFactory;
import demetra.desktop.modelling.ForecastsTableFactory;
import demetra.desktop.modelling.InputFactory;
import demetra.desktop.modelling.LikelihoodFactory;
import demetra.desktop.modelling.ModelArimaFactory;
import demetra.desktop.modelling.ModelRegressorsFactory;
import demetra.desktop.modelling.NiidTestsFactory;
import demetra.desktop.modelling.OutOfSampleTestFactory;
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
import demetra.util.Id;
import demetra.x13.io.information.RegArimaSpecMapping;
import java.util.concurrent.atomic.AtomicReference;
import jdplus.regsarima.regular.RegSarimaModel;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
public class RegArimaViewFactory extends ProcDocumentViewFactory<RegArimaDocument> {

    private static final AtomicReference<IProcDocumentViewFactory<RegArimaDocument>> INSTANCE = new AtomicReference(new RegArimaViewFactory());

    public static IProcDocumentViewFactory<RegArimaDocument> getDefault() {
        return INSTANCE.get();
    }

    public static void setDefault(IProcDocumentViewFactory<RegArimaDocument> factory) {
        INSTANCE.set(factory);
    }

    public RegArimaViewFactory() {
        registerFromLookup(RegArimaDocument.class);
    }

    @Override
    public Id getPreferredView() {
        return PreprocessingViews.MODEL_SUMMARY;
    }

//<editor-fold defaultstate="collapsed" desc="REGISTER SPEC">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 100010)
    public static class SpecFactory extends ProcDocumentItemFactory<RegArimaDocument, HtmlElement> {

        public SpecFactory() {
            super(RegArimaDocument.class, PreprocessingViews.INPUT_SPEC,
                    (RegArimaDocument doc) -> {
                        InformationSet info = RegArimaSpecMapping.write(doc.getSpecification(), true);
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
    public static class Input extends InputFactory<RegArimaDocument> {

        public Input() {
            super(RegArimaDocument.class, PreprocessingViews.INPUT_SERIES);
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
    public static class SummaryFactory extends ProcDocumentItemFactory<RegArimaDocument, HtmlElement> {

        public SummaryFactory() {
            super(RegArimaDocument.class, PreprocessingViews.MODEL_SUMMARY,
            source->new demetra.html.modelling.HtmlRegArima(source.getResult(), false),
            new HtmlItemUI());
        }

        @Override
        public int getPosition() {
            return 101000;
        }
    }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="REGISTER FORECASTS">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 200000 + 500)
    public static class ForecastsTable extends ForecastsTableFactory<RegArimaDocument> {

        public ForecastsTable() {
            super(RegArimaDocument.class, PreprocessingViews.MODEL_FCASTS_TABLE);
        }

        @Override
        public int getPosition() {
            return 200500;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 200000 + 1000)
    public static class FCastsFactory extends ForecastsFactory<RegArimaDocument> {

        public FCastsFactory() {
            super(RegArimaDocument.class, PreprocessingViews.MODEL_FCASTS);
        }

        @Override
        public int getPosition() {
            return 201000;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 200000 + 2000)
    public static class FCastsOutFactory extends OutOfSampleTestFactory<RegArimaDocument> {

        public FCastsOutFactory() {
            super(RegArimaDocument.class, PreprocessingViews.MODEL_FCASTS_OUTOFSAMPLE);
        }

        @Override
        public int getPosition() {
            return 202000;
        }
    }

//</editor-fold>
//
//<editor-fold defaultstate="collapsed" desc="REGISTER MODEL">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 300000 + 1000)
    public static class ModelRegsFactory extends ModelRegressorsFactory<RegArimaDocument> {

        public ModelRegsFactory() {
            super(RegArimaDocument.class, PreprocessingViews.MODEL_REGS);
        }

        @Override
        public int getPosition() {
            return 301000;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 300000 + 2000)
    public static class ArimaFactory extends ModelArimaFactory {

        public ArimaFactory() {
            super(RegArimaDocument.class, PreprocessingViews.MODEL_ARIMA);
        }

        @Override
        public int getPosition() {
            return 302000;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 300000 + 3000)
    public static class PreprocessingDetFactory extends ProcDocumentItemFactory<RegArimaDocument, RegSarimaModel> {

        public PreprocessingDetFactory() {
            super(RegArimaDocument.class, PreprocessingViews.MODEL_DET,
                    source->source.getResult(), new GenericTableUI(false, 
                            ModellingDictionary.Y_LIN, ModellingDictionary.DET, 
                    ModellingDictionary.CAL, ModellingDictionary.TDE, ModellingDictionary.EE, 
                    ModellingDictionary.OUT, ModellingDictionary.FULL_RES));
        }

        @Override
        public int getPosition() {
            return 303000;
        }
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="REGISTER RESIDUALS">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 400000 + 1000)
    public static class ModelResFactory extends ProcDocumentItemFactory<RegArimaDocument, TsData> {

        public ModelResFactory() {
            super(RegArimaDocument.class, PreprocessingViews.MODEL_RES,
                    (RegArimaDocument source)->source.getResult().fullResiduals(),
                    new ResidualsUI()
                    );
        }

        @Override
        public int getPosition() {
            return 401000;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 400000 + 2000)
    public static class ModelResStatsFactory extends NiidTestsFactory<RegArimaDocument> {

        public ModelResStatsFactory() {
            super(RegArimaDocument.class, PreprocessingViews.MODEL_RES_STATS);
        }

        @Override
        public int getPosition() {
            return 402000;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 400000 + 3000)
    public static class ModelResDist extends ProcDocumentItemFactory<RegArimaDocument, TsData> {

        public ModelResDist() {
            super(RegArimaDocument.class, PreprocessingViews.MODEL_RES_DIST,
                    (RegArimaDocument source)->source.getResult().fullResiduals(),
                    new ResidualsDistUI());
                   
        }

        @Override
        public int getPosition() {
            return 403000;
        }
    }

    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 400000 + 4000)
    public static class ModelResSpectrum extends ProcDocumentItemFactory<RegArimaDocument, TsData>  {

        public ModelResSpectrum() {
            super(RegArimaDocument.class, PreprocessingViews.MODEL_RES_SPECTRUM,
                    (RegArimaDocument source)->source.getResult().fullResiduals(),
                    new SpectrumUI(true));
                   
        }

        @Override
        public int getPosition() {
            return 404000;
        }
    }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="REGISTER DETAILS">
    @ServiceProvider(service = IProcDocumentItemFactory.class, position = 500000)
    public static class LFactory extends LikelihoodFactory<RegArimaDocument> {

        public LFactory() {
            super(RegArimaDocument.class, PreprocessingViews.MODEL_LIKELIHOOD);
            setAsync(true);
        }

        @Override
        public int getPosition() {
            return 500000;
        }
    }
//</editor-fold>
}
