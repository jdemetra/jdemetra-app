/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import demetra.ui.TsManager;
import ec.tss.TsCollection;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.utilities.NamedObject;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate
 */
public class TableUI< V extends IProcDocumentView<?>> extends DefaultItemUI<V, List<NamedObject<TsData>>> {

    public TableUI() {
    }

    @Override
    public JComponent getView(V host, List<NamedObject<TsData>> document) {
        TsCollection items = document.stream()
                .map(o -> TsManager.getDefault().newTs(o.name, null, o.object))
                .collect(TsManager.getDefault().getTsCollector());
        return host.getToolkit().getGrid(items.clean(true));
    }
}
