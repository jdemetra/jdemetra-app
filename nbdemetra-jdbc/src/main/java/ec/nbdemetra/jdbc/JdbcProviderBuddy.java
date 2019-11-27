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
import ec.nbdemetra.db.DbColumnListCellRenderer;
import ec.nbdemetra.db.DbIcon;
import ec.nbdemetra.db.DbProviderBuddy;
import ec.nbdemetra.ui.awt.SimpleHtmlListCellRenderer;
import ec.tss.tsproviders.jdbc.ConnectionSupplier;
import ec.tss.tsproviders.jdbc.JdbcBean;
import ec.tstoolkit.utilities.GuavaCaches;
import ec.util.completion.AutoCompletionSource;
import static ec.util.completion.AutoCompletionSource.Behavior.ASYNC;
import static ec.util.completion.AutoCompletionSource.Behavior.NONE;
import static ec.util.completion.AutoCompletionSource.Behavior.SYNC;
import ec.util.completion.ExtAutoCompletionSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.swing.Icon;
import org.checkerframework.checker.nullness.qual.NonNull;
import javax.swing.ListCellRenderer;
import nbbrd.sql.jdbc.SqlColumn;
import nbbrd.sql.jdbc.SqlIdentifierQuoter;
import nbbrd.sql.jdbc.SqlTable;

/**
 * An abstract provider buddy that targets Jdbc providers.
 *
 * @author Philippe Charles
 * @param <BEAN>
 */
public abstract class JdbcProviderBuddy<BEAN extends JdbcBean> extends DbProviderBuddy<BEAN> {

    protected final ConnectionSupplier supplier;

    public JdbcProviderBuddy(@NonNull ConnectionSupplier supplier) {
        this.supplier = supplier;
    }

    @Override
    protected AutoCompletionSource getTableSource(BEAN bean) {
        return ExtAutoCompletionSource
                .builder(o -> getSqlTables(supplier, bean))
                .behavior(o -> !Strings.isNullOrEmpty(bean.getDbName()) ? ASYNC : NONE)
                .postProcessor(JdbcProviderBuddy::getSqlTables)
                .valueToString(SqlTable::getName)
                .cache(GuavaCaches.ttlCacheAsMap(Duration.ofMinutes(1)), o -> bean.getDbName(), SYNC)
                .build();
    }

    @Override
    protected ListCellRenderer getTableRenderer(BEAN bean) {
        return new SqlTableRenderer();
    }

    @Override
    protected AutoCompletionSource getColumnSource(BEAN bean) {
        return ExtAutoCompletionSource
                .builder(o -> getSqlColumns(supplier, bean))
                .behavior(o -> !Strings.isNullOrEmpty(bean.getDbName()) && !Strings.isNullOrEmpty(bean.getTableName()) ? ASYNC : NONE)
                .postProcessor(JdbcProviderBuddy::getSqlColumns)
                .valueToString(SqlColumn::getName)
                .cache(GuavaCaches.ttlCacheAsMap(Duration.ofMinutes(1)), o -> bean.getDbName() + "/" + bean.getTableName(), SYNC)
                .build();
    }

    @Override
    protected ListCellRenderer getColumnRenderer(BEAN bean) {
        return new SqlColumnRenderer();
    }

    //<editor-fold defaultstate="collapsed" desc="Internal implementation">
    private static final class SqlTableRenderer extends SimpleHtmlListCellRenderer<SqlTable> {

        public SqlTableRenderer() {
            super(o -> "<html><b>" + o.getName() + "</b> - <i>" + o.getType() + "</i>");
        }
    }

    private static final class SqlColumnRenderer extends DbColumnListCellRenderer<SqlColumn> {

        @Override
        protected String getName(SqlColumn value) {
            return value.getName();
        }

        @Override
        protected String getTypeName(SqlColumn value) {
            return value.getTypeName();
        }

        @Override
        protected Icon getTypeIcon(SqlColumn value) {
            switch (value.getType()) {
                case Types.BIGINT:
                case Types.DECIMAL:
                case Types.INTEGER:
                case Types.NUMERIC:
                case Types.SMALLINT:
                case Types.TINYINT:
                    return DbIcon.DATA_TYPE_INTEGER;
                case Types.DOUBLE:
                case Types.FLOAT:
                case Types.REAL:
                    return DbIcon.DATA_TYPE_DOUBLE;
                case Types.BINARY:
                case Types.BLOB:
                case Types.CLOB:
                case Types.JAVA_OBJECT:
                case Types.LONGVARBINARY:
                case Types.NCLOB:
                case Types.VARBINARY:
                    return DbIcon.DATA_TYPE_BINARY;
                case Types.CHAR:
                case Types.LONGNVARCHAR:
                case Types.LONGVARCHAR:
                case Types.NCHAR:
                case Types.NVARCHAR:
                case Types.VARCHAR:
                    return DbIcon.DATA_TYPE_STRING;
                case Types.DATE:
                case Types.TIME:
                case Types.TIMESTAMP:
                    return DbIcon.DATA_TYPE_DATETIME;
                case Types.BIT:
                case Types.BOOLEAN:
                    return DbIcon.DATA_TYPE_BOOLEAN;
                case Types.ARRAY:
                case Types.DATALINK:
                case Types.DISTINCT:
                case Types.NULL:
                case Types.OTHER:
                case Types.REF:
                case Types.ROWID:
                case Types.SQLXML:
                case Types.STRUCT:
                    return DbIcon.DATA_TYPE_NULL;
            }
            return null;
        }
    }

    private static <T extends JdbcBean> List<SqlTable> getSqlTables(ConnectionSupplier supplier, T bean) throws SQLException {
        try (Connection c = supplier.getConnection(bean)) {
            return SqlTable.allOf(c.getMetaData(), c.getCatalog(), c.getSchema(), "%", new String[]{"TABLE", "VIEW"});
        }
    }

    private static <T extends JdbcBean> List<SqlColumn> getSqlColumns(ConnectionSupplier supplier, T bean) throws SQLException {
        try (Connection c = supplier.getConnection(bean)) {
            SqlIdentifierQuoter quoter = SqlIdentifierQuoter.of(c.getMetaData());
            try (Statement st = c.createStatement()) {
                try (ResultSet rs = st.executeQuery("select * from " + quoter.quote(bean.getTableName(), false) + " where 1 = 0")) {
                    return SqlColumn.allOf(rs.getMetaData());
                }
            }
        }
    }

    private static List<SqlTable> getSqlTables(List<SqlTable> values, String term) {
        Predicate<String> filter = ExtAutoCompletionSource.basicFilter(term);
        return values.stream()
                .filter(o -> filter.test(o.getName()) || filter.test(o.getSchema()) || filter.test(o.getCatalog()) || filter.test(o.getRemarks()))
                .sorted(Comparator
                        .comparing(SqlTable::getType)
                        .thenComparing(SqlTable::getCatalog)
                        .thenComparing(SqlTable::getSchema)
                        .thenComparing(SqlTable::getName)
                )
                .collect(Collectors.toList());
    }

    private static List<SqlColumn> getSqlColumns(List<SqlColumn> values, String term) {
        Predicate<String> filter = ExtAutoCompletionSource.basicFilter(term);
        return values.stream()
                .filter(o -> filter.test(o.getName()) || filter.test(o.getLabel()) || filter.test(o.getTypeName()))
                .sorted(Comparator.comparing(SqlColumn::getName))
                .collect(Collectors.toList());
    }
    //</editor-fold>
}
