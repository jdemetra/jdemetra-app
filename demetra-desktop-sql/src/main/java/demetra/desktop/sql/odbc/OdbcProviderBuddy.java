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
package demetra.desktop.sql.odbc;

import demetra.desktop.TsManager;
import demetra.desktop.actions.Configurable;
import demetra.desktop.properties.NodePropertySetBuilder;
import demetra.desktop.sql.SqlColumnListCellRenderer;
import demetra.desktop.sql.SqlProviderBuddy;
import demetra.desktop.sql.SqlTableListCellRenderer;
import demetra.desktop.util.SimpleHtmlCellRenderer;
import demetra.desktop.tsproviders.DataSourceProviderBuddy;
import demetra.desktop.tsproviders.DataSourceProviderBuddyUtil;
import demetra.desktop.tsproviders.TsProviderProperties;
import demetra.sql.HasSqlProperties;
import demetra.sql.odbc.OdbcBean;
import ec.util.completion.AutoCompletionSource;
import ec.util.completion.ExtAutoCompletionSource;
import java.awt.Image;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.swing.ListCellRenderer;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;
import nbbrd.sql.jdbc.SqlConnectionSupplier;
import nbbrd.sql.odbc.OdbcDataSource;
import nbbrd.sql.odbc.OdbcRegistry;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author Philippe Charles
 */
@DirectImpl
@ServiceProvider(DataSourceProviderBuddy.class)
public final class OdbcProviderBuddy implements DataSourceProviderBuddy, Configurable {

    private static final String SOURCE = "ODBCPRVDR";

    private final SqlConnectionSupplier supplier;
    private final AutoCompletionSource dbSource;
    private final ListCellRenderer dbRenderer;
    private final ListCellRenderer tableRenderer;
    private final ListCellRenderer columnRenderer;

    public OdbcProviderBuddy() {
        this.supplier = getOdbcConnectionSupplier();
        this.dbSource = odbcDsnSource();
        this.dbRenderer = new SimpleHtmlCellRenderer<>((OdbcDataSource o) -> "<html><b>" + o.getName() + "</b> - <i>" + o.getServerName() + "</i>");
        this.tableRenderer = new SqlTableListCellRenderer();
        this.columnRenderer = new SqlColumnListCellRenderer();
    }

    @Override
    public void configure() {
        launchOdbcDataSourceAdministrator();
    }

    @Override
    public String getProviderName() {
        return SOURCE;
    }

    @Override
    public Image getIconOrNull(int type, boolean opened) {
        return ImageUtilities.loadImage("ec/nbdemetra/odbc/database.png", true);
    }

    @Override
    public Sheet getSheetOfBeanOrNull(Object bean) throws IntrospectionException {
        return bean instanceof OdbcBean
                ? createSheetSets((OdbcBean) bean)
                : DataSourceProviderBuddy.super.getSheetOfBeanOrNull(bean);
    }

    private Sheet createSheetSets(OdbcBean bean) {
        NodePropertySetBuilder b = new NodePropertySetBuilder();
        return DataSourceProviderBuddyUtil.sheetOf(
                createSource(b, bean),
                createCube(b, bean),
                createParsing(b, bean),
                createCache(b, bean)
        );
    }

    @NbBundle.Messages({
        "bean.source.display=Source",
        "bean.source.description=",
        "bean.dsn.display=Data source name",
        "bean.dsn.description=Data structure describing the connection to the database.",
        "bean.table.display=Table name",
        "bean.table.description=The name of the table (or view) that contains observations.",})
    private Sheet.Set createSource(NodePropertySetBuilder b, OdbcBean bean) {
        b.reset("source")
                .display(Bundle.bean_source_display())
                .description(Bundle.bean_source_description());

        b.withAutoCompletion()
                .select("dsn", bean::getDsn, bean::setDsn)
                .source(dbSource)
                .cellRenderer(dbRenderer)
                .display(Bundle.bean_dsn_display())
                .description(Bundle.bean_dsn_description())
                .add();

        b.withAutoCompletion()
                .select("table", bean::getTable, bean::setTable)
                .source(SqlProviderBuddy.getTableSource(supplier, bean::getDsn, bean::getTable))
                .cellRenderer(tableRenderer)
                .display(Bundle.bean_table_display())
                .description(Bundle.bean_table_description())
                .add();

        return b.build();
    }

    @NbBundle.Messages({
        "bean.cube.display=Cube structure",
        "bean.cube.description=",})
    private Sheet.Set createCube(NodePropertySetBuilder b, OdbcBean bean) {
        b.reset("cube")
                .display(Bundle.bean_cube_display())
                .description(Bundle.bean_cube_description());

        TsProviderProperties.addTableAsCubeStructure(b, bean::getCube, bean::setCube,
                SqlProviderBuddy.getColumnSource(supplier, bean::getDsn, bean::getTable),
                columnRenderer
        );

        return b.build();
    }

    @NbBundle.Messages({
        "bean.parsing.display=Parsing",
        "bean.parsing.description=",})
    private Sheet.Set createParsing(NodePropertySetBuilder b, OdbcBean bean) {
        b.reset("parsing")
                .display(Bundle.bean_parsing_display())
                .description(Bundle.bean_parsing_description());

        TsProviderProperties.addTableAsCubeParsing(b, bean::getCube, bean::setCube);

        return b.build();
    }

    @NbBundle.Messages({
        "bean.cache.display=Cache",
        "bean.cache.description=Mechanism used to improve performance.",})
    private Sheet.Set createCache(NodePropertySetBuilder b, OdbcBean bean) {
        b.reset("cache")
                .display(Bundle.bean_cache_display())
                .description(Bundle.bean_cache_description());

        TsProviderProperties.addBulkCube(b, bean::getCache, bean::setCache);

        return b.build();
    }

    private static void launchOdbcDataSourceAdministrator() {
        try {
            // %SystemRoot%\\system32\\odbcad32.exe
            Runtime.getRuntime().exec("odbcad32.exe");
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private static SqlConnectionSupplier getOdbcConnectionSupplier() {
        Optional<HasSqlProperties> provider = TsManager.get()
                .getProvider(SOURCE)
                .filter(HasSqlProperties.class::isInstance)
                .map(HasSqlProperties.class::cast);
        return provider.isPresent()
                ? provider.get().getConnectionSupplier()
                : new FailingConnectionSupplier("Cannot load OdbcProvider");
    }

    private static final class FailingConnectionSupplier implements SqlConnectionSupplier {

        private final String cause;

        public FailingConnectionSupplier(String cause) {
            this.cause = cause;
        }

        @Override
        public Connection getConnection(String dbName) throws SQLException {
            throw new SQLException(cause);
        }
    }

    private static AutoCompletionSource odbcDsnSource() {
        return ExtAutoCompletionSource
                .builder(OdbcProviderBuddy::getDataSources)
                .behavior(AutoCompletionSource.Behavior.ASYNC)
                .postProcessor(OdbcProviderBuddy::getDataSources)
                .valueToString(OdbcDataSource::getName)
                .cache(new ConcurrentHashMap<>(), o -> "", AutoCompletionSource.Behavior.SYNC)
                .build();
    }

    private static List<OdbcDataSource> getDataSources() throws Exception {
        Optional<OdbcRegistry> odbcRegistry = OdbcRegistry.ofServiceLoader();
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
}
