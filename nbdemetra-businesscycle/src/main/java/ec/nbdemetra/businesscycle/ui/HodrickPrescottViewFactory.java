/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.businesscycle.ui;

import ec.tss.businesscycle.documents.HodrickPrescottDocument;
import ec.tstoolkit.utilities.DefaultInformationExtractor;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
import ec.ui.view.tsprocessing.ComposedProcDocumentItemFactory;
import ec.ui.view.tsprocessing.DualChartUI;
import ec.ui.view.tsprocessing.IProcDocumentViewFactory;
import ec.ui.view.tsprocessing.ProcDocumentItemFactory;
import ec.ui.view.tsprocessing.ProcDocumentViewFactory;
import java.util.concurrent.atomic.AtomicReference;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
public class HodrickPrescottViewFactory extends ProcDocumentViewFactory<HodrickPrescottDocument> {

    public static final String DECOMPOSITION = "Decomposition",
            BENCHMARKING = "Benchmarking",
            DIAGNOSTICS = "Diagnostics";
    public static final Id DECOMPOSITION_SUMMARY = new LinearId(DECOMPOSITION);
    private static final AtomicReference<IProcDocumentViewFactory<HodrickPrescottDocument>> INSTANCE = new AtomicReference(new HodrickPrescottViewFactory());

    public static IProcDocumentViewFactory<HodrickPrescottDocument> getDefault() {
        return INSTANCE.get();
    }

    public static void setDefault(IProcDocumentViewFactory<HodrickPrescottDocument> factory) {
        INSTANCE.set(factory);
    }

    public HodrickPrescottViewFactory() {
        registerDefault();
        registerFromLookup(HodrickPrescottDocument.class);
    }

    @Deprecated
    public void registerDefault() {
        registerMain();
    }

    @Deprecated
    public void registerMain() {
    }

    @Override
    public Id getPreferredView() {
        return DECOMPOSITION_SUMMARY;
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class)
    public static class DecompositionFactory extends ComposedProcDocumentItemFactory<HodrickPrescottDocument, String[][]> {

        public DecompositionFactory() {
            super(HodrickPrescottDocument.class, DECOMPOSITION_SUMMARY, new DefaultInformationExtractor<HodrickPrescottDocument, String[][]>() {
                @Override
                public String[][] retrieve(HodrickPrescottDocument source) {
                    switch (source.getSpecification().getTarget()) {
                        case Sa:
                            return new String[][]{new String[]{"sa.sa", "bc.trend"}, new String[]{"bc.cycle"}};
                        case Trend:
                            return new String[][]{new String[]{"sa.t", "bc.trend"}, new String[]{"bc.cycle"}};
                        default:
                            return new String[][]{new String[]{"series", "bc.trend"}, new String[]{"bc.cycle"}};
                    }
                }
            }, new DualChartUI());
        }
    }
}
