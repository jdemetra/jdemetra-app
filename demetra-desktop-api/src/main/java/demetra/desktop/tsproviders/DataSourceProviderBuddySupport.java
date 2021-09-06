/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved
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
package demetra.desktop.tsproviders;

import demetra.desktop.actions.Configurable;
import demetra.desktop.design.GlobalService;
import demetra.desktop.util.CollectionSupplier;
import demetra.desktop.util.FrozenTsHelper;
import demetra.desktop.util.LazyGlobalService;
import demetra.timeseries.TsMoniker;
import demetra.tsprovider.DataSet;
import demetra.tsprovider.DataSource;
import demetra.tsprovider.DataSourceProvider;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.*;
import java.io.IOException;
import java.util.Optional;

/**
 * @author Philippe Charles
 */
@GlobalService
public final class DataSourceProviderBuddySupport {

    @NonNull
    public static DataSourceProviderBuddySupport getDefault() {
        return LazyGlobalService.get(DataSourceProviderBuddySupport.class, DataSourceProviderBuddySupport::new);
    }

    private final CollectionSupplier<DataSourceProviderBuddy> providers;
    private final DataSourceProviderBuddy fallback;

    private DataSourceProviderBuddySupport() {
        this.providers = DataSourceProviderBuddyLoader::get;
        this.fallback = new DataSourceProviderBuddy() {
            @Override
            public @NonNull String getProviderName() {
                return "fallback";
            }
        };
    }

    @NonNull
    public DataSourceProviderBuddy get(@Nullable DataSourceProvider provider) {
        return get(provider.getSource());
    }

    @NonNull
    public DataSourceProviderBuddy get(@Nullable String providerName) {
        String tmp = providerName == null ? "" : providerName;
        return providers.stream()
                .filter(o -> o.getProviderName().equals(tmp))
                .map(DataSourceProviderBuddy.class::cast)
                .findFirst()
                .orElse(fallback);
    }

    @NonNull
    public DataSourceProviderBuddy get(@NonNull DataSource dataSource) {
        return get(dataSource.getProviderName());
    }

    @NonNull
    public DataSourceProviderBuddy get(@NonNull DataSet dataSet) {
        return get(dataSet.getDataSource());
    }

    @NonNull
    public DataSourceProviderBuddy get(@NonNull TsMoniker moniker) {
        return get(moniker.getSource());
    }

    /**
     * Gets an icon for a provider.
     *
     * @param providerName
     * @param type
     * @param opened
     * @return an optional icon
     * @since 2.2.0
     */
    @NonNull
    public Optional<Image> getIcon(@NonNull String providerName, int type, boolean opened) {
        return Optional.ofNullable(get(providerName).getIconOrNull(type, opened));
    }

    /**
     * Gets an icon for a data source.
     *
     * @param dataSource
     * @param type
     * @param opened
     * @return an optional icon
     * @since 2.2.0
     */
    @NonNull
    public Optional<Image> getIcon(@NonNull DataSource dataSource, int type, boolean opened) {
        return Optional.ofNullable(get(dataSource).getIconOrNull(dataSource, type, opened));
    }

    /**
     * Gets an icon for a data set.
     *
     * @param dataSet
     * @param type
     * @param opened
     * @return an optional icon
     * @since 2.2.0
     */
    @NonNull
    public Optional<Image> getIcon(@NonNull DataSet dataSet, int type, boolean opened) {
        return Optional.ofNullable(get(dataSet).getIconOrNull(dataSet, type, opened));
    }

    /**
     * Gets an icon for an exception thrown by a provider.
     *
     * @param providerName
     * @param ex
     * @param type
     * @param opened
     * @return an optional icon
     * @since 2.2.0
     */
    @NonNull
    public Optional<Image> getIcon(@NonNull String providerName, @NonNull IOException ex, int type, boolean opened) {
        return Optional.ofNullable(get(providerName).getIconOrNull(ex, type, opened));
    }

    /**
     * Gets an icon for a moniker.
     *
     * @param moniker
     * @param type
     * @param opened
     * @return an optional icon
     * @since 2.2.0
     */
    @NonNull
    public Optional<Image> getIcon(@NonNull TsMoniker moniker, int type, boolean opened) {
        TsMoniker original = FrozenTsHelper.getOriginalMoniker(moniker);
        return original != null ? Optional.ofNullable(get(original).getIconOrNull(moniker, type, opened)) : Optional.empty();
    }

    /**
     * Gets a configurable for a provider.
     *
     * @param providerName
     * @return an optional configurable
     * @since 2.2.0
     */
    @NonNull
    public Optional<Configurable> getConfigurable(@NonNull String providerName) {
        DataSourceProviderBuddy buddy = get(providerName);
        return buddy instanceof Configurable ? Optional.of((Configurable) buddy) : Optional.empty();
    }
}
