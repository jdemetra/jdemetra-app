/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.util;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
}
