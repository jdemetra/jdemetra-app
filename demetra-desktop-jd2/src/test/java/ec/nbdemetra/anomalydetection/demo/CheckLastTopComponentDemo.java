/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.anomalydetection.demo;

import ec.nbdemetra.anomalydetection.ui.CheckLastTopComponent;
import ec.util.various.swing.BasicSwingLauncher;

/**
 *
 * @author maggima
 */
public final class CheckLastTopComponentDemo {

    public static void main(String[] args) {
        new BasicSwingLauncher()
                .content(CheckLastTopComponent::new)
                .launch();
    }
}
