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
import ec.tss.TsMoniker;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.IDataSourceProvider;
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

    @Deprecated
    @Nonnull
    public static DataSourceProviderBuddySupport getInstance() {
        return getDefault();
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

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    private static final class CacheLoaderImpl extends CacheLoader<String, IDataSourceProviderBuddy> {

        @Override
        public IDataSourceProviderBuddy load(final String key) {
            return () -> key;
        }
    }
    //</editor-fold>
}
