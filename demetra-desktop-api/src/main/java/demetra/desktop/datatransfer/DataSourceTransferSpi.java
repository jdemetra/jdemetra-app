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
package demetra.desktop.datatransfer;

import demetra.tsprovider.DataSource;
import demetra.desktop.util.NetBeansServiceBackend;
import java.awt.datatransfer.Transferable;
import java.util.Optional;
import nbbrd.service.Quantifier;
import nbbrd.service.ServiceDefinition;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Class that can produce a DataSource from a Transferable. To be used through
 * {@link DataSourceTransfer}.
 *
 * @author Philippe Charles
 */
@ServiceDefinition(
        quantifier = Quantifier.MULTIPLE,
        backend = NetBeansServiceBackend.class,
        singleton = true
)
public interface DataSourceTransferSpi {

    /**
     * Checks if this class can handle the specified Transferable.
     *
     * @param t the input data
     * @return
     */
    boolean canHandle(@NonNull Transferable t);

    /**
     * Checks if this class can handle the specified Transferable with a
     * specific provider.
     *
     * @param t the input data
     * @param providerName a specific provider name
     * @return
     */
    boolean canHandle(@NonNull Transferable t, @NonNull String providerName);

    /**
     * Retrieve a DataSource from a Transferable.
     *
     * @param t the input data
     * @return an optional DataSource
     * @see #canHandle(java.awt.datatransfer.Transferable)
     */
    @NonNull
    Optional<DataSource> getDataSource(@NonNull Transferable t);

    /**
     * Retrieve a DataSource from a Transferable with a specific provider.
     *
     * @param t the input data
     * @param providerName a specific provider name
     * @return an optional DataSource
     * @see #canHandle(java.awt.datatransfer.Transferable, java.lang.String)
     */
    @NonNull
    Optional<DataSource> getDataSource(@NonNull Transferable t, @NonNull String providerName);
}
