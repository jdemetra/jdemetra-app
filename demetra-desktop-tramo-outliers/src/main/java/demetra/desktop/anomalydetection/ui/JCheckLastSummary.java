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

import demetra.desktop.design.SwingComponent;

import javax.swing.*;
import java.awt.*;

/**
 * Component containing all Html content regarding Check Last report of a Ts
 *
 * @author Mats Maggi
 */
@SwingComponent
public final class JCheckLastSummary extends JComponent implements IDisposable {

    private final Box document_;

    public JCheckLastSummary() {
        setLayout(new BorderLayout());
        document_ = Box.createHorizontalBox();
        add(document_, BorderLayout.CENTER);
    }

    public void set(AnomalyItem item, PreprocessingModel m) {
        Disposables.disposeAndRemoveAll(document_);
        if (item != null) {
            HtmlCheckLast document = new HtmlCheckLast(item, m);
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
