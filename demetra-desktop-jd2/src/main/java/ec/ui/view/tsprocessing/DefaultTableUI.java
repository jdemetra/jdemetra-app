/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tstoolkit.algorithm.IProcDocument;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate
 */
public class DefaultTableUI<D extends IProcDocument<?,?,?> >extends DefaultItemUI<IProcDocumentView<D>, ec.tss.TsCollection> {


    public DefaultTableUI(){
     }


    @Override
    public JComponent getView(IProcDocumentView<D> host, ec.tss.TsCollection rslts) {

        return TsViewToolkit.getGrid(rslts);
    }

 }
