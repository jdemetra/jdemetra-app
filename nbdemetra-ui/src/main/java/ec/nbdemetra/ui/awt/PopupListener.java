/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.awt;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

/**
 * Useful class to deal with popups.
 *
 * @author Philippe Charles
 */
public abstract class PopupListener extends MouseAdapter {

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
            JPopupMenu popup = getPopup(e);
            if (popup != null) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    /**
     * Get the popup to be shown. A null value won't show anything.
     *
     * @param e
     * @return
     */
    protected abstract JPopupMenu getPopup(MouseEvent e);

    /**
     *
     * @deprecated use
     * {@link JComponent#setComponentPopupMenu(javax.swing.JPopupMenu)} instead
     */
    @Deprecated
    public static class PopupAdapter extends PopupListener {

        final JPopupMenu menu;

        public PopupAdapter(JPopupMenu menu) {
            this.menu = menu;
        }

        @Override
        protected JPopupMenu getPopup(MouseEvent e) {
            return menu;
        }
    }
}
