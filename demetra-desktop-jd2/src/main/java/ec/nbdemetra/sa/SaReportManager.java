/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa;

import demetra.ui.GlobalService;
import demetra.ui.util.CollectionSupplier;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
@GlobalService
public final class SaReportManager  {

    private static final SaReportManager INSTANCE = new SaReportManager();

    public static SaReportManager getDefault() {
        return INSTANCE;
    }

    private final CollectionSupplier<ISaReportFactory> factories = ISaReportFactoryLoader::get;
    
    public List<ISaReportFactory> getFactories(){
        return new ArrayList<>(factories.get());
    }
}
