/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sql;

import demetra.desktop.util.SimpleHtmlCellRenderer;
import javax.swing.ListCellRenderer;
import nbbrd.design.DirectImpl;
import nbbrd.sql.jdbc.SqlTable;

/**
 *
 * @author Philippe Charles
 */
@DirectImpl
public final class SqlTableListCellRenderer implements ListCellRenderer<SqlTable> {

    @lombok.experimental.Delegate
    private final ListCellRenderer<SqlTable> delegate
            = new SimpleHtmlCellRenderer<>(SqlTableListCellRenderer::toHtml);

    private static String toHtml(SqlTable o) {
        return "<html><b>" + o.getName() + "</b> - <i>" + o.getType() + "</i>";
    }
}
