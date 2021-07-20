/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.ui.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.image.BufferedImage;
import javax.swing.CellRendererPane;

/**
 *
 * @author Philippe Charles
 */
public final class TransferHandlers {

    private TransferHandlers() {
        // static class
    }

    // http://stackoverflow.com/a/4154510
    public static BufferedImage paintComponent(Component c) {

        // Set it to it's preferred size. (optional)
        c.setSize(c.getPreferredSize());
        layoutComponent(c);

        BufferedImage img = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);

        CellRendererPane crp = new CellRendererPane();
        crp.add(c);
        crp.paintComponent(img.createGraphics(), c, crp, c.getBounds());
        return img;
    }

    private static void layoutComponent(Component c) {
        synchronized (c.getTreeLock()) {
            c.doLayout();
            if (c instanceof Container) {
                for (Component child : ((Container) c).getComponents()) {
                    layoutComponent(child);
                }
            }
        }
    }
}
