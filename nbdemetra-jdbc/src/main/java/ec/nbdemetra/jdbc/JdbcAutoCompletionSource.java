/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.jdbc;

import ec.tss.tsproviders.jdbc.JdbcBean;
import ec.tss.tsproviders.jdbc.ConnectionSupplier;
import ec.util.completion.ext.QuickAutoCompletionSource;
import java.sql.Connection;

/**
 *
 * @author Philippe Charles
 * @Deprecated use {@link ec.util.completion.ExtAutoCompletionSource} instead
 */
@Deprecated
public abstract class JdbcAutoCompletionSource<T> extends QuickAutoCompletionSource<T> {

    protected final ConnectionSupplier supplier;
    protected final JdbcBean bean;

    public JdbcAutoCompletionSource(ConnectionSupplier supplier, JdbcBean bean) {
        this.supplier = supplier;
        this.bean = bean;
    }

    @Override
    protected Iterable<T> getAllValues() throws Exception {
        try (Connection c = supplier.getConnection(bean)) {
            return getAllValues(c);
        }
    }

    abstract protected Iterable<T> getAllValues(Connection c) throws Exception;
}
