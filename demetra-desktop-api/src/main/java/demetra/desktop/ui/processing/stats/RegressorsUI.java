/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.processing.stats;

import demetra.desktop.ui.processing.ItemUI;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.desktop.ui.processing.TsViewToolkit;
import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsData;
import demetra.timeseries.TsDomain;
import demetra.timeseries.TsPeriod;
import demetra.timeseries.regression.ITsVariable;
import demetra.timeseries.regression.Variable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import jdplus.data.DataBlock;
import jdplus.math.matrices.FastMatrix;
import jdplus.modelling.regression.Regression;
import jdplus.regsarima.regular.RegSarimaModel;

/**
 *
 * @author Jean Palate
 * @param <V>
 */
public class RegressorsUI<V extends IProcDocumentView<?>> implements ItemUI<RegSarimaModel> {

    @Override
    public JComponent getView(RegSarimaModel information) {
        TsCollection items = createRegressors(information);
        return TsViewToolkit.getGrid(items);
    }

    private TsCollection createRegressors(RegSarimaModel information) {
        List<Ts> collection = new ArrayList<>();
        TsDomain domain = information.getDescription().getSeries().getDomain();
        TsPeriod start = domain.getStartPeriod();
        int n = domain.getLength();
        Variable[] vars = information.getDescription().getVariables();
        if (vars != null) {
            for (Variable cur : vars) {
                ITsVariable core = cur.getCore();
                int dim = cur.dim();
                if (dim > 1) {
                    FastMatrix matrix = Regression.matrix(domain, core);
                    for (int j = 0; j < dim; ++j) {
                        collection.add(Ts
                                .builder()
                                .name(core.description(j, domain))
                                .data(TsData.of(start, matrix.column(j)))
                                .build());
                    }
                }else{
                    DataBlock x = Regression.x(domain, core);
                    collection.add(Ts
                                .builder()
                                .name(cur.getName())
                                .data(TsData.ofInternal(start, x.getStorage()))
                                .build());
                }
            }
        }
        return TsCollection.of(collection);
    }
}
