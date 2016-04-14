/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties;

import java.awt.Component;
import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class ListSelectionEditor<T> extends PropertyEditorSupport  {

    private final List<T> all;
    private final ListSelection<T> component = new ListSelection<>();
    

    public ListSelectionEditor(List<T> allValues) {
        all = allValues;
        component.addPropertyChangeListener(evt -> {
            if (evt.getPropertyName().equals(ListSelection.SEL_CHANGED)){
                setValue(evt.getNewValue());
            }
        });
    }

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    @Override
    public Component getCustomEditor() {
        List<T> sel = (List<T>) getValue();
        ArrayList<T> tmpAll = new ArrayList<>(all);
        ArrayList<T> tmpSel = sel != null ? new ArrayList<>(sel) : new ArrayList<>();
        tmpAll.removeAll(tmpSel);
        component.set(tmpAll, tmpSel);
        return component;
    }

    @Override
    public String getAsText() {
        return super.getAsText();
    }
}
