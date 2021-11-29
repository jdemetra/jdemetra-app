/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.processing;

import demetra.desktop.TsDynamicProvider;
import demetra.information.Explorable;
import demetra.processing.ProcDocument;
import demetra.timeseries.Ts;
import demetra.timeseries.TsData;
import demetra.timeseries.TsDocument;
import demetra.timeseries.TsFactory;
import demetra.timeseries.TsInformationType;
import demetra.timeseries.TsMoniker;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author Jean
 */
public class GenericGrowthChartUI<D extends ProcDocument<?,?,?> >extends DefaultItemUI<IProcDocumentView<D>, TsDocument> {

    private final List<String> names_;
    private final boolean full_;

    public GenericGrowthChartUI(boolean fullNames, String...names){
        names_= Arrays.asList(names);
        full_=fullNames;
    }


    @Override
    public JComponent getView(IProcDocumentView<D> host, TsDocument doc) {

        List<Ts> items=new ArrayList<>();
        for (String s : names_){
            TsMoniker moniker = TsDynamicProvider.monikerOf(doc, s);
            Ts x = TsFactory.getDefault().makeTs(moniker, TsInformationType.All); 
            items.add(x);
        }
        return TsViewToolkit.getChart(items);
    }

 }
