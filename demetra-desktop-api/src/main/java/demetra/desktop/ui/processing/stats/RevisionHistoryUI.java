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
package demetra.desktop.ui.processing.stats;

import demetra.desktop.ui.JRevisionSeriesView;
import demetra.desktop.ui.processing.ItemUI;
import demetra.information.Explorable;
import javax.swing.JComponent;
import jdplus.timeseries.simplets.analysis.DiagnosticInfo;
import jdplus.timeseries.simplets.analysis.RevisionHistory;

/**
 *
 * @author Mats Maggi
 */
public class RevisionHistoryUI implements ItemUI<RevisionHistoryUI.Information> {

    public RevisionHistoryUI() {
    }

    @Override
    public JComponent getView(Information input) {
        JRevisionSeriesView view=new JRevisionSeriesView();
        view.setInfo(input.getInfo());
        view.setDiagnosticInfo(input.getDiag());
        view.setHistory(input.getRevisionHistory());
        return view;
    }

    @lombok.Value
    public static class Information {

        private String info;
        private DiagnosticInfo diag;
        private RevisionHistory<Explorable> revisionHistory;

    }

}
