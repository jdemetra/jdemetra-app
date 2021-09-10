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
import static demetra.desktop.util.JTextComponents.enableDecimalMappingOnNumpad;
import static demetra.desktop.util.JTextComponents.enableValidationFeedback;
import static demetra.desktop.util.JTextComponents.fixMaxDecimals;
import static demetra.desktop.util.JTextComponents.isDouble;
import static demetra.desktop.util.JTextComponents.peekValue;
import java.beans.PropertyEditor;
import java.text.DecimalFormat;
import java.util.function.BiConsumer;
import javax.swing.JFormattedTextField;

/**
 *
 * @author Philippe Charles
 * @since 2.2.0
 */
public final class DoublePropertyEditor implements PropertyEditor {

    @lombok.experimental.Delegate
    private final CustomPropertyEditorSupport support;

    @SuppressWarnings("LeakingThisInConstructor")
    public DoublePropertyEditor() {
        this.support = CustomPropertyEditorSupport.of(createEditor(), this, DoubleResource.INSTANCE);
    }

    private static JFormattedTextField createEditor() {
        DecimalFormat format = new DecimalFormat();
        fixMaxDecimals(format);
        JFormattedTextField result = new JFormattedTextField(format);
        result.setBorder(LookAndFeelTweaks.EMPTY_BORDER);
        enableValidationFeedback(result, o -> isDouble(format, o));
        enableDecimalMappingOnNumpad(result);
        return result;
    }

    private enum DoubleResource implements CustomPropertyEditorSupport.Resource<JFormattedTextField, Number> {

        INSTANCE;

        @Override
        public void bindValue(JFormattedTextField editor, BiConsumer<Number, Number> broadcaster) {
            editor.addPropertyChangeListener("value", o -> broadcaster.accept((Number) o.getOldValue(), (Number) o.getNewValue()));
        }

        @Override
        public Number getValue(JFormattedTextField editor) {
            return (Number) peekValue(editor).orElseGet(editor::getValue);
        }

        @Override
        public void setValue(JFormattedTextField editor, Number value) {
            editor.setValue(value);
        }
    }
}
