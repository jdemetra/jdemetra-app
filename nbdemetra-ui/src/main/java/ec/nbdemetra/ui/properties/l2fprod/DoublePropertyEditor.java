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
package ec.nbdemetra.ui.properties.l2fprod;

import com.l2fprod.common.swing.LookAndFeelTweaks;
import internal.CustomPropertyEditorSupport;
import static internal.JTextComponents.enableDecimalMappingOnNumpad;
import static internal.JTextComponents.enableValidationFeedback;
import static internal.JTextComponents.fixMaxDecimals;
import static internal.JTextComponents.isDouble;
import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyEditor;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.function.BiConsumer;
import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author Philippe Charles
 * @since 2.2.0
 */
public final class DoublePropertyEditor implements PropertyEditor {

    private final DecimalFormat format;
    private final JFormattedTextField editor;

    @lombok.experimental.Delegate
    private final CustomPropertyEditorSupport support;

    @SuppressWarnings("LeakingThisInConstructor")
    public DoublePropertyEditor() {
        this.format = new DecimalFormat();
        this.editor = new JFormattedTextField();
        this.support = CustomPropertyEditorSupport.of(editor, this, DoubleResource.INSTANCE);
        initComponents();
    }

    private void initComponents() {
        fixMaxDecimals(format);
        editor.setBorder(LookAndFeelTweaks.EMPTY_BORDER);
        editor.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(format)));
        enableValidationFeedback(editor, o -> isDouble(format, o), () -> Color.RED);
        enableDecimalMappingOnNumpad(editor, () -> format.getDecimalFormatSymbols().getDecimalSeparator());
    }

    private enum DoubleResource implements CustomPropertyEditorSupport.Resource {

        INSTANCE;

        @Override
        public void bindValue(Component editor, BiConsumer<Object, Object> y) {
            editor.addPropertyChangeListener("value", o -> y.accept(o.getOldValue(), o.getNewValue()));
        }

        @Override
        public Object getValue(Component editor) {
            try {
                return ((JFormattedTextField) editor).getFormatter().stringToValue(((JFormattedTextField) editor).getText());
            } catch (ParseException ex) {
                return ((JFormattedTextField) editor).getValue();
            }
        }

        @Override
        public void setValue(Component editor, Object value) {
            ((JFormattedTextField) editor).setValue(value);
        }
    }
}
