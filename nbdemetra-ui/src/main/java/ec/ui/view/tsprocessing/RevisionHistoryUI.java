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

import ec.tstoolkit.timeseries.analysis.RevisionHistory;
import ec.ui.view.RevisionSaSeriesView;

/**
 *
 * @author Mats Maggi
 */
public class RevisionHistoryUI <V extends IProcDocumentView<?>> extends PooledItemUI<V, RevisionHistory, RevisionSaSeriesView>{
    
    private String info;
    
    public RevisionHistoryUI() {
        super(RevisionSaSeriesView.class);
    }
    
    public RevisionHistoryUI(String info) {
        super(RevisionSaSeriesView.class);
        this.info = info;
    }

    @Override
    protected void init(RevisionSaSeriesView c, V host, RevisionHistory information) {
        c.setInfo(info);
        c.setHistory(information);
    }

}
