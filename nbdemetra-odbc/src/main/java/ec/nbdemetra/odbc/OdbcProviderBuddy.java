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

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import ec.nbdemetra.jdbc.JdbcProviderBuddy;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.IConfigurable;
import ec.nbdemetra.ui.awt.SimpleHtmlListCellRenderer;
import ec.nbdemetra.ui.tsproviders.IDataSourceProviderBuddy;
import ec.tss.tsproviders.TsProviders;
import ec.tss.tsproviders.jdbc.ConnectionSupplier;
import ec.tss.tsproviders.jdbc.JdbcBean;
import ec.tss.tsproviders.odbc.OdbcBean;
import ec.tss.tsproviders.odbc.OdbcProvider;
import ec.tstoolkit.utilities.GuavaCaches;
import ec.util.completion.AutoCompletionSource;
import ec.util.completion.ExtAutoCompletionSource;
import java.awt.Image;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.swing.ListCellRenderer;
import nbbrd.sql.odbc.OdbcDataSource;
import nbbrd.sql.odbc.OdbcRegistry;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
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
        super(getOdbcConnectionSupplier());
        this.dbSource = odbcDsnSource(OdbcRegistry.ofServiceLoader());
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
        launchOdbcDataSourceAdministrator();
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

    //<editor-fold defaultstate="collapsed" desc="Internal implementation">
    private static void launchOdbcDataSourceAdministrator() {
        try {
            // %SystemRoot%\\system32\\odbcad32.exe
            Runtime.getRuntime().exec("odbcad32.exe");
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private static ConnectionSupplier getOdbcConnectionSupplier() {
        Optional<OdbcProvider> provider = TsProviders.lookup(OdbcProvider.class, OdbcProvider.SOURCE);
        return provider.isPresent()
                ? provider.get().getConnectionSupplier()
                : new FailingConnectionSupplier("Cannot load OdbcProvider");
    }

    private static final class FailingConnectionSupplier implements ConnectionSupplier {

        private final String cause;

        public FailingConnectionSupplier(String cause) {
            this.cause = cause;
        }

        @Override
        public Connection getConnection(JdbcBean bean) throws SQLException {
            throw new SQLException(cause);
        }
    }

    private static AutoCompletionSource odbcDsnSource(java.util.Optional<OdbcRegistry> odbcRegistry) {
        return ExtAutoCompletionSource
                .builder(() -> getDataSources(odbcRegistry))
                .behavior(AutoCompletionSource.Behavior.ASYNC)
                .postProcessor(OdbcProviderBuddy::getDataSources)
                .valueToString(OdbcDataSource::getName)
                .cache(GuavaCaches.ttlCacheAsMap(Duration.ofSeconds(30)), o -> "", AutoCompletionSource.Behavior.SYNC)
                .build();
    }

    private static List<OdbcDataSource> getDataSources(java.util.Optional<OdbcRegistry> odbcRegistry) throws Exception {
        return odbcRegistry.isPresent()
                ? odbcRegistry.get().getDataSources(OdbcDataSource.Type.SYSTEM, OdbcDataSource.Type.USER)
                : Collections.emptyList();
    }

    private static List<OdbcDataSource> getDataSources(List<OdbcDataSource> allValues, String term) {
        Predicate<String> filter = ExtAutoCompletionSource.basicFilter(term);
        return allValues.stream()
                .filter(o -> filter.test(o.getName()) || filter.test(o.getServerName()))
                .sorted(Comparator.comparing(OdbcDataSource::getName))
                .collect(Collectors.toList());
    }
    //</editor-fold>
}
