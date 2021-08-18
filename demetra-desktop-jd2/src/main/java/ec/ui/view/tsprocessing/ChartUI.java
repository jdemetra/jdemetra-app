/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tss.documents.DocumentManager;
import ec.tstoolkit.algorithm.IProcDocument;
import ec.tstoolkit.algorithm.IProcResults;
import ec.tstoolkit.modelling.SeriesInfo;
import java.util.Arrays;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate
 */
public class ChartUI<D extends IProcDocument<?, ?, ?>> extends DefaultItemUI<IProcDocumentView<D>, IProcResults> {

    private final List<SeriesInfo> items_;
    private final String prefix_;
    private final List<String> names_;
    private boolean clean_ = true;

    public ChartUI(List<SeriesInfo> items, String prefix) {
        items_ = items;
        names_ = null;
        prefix_ = prefix;
    }

    public ChartUI(String... names) {
        names_ = Arrays.asList(names);
        items_ = null;
        prefix_ = null;
    }
    
    public boolean isClean(){
        return clean_;
    }
    
    public void setClean(boolean clean){
        clean_=clean;
    }

    @Override
    public JComponent getView(IProcDocumentView<D> host, IProcResults document) {

        ec.tss.TsCollection items;
        if (items_ != null) {
            items = DocumentManager.create(items_, host.getDocument(), prefix_, false);
        } else {
            items = DocumentManager.create(names_, host.getDocument());
        }
        if (clean_) {
            items = items.clean(true);
        }
        return host.getToolkit().getChart(items);
    }

}
