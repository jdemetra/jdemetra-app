/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tss.TsCollection;
import ec.tss.TsFactory;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.utilities.NamedObject;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate
 */
public class TableUI< V extends IProcDocumentView<?>>  extends DefaultItemUI<V, List<NamedObject<TsData>>> {

    public TableUI() {
    }

    @Override
    public JComponent getView(V host, List<NamedObject<TsData>> document) {

        TsCollection items = TsFactory.instance.createTsCollection();
        for (NamedObject<TsData> item : document) {
            items.add(TsFactory.instance.createTs(item.name, null, item.object));
        }
        return host.getToolkit().getGrid(items.clean(true));
    }
}
