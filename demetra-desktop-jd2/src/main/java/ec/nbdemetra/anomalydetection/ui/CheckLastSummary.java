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
package ec.nbdemetra.anomalydetection.ui;

import ec.nbdemetra.anomalydetection.AnomalyItem;
import ec.nbdemetra.anomalydetection.html.HtmlCheckLast;
import ec.tstoolkit.modelling.arima.PreprocessingModel;
import ec.ui.Disposables;
import ec.ui.interfaces.IDisposable;
import ec.ui.view.tsprocessing.ITsViewToolkit;
import ec.ui.view.tsprocessing.TsViewToolkit;
import java.awt.BorderLayout;
import javax.swing.Box;
import javax.swing.JComponent;

/**
 * Component containing all Html content regarding Check Last report of a Ts
 *
 * @author Mats Maggi
 */
public class CheckLastSummary extends JComponent implements IDisposable {

    private ITsViewToolkit toolkit_ = TsViewToolkit.getInstance();
    private final Box document_;

    public CheckLastSummary() {
        setLayout(new BorderLayout());
        document_ = Box.createHorizontalBox();
        add(document_, BorderLayout.CENTER);
    }

    public void set(AnomalyItem item, PreprocessingModel m) {
        Disposables.disposeAndRemoveAll(document_);
        if (item != null) {
            HtmlCheckLast document = new HtmlCheckLast(item, m);
            Disposables.disposeAndRemoveAll(document_).add(toolkit_.getHtmlViewer(document));
        }
        document_.revalidate();
        revalidate();
    }

    public void setTsToolkit(ITsViewToolkit toolkit) {
        toolkit_ = toolkit;
    }

    public ITsViewToolkit getTsToolkit() {
        return toolkit_;
    }

    @Override
    public void dispose() {
        Disposables.disposeAndRemoveAll(document_);
    }
}
