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
import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
import ec.ui.DemoUtils;
import ec.ui.interfaces.ITsControl;
import ec.ui.view.MarginView;
import java.awt.Component;
import java.util.Map;
import java.util.concurrent.Callable;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = DemoComponentFactory.class)
public final class TsControlFactory extends DemoComponentFactory {

    public static final Id ID = new LinearId("(1) Main", "TsControl");

    @Override
    public Map<Id, Callable<Component>> getComponents() {
        return builder()
                .put(ID, reflect(ITsControl.class))
                .put(ID.extend("MarginView"), marginView())
                .build();
    }

    private static Callable<Component> marginView() {
        return () -> {
            MarginView result = new MarginView();
            TsData series = DemoUtils.randomTsCollection(1).get(0).getTsData();
            DescriptiveStatistics stats = new DescriptiveStatistics(series);
            double val = (stats.getMax() - stats.getMin()) / 2;
            TsData lower = series.drop(14, 0).minus(val);
            TsData upper = series.drop(14, 0).plus(val);
            result.setData(series, lower, upper);
            return result;
        };
    }
}
