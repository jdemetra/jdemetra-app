/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.satoolkit.GenericSaProcessingFactory;
import ec.satoolkit.benchmarking.SaBenchmarkingResults;
import ec.tss.Ts;
import ec.tss.documents.DocumentManager;
import ec.tstoolkit.information.InformationSet;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate
 */
public class BenchmarkingUI<V extends IProcDocumentView<?>> extends DefaultItemUI<V, Boolean> {
    
    @Override
    public JComponent getView(V host, Boolean information) {
        if (information == null) {
            return null;
        }
        BenchmarkingView view = new BenchmarkingView();
        view.setTsToolkit(host.getToolkit());
        Ts tsb=DocumentManager.instance.getTs(host.getDocument(), InformationSet.item(GenericSaProcessingFactory.BENCHMARKING, SaBenchmarkingResults.BENCHMARKED));
        Ts tsa=DocumentManager.instance.getTs(host.getDocument(), InformationSet.item(GenericSaProcessingFactory.BENCHMARKING, SaBenchmarkingResults.ORIGINAL));
  
        if (tsb == null || tsa == null)
            return null;
        view.set(tsb, tsa, information);
        return view;
    }
}
