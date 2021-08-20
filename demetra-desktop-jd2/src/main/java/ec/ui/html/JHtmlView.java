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

import demetra.ui.DemetraOptions;
import demetra.ui.util.NbComponents;
import ec.ui.AHtmlView;
import ec.util.various.swing.JCommand;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JSlider;
import javax.swing.text.html.StyleSheet;
import org.openide.util.WeakListeners;

/**
 *
 * @author Philippe Charles
 */
public final class JHtmlView extends AHtmlView {

    private final JHtmlPane browser;

    public JHtmlView() {
        this.browser = new JHtmlPane();
        browser.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        browser.setComponentPopupMenu(createMenu().getPopupMenu());
        browser.setZoomRatio(DemetraOptions.getDefault().getHtmlZoomRatio());

        DEFAULT_STYLE_CMD.executeSafely(browser);

        DemetraOptions.getDefault().addPropertyChangeListener(WeakListeners.propertyChange(o -> browser.setZoomRatio(DemetraOptions.getDefault().getHtmlZoomRatio()), DemetraOptions.HTML_ZOOM_RATIO_PROPERTY, this));

        setLayout(new BorderLayout());
        add(NbComponents.newJScrollPane(browser), BorderLayout.CENTER);
    }

    @Override
    public void loadContent(String content) {
        browser.setText(content);
        browser.setCaretPosition(0);
    }

    private JMenu createMenu() {
        JMenu result = new JMenu();
        result.add(COPY_TEXT_CMD.toAction(browser)).setText("Copy");
        result.add(COPY_HTML_CMD.toAction(browser)).setText("Copy html");
        result.addSeparator();
        result.add(createZoomMenu()).setText("Zoom");
        return result;
    }

    private JMenu createZoomMenu() {
        JMenu result = new JMenu();
        final JSlider slider = new JSlider(10, 200, 100);
        {
            slider.setPreferredSize(new Dimension(50, slider.getPreferredSize().height));
            slider.addChangeListener(evt -> browser.setZoomRatio(slider.getValue()));
            browser.addPropertyChangeListener(JHtmlPane.ZOOM_RATIO_PROPERTY, evt -> slider.setValue(browser.getZoomRatio()));
        }
        result.add(slider);
        for (int o : new int[]{200, 175, 150, 125, 100, 75}) {
            result.add(new JCheckBoxMenuItem(new JHtmlPane.ApplyZoomRatioCommand(o).toAction(browser))).setText(o + "%");
        }
        return result;
    }

    private static final JCommand<JHtmlPane> COPY_TEXT_CMD = JCommand.of(JHtmlPane::copyText);
    private static final JCommand<JHtmlPane> COPY_HTML_CMD = JCommand.of(JHtmlPane::copyHtml);
    private static final JCommand<JHtmlPane> DEFAULT_STYLE_CMD = new JHtmlPane.ApplyStyleSheetCommand(newStyleSheet(JHtmlView::withClassic, JHtmlView::withBootstrap, JHtmlView::withSpecific));

    private static StyleSheet newStyleSheet(Consumer<StyleSheet>... rules) {
        StyleSheet result = new StyleSheet();
        for (Consumer<StyleSheet> o : rules) {
            o.accept(result);
        }
        return result;
    }

    private static void withClassic(StyleSheet s) {
//        s.addRule("body { font-family: arial, verdana; font-size: 11; }");
        s.addRule("h1 { font-weight: bold; font-size: 110%; text-decoration: underline; }");
        s.addRule("h2 { font-weight: bold; text-decoration: underline; }");
        s.addRule("h3 { text-decoration: underline; }");
        s.addRule("h4 { font-weight: italic; color: blue; }");
        s.addRule("th { text-align: right; margin-left: 5px; margin-right: 5 px; font-weight: bold; }");
        s.addRule("td { text-align: right; margin-left: 5px; margin-right: 5 px; }");
        s.addRule("table { border: solid; }");
    }

    private static void withBootstrap(StyleSheet s) {
        s.addRule(".text-left { text-align: left; }");
        s.addRule(".text-center { text-align: center; }");
        s.addRule(".text-right { text-align: right; }");

        s.addRule(".text-primary { color: #007bff; }");
        s.addRule(".text-secondary { color: #868e96; }");
        s.addRule(".text-success { color: #28a745; }");
        s.addRule(".text-danger { color: #dc3545; }");
        s.addRule(".text-warning { color: #ffc107; }");
        s.addRule(".text-info { color: #17a2b8; }");
        s.addRule(".text-light { color: #f8f9fa; }");
        s.addRule(".text-dark { color: #343a40; }");
        s.addRule(".text-white { color: #ffffff; }");

        s.addRule(".bg-primary { background-color: #007bff; }");
        s.addRule(".bg-secondary { background-color: #868e96; }");
        s.addRule(".bg-success { background-color: #28a745; }");
        s.addRule(".bg-danger { background-color: #dc3545; }");
        s.addRule(".bg-warning { background-color: #ffc107; }");
        s.addRule(".bg-info { background-color: #17a2b8; }");
        s.addRule(".bg-light { background-color: #f8f9fa; }");
        s.addRule(".bg-dark { background-color: #343a40; }");
        s.addRule(".bg-white { background-color: #ffffff; }");

        s.addRule(".font-weight-bold { font-weight: bold; }");
        s.addRule(".font-weight-normal { font-weight: normal; }");
        s.addRule(".font-italic { font-style: italic; }");

        s.addRule("strong { font-weight: bold; }");
        s.addRule("em { font-style: italic; }");
    }

    private static void withSpecific(StyleSheet s) {
        s.addRule(".tsdata-row-header { font-style: italic; margin-left: 10px; margin-right: 10px; }");

        s.addRule(".outlier-ao { background-color: #BFFF19; color: #323232; text-align: center; padding-left: 5px; padding-right: 5px; }");
        s.addRule(".outlier-ls { background-color: #FF4D19; color: #E6E6E6; text-align: center; padding-left: 5px; padding-right: 5px; }");
        s.addRule(".outlier-tc { background-color: #579BCC; color: #E6E6E6; text-align: center; padding-left: 5px; padding-right: 5px; }");
        s.addRule(".outlier-so { background-color: #FFCCCC; color: #000000; text-align: center; padding-left: 5px; padding-right: 5px; }");
    }
}
