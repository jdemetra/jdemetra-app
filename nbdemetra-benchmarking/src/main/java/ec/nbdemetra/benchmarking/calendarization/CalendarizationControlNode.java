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
package ec.nbdemetra.benchmarking.calendarization;

import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.tstoolkit.timeseries.DayOfWeek;
import java.lang.reflect.InvocationTargetException;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.Lookups;

/**
 * Properties sheet used to change the weights of days in the calendarization
 * @author Mats Maggi
 */
public class CalendarizationControlNode {

    public static Node onComponentOpened(final ExplorerManager mgr, final CalendarizationTopComponent view) {
        CalendarizationNode root = new CalendarizationNode(view);
        mgr.setRootContext(root);
        return root;
    }

    static class CalendarizationNode extends AbstractNode {

        CalendarizationNode(CalendarizationTopComponent comp) {
            super(Children.LEAF, Lookups.singleton(comp));
            setDisplayName("Calendarization");
        }

        @Override
        protected Sheet createSheet() {
            final CalendarizationTopComponent ui = getLookup().lookup(CalendarizationTopComponent.class);
            Sheet result = new Sheet();
            NodePropertySetBuilder b = new NodePropertySetBuilder();

            b.reset("Daily Weights");
            for (final DayOfWeek w : DayOfWeek.values()) {
                Property p = new PropertySupport.ReadWrite<Double>(w.toString(), Double.class, w.toString(), "Daily weight of " + w.toString()) {

                    @Override
                    public Double getValue() throws IllegalAccessException, InvocationTargetException {
                        return Double.valueOf(ui.getDailyWeight(w));
                    }

                    @Override
                    public void setValue(Double t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                        ui.setDailyWeight(w, t.doubleValue());
                    }
                };

                b.with(Double.class).select(p).add();
            }
            result.put(b.build());

            return result;
        }
    }
}
