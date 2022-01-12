/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.core.datatransfer;

import com.google.common.base.Suppliers;
import demetra.timeseries.TsCollection;
import demetra.desktop.util.TransferHandlers;
import demetra.desktop.components.JTsChart;
import demetra.timeseries.Ts;
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

    public abstract Component getTsDragRendererComponent(List<Ts> selection);

    public BufferedImage getTsDragRendererImage(List<Ts> selection) {
        return TransferHandlers.paintComponent(getTsDragRendererComponent(selection));
    }

    public static TsDragRenderer asChart() {
        return new ChartRenderer();
    }

    public static TsDragRenderer asCount() {
        return new CountRenderer();
    }

    private static class ChartRenderer extends TsDragRenderer {

        final Supplier<JTsChart> supplier = Suppliers.memoize(ChartRenderer::createChart);

        @Override
        public Component getTsDragRendererComponent(List<Ts> selection) {
            JTsChart result = supplier.get();
            result.setTsCollection(TsCollection.of(selection));
            return result;
        }

        private static JTsChart createChart() {
            JTsChart c = new JTsChart();
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
        public Component getTsDragRendererComponent(List<Ts> selection) {
            label.setText(String.valueOf(selection.size()));
            return component;
        }
    }
}
