/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ui.view.tsprocessing;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;

/**
 *
 * @author pcuser
 */
public interface ItemUI<H, I> {

    JComponent getView(H host, I information);
    boolean fillMenu(JMenu menu, H host, I information);
    Icon getIcon(H host, I information);
}
