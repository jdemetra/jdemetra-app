/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.anomalydetection.demo;

import ec.nbdemetra.anomalydetection.ui.OutliersTopComponent;
import ec.util.various.swing.BasicSwingLauncher;

/**
 *
 * @author maggima
 */
public final class OutliersTopComponentDemo {

    public static void main(String[] args) {
        new BasicSwingLauncher()
                .content(OutliersTopComponent.class)
                .launch();
    }
}