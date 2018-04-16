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
package ec.nbdemetra.anomalydetection.html;

import ec.nbdemetra.anomalydetection.AnomalyItem;
import ec.tss.html.AbstractHtmlElement;
import ec.tss.html.HtmlStream;
import ec.tss.html.HtmlTag;
import ec.tss.html.IHtmlElement;
import ec.tss.html.implementation.HtmlRegArima;
import ec.tstoolkit.modelling.arima.PreprocessingModel;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Html report of a check last processed Ts
 *
 * @author Mats Maggi
 */
public class HtmlCheckLast extends AbstractHtmlElement implements IHtmlElement {

    private AnomalyItem anomalyItem;
    private PreprocessingModel model;
    private DecimalFormat df = new DecimalFormat("0.000");

    public HtmlCheckLast(AnomalyItem a, PreprocessingModel m) {
        anomalyItem = a;
        model = m;
    }

    @Override
    public void write(HtmlStream stream) throws IOException {
        stream.write(HtmlTag.HEADER1, anomalyItem.getTs().getName()).newLine();       

        HtmlRegArima reg = new HtmlRegArima(model, false);
        reg.writeDetails(stream, false);
        
        stream.write(new HtmlOutliers(model.outliersEstimation(true, false)));
    }
}
