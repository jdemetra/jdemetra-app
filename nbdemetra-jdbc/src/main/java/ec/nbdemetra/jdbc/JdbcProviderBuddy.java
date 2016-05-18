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
package ec.nbdemetra.jdbc;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import ec.nbdemetra.db.DbProviderBuddy;
import ec.tss.tsproviders.jdbc.ConnectionSupplier;
import ec.tss.tsproviders.jdbc.JdbcBean;
import ec.util.completion.AutoCompletionSource;
import static ec.util.completion.AutoCompletionSource.Behavior.ASYNC;
import static ec.util.completion.AutoCompletionSource.Behavior.NONE;
import static ec.util.completion.AutoCompletionSource.Behavior.SYNC;
import ec.util.completion.ExtAutoCompletionSource;
import ec.util.jdbc.JdbcColumn;
import ec.util.jdbc.JdbcTable;
import ec.util.jdbc.SqlIdentifierQuoter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.swing.ListCellRenderer;

/**
 * An abstract provider buddy that targets Jdbc providers.
 *
 * @author Philippe Charles
 * @param <BEAN>
 */
public abstract class JdbcProviderBuddy<BEAN extends JdbcBean> extends DbProviderBuddy<BEAN> {

    protected final ConnectionSupplier supplier;

    public JdbcProviderBuddy(@Nonnull ConnectionSupplier supplier) {
        this.supplier = supplier;
    }

    @Override
    protected AutoCompletionSource getTableSource(BEAN bean) {
        return ExtAutoCompletionSource
                .builder(o -> getJdbcTables(supplier, bean))
                .behavior(o -> !Strings.isNullOrEmpty(bean.getDbName()) ? ASYNC : NONE)
                .postProcessor(JdbcProviderBuddy::getJdbcTables)
                .valueToString(JdbcTable::getName)
                .cache(cacheTtl(1, TimeUnit.MINUTES), o -> bean.getDbName(), SYNC)
                .build();
    }

    @Override
    protected ListCellRenderer getTableRenderer(BEAN bean) {
        return new JdbcTableListCellRenderer();
    }

    @Override
    protected AutoCompletionSource getColumnSource(BEAN bean) {
        return ExtAutoCompletionSource
                .builder(o -> getJdbcColumns(supplier, bean))
                .behavior(o -> !Strings.isNullOrEmpty(bean.getDbName()) && !Strings.isNullOrEmpty(bean.getTableName()) ? ASYNC : NONE)
                .postProcessor(JdbcProviderBuddy::getJdbcColumns)
                .valueToString(JdbcColumn::getName)
                .cache(cacheTtl(1, TimeUnit.MINUTES), o -> bean.getDbName() + "/" + bean.getTableName(), SYNC)
                .build();
    }

    @Override
    protected ListCellRenderer getColumnRenderer(BEAN bean) {
        return new JdbcColumnListCellRenderer();
    }

    //<editor-fold defaultstate="collapsed" desc="Internal implementation">
    private static <T extends JdbcBean> List<JdbcTable> getJdbcTables(ConnectionSupplier supplier, T bean) throws SQLException {
        try (Connection c = supplier.getConnection(bean)) {
            return JdbcTable.allOf(c.getMetaData(), c.getCatalog(), c.getSchema(), "%", new String[]{"TABLE", "VIEW"});
        }
    }

    private static <T extends JdbcBean> List<JdbcColumn> getJdbcColumns(ConnectionSupplier supplier, T bean) throws SQLException {
        try (Connection c = supplier.getConnection(bean)) {
            SqlIdentifierQuoter quoter = SqlIdentifierQuoter.create(c.getMetaData());
            try (Statement st = c.createStatement()) {
                try (ResultSet rs = st.executeQuery("select * from " + quoter.quote(bean.getTableName(), false) + " where 1 = 0")) {
                    return JdbcColumn.ofAll(rs.getMetaData());
                }
            }
        }
    }

    private static List<JdbcTable> getJdbcTables(List<JdbcTable> values, String term) {
        Predicate<String> filter = ExtAutoCompletionSource.basicFilter(term);
        return values.stream()
                .filter(o -> filter.test(o.getName()) || filter.test(o.getSchema()) || filter.test(o.getCatalog()) || filter.test(o.getRemarks()))
                .sorted()
                .collect(Collectors.toList());
    }

    private static List<JdbcColumn> getJdbcColumns(List<JdbcColumn> values, String term) {
        Predicate<String> filter = ExtAutoCompletionSource.basicFilter(term);
        return values.stream()
                .filter(o -> filter.test(o.getName()) || filter.test(o.getLabel()) || filter.test(o.getTypeName()))
                .sorted(Comparator.comparing(JdbcColumn::getName))
                .collect(Collectors.toList());
    }

    private static <K, V> ConcurrentMap<K, V> cacheTtl(long duration, TimeUnit unit) {
        return CacheBuilder.newBuilder().expireAfterWrite(duration, unit).<K, V>build().asMap();
    }
    //</editor-fold>
}
