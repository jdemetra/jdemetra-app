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
package ec.ui.view.res;

import demetra.ui.components.HasObsFormat;
import demetra.ui.components.HasTsData;
import demetra.ui.components.TimeSeriesComponent;
import internal.InternalResidualsViewUI;
import internal.ui.components.InternalUI;
import javax.swing.JComponent;

/**
 *
 * @author Kristof Bayens
 */
public final class ResidualsView extends JComponent implements TimeSeriesComponent, HasTsData, HasObsFormat {

    @lombok.experimental.Delegate
    private final HasTsData tsData;

    @lombok.experimental.Delegate
    private final HasObsFormat obsFormat;

    private final InternalUI<ResidualsView> ui;

    public ResidualsView() {
        this.tsData = HasTsData.of(this::firePropertyChange);
        this.obsFormat = HasObsFormat.of(this::firePropertyChange);
        this.ui = new InternalResidualsViewUI();

        ui.install(this);
    }
}
