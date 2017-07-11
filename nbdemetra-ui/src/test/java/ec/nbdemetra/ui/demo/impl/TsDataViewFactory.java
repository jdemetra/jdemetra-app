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
import ec.nbdemetra.ui.demo.ReflectComponent;
import ec.tstoolkit.utilities.Id;
import ec.ui.interfaces.ITsDataView;
import ec.ui.view.res.ResidualsView;
import java.awt.Component;
import java.util.Map;
import java.util.concurrent.Callable;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = DemoComponentFactory.class)
public final class TsDataViewFactory extends DemoComponentFactory {

    public static final Id ID = MainFactory.ID.extend(idOf("TsDataView", 1, true));

    @Override
    public Map<Id, Callable<Component>> getComponents() {
        return builder()
                .put(ID, () -> ReflectComponent.of(ITsDataView.class))
                .put(ID.extend("ResidualsView"), ResidualsView::new)
                .build();
    }
}
