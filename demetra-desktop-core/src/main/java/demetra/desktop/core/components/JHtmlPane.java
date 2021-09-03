/*
 * Copyright 2013 National Bank of Belgium
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
package demetra.desktop.core.components;

import demetra.desktop.components.ComponentCommand;
import demetra.desktop.components.parts.HasZoomRatio;
import demetra.desktop.components.parts.HasZoomRatioSupport;
import demetra.desktop.design.SwingProperty;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Kristof Bayens
 * @author Philippe Charles
 * @see "SwingBrowserImpl"
 */
final class JHtmlPane extends JEditorPane implements HasZoomRatio {

    @SwingProperty
    public static final String STYLE_SHEET_PROPERTY = "styleSheet";
    private static final Supplier<StyleSheet> DEFAULT_STYLE_SHEET = StyleSheet::new;
    private StyleSheet styleSheet = DEFAULT_STYLE_SHEET.get();

    @lombok.experimental.Delegate
    private final HasZoomRatio zoomRatio;

    private boolean lastPaintException = false;

    public JHtmlPane() {
        this.zoomRatio = HasZoomRatioSupport.of(this::firePropertyChange);

        registerActions();
        initComponent();
        enableProperties();
    }

    private void initComponent() {
        setEditable(false);
        setEditorKit(new HTMLEditorKit() {
//            @Override
//            public Document createDefaultDocument() {
//                StyleSheet styles = getStyleSheet();
//                //#200472 - hack to make JDK 1.7 javadoc readable
//                StyleSheet ss = new FilteredStyleSheet();
//                ss.addStyleSheet(styles);
//
//                HTMLDocument doc = new HTMLDocument(ss);
//                doc.setParser(getParser());
//                doc.setAsynchronousLoadPriority(4);
//                doc.setTokenThreshold(100);
//                return doc;
//            }
        });
    }

    private void registerActions() {
        //when up/down arrow keys are pressed, ensure the whole browser content
        //scrolls up/down instead of moving the caret position only
        ActionMap actionMap = getActionMap();
        actionMap.put(DefaultEditorKit.upAction, new ScrollAction(-1));
        actionMap.put(DefaultEditorKit.downAction, new ScrollAction(1));
    }

    private void enableProperties() {
        addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case STYLE_SHEET_PROPERTY:
                    onStyleSheetChange();
                    break;
                case ZOOM_RATIO_PROPERTY:
                    onZoomRatioChange();
                    break;
            }
        });
    }

    public StyleSheet getStyleSheet() {
        return styleSheet;
    }

    public void setStyleSheet(StyleSheet styleSheet) {
        StyleSheet old = this.styleSheet;
        this.styleSheet = styleSheet != null ? styleSheet : DEFAULT_STYLE_SHEET.get();
        firePropertyChange(STYLE_SHEET_PROPERTY, old, this.styleSheet);
    }

    @Override
    public Dimension getPreferredSize() {
        try {
            return super.getPreferredSize();
        } catch (RuntimeException e) {
            //Bug in javax.swing.text.html.BlockView
            return new Dimension(400, 600);
        }
    }

    @Override
    public void paint(Graphics g) {
        try {
            super.paint(g);
            lastPaintException = false;
        } catch (RuntimeException e) {
            //Bug in javax.swing.text.html.BlockView
            //do nothing
            if (!lastPaintException) {
                repaint();
            }

            lastPaintException = true;
        }
    }

    @Override
    public void scrollToReference(String reference) {
        if (!isShowing() || null == getParent() || getWidth() < 1 || getHeight() < 1) {
            return;
        }
        super.scrollToReference(reference);
    }

    /**
     * An action to scroll the browser content up or down.
     */
    private class ScrollAction extends AbstractAction {

        int direction;

        public ScrollAction(int direction) {
            this.direction = direction;
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            Rectangle r = getVisibleRect();
            int increment = getScrollableUnitIncrement(r, SwingConstants.VERTICAL, direction);
            r.y += (increment * direction);
            scrollRectToVisible(r);
        }
    }

    private void onStyleSheetChange() {
        updateInternalStyle();
    }

    private void onZoomRatioChange() {
        updateInternalStyle();
    }

    private void updateInternalStyle() {
        StyleSheet newStyle = new StyleSheet();
        newStyle.addStyleSheet(styleSheet);
        newStyle.addRule("html { font-size: " + (getFont().getSize() * getZoomRatio() / 100) + "; }");

        ((HTMLEditorKit) getEditorKit()).setStyleSheet(newStyle);
        // refresh stylesheet
        String text = getText();
        setDocument(getEditorKit().createDefaultDocument());
        setText(text);
    }

    static void copyText(JHtmlPane c) {
        if (c.getSelectedText() != null) {
            c.copy();
        } else {
            c.selectAll();
            c.copy();
            c.select(0, 0);
        }
    }

    static void copyHtml(JHtmlPane c) {
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        cb.setContents(new StringSelection(c.getText()), null);
    }

    static final class ApplyStyleSheetCommand extends ComponentCommand<JHtmlPane> {

        private final StyleSheet styleSheet;

        ApplyStyleSheetCommand(StyleSheet styleSheet) {
            super(STYLE_SHEET_PROPERTY);
            this.styleSheet = styleSheet;
        }

        @Override
        public void execute(JHtmlPane component) throws Exception {
            component.setStyleSheet(styleSheet);
        }

        @Override
        public boolean isSelected(JHtmlPane component) {
            return Objects.equals(component.getStyleSheet(), styleSheet);
        }
    }

    private static final class FilteredStyleSheet extends StyleSheet {

        @Override
        public void addCSSAttribute(MutableAttributeSet attr, CSS.Attribute key, String value) {
            value = fixFontSize(key, value);
            super.addCSSAttribute(attr, key, value);
        }

        @Override
        public boolean addCSSAttributeFromHTML(MutableAttributeSet attr, CSS.Attribute key, String value) {
            value = fixFontSize(key, value);
            return super.addCSSAttributeFromHTML(attr, key, value);
        }

        /**
         * CSS with e.g. 'font-size: 75%' makes the HTML unreadable in the
         * default JEditorPane
         *
         * @param key
         * @param value
         * @return
         */
        private static String fixFontSize(CSS.Attribute key, String value) {
            if ("font-size".equals(key.toString()) && null != value && value.endsWith("%")) {
                String strPercentage = value.replace("%", "");
                int percentage = Integer.parseInt(strPercentage);
                if (percentage < 100) {
                    value = "100%";
                }
            }
            return value;
        }
    }
}
