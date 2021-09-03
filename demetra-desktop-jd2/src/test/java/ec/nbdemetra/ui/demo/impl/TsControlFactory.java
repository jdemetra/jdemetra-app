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

import demetra.bridge.TsConverter;
import demetra.demo.DemoTsBuilder;
import demetra.desktop.components.TimeSeriesComponent;
import ec.nbdemetra.ui.demo.DemoComponentFactory;
import ec.nbdemetra.ui.demo.JReflectComponent;
import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.utilities.Id;
import ec.ui.view.JMarginView;
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
public final class TsControlFactory implements DemoComponentFactory {

    public static final Id ID = MainFactory.ID.extend(DemoComponentFactory.idOf("TsControl", 0, true));

    @Override
    public Map<Id, Callable<Component>> getComponents() {
        return DemoComponentFactory
                .builder()
                .put(ID, () -> JReflectComponent.of(TimeSeriesComponent.class))
                .put(ID.extend("MarginView"), TsControlFactory::marginView)
                .build();
    }

    private static Component marginView() {
        JMarginView result = new JMarginView();
        TsData series = TsConverter.fromTsData(DemoTsBuilder.randomTsCollection(1).get(0).getData()).get();
        DescriptiveStatistics stats = new DescriptiveStatistics(series);
        double val = (stats.getMax() - stats.getMin()) / 2;
        TsData lower = series.drop(14, 0).minus(val);
        TsData upper = series.drop(14, 0).plus(val);
        result.setData(series, lower, upper);
        return result;
    }
}
