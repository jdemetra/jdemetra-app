/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.jdbc;

import ec.nbdemetra.ui.awt.SimpleHtmlListCellRenderer;
import ec.util.jdbc.JdbcTable;

/**
 *
 * @author Philippe Charles
 */
@Deprecated
public class JdbcTableListCellRenderer extends SimpleHtmlListCellRenderer<JdbcTable> {

    public JdbcTableListCellRenderer() {
        super(o -> "<html><b>" + o.getName() + "</b> - <i>" + o.getType() + "</i>");
    }
}
