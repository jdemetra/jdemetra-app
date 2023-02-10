/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.*;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

/**
 *
 * @author Demortier Jeremy
 * @author Mats Maggi
 */
public class HighFreqOutlierDescriptorsEditor extends AbstractPropertyEditor {

    private Map<LocalDate, List<HighFreqOutlierDescriptor>> definitions_;

    public HighFreqOutlierDescriptorsEditor() {
        editor = new JButton(new AbstractAction("...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window ancestor = SwingUtilities.getWindowAncestor(editor);
                final ArrayEditorDialog<HighFreqOutlierDescriptor> arrayEditorDialog = new ArrayEditorDialog<>(ancestor,
                        null != definitions_ ? getDescriptors() : new HighFreqOutlierDescriptor[]{},
                        HighFreqOutlierDescriptor::new, HighFreqOutlierDescriptor::duplicate);
                arrayEditorDialog.setTitle("Pre-specified outliers");
                arrayEditorDialog.setLocationRelativeTo(ancestor);
                arrayEditorDialog.setVisible(true);
                if (arrayEditorDialog.isDirty()) {
                    setDescriptors(arrayEditorDialog.getElements());
                }
            }
        });
    }

    private void setDescriptors(List<HighFreqOutlierDescriptor> elements) {
        Map<LocalDate, List<HighFreqOutlierDescriptor>> old = definitions_;
        definitions_ = new HashMap<>();
        for (HighFreqOutlierDescriptor element : elements) {
            LocalDate key = element.getPosition();
            if (!definitions_.containsKey(key) || definitions_.get(key) == null) {
                definitions_.put(key, new ArrayList<>());
            }
            definitions_.get(key).add(element.duplicate());
        }
        firePropertyChange(old, definitions_);
    }

    private HighFreqOutlierDescriptor[] getDescriptors() {
        return definitions_
                .values()
                .stream().flatMap(Collection::stream)
                .map(HighFreqOutlierDescriptor::new)
                .sorted((o1, o2) -> o1.getPosition().compareTo(o2.getPosition()))
                .toArray(HighFreqOutlierDescriptor[]::new);
    }

    @Override
    public void setValue(Object value) {
        definitions_ = new HashMap<>();
        if (value instanceof HighFreqOutlierDescriptor[] highFreqOutlierDescriptors) {
            HighFreqOutlierDescriptor[] outs = highFreqOutlierDescriptors;
            for (HighFreqOutlierDescriptor out : outs) {
                LocalDate key = out.getPosition();
                if (!definitions_.containsKey(key) || definitions_.get(key) == null) {
                    definitions_.put(key, new ArrayList<>());
                }
                // makes copies
                definitions_.get(key).add(out.duplicate());
            }
        }
    }

    @Override
    public Object getValue() {
        return definitions_
                .values()
                .stream().flatMap(Collection::stream)
                .toArray(HighFreqOutlierDescriptor[]::new);

    }

 }
