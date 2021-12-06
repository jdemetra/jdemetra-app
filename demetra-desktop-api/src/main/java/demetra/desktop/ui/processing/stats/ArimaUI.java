/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.processing.stats;

import demetra.desktop.components.tools.JArimaView;
import demetra.desktop.ui.processing.ItemUI;
import java.util.Map;
import javax.swing.JComponent;
import jdplus.arima.IArimaModel;

/**
 *
 * @author Jean Palate
 */
public class ArimaUI implements ItemUI<Map<String, IArimaModel>> {

    @Override
    public JComponent getView(Map<String, IArimaModel> information) {
        JArimaView arimaView = new JArimaView(information);
        return arimaView;
    }
}
