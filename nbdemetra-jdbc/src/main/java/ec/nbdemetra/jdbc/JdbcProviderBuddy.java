/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.jdbc;

import ec.nbdemetra.db.DbProviderBuddy;
import ec.tss.tsproviders.jdbc.ConnectionSupplier;
import ec.tss.tsproviders.jdbc.JdbcBean;
import ec.util.completion.AutoCompletionSource;
import javax.annotation.Nonnull;
import javax.swing.ListCellRenderer;

/**
 * An abstract provider buddy that targets Jdbc providers.
 *
 * @author Philippe Charles
 */
public abstract class JdbcProviderBuddy<BEAN extends JdbcBean> extends DbProviderBuddy<BEAN> {

    protected final ConnectionSupplier supplier;

    public JdbcProviderBuddy(@Nonnull ConnectionSupplier supplier) {
        this.supplier = supplier;
    }

    @Override
    protected AutoCompletionSource getTableSource(BEAN bean) {
        return new JdbcTableAutoCompletionSource(supplier, bean);
    }

    @Override
    protected ListCellRenderer getTableRenderer(BEAN bean) {
        return new JdbcTableListCellRenderer();
    }

    @Override
    protected AutoCompletionSource getColumnSource(BEAN bean) {
        return new JdbcColumnAutoCompletionSource(supplier, bean);
    }

    @Override
    protected ListCellRenderer getColumnRenderer(BEAN bean) {
        return new JdbcColumnListCellRenderer();
    }
}
