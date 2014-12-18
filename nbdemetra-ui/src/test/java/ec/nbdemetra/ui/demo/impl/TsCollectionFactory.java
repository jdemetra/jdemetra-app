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
import ec.tstoolkit.utilities.Id;
import ec.ui.chart.JTsChart;
import ec.ui.chart.JTsDualChart;
import ec.ui.chart.JTsGrowthChart;
import ec.ui.grid.JTsGrid;
import ec.ui.grid.TsGridObs;
import ec.ui.interfaces.ITsCollectionView;
import ec.ui.list.JTsList;
import java.awt.Color;
import java.awt.Component;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.swing.JTable;
import static javax.swing.SwingConstants.TRAILING;
import javax.swing.table.DefaultTableCellRenderer;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = DemoComponentFactory.class)
public final class TsCollectionFactory extends DemoComponentFactory {

    public static final Id ID = TsControlFactory.ID.extend("Collection");

    @Override
    public Map<Id, Callable<Component>> getComponents() {
        return builder()
                .put(ID, reflect(ITsCollectionView.class))
                .put(ID.extend("JTsGrid"), newInstance(JTsGrid.class))
                .put(ID.extend("JTsChart"), newInstance(JTsChart.class))
                .put(ID.extend("JTsGrowthChart"), newInstance(JTsGrowthChart.class))
                .put(ID.extend("JTsDualChart"), newInstance(JTsDualChart.class))
                .put(ID.extend("JTsList"), newInstance(JTsList.class))
                .put(ID.extend("JTsGrid++"), gridWithCustomCellRenderer())
                .build();
    }

    private static Callable<Component> gridWithCustomCellRenderer() {
        return new Callable<Component>() {
            @Override
            public Component call() throws Exception {
                JTsGrid result = new JTsGrid();
                result.setCellRenderer(new CustomCellRenderer());
                return result;
            }
        };
    }

    private static final class CustomCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setForeground(null);
            setHorizontalAlignment(TRAILING);
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value instanceof TsGridObs) {
                TsGridObs obs = (TsGridObs) value;
                switch (obs.getInfo()) {
                    case Empty:
                        setText("Empty");
                    case Missing:
                        setText("Missing");
                    case Valid:
                        long longValue = (long) obs.getValue();
                        setText(Long.toString(longValue));
                        if (longValue % 2 != 0) {
                            setForeground(Color.RED);
                        }
                }
            }

            return this;
        }
    }
}
