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
package ec.tss.datatransfer;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import ec.nbdemetra.core.GlobalService;
import ec.tss.tsproviders.DataSource;
import java.awt.datatransfer.Transferable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * A support class that deals with DataSource in Transferable.
 *
 * @author Philippe Charles
 */
@GlobalService
@ServiceProvider(service = DataSourceTransferSupport.class)
public class DataSourceTransferSupport {

    @NonNull
    public static DataSourceTransferSupport getDefault() {
        return Lookup.getDefault().lookup(DataSourceTransferSupport.class);
    }

    @Deprecated
    @NonNull
    public static DataSourceTransferSupport getInstance() {
        return getDefault();
    }

    @NonNull
    public FluentIterable<? extends DataSourceTransferHandler> all() {
        return FluentIterable.from(Lookup.getDefault().lookupAll(DataSourceTransferHandler.class));
    }

    public boolean canHandle(@NonNull Transferable t) {
        return all().anyMatch(o -> o != null ? o.canHandle(t) : false);
    }

    public boolean canHandle(@NonNull Transferable t, @NonNull String providerName) {
        return all().anyMatch(o -> o != null ? o.canHandle(t, providerName) : false);
    }

    @NonNull
    public Optional<DataSource> getDataSource(@NonNull Transferable t) {
        for (DataSourceTransferHandler o : all().filter(o -> o != null ? o.canHandle(t) : false)) {
            Optional<DataSource> dataSource = o.getDataSource(t);
            if (dataSource.isPresent()) {
                return dataSource;
            }
        }
        return Optional.absent();
    }

    @NonNull
    public Optional<DataSource> getDataSource(@NonNull Transferable t, @NonNull String providerName) {
        for (DataSourceTransferHandler o : all().filter(o -> o != null ? o.canHandle(t, providerName) : false)) {
            Optional<DataSource> dataSource = o.getDataSource(t, providerName);
            if (dataSource.isPresent()) {
                return dataSource;
            }
        }
        return Optional.absent();
    }
}
