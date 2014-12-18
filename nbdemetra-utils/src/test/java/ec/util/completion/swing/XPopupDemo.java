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
package ec.util.completion.swing;

import ec.util.completion.AutoCompletionSources;
import ec.util.various.swing.BasicSwingLauncher;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.logging.Level;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Philippe Charles
 */
public final class XPopupDemo extends JPanel {

    public static void main(String[] args) {
        new BasicSwingLauncher()
                .content(XPopupDemo.class)
                .title("XPopup Demo")
                .logLevel(Level.FINE)
                .launch();
    }

    public XPopupDemo() {
        setLayout(new FlowLayout());
        for (final XPopup.Anchor o : XPopup.Anchor.values()) {
            final JButton button = new JButton(o.name());
            button.addActionListener(new ActionListener() {
                XPopup popup = new XPopup();
                boolean visible = false;

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!visible) {
                        popup.show(button, new JLabel("<html><b>hello</b><br>world"), o, new Dimension(0, 0));
                    } else {
                        popup.hide();
                    }
                    visible = !visible;
                }
            });
            add(button);
        }
        JTextField textField = new JTextField(20);
        JAutoCompletion ac = new JAutoCompletion(textField);
        ac.setSource(AutoCompletionSources.of(false, Locale.getAvailableLocales()));
        add(textField);
    }
}
