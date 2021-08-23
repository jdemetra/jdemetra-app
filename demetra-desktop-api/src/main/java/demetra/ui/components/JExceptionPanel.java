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
package demetra.ui.components;

import demetra.ui.util.IDialogDescriptorProvider;
import demetra.ui.util.NbComponents;
import static ec.util.chart.ColorSchemeSupport.toHex;
import ec.util.chart.impl.TangoColorScheme;
import ec.util.desktop.Desktop;
import ec.util.desktop.DesktopManager;
import ec.util.various.swing.JCommand;
import internal.ui.components.StackTracePrinter;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import org.openide.DialogDescriptor;
import static org.openide.DialogDescriptor.DEFAULT_ALIGN;
import static org.openide.NotifyDescriptor.DEFAULT_OPTION;
import static org.openide.NotifyDescriptor.OK_OPTION;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author Philippe Charles
 */
public final class JExceptionPanel extends JComponent implements IDialogDescriptorProvider {

    // METHOD FACTORIES
    public static JExceptionPanel create(Exception ex) {
        JExceptionPanel result = new JExceptionPanel();
        result.setException(ex);
        return result;
    }
    
    // PROPERTIES DEFINITIONS
    public static final String EXCEPTION_PROPERTY = "exception";
    
    // PROPERTIES
    protected Exception exception;
    
    // VISUAL COMPONENTS
    private final JEditorPane editorPane;

    public JExceptionPanel() {
        this.editorPane = new JEditorPane();
        HTMLEditorKit editor = new HTMLEditorKit();
        editor.setStyleSheet(createStyleSheet());
        editorPane.setEditorKit(editor);
        editorPane.setCaretPosition(0);
        editorPane.setEditable(false);
        editorPane.addHyperlinkListener(event -> {
            if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                Desktop desktop = DesktopManager.get();
                if (desktop.isSupported(Desktop.Action.SHOW_IN_FOLDER)) {
                    try {
                        File file = Utilities.toFile(event.getURL().toURI());
                        DesktopManager.get().showInFolder(file);
                    } catch (URISyntaxException | IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        });
        editorPane.setComponentPopupMenu(createMenu().getPopupMenu());

        setLayout(new BorderLayout());
        add(NbComponents.newJScrollPane(editorPane), BorderLayout.CENTER);

        addPropertyChangeListener(evt -> {
            String p = evt.getPropertyName();
            if (p.equals(EXCEPTION_PROPERTY)) {
                onExceptionChange();
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="Event handlers">
    public void onExceptionChange() {
        if (exception != null) {
            editorPane.setText(StackTracePrinter.htmlBuilder().toString(exception));
        } else {
            editorPane.setText("");
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public void setException(Exception exception) {
        Exception old = this.exception;
        this.exception = exception;
        firePropertyChange(EXCEPTION_PROPERTY, old, this.exception);
    }

    public Exception getException() {
        return exception;
    }
    //</editor-fold>

    private static StyleSheet createStyleSheet() {
        StyleSheet ss = new StyleSheet();
        ss.addRule("body { font-family: Courier; font-size : 10px monaco;}");
        ss.addRule(createColorRule(StackTracePrinter.NAME_CSS, TangoColorScheme.DARK_SKY_BLUE));
        ss.addRule(createColorRule(StackTracePrinter.MESSAGE_CSS, TangoColorScheme.CHOCOLATE));
        ss.addRule("a.message { text-decoration: underline; color: " + toHex(TangoColorScheme.CHOCOLATE) + "; }");
        ss.addRule(createColorRule(StackTracePrinter.KEYWORD_CSS, TangoColorScheme.ALUMINIUM6));
        ss.addRule(createColorRule(StackTracePrinter.ELEMENT_NAME_CSS, TangoColorScheme.ALUMINIUM5));
        ss.addRule(createColorRule(StackTracePrinter.ELEMENT_SOURCE_CSS, TangoColorScheme.DARK_BUTTER));
        return ss;
    }

    private static String createColorRule(String className, int color) {
        return "." + className + " {color: " + toHex(color) + ";}";
    }

    private JMenu createMenu() {
        JMenu result = new JMenu();
        result.add(CopyCmd.INSTANCE.toAction(editorPane)).setText("Copy");
        return result;
    }

    private static final class CopyCmd extends JCommand<JEditorPane> {

        private static final CopyCmd INSTANCE = new CopyCmd();

        @Override
        public void execute(JEditorPane c) throws Exception {
            if (c.getSelectedText() != null) {
                c.copy();
            } else {
                c.selectAll();
                c.copy();
                c.select(0, 0);
            }
        }
    }

    @Override
    public DialogDescriptor createDialogDescriptor(String title) {
        return new DialogDescriptor(this, title, true, DEFAULT_OPTION, OK_OPTION, DEFAULT_ALIGN, null, null);
    }
}
