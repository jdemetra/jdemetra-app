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
package ec.ui.html;

import demetra.ui.components.ComponentCommand;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JEditorPane;
import javax.swing.SwingConstants;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 *
 * @author Kristof Bayens
 * @author Philippe Charles
 * @see "SwingBrowserImpl"
 */
public class JHtmlPane extends JEditorPane {

    public static final String STYLE_SHEET_PROPERTY = "styleSheet";
    public static final String ZOOM_RATIO_PROPERTY = "zoomRatio";

    private boolean lastPaintException = false;

    private StyleSheet styleSheet;
    private int zoomRatio;

    public JHtmlPane() {
        this.styleSheet = new StyleSheet();
        this.zoomRatio = 100;

        setEditable(false);
        //when up/down arrow keys are pressed, ensure the whole browser content 
        //scrolls up/down instead of moving the caret position only
        ActionMap actionMap = getActionMap();
        actionMap.put(DefaultEditorKit.upAction, new ScrollAction(-1));
        actionMap.put(DefaultEditorKit.downAction, new ScrollAction(1));

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

        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case STYLE_SHEET_PROPERTY:
                        onStyleSheetChange();
                        break;
                    case ZOOM_RATIO_PROPERTY:
                        onZoomRatioChange();
                        break;
                }
            }
        });
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

    public StyleSheet getStyleSheet() {
        return styleSheet;
    }

    public void setStyleSheet(StyleSheet styleSheet) {
        StyleSheet old = this.styleSheet;
        this.styleSheet = styleSheet != null ? styleSheet : new StyleSheet();
        firePropertyChange(STYLE_SHEET_PROPERTY, old, this.styleSheet);
    }

    public int getZoomRatio() {
        return zoomRatio;
    }

    public void setZoomRatio(int zoomRatio) {
        int old = this.zoomRatio;
        this.zoomRatio = zoomRatio >= 10 && zoomRatio <= 200 ? zoomRatio : 100;
        firePropertyChange(ZOOM_RATIO_PROPERTY, old, this.zoomRatio);
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
        newStyle.addRule("html { font-size: " + (getFont().getSize() * zoomRatio / 100) + "; }");

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

    static final class ApplyZoomRatioCommand extends ComponentCommand<JHtmlPane> {

        private final int zoomRatio;

        ApplyZoomRatioCommand(int zoomRatio) {
            super(ZOOM_RATIO_PROPERTY);
            this.zoomRatio = zoomRatio;
        }

        @Override
        public void execute(JHtmlPane component) throws Exception {
            component.setZoomRatio(zoomRatio);
        }

        @Override
        public boolean isSelected(JHtmlPane component) {
            return component.getZoomRatio() == zoomRatio;
        }
    }

    private static class FilteredStyleSheet extends StyleSheet {

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
