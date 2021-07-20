/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.ui.view.ArimaView;
import ec.tstoolkit.arima.IArimaModel;
import java.util.LinkedHashMap;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate
 */
public class ArimaUI<V extends IProcDocumentView<?>> extends DefaultItemUI<V, LinkedHashMap<String, IArimaModel>> {

    @Override
    public JComponent getView(V host, LinkedHashMap<String, IArimaModel> information) {
        ArimaView arimaView = new ArimaView(information);
        return arimaView;
    }
}
