/*
 * Copyright 2016 National Bank of Belgium
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
package _demo;

import internal.ui.properties.AutoCompletedComponent;
import static ec.util.completion.AutoCompletionSource.Behavior.SYNC;
import ec.util.completion.ExtAutoCompletionSource;
import ec.util.completion.swing.CustomListCellRenderer;
import ec.util.completion.swing.JAutoCompletion;
import ec.util.various.swing.BasicSwingLauncher;
import java.awt.Component;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JTextField;

/**
 *
 * @author Philippe Charles
 */
final class AutoCompletedComponentDemo {

    public static void main(String[] args) {
        new BasicSwingLauncher()
                .content(AutoCompletedComponentDemo::create)
                .size(400, 300)
                .logLevel(Level.FINE)
                .launch();
    }

    private static Component create() {
        AutoCompletedComponent result = new AutoCompletedComponent();
        result.setAutoCompletion(AutoCompletedComponentDemo::applyAutoCompletion);
        result.setDefaultValueSupplier(AutoCompletedComponentDemo::loadDefaultValue);
        return result;
    }

    private static void applyAutoCompletion(JTextField textField) {
        JAutoCompletion autoCompletion = new JAutoCompletion(textField);
        autoCompletion.setSource(ExtAutoCompletionSource
                .builder(AutoCompletedComponentDemo::load)
                .behavior(SYNC)
                .valueToString(StandardSystemProperty::getName)
                .build());
        autoCompletion.getList().setCellRenderer(new CustomListCellRenderer<StandardSystemProperty>() {
            @Override
            protected String getValueAsString(StandardSystemProperty value) {
                return value.getName();
            }
        });
    }

    private static List<StandardSystemProperty> load(String term) {
        Predicate<String> filter = ExtAutoCompletionSource.basicFilter(term);
        return Stream.of(StandardSystemProperty.values())
                .filter(o -> filter.test(o.getName()))
                .sorted(Comparator.comparing(StandardSystemProperty::getName))
                .collect(Collectors.toList());
    }

    private static String loadDefaultValue() throws InterruptedException {
        Thread.sleep(2000);
        return Stream.of(StandardSystemProperty.values())
                .map(StandardSystemProperty::getName)
                .collect(Collectors.joining(","));
    }

    @lombok.Value
    private static final class StandardSystemProperty {

        private String name;

        static StandardSystemProperty[] values() {
            return System
                    .getProperties()
                    .entrySet()
                    .stream()
                    .map(o -> o.getKey().toString())
                    .map(StandardSystemProperty::new)
                    .toArray(StandardSystemProperty[]::new);
        }
    }
}
