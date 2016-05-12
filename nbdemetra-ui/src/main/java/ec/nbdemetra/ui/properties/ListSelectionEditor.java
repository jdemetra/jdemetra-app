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
package ec.nbdemetra.ui.properties;

import ec.util.list.swing.JListSelection;
import ec.util.list.swing.JLists;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyEditorSupport;
import java.util.List;
import javax.swing.BorderFactory;

/**
 *
 * @author Jean Palate
 * @param <T>
 */
public class ListSelectionEditor<T> extends PropertyEditorSupport {

    private final JListSelection<T> component;

    public ListSelectionEditor(List<T> allValues) {
        this.component = new JListSelection<>();
        allValues.forEach(component.getSourceModel()::addElement);
        component.getTargetModel().addListDataListener(JLists.dataListenerOf(o -> setValue(component.getSelectedValues())));
        component.setPreferredSize(new Dimension(400, 300));
        component.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    @Override
    public Component getCustomEditor() {
        component.getTargetModel().clear();
        ((List<T>) getValue()).forEach(component.getTargetModel()::addElement);
        return component;
    }

    @Override
    public String getAsText() {
        return super.getAsText();
    }
}
