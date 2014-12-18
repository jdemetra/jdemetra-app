/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tss.TsCollection;
import ec.tstoolkit.algorithm.IProcDocument;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate
 */
public class DefaultChartUI<D extends IProcDocument<?,?,?> >extends DefaultItemUI<IProcDocumentView<D>, TsCollection> {


    public DefaultChartUI(){
     }


    @Override
    public JComponent getView(IProcDocumentView<D> host, TsCollection rslts) {

        return host.getToolkit().getGrid(rslts);
    }

 }

