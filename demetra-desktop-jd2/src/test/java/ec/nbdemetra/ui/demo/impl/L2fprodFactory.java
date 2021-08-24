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

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;
import ec.nbdemetra.ui.demo.DemoComponentFactory;
import ec.nbdemetra.ui.properties.l2fprod.CustomPropertyEditorRegistry;
import ec.nbdemetra.ui.properties.l2fprod.PropertiesPanelFactory;
import ec.satoolkit.x11.CalendarSigma;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IPropertyDescriptors;
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.Month;
import ec.tstoolkit.utilities.Id;
import ec.util.various.swing.BasicSwingLauncher;
import java.awt.Component;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.SwingWorker;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;

@DirectImpl
@ServiceProvider
public final class L2fprodFactory implements DemoComponentFactory {

    @Override
    public Map<Id, Callable<Component>> getComponents() {
        return ImmutableMap.of(OtherFactory.ID.extend("l2fprod"), L2fprodFactory::create);
    }

    private static Component create() {
        return PropertiesPanelFactory.INSTANCE.createPanel(new Example());
    }

    @lombok.Data
    public static final class Primitives implements AutoDescriptors {

        byte byteValue = 1;
        short shortValue = 123;
        int intValue = 123;
        long longValue = 123;
        float floatValue = (float) Math.PI;
        double doubleValue = Math.PI;
        double smallDouble = 0.0000001;
        boolean boolValue = true;
        char charValue = 'A';
    }

    @lombok.Data
    public static final class JdkObjects implements AutoDescriptors {

        String stringValue = "hello";
        SwingWorker.StateValue enumValue = SwingWorker.StateValue.PENDING;
        Date dateValue = new Date();
    }

    @lombok.Data
    public static final class DemetraObjects implements AutoDescriptors {

        CalendarSigma calendarSigma = CalendarSigma.Signif;
        Day day = new Day(2010, Month.April, 1);
        String[] stringArray = {"hello", "world"};
    }

    @lombok.Data
    public static final class Example implements AutoDescriptors {

        Primitives primitives = new Primitives();
        JdkObjects jdk = new JdkObjects();
        DemetraObjects demetra = new DemetraObjects();
    }

    private interface AutoDescriptors extends IPropertyDescriptors {

        @Override
        default List<EnhancedPropertyDescriptor> getProperties() {
            return PropertyDescriptors.descriptorsOf(getClass());
        }

        @Override
        default String getDisplayName() {
            return getClass().getSimpleName();
        }
    }

    @lombok.experimental.UtilityClass
    private static class PropertyDescriptors {

        static List<EnhancedPropertyDescriptor> descriptorsOf(Class<?> beanClass) {
            return descriptorsOf(beanClass, Stream.of(beanClass.getDeclaredFields()).map(Field::getName).collect(Collectors.toList()));
        }

        static List<EnhancedPropertyDescriptor> descriptorsOf(Class<?> beanClass, List<String> propertyNames) {
            List<EnhancedPropertyDescriptor> result = new ArrayList<>();
            for (int i = 0; i < propertyNames.size(); i++) {
                result.add(descriptorOf(beanClass, propertyNames.get(i), i));
            }
            return result;
        }

        static EnhancedPropertyDescriptor descriptorOf(Class<?> beanClass, String propertyName, int position) {
            try {
                PropertyDescriptor desc = new PropertyDescriptor(propertyName, beanClass);
                String displayName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, desc.getName()).replace("-", " ");
                Optional<Class<?>> registeredEditor = getRegisteredEditor(desc);
                if (registeredEditor.isPresent()) {
                    desc.setDisplayName(isL2fprodEditor(registeredEditor.get()) ? displayName : ("<html><b>" + displayName));
                    desc.setShortDescription(registeredEditor.get().getName());
                } else if (!IPropertyDescriptors.class.isAssignableFrom(desc.getPropertyType())) {
                    desc.setDisplayName("<html><body><span style='text-decoration: line-through;'>" + displayName);
                } else {
                    desc.setDisplayName(displayName);
                }
                return new EnhancedPropertyDescriptor(desc, position);
            } catch (IntrospectionException ex) {
                throw new RuntimeException(ex);
            }
        }

        static Optional<Class<?>> getRegisteredEditor(PropertyDescriptor desc) {
            return getRegisteredEditor(desc.getPropertyType());
        }

        static Optional<Class<?>> getRegisteredEditor(Class<?> propertyType) {
            return Optional.ofNullable(CustomPropertyEditorRegistry.INSTANCE.getRegistry().getEditor(propertyType)).map(Object::getClass);
        }

        static boolean isL2fprodEditor(Class<?> registeredEditor) {
            return registeredEditor.getPackage().getName().startsWith("com.l2fprod");
        }
    }

    public static void main(String[] args) {
        new BasicSwingLauncher().content(L2fprodFactory::create).size(400, 550).launch();
    }
}
