/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.jdbc;

import ec.nbdemetra.db.DbColumnListCellRenderer;
import ec.nbdemetra.db.DbIcon;
import ec.util.jdbc.JdbcColumn;
import java.sql.Types;
import javax.swing.Icon;

/**
 *
 * @author Philippe Charles
 */
public class JdbcColumnListCellRenderer extends DbColumnListCellRenderer<JdbcColumn> {

    @Override
    protected String getName(JdbcColumn value) {
        return value.getName();
    }

    @Override
    protected String getTypeName(JdbcColumn value) {
        return value.getTypeName();
    }

    @Override
    protected Icon getTypeIcon(JdbcColumn value) {
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
