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

import ec.nbdemetra.core.GlobalService;
import ec.tss.tsproviders.DataSource;
import java.awt.datatransfer.Transferable;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
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

    @Nonnull
    public static DataSourceTransferSupport getDefault() {
        return Lookup.getDefault().lookup(DataSourceTransferSupport.class);
    }

    @Nonnull
    public Stream<? extends DataSourceTransferHandler> all() {
        return Lookup.getDefault().lookupAll(DataSourceTransferHandler.class).stream().filter(Objects::nonNull);
    }

    public boolean canHandle(@Nonnull Transferable t) {
        return all().anyMatch(o -> o.canHandle(t));
    }

    public boolean canHandle(@Nonnull Transferable t, @Nonnull String providerName) {
        return all().anyMatch(o -> o.canHandle(t, providerName));
    }

    @Nonnull
    public Optional<DataSource> getDataSource(@Nonnull Transferable t) {
        return all()
                .filter(o -> o.canHandle(t))
                .map(o -> o.getDataSource(t))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    @Nonnull
    public Optional<DataSource> getDataSource(@Nonnull Transferable t, @Nonnull String providerName) {
        return all()
                .filter(o -> o.canHandle(t, providerName))
                .map(o -> o.getDataSource(t, providerName))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }
}
