/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package demetra.desktop.ui.processing;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;

/**
 *
 * @author Jean Palate
 * @param <I>
 */
public interface ItemUI<I> {

    JComponent getView(I document);
    default boolean fillMenu(JMenu menu, I info){return false;}
    default Icon getIcon(I info){return null;}
}
