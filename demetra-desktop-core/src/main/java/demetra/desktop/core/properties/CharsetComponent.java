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

import demetra.ui.completion.AutoCompletion;
import demetra.ui.design.SwingComponent;
import ec.util.various.swing.TextPrompt;
import java.awt.BorderLayout;
import java.nio.charset.Charset;
import org.checkerframework.checker.nullness.qual.Nullable;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import nbbrd.io.text.Parser;

/**
 *
 * @author Philippe Charles
 * @since 1.3.2
 */
@SwingComponent
final class CharsetComponent extends JComponent {

    public static final String CHARSET_PROPERTY = "charset";

    private final JTextField textField;
    private final Listener listener;
    private Charset charset;

    public CharsetComponent() {
        this.textField = new JTextField();
        this.listener = new Listener();
        this.charset = null;

        initComponents();
        enableProperties();
    }

    private void initComponents() {
        AutoCompletion.getDefault().bind(Charset.class, textField);

        new TextPrompt(Charset.defaultCharset().name(), textField).setEnabled(false);

        onCharsetChange();

        textField.getDocument().addDocumentListener(listener);

        setLayout(new BorderLayout());
        add(textField);
    }

    private void enableProperties() {
        addPropertyChangeListener(evt -> {
            String p = evt.getPropertyName();
            if (p.equals(CHARSET_PROPERTY)) {
                onCharsetChange();
            }
        });
    }

    private void onCharsetChange() {
        if (listener.enabled) {
            listener.enabled = false;
            textField.setText(charset != null ? charset.name() : "");
            listener.enabled = true;
        }
    }

    @Nullable
    public Charset getCharset() {
        return charset;
    }

    public void setCharset(@Nullable Charset value) {
        Charset old = this.charset;
        this.charset = value;
        firePropertyChange(CHARSET_PROPERTY, old, this.charset);
    }

    //<editor-fold defaultstate="collapsed" desc="Internal implementation">
    private final class Listener implements DocumentListener {

        boolean enabled = true;

        @Override
        public void removeUpdate(DocumentEvent e) {
            changedUpdate(e);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            changedUpdate(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            if (enabled) {
                enabled = false;
                if (textField.getText().isEmpty()) {
                    setCharset(null);
                } else {
                    Charset charset = Parser.onCharset().parse(textField.getText());
                    if (charset != null) {
                        setCharset(charset);
                    }
                }
                enabled = true;
            }
        }
    }

    @Override
    public void requestFocus() {
        textField.requestFocus();
    }

    @Override
    public void setBorder(Border border) {
        textField.setBorder(border);
    }

    @Override
    public Border getBorder() {
        return textField.getBorder();
    }
    //</editor-fold>
}
