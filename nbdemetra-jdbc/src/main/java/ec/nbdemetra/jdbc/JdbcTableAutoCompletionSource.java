/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.jdbc;

import com.google.common.base.Strings;
import com.google.common.collect.Ordering;
import ec.tss.tsproviders.jdbc.JdbcBean;
import ec.tss.tsproviders.jdbc.ConnectionSupplier;
import ec.util.jdbc.JdbcTable;
import java.sql.Connection;

/**
 *
 * @author Philippe Charles
 */
public class JdbcTableAutoCompletionSource extends JdbcAutoCompletionSource<JdbcTable> {

    public JdbcTableAutoCompletionSource(ConnectionSupplier supplier, JdbcBean bean) {
        super(supplier, bean);
    }

    @Override
    public Behavior getBehavior(String term) {
        return !Strings.isNullOrEmpty(bean.getDbName()) ? Behavior.ASYNC : Behavior.NONE;
    }

    @Override
    protected String getValueAsString(JdbcTable value) {
        return value.getName();
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
