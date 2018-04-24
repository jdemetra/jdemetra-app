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
import ec.nbdemetra.core.GlobalService;
import ec.nbdemetra.ui.IConfigurable;
import ec.tss.TsMoniker;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.IDataSourceProvider;
import internal.FrozenTsHelper;
import java.awt.Image;
import java.io.IOException;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@GlobalService
@ServiceProvider(service = DataSourceProviderBuddySupport.class)
public class DataSourceProviderBuddySupport {

    @Nonnull
    public static DataSourceProviderBuddySupport getDefault() {
        return Lookup.getDefault().lookup(DataSourceProviderBuddySupport.class);
    }

    private final LoadingCache<String, IDataSourceProviderBuddy> fallback;

    public DataSourceProviderBuddySupport() {
        fallback = CacheBuilder.newBuilder().build(new CacheLoaderImpl());
    }

    @Nonnull
    protected IDataSourceProviderBuddy getFallback(@Nonnull String providerName) {
        return fallback.getUnchecked(providerName);
    }

    @Nonnull
    public IDataSourceProviderBuddy get(@Nullable String providerName) {
        String tmp = Strings.nullToEmpty(providerName);
        return Lookup.getDefault().lookupAll(IDataSourceProviderBuddy.class).stream()
                .filter(o -> o.getProviderName().equals(tmp))
                .map(o -> (IDataSourceProviderBuddy) o)
                .findFirst()
                .orElseGet(() -> getFallback(tmp));
    }

    @Nonnull
    public IDataSourceProviderBuddy get(@Nonnull IDataSourceProvider provider) {
        return get(provider.getSource());
    }

    @Nonnull
    public IDataSourceProviderBuddy get(@Nonnull DataSource dataSource) {
        return get(dataSource.getProviderName());
    }

    @Nonnull
    public IDataSourceProviderBuddy get(@Nonnull DataSet dataSet) {
        return get(dataSet.getDataSource());
    }

    @Nonnull
    public IDataSourceProviderBuddy get(@Nonnull TsMoniker moniker) {
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
    @Nonnull
    public Optional<Image> getIcon(@Nonnull String providerName, int type, boolean opened) {
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
    @Nonnull
    public Optional<Image> getIcon(@Nonnull DataSource dataSource, int type, boolean opened) {
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
    @Nonnull
    public Optional<Image> getIcon(@Nonnull DataSet dataSet, int type, boolean opened) {
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
    @Nonnull
    public Optional<Image> getIcon(@Nonnull String providerName, @Nonnull IOException ex, int type, boolean opened) {
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
    @Nonnull
    public Optional<Image> getIcon(@Nonnull TsMoniker moniker, int type, boolean opened) {
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
    @Nonnull
    public Optional<IConfigurable> getConfigurable(@Nonnull String providerName) {
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
