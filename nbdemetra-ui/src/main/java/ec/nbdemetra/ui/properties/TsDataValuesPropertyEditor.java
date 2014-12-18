/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties;

import ec.nbdemetra.ui.DemetraUI;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.ui.chart.TsCharts;
import ec.util.chart.swing.Charts;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.beans.PropertyEditorSupport;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.RectangleInsets;
import org.openide.nodes.PropertyEditorRegistration;

/**
 *
 * @author Philippe Charles
 */
@PropertyEditorRegistration(targetType = TsData.class)
public class TsDataValuesPropertyEditor extends PropertyEditorSupport {

    static final RectangleInsets PADDING = new RectangleInsets(2, 2, 2, 2);
    final JFreeChart sparkLinePainter;
    final JLabel singleValuePainter;

    public TsDataValuesPropertyEditor() {
        Color disabledTextColor = new JTextField().getDisabledTextColor();
        sparkLinePainter = Charts.createSparkLineChart(null);
        sparkLinePainter.getXYPlot().getRenderer().setBasePaint(disabledTextColor);
        sparkLinePainter.setPadding(PADDING);
        singleValuePainter = new JLabel();
        singleValuePainter.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        singleValuePainter.setForeground(disabledTextColor);
    }

    @Override
    public boolean isPaintable() {
        return true;
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box) {
        TsData data = (TsData) getValue();
        if (data.getObsCount() > 1) {
            sparkLinePainter.getXYPlot().setDataset(TsCharts.newSparklineDataset(data));
            sparkLinePainter.draw((Graphics2D) gfx, box);
        } else {
            DataFormat dataFormat = DemetraUI.getDefault().getDataFormat();
            String str = "Single: " + dataFormat.numberFormatter().formatAsString(data.get(0));
            singleValuePainter.setText(str);
            singleValuePainter.setBounds(box);
            singleValuePainter.paint(gfx);
        }
    }
}
