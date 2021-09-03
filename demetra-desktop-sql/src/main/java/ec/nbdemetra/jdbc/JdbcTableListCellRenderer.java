/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.jdbc;

import demetra.desktop.util.SimpleHtmlListCellRenderer;
import ec.util.jdbc.JdbcTable;

/**
 *
 * @author Philippe Charles
 */
public class JdbcTableListCellRenderer extends SimpleHtmlListCellRenderer<JdbcTable> {

    public JdbcTableListCellRenderer() {
        super(o -> "<html><b>" + o.getName() + "</b> - <i>" + o.getType() + "</i>");
    }
}
