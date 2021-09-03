/*
 * Copyright 2017 National Bank of Belgium
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
package demetra.desktop.core.properties;

import demetra.desktop.properties.AbstractExPropertyEditor;
import demetra.desktop.properties.AbstractInplaceEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.nodes.PropertyEditorRegistration;

import javax.swing.*;
import java.beans.PropertyEditor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Philippe Charles
 * @since 2.2.0
 */
@PropertyEditorRegistration(targetType = LocalDateTime.class)
public final class LocalDateTimePropertyEditor extends AbstractExPropertyEditor {

    public static final String NULL_STRING = "nullString";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private String nullString;

    @Override
    public void attachEnv(PropertyEnv env) {
        nullString = attr(env, NULL_STRING, String.class).orElse("");
        super.attachEnv(env);
    }

    @Override
    public String getAsText() {
        return formatLocalDateTime(getValue(), nullString, FORMATTER);
    }

    @Override
    public InplaceEditor createInplaceEditor() {
        return new LocalDateTimeInplaceEditor();
    }

    private static final class LocalDateTimeInplaceEditor extends AbstractInplaceEditor {

        private final JTextField component = new JTextField();
        private String nullString;

        @Override
        public void connect(PropertyEditor propertyEditor, PropertyEnv env) {
            nullString = attr(env, NULL_STRING, String.class).orElse("");
            super.connect(propertyEditor, env);
        }

        @Override
        public JComponent getComponent() {
            return component;
        }

        @Override
        public Object getValue() {
            return parseLocalDateTime(component.getText(), nullString, FORMATTER);
        }

        @Override
        public void setValue(Object o) {
            component.setText(formatLocalDateTime(o, nullString, FORMATTER));
        }
    }

    private static Object parseLocalDateTime(String o, String nullString, DateTimeFormatter formatter) {
        return o != null && !o.trim().equals(nullString) ? LocalDateTime.parse(o, formatter) : null;
    }

    private static String formatLocalDateTime(Object o, String nullString, DateTimeFormatter formatter) {
        return o instanceof LocalDateTime ? ((LocalDateTime) o).format(formatter) : nullString;
    }
}
