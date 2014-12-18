/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tss.Ts;
import ec.tss.TsStatus;
import ec.tss.documents.DocumentManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JComponent;
import ec.tstoolkit.algorithm.IProcDocument;
import ec.tstoolkit.algorithm.IProcResults;

/**
 *
 * @author Jean Palate
 */
public class GenericTableUI<D extends IProcDocument<?,?,?> >extends DefaultItemUI<IProcDocumentView<D>, IProcResults> {

    private final List<String> names_;
    private final boolean full_;

    public GenericTableUI(boolean fullNames, String...names){
        names_= Arrays.asList(names);
        full_=fullNames;
    }


    @Override
    public JComponent getView(IProcDocumentView<D> host, IProcResults rslts) {

        List<Ts> items=new ArrayList<>();
        for (String s : names_){
            Ts x=DocumentManager.instance.getTs(host.getDocument(), s, full_); 
            if (x != null && x.hasData()== TsStatus.Valid)
                items.add(x);
        }
        return host.getToolkit().getGrid(items);
    }

 }
