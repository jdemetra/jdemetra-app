/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.tss.datatransfer;

import com.google.common.base.Suppliers;
import ec.nbdemetra.ui.ComponentFactory;
import ec.nbdemetra.ui.awt.TransferHandlers;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsFactory;
import ec.ui.ATsChart;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Supplier;
import static javax.swing.BorderFactory.*;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Philippe Charles
 */
public abstract class TsDragRenderer {

    public abstract Component getTsDragRendererComponent(List<? extends Ts> selection);

    public BufferedImage getTsDragRendererImage(List<? extends Ts> selection) {
        return TransferHandlers.paintComponent(getTsDragRendererComponent(selection));
    }

    public static TsDragRenderer asChart() {
        return new ChartRenderer();
    }

    public static TsDragRenderer asCount() {
        return new CountRenderer();
    }

    private static class ChartRenderer extends TsDragRenderer {

        final Supplier<ATsChart> supplier = Suppliers.memoize(ChartRenderer::createChart);

        @Override
        public Component getTsDragRendererComponent(List<? extends Ts> selection) {
            TsCollection col = TsFactory.instance.createTsCollection();
            col.quietAppend(selection);
            ATsChart result = supplier.get();
            result.setTsCollection(col);
            return result;
        }

        private static ATsChart createChart() {
            ATsChart c = ComponentFactory.getDefault().newTsChart();
            c.setPreferredSize(new Dimension(150, 90));
            c.setAxisVisible(false);
            c.setLegendVisible(false);
            return c;
        }
    }

    private static class CountRenderer extends TsDragRenderer {

        final JPanel component;
        final JLabel label;

        public CountRenderer() {
            component = new JPanel();
            component.setBorder(createEmptyBorder(25, 25, 0, 0));
            component.setOpaque(false);
            label = new JLabel();
            label.setBackground(Color.BLACK);
            label.setForeground(Color.WHITE);
            label.setBorder(createEmptyBorder(0, 5, 0, 5));
            label.setOpaque(true);
            Font normal = label.getFont();
            label.setFont(normal.deriveFont(normal.getSize2D() * 2));
            component.add(label);
        }

        @Override
        public Component getTsDragRendererComponent(List<? extends Ts> selection) {
            label.setText(String.valueOf(selection.size()));
            return component;
        }
    }
}
