/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.jdbc;

import com.google.common.base.Strings;
import ec.tss.tsproviders.jdbc.JdbcBean;
import ec.tss.tsproviders.jdbc.ConnectionSupplier;
import ec.util.jdbc.JdbcColumn;
import ec.util.jdbc.SqlIdentifierQuoter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Philippe Charles
 */
public class JdbcColumnAutoCompletionSource extends JdbcAutoCompletionSource<JdbcColumn> {

    public JdbcColumnAutoCompletionSource(ConnectionSupplier supplier, JdbcBean bean) {
        super(supplier, bean);
    }

    @Override
    public Behavior getBehavior(String term) {
        return !Strings.isNullOrEmpty(bean.getDbName()) && !Strings.isNullOrEmpty(bean.getTableName())
                ? Behavior.ASYNC
                : Behavior.NONE;
    }

    @Override
    protected String getValueAsString(JdbcColumn value) {
        return value.getName();
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
