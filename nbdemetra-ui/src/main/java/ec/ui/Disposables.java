/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui;

import ec.tstoolkit.design.UtilityClass;
import ec.ui.interfaces.IDisposable;
import java.awt.Component;
import java.awt.Container;

/**
 *
 * @author Philippe Charles
 */
@UtilityClass(IDisposable.class)
public final class Disposables {

    private Disposables() {
        // static class
    }

    public static <C extends Container> C disposeAndRemoveAll(C c) {
        disposeAll(c.getComponents());
        c.removeAll();
        return c;
    }

    public static void disposeAll(Component... list) {
        for (Component o : list) {
            dispose(o);
        }
    }

    public static void dispose(Component c) {
        if (c instanceof IDisposable) {
            ((IDisposable) c).dispose();
        }
    }
}
