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
public class JdbcTableListCellRenderer extends SimpleHtmlListCellRenderer<JdbcTable> {

    public JdbcTableListCellRenderer() {
        super(new HtmlProvider<JdbcTable>() {
            @Override
            public String getHtmlDisplayName(JdbcTable value) {
                return "<html><b>" + value.getName() + "</b> - <i>" + value.getType() + "</i>";
            }
        });
    }
}
