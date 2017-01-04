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

import com.google.common.collect.ImmutableMap;
import ec.nbdemetra.ui.demo.DemoComponentFactory;
import ec.nbdemetra.ui.properties.l2fprod.PropertiesPanelFactory;
import ec.satoolkit.x11.CalendarSigma;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IPropertyDescriptors;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
import ec.ui.interfaces.ITsGrid;
import java.awt.Component;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = DemoComponentFactory.class)
public final class L2fprodFactory extends DemoComponentFactory {

    @Override
    public Map<Id, Callable<Component>> getComponents() {
        return ImmutableMap.of(new LinearId("(2) Other", "l2fprod"), L2fprodFactory::create);
    }

    private static Component create() {
        return PropertiesPanelFactory.INSTANCE.createPanel(new CustomObj());
    }

    @lombok.Data
    public static final class CustomObj implements IPropertyDescriptors {

        private double doubleValue;
        private double smallValue;
        private CalendarSigma enumValue1;
        private ITsGrid.Orientation enumValue2;
        private String stringValue;

        public CustomObj() {
            this.doubleValue = 3.14;
            this.smallValue = 0.0000001;
            this.enumValue1 = CalendarSigma.Select;
            this.enumValue2 = ITsGrid.Orientation.REVERSED;
            this.stringValue = "hello";
        }

        @Override
        public List<EnhancedPropertyDescriptor> getProperties() {
            int i = 0;
            return Arrays.asList(
                    descriptorOf("doubleValue", i++),
                    descriptorOf("smallValue", i++),
                    descriptorOf("enumValue1", i++),
                    descriptorOf("enumValue2", i++),
                    descriptorOf("stringValue", i++)
            );
        }

        @Override
        public String getDisplayName() {
            return "Some custom object";
        }

        private EnhancedPropertyDescriptor descriptorOf(String propertyName, int position) {
            try {
                PropertyDescriptor desc = new PropertyDescriptor(propertyName, this.getClass());
                return new EnhancedPropertyDescriptor(desc, position);
            } catch (IntrospectionException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
