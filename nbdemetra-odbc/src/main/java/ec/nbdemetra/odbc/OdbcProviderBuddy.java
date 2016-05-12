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
package ec.nbdemetra.odbc;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import ec.nbdemetra.jdbc.JdbcProviderBuddy;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.IConfigurable;
import ec.nbdemetra.ui.awt.SimpleHtmlListCellRenderer;
import ec.nbdemetra.ui.tsproviders.IDataSourceProviderBuddy;
import ec.tss.tsproviders.TsProviders;
import ec.tss.tsproviders.odbc.OdbcBean;
import ec.tss.tsproviders.odbc.OdbcProvider;
import ec.tss.tsproviders.odbc.registry.IOdbcRegistry;
import ec.tss.tsproviders.odbc.registry.OdbcDataSource;
import ec.util.completion.AutoCompletionSource;
import ec.util.completion.ext.QuickAutoCompletionSource;
import java.awt.Image;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import javax.swing.ListCellRenderer;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = IDataSourceProviderBuddy.class)
public class OdbcProviderBuddy extends JdbcProviderBuddy<OdbcBean> implements IConfigurable {

    private final static Config DEFAULT = Config.builder("", "", "").build();
    private final AutoCompletionSource dbSource;
    private final ListCellRenderer dbRenderer;

    public OdbcProviderBuddy() {
        super(TsProviders.lookup(OdbcProvider.class, OdbcProvider.SOURCE).get().getConnectionSupplier());
        this.dbSource = new OdbcDsnSource();
        this.dbRenderer = new SimpleHtmlListCellRenderer<>((OdbcDataSource o) -> "<html><b>" + o.getName() + "</b> - <i>" + o.getServerName() + "</i>");
    }

    @Override
    public Config getConfig() {
        return DEFAULT;
    }

    @Override
    public void setConfig(Config config) throws IllegalArgumentException {
        Preconditions.checkArgument(config.equals(DEFAULT));
    }

    @Override
    public Config editConfig(Config config) throws IllegalArgumentException {
        try {
            // %SystemRoot%\\system32\\odbcad32.exe
            Runtime.getRuntime().exec("odbcad32.exe");
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return config;
    }

    @Override
    protected boolean isFile() {
        return false;
    }

    @Override
    public String getProviderName() {
        return OdbcProvider.SOURCE;
    }

    @Override
    public Image getIcon(int type, boolean opened) {
        return ImageUtilities.loadImage("ec/nbdemetra/odbc/database.png", true);
    }

    @Override
    protected AutoCompletionSource getDbSource(OdbcBean bean) {
        return dbSource;
    }

    @Override
    protected ListCellRenderer getDbRenderer(OdbcBean bean) {
        return dbRenderer;
    }

    private static final class OdbcDsnSource extends QuickAutoCompletionSource<OdbcDataSource> {

        private final Cache<String, Iterable<OdbcDataSource>> cache = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();

        private String getKey() {
            return "";
        }

        private boolean isValid() {
            return true;
        }

        @Override
        public Request getRequest(String term) {
            String key = getKey();
            Iterable<OdbcDataSource> result = cache.getIfPresent(key);
            return result != null ? createCachedRequest(term, result) : super.getRequest(term);
        }

        @Override
        public Behavior getBehavior(String term) {
            return isValid() ? Behavior.ASYNC : Behavior.NONE;
        }

        @Override
        protected String getValueAsString(OdbcDataSource value) {
            return value.getName();
        }

        @Override
        protected Iterable<OdbcDataSource> getAllValues() throws Exception {
            String key = getKey();
            Iterable<OdbcDataSource> result = cache.getIfPresent(key);
            if (result == null) {
                IOdbcRegistry odbcRegistry = Lookup.getDefault().lookup(IOdbcRegistry.class);
                result = odbcRegistry != null
                        ? odbcRegistry.getDataSources(OdbcDataSource.Type.SYSTEM, OdbcDataSource.Type.USER)
                        : Collections.<OdbcDataSource>emptyList();
                cache.put(key, result);
            }
            return result;
        }

        @Override
        protected boolean matches(TermMatcher termMatcher, OdbcDataSource input) {
            return termMatcher.matches(input.getName()) || termMatcher.matches(input.getServerName());
        }
    }
}
