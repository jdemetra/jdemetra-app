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

import demetra.desktop.components.parts.HasTsCollection;
import ec.nbdemetra.ui.demo.DemoComponentFactory;
import ec.nbdemetra.ui.demo.JReflectComponent;
import ec.tstoolkit.utilities.Id;
import demetra.desktop.components.JTsChart;
import demetra.desktop.components.JTsGrowthChart;
import demetra.desktop.components.JTsGrid;
import demetra.desktop.components.JTsTable;
import demetra.desktop.components.TsGridObs;
import java.awt.Color;
import java.awt.Component;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.swing.JLabel;
import javax.swing.JTable;
import static javax.swing.SwingConstants.TRAILING;
import javax.swing.table.TableCellRenderer;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@DirectImpl
@ServiceProvider
public final class TsCollectionFactory implements DemoComponentFactory {

    public static final Id ID = TsControlFactory.ID.extend(DemoComponentFactory.idOf("TsCollectionView", 0, true));

    @Override
    public Map<Id, Callable<Component>> getComponents() {
        return DemoComponentFactory
                .builder()
                .put(ID, () -> JReflectComponent.of(HasTsCollection.class))
                .put(ID.extend("JTsGrid"), JTsGrid::new)
                .put(ID.extend("JTsChart"), JTsChart::new)
                .put(ID.extend("JTsGrowthChart"), JTsGrowthChart::new)
                .put(ID.extend("JTsDualChart"), () -> {
                    JTsChart result = new JTsChart();
                    result.setDualChart(true);
                    result.addPropertyChangeListener(HasTsCollection.TS_COLLECTION_PROPERTY, evt -> {
                        if (result.getTsCollection().size() > 0) {
                            result.getDualDispatcher().setSelectionInterval(0, 0);
                        }
                    });
                    return result;
                })
                .put(ID.extend("JTsTable"), JTsTable::new)
                .put(ID.extend("JTsGrid++"), TsCollectionFactory::gridWithCustomCellRenderer)
                .build();
    }

    private static Component gridWithCustomCellRenderer() {
        JTsGrid result = new JTsGrid();
        result.setCellRenderer(new CustomCellRenderer(result.getCellRenderer()));
        return result;
    }

    private static final class CustomCellRenderer implements TableCellRenderer {

        private final TableCellRenderer delegate;

        public CustomCellRenderer(TableCellRenderer delegate) {
            this.delegate = delegate;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component result = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value instanceof TsGridObs && result instanceof JLabel) {
                TsGridObs obs = (TsGridObs) value;
                JLabel label = (JLabel) result;
                label.setHorizontalAlignment(TRAILING);
                switch (obs.getStatus()) {
                    case AFTER:
                    case BEFORE:
                    case EMPTY:
                    case UNUSED:
                        label.setText("Empty");
                        break;
                    case PRESENT:
                        if (Double.isNaN(obs.getValue())) {
                            label.setText("Missing");
                        } else {
                            long longValue = (long) obs.getValue();
                            label.setText(Long.toString(longValue));
                            if (longValue % 2 != 0) {
                                label.setForeground(Color.RED);
                            }
                        }
                        break;
                }
            }

            return result;
        }
    }
}
