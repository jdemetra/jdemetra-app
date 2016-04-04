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
import com.google.common.collect.Ordering;
import ec.tss.tsproviders.jdbc.JdbcBean;
import ec.tss.tsproviders.jdbc.ConnectionSupplier;
import ec.util.jdbc.JdbcTable;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Philippe Charles
 */
public class JdbcTableAutoCompletionSource extends JdbcAutoCompletionSource<JdbcTable> {

    private final Cache<String, Iterable<JdbcTable>> cache;

    public JdbcTableAutoCompletionSource(ConnectionSupplier supplier, JdbcBean bean) {
        super(supplier, bean);
        this.cache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();
    }

    private String getKey() {
        return bean.getDbName();
    }

    private boolean isValid() {
        return !Strings.isNullOrEmpty(bean.getDbName());
    }

    @Override
    public Request getRequest(String term) {
        String key = getKey();
        Iterable<JdbcTable> result = cache.getIfPresent(key);
        return result != null ? createCachedRequest(term, result) : super.getRequest(term);
    }

    @Override
    public Behavior getBehavior(String term) {
        return isValid() ? Behavior.ASYNC : Behavior.NONE;
    }

    @Override
    protected String getValueAsString(JdbcTable value) {
        return value.getName();
    }

    @Override
    protected Iterable<JdbcTable> getAllValues() throws Exception {
        String key = getKey();
        Iterable<JdbcTable> result = cache.getIfPresent(key);
        if (result == null) {
            result = super.getAllValues();
            cache.put(key, result);
        }
        return result;
    }

    @Override
    protected Iterable<JdbcTable> getAllValues(Connection c) throws Exception {
        return JdbcTable.allOf(c.getMetaData(), c.getCatalog(), c.getSchema(), "%", new String[]{"TABLE", "VIEW"});
    }

    @Override
    protected boolean matches(TermMatcher termMatcher, JdbcTable input) {
        return termMatcher.matches(input.getName())
                || termMatcher.matches(input.getSchema())
                || termMatcher.matches(input.getCatalog())
                || termMatcher.matches(input.getRemarks());
    }

    @Override
    protected Ordering getSorter() {
        return Ordering.natural();
    }
}
