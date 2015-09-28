/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13.ui;

import ec.tss.html.IHtmlElement;
import ec.tss.html.implementation.HtmlX13Preprocessing;
import ec.tss.modelling.documents.RegArimaDocument;
import ec.tstoolkit.modelling.arima.PreprocessingModel;
import ec.tstoolkit.modelling.arima.x13.RegArimaSpecification;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.InformationExtractor;
import ec.ui.view.tsprocessing.ComposedProcDocumentItemFactory;
import ec.ui.view.tsprocessing.HtmlItemUI;
import ec.ui.view.tsprocessing.IProcDocumentView;
import ec.ui.view.tsprocessing.IProcDocumentViewFactory;
import ec.ui.view.tsprocessing.ItemUI;
import ec.ui.view.tsprocessing.PreprocessingViewFactory;
import ec.ui.view.tsprocessing.ProcDocumentItemFactory;
import static ec.ui.view.tsprocessing.PreprocessingViewFactory.PROCESSING_DETAILS;
import java.util.concurrent.atomic.AtomicReference;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author pcuser
 */
public class RegArimaViewFactory extends PreprocessingViewFactory<RegArimaSpecification, RegArimaDocument> {

    private static final AtomicReference<IProcDocumentViewFactory<RegArimaDocument>> INSTANCE = new AtomicReference(new RegArimaViewFactory());

    public static IProcDocumentViewFactory<RegArimaDocument> getDefault() {
        return INSTANCE.get();
    }

    public static void setDefault(IProcDocumentViewFactory<RegArimaDocument> factory) {
        INSTANCE.set(factory);
    }

    public RegArimaViewFactory() {
        registerDefault();
        registerFromLookup(RegArimaDocument.class);
    }

    //<editor-fold defaultstate="collapsed" desc="REGISTER SPEC">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000)
    public static class SpecAllFactory extends PreprocessingViewFactory.SpecAllFactory<RegArimaDocument> {

        public SpecAllFactory() {
            super(RegArimaDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100010)
    public static class InputFactory extends PreprocessingViewFactory.InputFactory<RegArimaDocument> {

        public InputFactory() {
            super(RegArimaDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER SUMMARY">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000 + 1000)
    public static class SummaryFactory extends PreprocessingViewFactory.SummaryFactory<RegArimaDocument> {

        public SummaryFactory() {
            super(RegArimaDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER FORECASTS">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 200000 + 1000)
    public static class ModelFCastsFactory extends PreprocessingViewFactory.ModelFCastsFactory<RegArimaDocument> {

        public ModelFCastsFactory() {
            super(RegArimaDocument.class);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="REGISTER FORECASTS">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 200000 + 500)
    public static class ModelFCastsTableFactory extends PreprocessingViewFactory.PreprocessingFCastsTableFactory<RegArimaDocument> {

        public ModelFCastsTableFactory() {
            super(RegArimaDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 200000 + 2000)
    public static class ModelFCastsOutFactory extends PreprocessingViewFactory.ModelFCastsOutFactory<RegArimaDocument> {

        public ModelFCastsOutFactory() {
            super(RegArimaDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER MODEL">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 1000)
    public static class ModelRegsFactory extends PreprocessingViewFactory.ModelRegsFactory<RegArimaDocument> {

        public ModelRegsFactory() {
            super(RegArimaDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 2000)
    public static class ModelArimaFactory extends PreprocessingViewFactory.ModelArimaFactory<RegArimaDocument> {

        public ModelArimaFactory() {
            super(RegArimaDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 3000)
    public static class PreprocessingDetFactory extends PreprocessingViewFactory.PreprocessingDetFactory<RegArimaDocument> {

        public PreprocessingDetFactory() {
            super(RegArimaDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER RESIDUALS">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 400000 + 1000)
    public static class ModelResFactory extends PreprocessingViewFactory.ModelResFactory<RegArimaDocument> {

        public ModelResFactory() {
            super(RegArimaDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 400000 + 2000)
    public static class ModelResStatsFactory extends PreprocessingViewFactory.ModelResStatsFactory<RegArimaDocument> {

        public ModelResStatsFactory() {
            super(RegArimaDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 400000 + 3000)
    public static class ModelResDist extends PreprocessingViewFactory.ModelResDist<RegArimaDocument> {

        public ModelResDist() {
            super(RegArimaDocument.class);
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 400000 + 4000)
    public static class ModelResSpectrum extends PreprocessingViewFactory.ModelResSpectrum<RegArimaDocument> {

        public ModelResSpectrum() {
            super(RegArimaDocument.class);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER DETAILS">
    @Deprecated
    @Override
    public void registerDetails() {
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 500000)
    public static class LikelihoodFactory extends PreprocessingViewFactory.LikelihoodFactory<RegArimaDocument> {

        public LikelihoodFactory() {
            super(RegArimaDocument.class);
            setAsync(true);
        }
    }
    //</editor-fold>
}
