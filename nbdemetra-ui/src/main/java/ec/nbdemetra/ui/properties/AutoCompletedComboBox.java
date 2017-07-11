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

import ec.util.completion.AutoCompletionSource;
import ec.util.completion.AutoCompletionSources;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

@Deprecated
public abstract class AutoCompletedComboBox<T> extends JComponent {

    protected final JTextComponent textComponent;
    protected final DefaultComboBoxModel model;
    protected final AutoCompleteDocument autoCompleteDocument;

    public AutoCompletedComboBox() {

        model = new DefaultComboBoxModel();

        JComboBox comboBox = new JComboBox(model);
        comboBox.setBorder(BorderFactory.createEmptyBorder());
        comboBox.setEditable(true);
        comboBox.setSelectedIndex(-1);

        this.textComponent = (JTextComponent) comboBox.getEditor().getEditorComponent();
        this.autoCompleteDocument = AutoCompleteDocument.on(textComponent, AutoCompletionSources.empty(), null);

        setLayout(new BorderLayout());
        add(comboBox, BorderLayout.CENTER);
    }

    public void setAutoCompletion(AutoCompletionSource autoCompletion) {
        model.removeAllElements();
        try {
            for (Object o : autoCompletion.getValues("")) {
                model.addElement(o);
            }
        } catch (Exception ex) {
            // do nothing?
        }
        autoCompleteDocument.setAutoCompletion(autoCompletion);
    }

    public void setSeparator(String separator) {
        autoCompleteDocument.setSeparator(separator);
    }

    abstract public T getValue();

    abstract public void setValue(T value);
}
