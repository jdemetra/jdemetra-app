/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import demetra.bridge.TsConverter;
import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsData;
import ec.tstoolkit.data.DataBlock;
import ec.tstoolkit.modelling.arima.PreprocessingModel;
import ec.tstoolkit.timeseries.regression.ITsVariable;
import ec.tstoolkit.timeseries.regression.TsVariableList;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import java.util.ArrayList;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate
 */
public class RegressorsUI<V extends IProcDocumentView<?>> extends DefaultItemUI<V, PreprocessingModel> {

    @Override
    public JComponent getView(V host, PreprocessingModel information) {
        TsCollection items = createRegressors(information);
        return host.getToolkit().getGrid(TsConverter.fromTsCollection(items));
    }

    private TsCollection createRegressors(PreprocessingModel information) {
        TsCollection.Builder collection = TsCollection.builder();
        TsDomain edomain = information.description.getSeriesDomain();
        TsPeriod start = edomain.getStart();
        int n = edomain.getLength();
        TsVariableList list = information.description.buildRegressionVariables();
        ITsVariable[] vars = list.items();
        if (vars != null) {
            for (int i = 0; i < vars.length; ++i) {
                ITsVariable cur = vars[i];
                int dim = cur.getDim();
                ArrayList<DataBlock> tmp = new ArrayList<>();
                for (int j = 0; j < dim; ++j) {
                    tmp.add(new DataBlock(n));
                }
                cur.data(edomain, tmp);
                for (int j = 0; j < dim; ++j) {
                    collection.data(Ts
                            .builder()
                            .name(cur.getItemDescription(j, information.getFrequency()))
                            .data(TsData.ofInternal(TsConverter.toTsPeriod(start), tmp.get(j).getData()))
                            .build());
                }
            }
        }
        return collection.build();
    }
}
