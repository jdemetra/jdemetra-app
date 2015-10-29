/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import javax.swing.Icon;
import javax.swing.JMenu;

/**
 *
 * @author Jean Palate
 */
public abstract class DefaultItemUI<H, I> implements ItemUI<H, I> {

    @Override
    public boolean fillMenu(JMenu menu, H host, I information) {
        return false;
    }

    @Override
    public Icon getIcon(H host, I information) {
        return null;
    }
}
