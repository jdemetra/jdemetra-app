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
package internal.ui;

import demetra.ui.components.HasTs;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;
import demetra.ui.TsActionSpi;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = TsActionSpi.class)
public final class TsViewsTsAction implements TsActionSpi {

    @Override
    public String getName() {
        return "TsViewsTs";
    }

    @Override
    public String getDisplayName() {
        return "All ts views";
    }

    @Override
    public void open(demetra.timeseries.Ts ts) {
        TopComponent.getRegistry().getOpened().stream()
                .filter(HasTs.class::isInstance)
                .forEach(o -> ((HasTs) o).setTs(ts));
    }
}
