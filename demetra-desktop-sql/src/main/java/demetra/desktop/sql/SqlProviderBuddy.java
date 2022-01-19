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
package demetra.desktop.sql;

import com.google.common.base.Strings;
import ec.util.completion.AutoCompletionSource;
import static ec.util.completion.AutoCompletionSource.Behavior.ASYNC;
import static ec.util.completion.AutoCompletionSource.Behavior.NONE;
import static ec.util.completion.AutoCompletionSource.Behavior.SYNC;
import ec.util.completion.ExtAutoCompletionSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import nbbrd.sql.jdbc.SqlColumn;
import nbbrd.sql.jdbc.SqlConnectionSupplier;
import nbbrd.sql.jdbc.SqlIdentifierQuoter;
import nbbrd.sql.jdbc.SqlTable;

/**
 * An abstract provider buddy that targets Jdbc providers.
 *
 * @author Philippe Charles
 */
@lombok.experimental.UtilityClass
public class SqlProviderBuddy {

    public static AutoCompletionSource getTableSource(SqlConnectionSupplier supplier, Supplier<String> connectionString, Supplier<String> tableName) {
        return ExtAutoCompletionSource
                .builder(o -> getJdbcTables(supplier, connectionString.get()))
                .behavior(o -> !Strings.isNullOrEmpty(connectionString.get()) ? ASYNC : NONE)
                .postProcessor(SqlProviderBuddy::getJdbcTables)
                .valueToString(SqlTable::getName)
                .cache(new ConcurrentHashMap<>(), o -> connectionString.get(), SYNC)
                .build();
    }

    public static AutoCompletionSource getColumnSource(SqlConnectionSupplier supplier, Supplier<String> connectionString, Supplier<String> tableName) {
        return ExtAutoCompletionSource
                .builder(o -> getJdbcColumns(supplier, connectionString.get(), tableName.get()))
                .behavior(o -> !Strings.isNullOrEmpty(connectionString.get()) && !Strings.isNullOrEmpty(tableName.get()) ? ASYNC : NONE)
                .postProcessor(SqlProviderBuddy::getJdbcColumns)
                .valueToString(SqlColumn::getName)
                .cache(new ConcurrentHashMap<>(), o -> connectionString.get() + "/" + tableName.get(), SYNC)
                .build();
    }

    private static List<SqlTable> getJdbcTables(SqlConnectionSupplier supplier, String connectionString) throws SQLException {
        try (Connection c = supplier.getConnection(connectionString)) {
            return SqlTable.allOf(c.getMetaData(), c.getCatalog(), c.getSchema(), "%", new String[]{"TABLE", "VIEW"});
        }
    }

    private static List<SqlColumn> getJdbcColumns(SqlConnectionSupplier supplier, String connectionString, String tableName) throws SQLException {
        try (Connection c = supplier.getConnection(connectionString)) {
            SqlIdentifierQuoter quoter = SqlIdentifierQuoter.of(c.getMetaData());
            try (Statement st = c.createStatement()) {
                try (ResultSet rs = st.executeQuery("select * from " + quoter.quote(tableName, false) + " where 1 = 0")) {
                    return SqlColumn.allOf(rs.getMetaData());
                }
            }
        }
    }

    private static List<SqlTable> getJdbcTables(List<SqlTable> values, String term) {
        Predicate<String> filter = ExtAutoCompletionSource.basicFilter(term);
        return values.stream()
                .filter(o -> filter.test(o.getName()) || filter.test(o.getSchema()) || filter.test(o.getCatalog()) || filter.test(o.getRemarks()))
                .sorted(Comparator.comparing(SqlTable::getName))
                .collect(Collectors.toList());
    }

    private static List<SqlColumn> getJdbcColumns(List<SqlColumn> values, String term) {
        Predicate<String> filter = ExtAutoCompletionSource.basicFilter(term);
        return values.stream()
                .filter(o -> filter.test(o.getName()) || filter.test(o.getLabel()) || filter.test(o.getTypeName()))
                .sorted(Comparator.comparing(SqlColumn::getName))
                .collect(Collectors.toList());
    }
}
