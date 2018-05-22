/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.anomalydetection.demo;

import ec.nbdemetra.anomalydetection.ui.CheckLastBatchUI;
import ec.util.various.swing.BasicSwingLauncher;

/**
 *
 * @author maggima
 */
public final class CheckLastTest {

    public static void main(String[] args) {
        new BasicSwingLauncher()
                .content(CheckLastBatchUI::new)
                .launch();
    }
}
