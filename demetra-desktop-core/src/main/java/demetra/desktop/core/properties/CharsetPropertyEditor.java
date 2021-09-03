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
package demetra.desktop.core.properties;

import demetra.desktop.components.JCharsetField;
import demetra.desktop.properties.AbstractExPropertyEditor;
import demetra.desktop.properties.AbstractInplaceEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.nodes.PropertyEditorRegistration;

import javax.swing.*;
import java.nio.charset.Charset;

/**
 *
 * @author Philippe Charles
 */
@PropertyEditorRegistration(targetType = Charset.class)
public final class CharsetPropertyEditor extends AbstractExPropertyEditor {

    @Override
    public InplaceEditor createInplaceEditor() {
        return new CharsetInplaceEditor();
    }

    private static final class CharsetInplaceEditor extends AbstractInplaceEditor {

        final JCharsetField component = new JCharsetField();

        @Override
        public JComponent getComponent() {
            return component;
        }

        @Override
        public Object getValue() {
            return component.getCharset();
        }

        @Override
        public void setValue(Object o) {
            component.setCharset((Charset) o);
        }
    }
}
