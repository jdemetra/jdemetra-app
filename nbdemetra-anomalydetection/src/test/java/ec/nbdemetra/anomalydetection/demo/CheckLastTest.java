/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.anomalydetection.demo;

import ec.nbdemetra.anomalydetection.ui.CheckLastBatchUI;
import ec.util.various.swing.BasicSwingLauncher;
import javax.swing.JPanel;

/**
 *
 * @author maggima
 */
public class CheckLastTest extends JPanel {
    
    public static void main(String[] args) {

        new BasicSwingLauncher()
                .content(CheckLastBatchUI.class)
                .launch();
    }
}