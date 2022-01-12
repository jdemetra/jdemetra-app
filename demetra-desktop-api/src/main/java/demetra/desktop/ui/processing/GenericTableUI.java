/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.processing;

import demetra.desktop.TsDynamicProvider;
import demetra.information.BasicInformationExtractor;
import demetra.processing.ProcDocument;
import demetra.timeseries.Ts;
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
 * @author Jean Palate
 * @param <D>
 */
public class GenericTableUI<D extends TsDocument> implements ItemUI<D> {

    private final String[] ids;
    private final boolean fullNames;

    public GenericTableUI(boolean fullNames, String... ids) {
        this.ids = ids.clone();
        this.fullNames = fullNames;
    }

    @Override
    public JComponent getView(D doc) {

        List<Ts> items = new ArrayList<>();
        for (String s : ids) {
            TsMoniker moniker = TsDynamicProvider.monikerOf(doc, s);
            Ts x = TsFactory.getDefault().makeTs(moniker, TsInformationType.All);
            if (!fullNames) {
                int idx = s.lastIndexOf(BasicInformationExtractor.SEP);
                if (idx > 0) {
                    x = x.withName(s.substring(idx + 1));
                }
            }
            items.add(x);
        }
        return TsViewToolkit.getGrid(items);
    }

}
