/*
 * Copyright 2013 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
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
package ec.nbdemetra.ui;

import ec.nbdemetra.core.GlobalService;
import ec.ui.AHtmlView;
import ec.ui.ATsChart;
import ec.ui.ATsGrid;
import ec.ui.ATsGrowthChart;
import ec.ui.ATsList;
import ec.ui.chart.JTsChart;
import ec.ui.chart.JTsGrowthChart;
import ec.ui.grid.JTsGrid;
import ec.ui.html.JHtmlView;
import ec.ui.list.JTsList;
import javax.annotation.Nonnull;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@GlobalService
@ServiceProvider(service = ComponentFactory.class)
public class ComponentFactory {

    @Nonnull
    public static ComponentFactory getDefault() {
        return Lookup.getDefault().lookup(ComponentFactory.class);
    }

    @Nonnull
    public ATsChart newTsChart() {
        return new JTsChart();
    }

    @Nonnull
    public ATsGrid newTsGrid() {
        return new JTsGrid();
    }

    @Nonnull
    public ATsGrowthChart newTsGrowthChart() {
        return new JTsGrowthChart();
    }

    @Nonnull
    public ATsList newTsList() {
        return new JTsList();
    }

    @Nonnull
    public AHtmlView newHtmlView() {
        return new JHtmlView();
    }
}
