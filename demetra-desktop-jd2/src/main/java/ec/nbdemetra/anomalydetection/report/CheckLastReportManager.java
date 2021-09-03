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
package ec.nbdemetra.anomalydetection.report;

import demetra.desktop.design.GlobalService;
import demetra.desktop.util.CollectionSupplier;
import demetra.desktop.util.LazyGlobalService;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mats Maggi
 */
@GlobalService
public final class CheckLastReportManager {

    public static CheckLastReportManager getDefault() {
        return LazyGlobalService.get(CheckLastReportManager.class, CheckLastReportManager::new);
    }

    private final CollectionSupplier<ICheckLastReportFactory> factories;

    private CheckLastReportManager() {
        factories = ICheckLastReportFactoryLoader::get;
    }

    public List<ICheckLastReportFactory> getFactories() {
        return new ArrayList<>(factories.get());
    }
}
