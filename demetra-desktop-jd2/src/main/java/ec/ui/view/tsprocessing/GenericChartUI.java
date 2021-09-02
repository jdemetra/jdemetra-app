/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tss.Ts;
import ec.tss.documents.DocumentManager;
import ec.tstoolkit.algorithm.IProcResults;
import ec.tstoolkit.algorithm.IProcDocument;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate
 * @param <D> Document type
 */
public class GenericChartUI<D extends IProcDocument<?,?,?> >extends DefaultItemUI<IProcDocumentView<D>, IProcResults> {

    private final List<String> names_;
    private final boolean full_;

    public GenericChartUI(boolean fullNames, String...names){
        names_= Arrays.asList(names);
        full_=fullNames;
    }


    @Override
    public JComponent getView(IProcDocumentView<D> host, IProcResults rslts) {

        List<Ts> items=new ArrayList<>();
        for (String s : names_){
            Ts x=DocumentManager.instance.getTs(host.getDocument(), s, full_); 
            if (x != null && x.getTsData() != null)
                items.add(x);
        }
        return TsViewToolkit.getChart(items);
    }

 }
