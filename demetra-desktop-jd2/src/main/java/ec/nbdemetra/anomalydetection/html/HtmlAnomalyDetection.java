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

import ec.tss.html.AbstractHtmlElement;
import ec.tss.html.HtmlStream;
import ec.tss.html.HtmlTag;
import ec.tss.html.IHtmlElement;
import ec.tss.html.implementation.HtmlRegArima;
import ec.tstoolkit.modelling.arima.PreprocessingModel;
import java.io.IOException;

/**
 * Html report for the anomaly detection of a Ts
 * @author Mats Maggi
 */
public class HtmlAnomalyDetection extends AbstractHtmlElement implements IHtmlElement {

    private final PreprocessingModel model_;

    public HtmlAnomalyDetection(PreprocessingModel model) {
        this.model_ = model;
    }

    @Override
    public void write(HtmlStream stream) throws IOException {
        stream.write(HtmlTag.HEADER1, "Anomaly Detection").newLine();

        HtmlRegArima reg = new HtmlRegArima(model_, false);
        reg.writeDetails(stream, false);
        
        stream.write(new HtmlOutliers(model_.outliersEstimation(true, false)));
        
    }
}
