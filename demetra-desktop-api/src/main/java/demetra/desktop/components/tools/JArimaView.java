/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.components.tools;

import demetra.desktop.design.SwingComponent;
import demetra.desktop.util.NbComponents;
import demetra.desktop.components.JHtmlView;
import demetra.html.HtmlStream;
import demetra.html.HtmlTag;
import demetra.html.HtmlUtil;
import demetra.html.modelling.HtmlArima;
import demetra.html.modelling.HtmlSarimaPolynomials;
import internal.uihelpers.ModelInformationProvider;
import nbbrd.design.SkipProcessing;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import jdplus.arima.IArimaModel;
import jdplus.sarima.SarimaModel;

/**
 * @author Kristof Bayens
 */
@SwingComponent
@SkipProcessing(target = SwingComponent.class, reason = "parameters in constructor")
public final class JArimaView extends JComponent {

    private final PiView spectrumPanel_;
    private final JHtmlView documentPanel_;

    public JArimaView(Map<String, ? extends IArimaModel> models) {
        setLayout(new BorderLayout());
        ModelInformationProvider spectrum = new ModelInformationProvider(models);
        spectrum.setFrequency(12);
        spectrum.setInformation(ModelInformationProvider.SPECTRUM);
        spectrumPanel_ = new PiView(spectrum);
        ModelInformationProvider ac = new ModelInformationProvider(models);
        ac.setFrequency(12);
        ac.setInformation(ModelInformationProvider.AUTOCORRELATIONS);

        documentPanel_ = new JHtmlView();

        JSplitPane split = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, spectrumPanel_, NbComponents.newJScrollPane(documentPanel_));
        split.setDividerLocation(0.5);
        split.setResizeWeight(0.5);

        add(split, BorderLayout.CENTER);

        displayHtml(models);
    }

    private void displayHtml(Map<String, ? extends IArimaModel> models) {
        documentPanel_.setHtml(HtmlUtil.toString(o -> write(o, models)));
    }

    public void write(HtmlStream stream, Map<String, ? extends IArimaModel> models) throws IOException {
        stream.open();
        IArimaModel[] m = new IArimaModel[models.size()];
        int cur = 0;
        for (Entry<String, ? extends IArimaModel> o : models.entrySet()) {
            IArimaModel model = o.getValue();
            m[cur++] = model;
            if (model instanceof SarimaModel) {
                SarimaModel sarima = (SarimaModel) model;
                HtmlSarimaPolynomials document = new HtmlSarimaPolynomials(sarima);
                StringBuilder title = new StringBuilder();
                title.append(o.getKey()).append(" ").append(sarima.orders().toString());
                stream.write(HtmlTag.HEADER4, title.toString()).newLine();
                document.write(stream);
            } else {
                stream.write(HtmlTag.HEADER4, o.getKey()).newLine();
                HtmlArima document = new HtmlArima(model);
                document.write(stream);
            }
            stream.newLine();
        }
    }
}
