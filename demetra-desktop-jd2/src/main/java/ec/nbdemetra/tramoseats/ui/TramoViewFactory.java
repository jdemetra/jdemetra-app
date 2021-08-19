/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats.ui;

import ec.tss.modelling.documents.TramoDocument;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import ec.ui.view.tsprocessing.IProcDocumentViewFactory;
import ec.ui.view.tsprocessing.PreprocessingViewFactory;
import ec.ui.view.tsprocessing.ProcDocumentItemFactory;
import java.util.concurrent.atomic.AtomicReference;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
public class TramoViewFactory extends PreprocessingViewFactory<TramoSpecification, TramoDocument> {

    private static final AtomicReference<IProcDocumentViewFactory<TramoDocument>> INSTANCE = new AtomicReference(new TramoViewFactory());

    public static IProcDocumentViewFactory<TramoDocument> getDefault() {
        return INSTANCE.get();
    }

    public static void setDefault(IProcDocumentViewFactory<TramoDocument> factory) {
        INSTANCE.set(factory);
    }

    public TramoViewFactory() {
        registerFromLookup(TramoDocument.class);
    }

    //<editor-fold defaultstate="collapsed" desc="REGISTER SPEC">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000)
    public static class SpecAllFactory extends PreprocessingViewFactory.SpecAllFactory<TramoDocument> {

        public SpecAllFactory() {
            super(TramoDocument.class);
        }

        @Override
        public int getPosition() {
            return 100000;
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100010)
    public static class InputFactory extends PreprocessingViewFactory.InputFactory<TramoDocument> {

        public InputFactory() {
            super(TramoDocument.class);
        }

        @Override
        public int getPosition() {
            return 100010;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER SUMMARY">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 100000 + 1000)
    public static class SummaryFactory extends PreprocessingViewFactory.SummaryFactory<TramoDocument> {

        public SummaryFactory() {
            super(TramoDocument.class);
        }

        @Override
        public int getPosition() {
            return 101000;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER FORECASTS">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 200000 + 500)
    public static class ModelFCastsTableFactory extends PreprocessingViewFactory.PreprocessingFCastsTableFactory<TramoDocument> {

        public ModelFCastsTableFactory() {
            super(TramoDocument.class);
        }

        @Override
        public int getPosition() {
            return 200500;
        }
    }

    //<editor-fold defaultstate="collapsed" desc="REGISTER FORECASTS">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 200000 + 1000)
    public static class ModelFCastsFactory extends PreprocessingViewFactory.ModelFCastsFactory<TramoDocument> {

        public ModelFCastsFactory() {
            super(TramoDocument.class);
        }

        @Override
        public int getPosition() {
            return 201000;
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 200000 + 2000)
    public static class ModelFCastsOutFactory extends PreprocessingViewFactory.ModelFCastsOutFactory<TramoDocument> {

        public ModelFCastsOutFactory() {
            super(TramoDocument.class);
        }

        @Override
        public int getPosition() {
            return 202000;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER MODEL">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 1000)
    public static class ModelRegsFactory extends PreprocessingViewFactory.ModelRegsFactory<TramoDocument> {

        public ModelRegsFactory() {
            super(TramoDocument.class);
        }

        @Override
        public int getPosition() {
            return 301000;
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 2000)
    public static class ModelArimaFactory extends PreprocessingViewFactory.ModelArimaFactory<TramoDocument> {

        public ModelArimaFactory() {
            super(TramoDocument.class);
        }

        @Override
        public int getPosition() {
            return 302000;
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 300000 + 3000)
    public static class PreprocessingDetFactory extends PreprocessingViewFactory.PreprocessingDetFactory<TramoDocument> {

        public PreprocessingDetFactory() {
            super(TramoDocument.class);
        }

        @Override
        public int getPosition() {
            return 303000;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REGISTER RESIDUALS">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 400000 + 1000)
    public static class ModelResFactory extends PreprocessingViewFactory.ModelResFactory<TramoDocument> {

        public ModelResFactory() {
            super(TramoDocument.class);
        }

        @Override
        public int getPosition() {
            return 401000;
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 400000 + 2000)
    public static class ModelResStatsFactory extends PreprocessingViewFactory.ModelResStatsFactory<TramoDocument> {

        public ModelResStatsFactory() {
            super(TramoDocument.class);
        }

        @Override
        public int getPosition() {
            return 402000;
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 400000 + 3000)
    public static class ModelResDist extends PreprocessingViewFactory.ModelResDist<TramoDocument> {

        public ModelResDist() {
            super(TramoDocument.class);
        }

        @Override
        public int getPosition() {
            return 403000;
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 400000 + 4000)
    public static class ModelResSpectrum extends PreprocessingViewFactory.ModelResSpectrum<TramoDocument> {

        public ModelResSpectrum() {
            super(TramoDocument.class);
        }

        @Override
        public int getPosition() {
            return 404000;
        }
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="REGISTER DETAILS">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 500000)
    public static class LikelihoodFactory extends PreprocessingViewFactory.LikelihoodFactory<TramoDocument> {

        public LikelihoodFactory() {
            super(TramoDocument.class);
            setAsync(true);
        }

        @Override
        public int getPosition() {
            return 500000;
        }
    }
    //</editor-fold>
}
