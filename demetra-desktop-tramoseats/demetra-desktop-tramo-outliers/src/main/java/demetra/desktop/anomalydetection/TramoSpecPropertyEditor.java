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
package demetra.desktop.anomalydetection;

import demetra.tramo.TramoSpec;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;
import org.openide.nodes.PropertyEditorRegistration;

/**
 * Custom property editor for TramoSpecification
 * @author Mats Maggi
 */
@PropertyEditorRegistration(targetType = TramoSpec.class)
public class TramoSpecPropertyEditor extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory {

    private InplaceEditor ed = null;

    @Override
    public void attachEnv(PropertyEnv pe) {
        pe.registerInplaceEditorFactory(this);
    }

    @Override
    public InplaceEditor getInplaceEditor() {
        if (ed == null) {
            ed = new TramoSpecEditor();
        }
        return ed;
    }

    private static class TramoSpecEditor implements InplaceEditor {

        private PropertyEditor editor = null;
        private PropertyModel model = null;
        private final JComboBox specs = new JComboBox(TramoSpec.allSpecifications());

        @Override
        public void connect(PropertyEditor pe, PropertyEnv pe1) {
            editor = pe;
            reset();
        }

        @Override
        public JComponent getComponent() {
            return specs;
        }

        @Override
        public Object getValue() {
            return specs.getSelectedItem();
        }

        @Override
        public void setValue(Object o) {
            if (o == null) {
            }
        }

        @Override
        public boolean supportsTextEntry() {
            return false;
        }

        @Override
        public void reset() {
            TramoSpec spec = (TramoSpec) editor.getValue();
            if (spec != null) {
                specs.setSelectedItem(spec);
            }
        }

        @Override
        public void clear() {
            editor = null;
            model = null;
        }

        @Override
        public void addActionListener(ActionListener al) {
            // do nothing
        }

        @Override
        public void removeActionListener(ActionListener al) {
            // do nothing
        }

        @Override
        public KeyStroke[] getKeyStrokes() {
            return new KeyStroke[0];
        }

        @Override
        public PropertyEditor getPropertyEditor() {
            return editor;
        }

        @Override
        public PropertyModel getPropertyModel() {
            return model;
        }

        @Override
        public void setPropertyModel(PropertyModel pm) {
            this.model = pm;
        }

        @Override
        public boolean isKnownComponent(Component cmpnt) {
            return cmpnt == specs || specs.isAncestorOf(cmpnt);

        }
    }
}
