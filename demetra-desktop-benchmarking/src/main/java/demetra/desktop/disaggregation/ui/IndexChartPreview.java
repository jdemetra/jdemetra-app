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
package demetra.desktop.disaggregation.ui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author Jean
 */
public class IndexChartPreview extends DefaultItemUI<IProcDocumentView<TsDisaggregationModelDocument>, DisaggregationResults> {

    public IndexChartPreview() {
    }

    @Override
    public JComponent getView(IProcDocumentView<TsDisaggregationModelDocument> host, DisaggregationResults rslts) {

        List<Ts> items = new ArrayList<>();
        Ts[] ts = host.getDocument().getTs();
        int year = ts[0].getTsData().getStart().getYear();
        for (int i = 1; i < ts.length; ++i) {
            int nyear = ts[i].getTsData().getStart().getYear();
            if (nyear > year) {
                year = nyear;
            }
        }
        TsFrequency freq = ts[0].getTsData().getFrequency();
        for (int i = 0; i < ts.length; ++i) {
            TsPeriod ref = new TsPeriod(freq, year, 0);
            TsData s = ts[i].getTsData().index(ref, 100);
            if (s != null) {
                items.add(TsFactory.instance.createTs(ts[i].getName(), null, s));
            }
        }

        return host.getToolkit().getChart(items);
    }
}
