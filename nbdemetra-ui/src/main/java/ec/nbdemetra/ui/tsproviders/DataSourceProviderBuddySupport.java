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
package ec.nbdemetra.ui.tsproviders;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import demetra.ui.GlobalService;
import ec.nbdemetra.ui.IConfigurable;
import ec.tss.TsMoniker;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.IDataSourceProvider;
import internal.FrozenTsHelper;
import java.awt.Image;
import java.io.IOException;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@GlobalService
@ServiceProvider(service = DataSourceProviderBuddySupport.class)
public class DataSourceProviderBuddySupport {

    @NonNull
    public static DataSourceProviderBuddySupport getDefault() {
        return Lookup.getDefault().lookup(DataSourceProviderBuddySupport.class);
    }

    private final LoadingCache<String, IDataSourceProviderBuddy> fallback;

    public DataSourceProviderBuddySupport() {
        fallback = CacheBuilder.newBuilder().build(new CacheLoaderImpl());
    }

    @NonNull
    protected IDataSourceProviderBuddy getFallback(@NonNull String providerName) {
        return fallback.getUnchecked(providerName);
    }

    @NonNull
    public IDataSourceProviderBuddy get(@Nullable String providerName) {
        String tmp = Strings.nullToEmpty(providerName);
        return Lookup.getDefault().lookupAll(IDataSourceProviderBuddy.class).stream()
                .filter(o -> o.getProviderName().equals(tmp))
                .map(o -> (IDataSourceProviderBuddy) o)
                .findFirst()
                .orElseGet(() -> getFallback(tmp));
    }

    @NonNull
    public IDataSourceProviderBuddy get(@NonNull IDataSourceProvider provider) {
        return get(provider.getSource());
    }

    @NonNull
    public IDataSourceProviderBuddy get(@NonNull DataSource dataSource) {
        return get(dataSource.getProviderName());
    }

    @NonNull
    public IDataSourceProviderBuddy get(@NonNull DataSet dataSet) {
        return get(dataSet.getDataSource());
    }

    @NonNull
    public IDataSourceProviderBuddy get(@NonNull TsMoniker moniker) {
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
        return Optional.ofNullable(get(providerName).getIcon(type, opened));
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
        return Optional.ofNullable(get(dataSource).getIcon(dataSource, type, opened));
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
        return Optional.ofNullable(get(dataSet).getIcon(dataSet, type, opened));
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
        return Optional.ofNullable(get(providerName).getIcon(ex, type, opened));
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
        return original != null ? Optional.ofNullable(get(original).getIcon(moniker, type, opened)) : Optional.empty();
    }

    /**
     * Gets a configurable for a provider.
     *
     * @param providerName
     * @return an optional configurable
     * @since 2.2.0
     */
    @NonNull
    public Optional<IConfigurable> getConfigurable(@NonNull String providerName) {
        IDataSourceProviderBuddy buddy = get(providerName);
        return buddy instanceof IConfigurable ? Optional.of((IConfigurable) buddy) : Optional.empty();
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    private static final class CacheLoaderImpl extends CacheLoader<String, IDataSourceProviderBuddy> {

        @Override
        public IDataSourceProviderBuddy load(final String key) {
            return () -> key;
        }
    }
    //</editor-fold>
}
