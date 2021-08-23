/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa;

import demetra.ui.GlobalService;
import demetra.ui.util.CollectionSupplier;
import demetra.ui.util.LazyGlobalService;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
@GlobalService
public final class SaReportManager {

    public static SaReportManager getDefault() {
        return LazyGlobalService.get(SaReportManager.class, SaReportManager::new);
    }

    private SaReportManager() {
    }

    private final CollectionSupplier<ISaReportFactory> factories = ISaReportFactoryLoader::get;

    public List<ISaReportFactory> getFactories() {
        return new ArrayList<>(factories.get());
    }
}
