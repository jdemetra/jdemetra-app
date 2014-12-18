/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tss.TsCollection;
import ec.tss.TsFactory;
import ec.tss.documents.DocumentManager;
import ec.tss.documents.TsDocument;
import ec.ui.chart.JTsDualChart;
import ec.ui.interfaces.ITsCollectionView.TsUpdateMode;

/**
 *
 * @author Jean Palate
 */
public class DualChartUI<D extends TsDocument<?, ?>> extends PooledItemUI<IProcDocumentView<D>, String[][], JTsDualChart> {

    public DualChartUI() {
        super(JTsDualChart.class);
    }

    @Override
    protected void init(JTsDualChart c, IProcDocumentView<D> host, String[][] information) {
        String[] hnames = information[0], lnames = information[1];
        TsCollection items = TsFactory.instance.createTsCollection();
        if (hnames != null) {
            for (int i = 0; i < hnames.length; ++i) {
                items.quietAdd(DocumentManager.instance.getTs(host.getDocument(), hnames[i]));
            }
        }
        if (lnames != null) {
            for (int i = 0; i < lnames.length; ++i) {
                items.quietAdd(DocumentManager.instance.getTs(host.getDocument(), lnames[i]));
            }
        }
        c.getTsCollection().quietAppend(items);
        c.setTsUpdateMode(TsUpdateMode.None);
        int i = 0;
        if (hnames != null) {
            for (; i < hnames.length; ++i) {
                c.setTsLevel(i, false);
            }
        }
        if (lnames != null) {
            for (int j = 0; j < lnames.length; ++j, ++i) {
                c.setTsLevel(i, true);
            }
        }
    }
}
