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
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import ec.tss.tsproviders.jdbc.JdbcBean;
import ec.tss.tsproviders.jdbc.ConnectionSupplier;
import ec.util.jdbc.JdbcColumn;
import ec.util.jdbc.SqlIdentifierQuoter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Philippe Charles
 * @Deprecated use {@link ec.util.completion.ExtAutoCompletionSource} instead
 */
@Deprecated
public class JdbcColumnAutoCompletionSource extends JdbcAutoCompletionSource<JdbcColumn> {

    private final Cache<String, Iterable<JdbcColumn>> cache;

    public JdbcColumnAutoCompletionSource(ConnectionSupplier supplier, JdbcBean bean) {
        super(supplier, bean);
        this.cache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();
    }

    private String getKey() {
        return bean.getDbName() + "/" + bean.getTableName();
    }

    private boolean isValid() {
        return !Strings.isNullOrEmpty(bean.getDbName()) && !Strings.isNullOrEmpty(bean.getTableName());
    }

    @Override
    public Request getRequest(String term) {
        String key = getKey();
        Iterable<JdbcColumn> result = cache.getIfPresent(key);
        return result != null ? createCachedRequest(term, result) : super.getRequest(term);
    }

    @Override
    public Behavior getBehavior(String term) {
        return isValid() ? Behavior.ASYNC : Behavior.NONE;
    }

    @Override
    protected String getValueAsString(JdbcColumn value) {
        return value.getName();
    }

    @Override
    protected Iterable<JdbcColumn> getAllValues() throws Exception {
        String key = getKey();
        Iterable<JdbcColumn> result = cache.getIfPresent(key);
        if (result == null) {
            result = super.getAllValues();
            cache.put(key, result);
        }
        return result;
    }

    @Override
    protected Iterable<JdbcColumn> getAllValues(Connection c) throws SQLException, IOException {
        SqlIdentifierQuoter quoter = SqlIdentifierQuoter.create(c.getMetaData());
        try (Statement st = c.createStatement()) {
            try (ResultSet rs = st.executeQuery("select * from " + quoter.quote(bean.getTableName(), false) + " where 1 = 0")) {
                return JdbcColumn.ofAll(rs.getMetaData());
            }
        }
    }
}
