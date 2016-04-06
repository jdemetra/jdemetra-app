/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.ui.view.tsprocessing;

import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.timeseries.analysis.MovingProcessing;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.ui.view.StabilityView;
import ec.ui.view.tsprocessing.sa.SaDocumentViewFactory;
import java.util.Map;

/**
 *
 * @author Mats Maggi
 */
public class StabilityUI<V extends IProcDocumentView<?>> extends PooledItemUI<V, MovingProcessing, StabilityView> {

    private String node;
    private final String[] items;

    public StabilityUI(String node, String[] items) {
        super(StabilityView.class);
        this.node = node;
        this.items = items;
    }

    @Override
    protected void init(StabilityView c, V host, MovingProcessing information) {
        c.reset();
        boolean empty = true;
        for (int i = 0; i < items.length; i++) {
            Map<TsDomain, Double> movingInfo = information.movingInfo(items[i]);
            if (isDefined(movingInfo)) {
                empty = false;
                c.add(items[i], movingInfo, null, false);
            }
        }
        if (empty) {
            switch (node) {
                case SaDocumentViewFactory.TRADINGDAYS:
                    c.showException("No information available on trading days !");
                    break;
                case SaDocumentViewFactory.EASTER:
                    c.showException("No information available on easter effects !");
                    break;
                case SaDocumentViewFactory.ARIMA:
                    c.showException("No information available !");
            }
        } else {
            c.display();
        }
    }

    private boolean isDefined(Map<TsDomain, Double> data) {
        if (data == null || data.isEmpty()) {
            return false;
        }

        for (Map.Entry<TsDomain, Double> d : data.entrySet()) {
            if (Double.isFinite(d.getValue())) {
                return true;
            }
        }
        return false;
    }
}
