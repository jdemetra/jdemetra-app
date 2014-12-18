/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.util.various.swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

/**
 * Useful class to deal with popups.
 *
 * @author Philippe Charles
 */
public abstract class PopupMouseAdapter extends MouseAdapter {

    @Override
    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    protected void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            showPopup(e);
        }
    }

    protected abstract void showPopup(MouseEvent e);

    public static PopupMouseAdapter fromMenu(JMenu menu) {
        return fromPopupMenu(menu.getPopupMenu());
    }

    public static PopupMouseAdapter fromPopupMenu(final JPopupMenu popup) {
        return new PopupMouseAdapter() {
            @Override
            protected void showPopup(MouseEvent e) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        };
    }
}
