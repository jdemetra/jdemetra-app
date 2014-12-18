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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private boolean lastPaintException = false;

    public JHtmlPane() {
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

        initStyleSheet();
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

    @Override
    @Deprecated
    public void layout() {
        try {
            super.layout();
        } catch (ArrayIndexOutOfBoundsException aioobE) {
            //HACK - workaround for issue #168988
            StackTraceElement[] stack = aioobE.getStackTrace();
            if (stack.length > 0 && stack[0].getClassName().endsWith("BoxView")) { //NOI18N
                Logger.getLogger(JHtmlPane.class.getName()).log(Level.INFO, null, aioobE);
            } else {
                throw aioobE;
            }
        }
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

    private void initStyleSheet() {
        StyleSheet ss = new StyleSheet();
        ss.addRule("body {font-family: arial, verdana;}");
        ss.addRule("body {font-size: 11;}");
        ss.addRule("h4 {color: blue;}");
        ss.addRule("td, th{text-align: right; margin-left: 5px; margin-right: 5 px;}");
        ss.addRule("table {border: solid;}");
        setStyleSheet(ss);
    }

    public void setStyleSheet(StyleSheet s) {
        ((HTMLEditorKit) getEditorKit()).setStyleSheet(s);
        // refresh stylesheet
        String text = getText();
        setDocument(getEditorKit().createDefaultDocument());
        setText(text);
    }
}
