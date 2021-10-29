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
package demetra.desktop.anomalydetection.ui;

import demetra.desktop.anomalydetection.html.HtmlAnomalyDetection;
import demetra.desktop.core.components.TsViewToolkit;
import demetra.desktop.design.SwingComponent;
import demetra.desktop.interfaces.Disposable;
import demetra.desktop.ui.Disposables;

import javax.swing.*;
import java.awt.*;
import jdplus.regsarima.regular.RegSarimaModel;

/**
 * Component containing all Html contents regarding an Outlier Detection of a Ts
 *
 * @author Mats Maggi
 */
@SwingComponent
public final class JAnomalyDetectionSummary extends JComponent implements Disposable {

    private final Box document_;

    public JAnomalyDetectionSummary() {
        setLayout(new BorderLayout());
        document_ = Box.createHorizontalBox();
        add(document_, BorderLayout.CENTER);
    }

    public void set(RegSarimaModel model) {
        Disposables.disposeAndRemoveAll(document_);
        if (model != null) {
            HtmlAnomalyDetection document = new HtmlAnomalyDetection(model);
            Disposables.disposeAndRemoveAll(document_).add(TsViewToolkit.getHtmlViewer(document));
        }
        document_.revalidate();
        revalidate();
    }

    @Override
    public void dispose() {
        Disposables.disposeAndRemoveAll(document_);
    }
}
