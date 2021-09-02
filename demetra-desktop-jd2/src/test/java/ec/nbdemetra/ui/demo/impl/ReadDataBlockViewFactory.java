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
import ec.nbdemetra.ui.demo.JReflectComponent;
import ec.tstoolkit.utilities.Id;
import ec.ui.interfaces.IReadDataBlockView;
import ec.ui.view.JAutoCorrelationsView;
import ec.ui.view.JDistributionView;
import java.awt.Component;
import java.util.Map;
import java.util.concurrent.Callable;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@DirectImpl
@ServiceProvider
public final class ReadDataBlockViewFactory implements DemoComponentFactory {

    public static final Id ID = TsControlFactory.ID.extend(DemoComponentFactory.idOf("ReadDataBlockView", 2, true));

    @Override
    public Map<Id, Callable<Component>> getComponents() {
        return DemoComponentFactory
                .builder()
                .put(ID, () -> JReflectComponent.of(IReadDataBlockView.class))
                .put(ID.extend("AutoCorrelationsView"), JAutoCorrelationsView::new)
                .put(ID.extend("DistributionView"), JDistributionView::new)
                .build();
    }
}
