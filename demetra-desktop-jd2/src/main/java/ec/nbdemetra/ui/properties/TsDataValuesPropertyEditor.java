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
package ec.nbdemetra.ui.properties;

import ec.nbdemetra.ui.DemetraUI;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.ui.chart.TsCharts;
import ec.util.chart.swing.Charts;
import ec.util.various.swing.StandardSwingColor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.beans.PropertyEditorSupport;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.RectangleInsets;
import org.openide.nodes.PropertyEditorRegistration;

/**
 *
 * @author Philippe Charles
 */
@PropertyEditorRegistration(targetType = TsData.class)
public class TsDataValuesPropertyEditor extends PropertyEditorSupport {

    private static final RectangleInsets PADDING = new RectangleInsets(2, 2, 2, 2);
    private final JFreeChart sparkLinePainter;
    private final JLabel singleValuePainter;

    public TsDataValuesPropertyEditor() {
        sparkLinePainter = Charts.createSparkLineChart(Charts.emptyXYDataset());
        sparkLinePainter.setPadding(PADDING);
        singleValuePainter = new JLabel();
        singleValuePainter.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        StandardSwingColor.TEXT_FIELD_INACTIVE_FOREGROUND.lookup().ifPresent(o -> {
            sparkLinePainter.getXYPlot().getRenderer().setBasePaint(o);
            singleValuePainter.setForeground(o);
        });
    }

    @Override
    public boolean isPaintable() {
        return true;
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box) {
        TsData data = (TsData) getValue();
        switch (data.getObsCount()) {
            case 0:
                singleValuePainter.setText("No obs");
                singleValuePainter.setBounds(box);
                singleValuePainter.paint(gfx);
                break;
            case 1:
                DataFormat dataFormat = DemetraUI.getDefault().getDataFormat();
                String str = "Single: " + dataFormat.numberFormatter().formatAsString(data.get(0));
                singleValuePainter.setText(str);
                singleValuePainter.setBounds(box);
                singleValuePainter.paint(gfx);
                break;
            default:
                sparkLinePainter.getXYPlot().setDataset(TsCharts.newSparklineDataset(data));
                sparkLinePainter.draw((Graphics2D) gfx, box);
                break;
        }
    }
}
