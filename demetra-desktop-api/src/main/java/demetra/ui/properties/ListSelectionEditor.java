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
package demetra.ui.properties;

import demetra.ui.properties.AbstractInplaceEditor;
import ec.util.list.swing.JListSelection;
import ec.util.list.swing.JLists;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextField;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

/**
 *
 * @author Jean Palate
 * @param <T>
 */
public class ListSelectionEditor<T> extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory {

    private final List<T> allValues;
    private final JListSelection<T> customEditor;
    private PropertyEnv currentEnv;

    public ListSelectionEditor(List<T> allValues) {
        this.allValues = allValues;
        this.customEditor = new JListSelection<>();
        this.currentEnv = null;
        customEditor.getTargetModel().addListDataListener(JLists.dataListenerOf(o -> setValue(customEditor.getSelectedValues())));
        customEditor.setPreferredSize(new Dimension(400, 300));
        customEditor.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    @Override
    public void attachEnv(PropertyEnv env) {
        env.registerInplaceEditorFactory(this);
        currentEnv = env;
    }

    @Override
    public InplaceEditor getInplaceEditor() {
        return new ListSelectionInplaceEditor();
    }

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    @Override
    public Component getCustomEditor() {
        List<T> targetList = ((List<T>) getValue());
        List<T> sourceList = new ArrayList<>(allValues);
        sourceList.removeAll(targetList);

        customEditor.getSourceModel().clear();
        sourceList.forEach(customEditor.getSourceModel()::addElement);
        customEditor.getTargetModel().clear();
        targetList.forEach(customEditor.getTargetModel()::addElement);

        return customEditor;
    }

    @Override
    public String getAsText() {
        return super.getAsText();
    }

    private static final class ListSelectionInplaceEditor extends AbstractInplaceEditor {

        JTextField component = new JTextField();
        List<?> currentValue;

        {
            component.setEditable(false);
        }

        @Override
        public void connect(PropertyEditor propertyEditor, PropertyEnv env) {
            super.connect(propertyEditor, env);
        }

        @Override
        public JComponent getComponent() {
            return component;
        }

        @Override
        public Object getValue() {
            return currentValue;
        }

        @Override
        public void setValue(Object o) {
            currentValue = (List<?>) o;
            component.setText(currentValue.stream().map(Object::toString).collect(Collectors.joining(", ")));
        }
    }
}
