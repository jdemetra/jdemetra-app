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

import demetra.ui.components.parts.HasTs;
import ec.nbdemetra.ui.chart3d.functions.JFunctions2DChart;
import ec.nbdemetra.ui.demo.DemoComponentFactory;
import ec.nbdemetra.ui.demo.JReflectComponent;
import ec.tstoolkit.utilities.Id;
import ec.ui.view.AutoRegressiveSpectrumView;
import ec.ui.view.PeriodogramView;
import ec.ui.view.JRevisionSaSeriesView;
import ec.ui.view.JSIView;
import ec.ui.view.JStabilityView;
import ec.ui.view.TukeySpectrumView;
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
public final class TsViewFactory implements DemoComponentFactory {

    public static final Id ID = TsControlFactory.ID.extend(DemoComponentFactory.idOf("TsView", 1, true));

    @Override
    public Map<Id, Callable<Component>> getComponents() {
        return DemoComponentFactory
                .builder()
                .put(ID, () -> JReflectComponent.of(HasTs.class))
                .put(ID.extend("AutoRegressiveSpectrumView"), AutoRegressiveSpectrumView::new)
                .put(ID.extend("PeriodogramView"), PeriodogramView::new)
                .put(ID.extend("TukeySpectrumView"), TukeySpectrumView::new)
                .put(ID.extend("Functions2DChart"), TsViewFactory::newFunctions2DChart)
                .put(ID.extend("RevisionSaSeriesView"), JRevisionSaSeriesView::new)
                .put(ID.extend("StabilityView"), JStabilityView::new)
                .put(ID.extend("SIView"), JSIView::new)
                .build();
    }

    private static Component newFunctions2DChart() {
        return new JFunctions2DChart(null, null, 100);
    }
}
