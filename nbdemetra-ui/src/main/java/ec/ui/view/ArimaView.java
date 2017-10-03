/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view;

import ec.nbdemetra.ui.ComponentFactory;
import ec.nbdemetra.ui.NbComponents;
import ec.tss.html.HtmlStream;
import ec.tss.html.HtmlTag;
import ec.tss.html.HtmlUtil;
import ec.tss.html.implementation.HtmlArima;
import ec.tss.html.implementation.HtmlSarimaPolynomials;
import ec.tstoolkit.arima.IArimaModel;
import ec.tstoolkit.sarima.SarimaModel;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.uihelper.ModelInformationProvider;
import ec.ui.AHtmlView;
import java.awt.BorderLayout;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JComponent;
import javax.swing.JSplitPane;

/**
 *
 * @author Kristof Bayens
 */
public class ArimaView extends JComponent {

    private final PiView spectrumPanel_;
    private final AHtmlView documentPanel_;

    public ArimaView(Map<String, ? extends IArimaModel> models) {
        setLayout(new BorderLayout());
        ModelInformationProvider spectrum = new ModelInformationProvider(models);
        spectrum.setFrequency(TsFrequency.Monthly);
        spectrum.setInformation(ModelInformationProvider.SPECTRUM);
        spectrumPanel_ = new PiView(spectrum);
        ModelInformationProvider ac = new ModelInformationProvider(models);
        ac.setFrequency(TsFrequency.Monthly);
        ac.setInformation(ModelInformationProvider.AUTOCORRELATIONS);

        documentPanel_ = ComponentFactory.getDefault().newHtmlView();

        JSplitPane split = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, spectrumPanel_, NbComponents.newJScrollPane(documentPanel_));
        split.setDividerLocation(0.5);
        split.setResizeWeight(0.5);

        add(split, BorderLayout.CENTER);

        displayHtml(models);
    }

    private void displayHtml(Map<String, ? extends IArimaModel> models) {
        documentPanel_.loadContent(HtmlUtil.toString(o -> write(o, models)));
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
                title.append(o.getKey()).append(" ").append(sarima.getSpecification().toString());
                stream.write(HtmlTag.HEADER4, title.toString()).newLine();
                document.write(stream);
            } else {
                stream.write(HtmlTag.HEADER4, o.getKey()).newLine();
                HtmlArima document = new HtmlArima(model);
                document.write(stream);
            }
            stream.newLine();
        }
//            if (m.length == 2){
//                    stream.newLine();
//                    double d=AutoRegressiveDistance.compute(m[0], m[1], 200);
//                    stream.write(HtmlTag.HEADER4, "AR-Distance between the two models = " + new Formatter().format("%g4", d)).newLine();
//                    double md=MovingAverageDistance.compute2(m[0], m[1], 200);
//                    stream.write(HtmlTag.HEADER4, "MA-Distance between the two models = " + new Formatter().format("%g4", md)).newLine();
//                
//            }
    }
}
