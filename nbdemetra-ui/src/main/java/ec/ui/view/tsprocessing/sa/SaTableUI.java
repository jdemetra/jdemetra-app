/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing.sa;

import ec.tss.TsCollection;
import ec.tss.documents.DocumentManager;
import ec.tss.documents.TsDocument;
import ec.tstoolkit.algorithm.CompositeResults;
import ec.tstoolkit.algorithm.IProcResults;
import ec.tstoolkit.modelling.SeriesInfo;
import ec.ui.view.tsprocessing.DefaultItemUI;
import ec.ui.view.tsprocessing.IProcDocumentView;
import java.util.Arrays;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author pcuser
 */
public class SaTableUI <D extends TsDocument<?,?> >extends DefaultItemUI<IProcDocumentView<D>, IProcResults> {

    private final List<SeriesInfo> items_;
    private final String prefix_;
    private final List<String> names_;
    

    public SaTableUI(List<SeriesInfo> items, String prefix) {
        items_ = items;
        names_ = null;
        prefix_=prefix;
    }

    public SaTableUI(String... names) {
        names_ = Arrays.asList(names);
        items_ = null;
        prefix_=null;
    }

    @Override
    public JComponent getView(IProcDocumentView<D> host, IProcResults document) {

        TsCollection items;
        if (items_ != null) {
            items = DocumentManager.create(items_, host.getDocument(), prefix_, false);
        } else {
            items = DocumentManager.create(names_, host.getDocument());
        }


        return host.getToolkit().getGrid(items.clean(true));
    }
}
