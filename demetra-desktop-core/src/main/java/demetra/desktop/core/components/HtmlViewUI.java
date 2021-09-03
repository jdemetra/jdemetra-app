package demetra.desktop.core.components;

import demetra.desktop.DemetraOptions;
import demetra.desktop.components.JHtmlView;
import demetra.desktop.components.parts.HasZoomRatioSupport;
import demetra.desktop.util.NbComponents;
import ec.util.various.swing.JCommand;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.swing.*;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.util.function.Consumer;

final class HtmlViewUI implements InternalUI<JHtmlView> {

    private JHtmlView target;

    private final JHtmlPane browser = createBrowser();

    @Override
    public void install(@NonNull JHtmlView component) {
        target = component;

        enableProperties();

        target.setLayout(new BorderLayout());
        target.add(NbComponents.newJScrollPane(browser), BorderLayout.CENTER);
    }

    private void enableProperties() {
        target.addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case JHtmlView.HTML_PROPERTY:
                    onHtmlChange();
                    break;
            }
        });
    }

    private void onHtmlChange() {
        browser.setText(target.getHtml());
        browser.setCaretPosition(0);
    }

    private static JHtmlPane createBrowser() {
        JHtmlPane result = new JHtmlPane();
        result.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        result.setComponentPopupMenu(createMenu(result).getPopupMenu());
        result.setZoomRatio(DemetraOptions.getDefault().getHtmlZoomRatio());
        DEFAULT_STYLE_CMD.executeSafely(result);
        DemetraOptions.getDefault()
                .addWeakPropertyChangeListener(DemetraOptions.HTML_ZOOM_RATIO_PROPERTY, o -> result.setZoomRatio(DemetraOptions.getDefault().getHtmlZoomRatio()));
        return result;
    }

    private static JMenu createMenu(JHtmlPane browser) {
        JMenu result = new JMenu();
        result.add(COPY_TEXT_CMD.toAction(browser)).setText("Copy");
        result.add(COPY_HTML_CMD.toAction(browser)).setText("Copy html");
        result.addSeparator();
        result.add(HasZoomRatioSupport.newZoomRatioMenu(browser)).setText("Zoom");
        return result;
    }

    private static final JCommand<JHtmlPane> COPY_TEXT_CMD = JCommand.of(JHtmlPane::copyText);
    private static final JCommand<JHtmlPane> COPY_HTML_CMD = JCommand.of(JHtmlPane::copyHtml);

    private static final JCommand<JHtmlPane> DEFAULT_STYLE_CMD = new JHtmlPane.ApplyStyleSheetCommand(newStyleSheet(HtmlViewUI::withClassic, HtmlViewUI::withBootstrap, HtmlViewUI::withSpecific));

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
