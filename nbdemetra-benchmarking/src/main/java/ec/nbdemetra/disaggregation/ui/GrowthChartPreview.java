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
package ec.nbdemetra.disaggregation.ui;

import ec.tss.Ts;
import ec.tss.TsFactory;
import ec.tss.disaggregation.documents.DisaggregationResults;
import ec.tss.disaggregation.documents.TsDisaggregationModelDocument;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.ui.view.tsprocessing.DefaultItemUI;
import ec.ui.view.tsprocessing.IProcDocumentView;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author Jean
 */
public class GrowthChartPreview extends DefaultItemUI<IProcDocumentView<TsDisaggregationModelDocument>, DisaggregationResults> {


    public GrowthChartPreview(){
    }


    @Override
    public JComponent getView(IProcDocumentView<TsDisaggregationModelDocument> host, DisaggregationResults rslts) {

        List<Ts> items=new ArrayList<>();
        Ts[] ts = host.getDocument().getTs();
        items.add(ts[0]);
        TsFrequency freq=ts[0].getTsData().getFrequency();
        for (int i=1; i<ts.length; ++i){
            TsData s=ts[i].getTsData().changeFrequency(freq, host.getDocument().getSpecification().getType(), true);
            Ts x=TsFactory.instance.createTs(ts[i].getName(), null, s);
            items.add(x);
        }
        return host.getToolkit().getGrowthChart(items);
    }

 }
