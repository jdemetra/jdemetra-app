/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.modelling;

import demetra.desktop.components.tools.JArimaView;
import demetra.desktop.ui.processing.DefaultItemUI;
import demetra.desktop.ui.processing.IProcDocumentView;
import java.util.Map;
import javax.swing.JComponent;
import jdplus.arima.IArimaModel;

/**
 *
 * @author Jean Palate
 * @param <V>
 */
public class ArimaUI<V extends IProcDocumentView<?>> extends DefaultItemUI<V, Map<String, IArimaModel>> {

    @Override
    public JComponent getView(V host, Map<String, IArimaModel> information) {
        JArimaView arimaView = new JArimaView(information);
        return arimaView;
    }
}
