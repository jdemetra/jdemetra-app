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
import java.awt.Component;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
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
        return PropertiesPanelFactory.INSTANCE.createPanel(new CustomObj(3.14, CalendarSigma.Signif));
    }

    public static final class CustomObj implements IPropertyDescriptors {

        private double doubleValue;
        private CalendarSigma enumValue;

        public CustomObj(double doubleValue, CalendarSigma enumValue) {
            this.doubleValue = doubleValue;
            this.enumValue = enumValue;
        }

        public double getDoubleValue() {
            return doubleValue;
        }

        public void setDoubleValue(double doubleValue) {
            this.doubleValue = doubleValue;
        }

        public CalendarSigma getEnumValue() {
            return enumValue;
        }

        public void setEnumValue(CalendarSigma enumValue) {
            this.enumValue = enumValue;
        }

        @Override
        public List<EnhancedPropertyDescriptor> getProperties() {
            List<EnhancedPropertyDescriptor> result = new ArrayList<>();
            EnhancedPropertyDescriptor desc = doubleValueDesc();
            if (desc != null) {
                result.add(desc);
            }
            desc = enumValueDesc();
            if (desc != null) {
                result.add(desc);
            }
            return result;
        }

        @Override
        public String getDisplayName() {
            return "Some custom object";
        }

        private EnhancedPropertyDescriptor doubleValueDesc() {
            try {
                PropertyDescriptor desc = new PropertyDescriptor("doubleValue", this.getClass());
                EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, 0);
                desc.setDisplayName("Double value");
                return edesc;
            } catch (IntrospectionException ex) {
                return null;
            }
        }

        private EnhancedPropertyDescriptor enumValueDesc() {
            try {
                PropertyDescriptor desc = new PropertyDescriptor("enumValue", this.getClass());
                EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, 1);
                desc.setDisplayName("Enum value");
                return edesc;
            } catch (IntrospectionException ex) {
                return null;
            }
        }
    }
}
