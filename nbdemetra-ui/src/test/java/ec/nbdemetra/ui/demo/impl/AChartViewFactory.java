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
package ec.nbdemetra.ui.demo.impl;

import ec.nbdemetra.ui.demo.DemoComponentFactory;
import ec.tstoolkit.uihelper.ModelInformationProvider;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
import ec.ui.view.FilterView;
import ec.ui.view.PiView;
import ec.ui.view.ScatterView;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = DemoComponentFactory.class)
public final class AChartViewFactory extends DemoComponentFactory {

    static final Id ID = new LinearId("(1) Main", "AChartView");

    @Override
    public Map<Id, Callable<Component>> getComponents() {
        return builder()
                .put(ID.extend("FilterView"), () -> new FilterView(new ModelInformationProvider(new ArrayList<>())))
                .put(ID.extend("PiView"), () -> new PiView(new ModelInformationProvider(new ArrayList<>())))
                .put(ID.extend("ScatterView"), () -> new ScatterView(new ModelInformationProvider(new ArrayList<>())))
                .build();
    }
}
