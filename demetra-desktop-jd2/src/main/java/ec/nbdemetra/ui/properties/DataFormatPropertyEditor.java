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
package ec.nbdemetra.ui.properties;

import demetra.ui.properties.AbstractInplaceEditor;
import demetra.ui.properties.AbstractExPropertyEditor;
import ec.tss.tsproviders.utils.DataFormat;
import javax.swing.JComponent;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.nodes.PropertyEditorRegistration;

/**
 *
 * @author Philippe Charles
 */
@PropertyEditorRegistration(targetType = DataFormat.class)
public class DataFormatPropertyEditor extends AbstractExPropertyEditor {

    @Override
    public InplaceEditor createInplaceEditor() {
        return new DataFormatInplaceEditor();
    }

    private static final class DataFormatInplaceEditor extends AbstractInplaceEditor {

        final DataFormatComponent2 component = new DataFormatComponent2();

        @Override
        public JComponent getComponent() {
            return component;
        }

        @Override
        public Object getValue() {
            return component.getDataFormat();
        }

        @Override
        public void setValue(Object o) {
            component.setDataFormat((DataFormat) o);
        }
    }
}
