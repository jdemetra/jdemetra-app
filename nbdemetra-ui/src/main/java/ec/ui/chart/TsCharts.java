package ec.ui.chart;

import ec.tss.TsCollection;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.ui.interfaces.ITsChart.LinesThickness;
import ec.util.chart.swing.HighlightedXYItemRenderer;
import ec.util.chart.swing.SwingColorSchemeSupport;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.List;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

/**
 *
 * @author Jeremy Demortier
 * @author Philippe Charles
 */
public final class TsCharts {

    // CONSTANTS
    public static final RectangleInsets CHART_PADDING = new RectangleInsets(5, 5, 5, 5);
    public static final Color CHART_TICK_LABEL_COLOR = Color.GRAY;
    public static final Font CHART_TITLE_FONT = new Font(Font.SANS_SERIF, Font.ITALIC, 13);
    public static final Font NO_DATA_FONT = new Font(Font.SANS_SERIF, Font.BOLD | Font.ITALIC, 12);
    public static final Paint NO_DATA_PAINT = Color.LIGHT_GRAY;
    public static final int ALPHA = 50;
    public static final Font ITEM_LABEL_FONT = LegendTitle.DEFAULT_ITEM_FONT;
    private static final BasicStroke STROKE1 = new BasicStroke(1.0f);
    private static final BasicStroke STROKE2 = new BasicStroke(2.0f);
    private static final BasicStroke STROKE3 = new BasicStroke(3.0f);

    private TsCharts() {
        // static class
    }

    public static Stroke getNormalStroke(LinesThickness thickness) {
        return thickness == LinesThickness.Thin ? STROKE1 : STROKE2;
    }

    public static Stroke getStrongStroke(LinesThickness thickness) {
        return thickness == LinesThickness.Thin ? STROKE2 : STROKE3;
    }

    @Deprecated
    public static StandardXYItemLabelGenerator getXYItemLabelGenerator(DataFormat dataFormat) {
        DateFormat dateFormat = dataFormat.newDateFormat();
        NumberFormat numberFormat = dataFormat.newNumberFormat();
        return new StandardXYItemLabelGenerator("{1}: {2}", dateFormat, numberFormat);
    }

    @Deprecated
    public static XYSeriesLabelGenerator getXYSeriesLabelGenerator(final TsCollection col) {
        return (XYDataset dataset, int series) -> col.get(series).getName();
    }

    public static XYDataset newSparklineDataset(TsData data) {
        return new TsDataAsXYDataset(data);
    }

    private static class TsDataAsXYDataset extends AbstractXYDataset {

        final TsData data;

        public TsDataAsXYDataset(TsData data) {
            this.data = data;
        }

        @Override
        public int getSeriesCount() {
            return 1;
        }

        @Override
        public Comparable getSeriesKey(int i) {
            return "";
        }

        @Override
        public int getItemCount(int i) {
            return data.getLength();
        }

        @Override
        public Number getX(int i, int i1) {
            return i1;
        }

        @Override
        public Number getY(int i, int i1) {
            return data.get(i1);
        }
    }

    @Deprecated
    public static void setDataFormat(XYPlot plot, DataFormat dataFormat) {
        StandardXYItemLabelGenerator itemLabelGenerator = TsCharts.getXYItemLabelGenerator(dataFormat);
        plot.getRenderer().setBaseItemLabelGenerator(itemLabelGenerator);
        ((DateAxis) plot.getDomainAxis()).setDateFormatOverride(itemLabelGenerator.getXDateFormat());
    }

    @Deprecated
    public static void render(JFreeChart chart, SwingColorSchemeSupport support, LinesThickness linesThickness, RenderingStrategy strategy) {
        chart.setBackgroundPaint(support.getBackColor());
        if (chart.getPlot() instanceof CombinedDomainXYPlot) {
            for (XYPlot o : (List<XYPlot>) ((CombinedDomainXYPlot) chart.getPlot()).getSubplots()) {
                render(o, support, linesThickness, strategy);
            }
        } else if (chart.getPlot() instanceof XYPlot) {
            render((XYPlot) chart.getPlot(), support, linesThickness, strategy);
        }
    }

    @Deprecated
    public static void render(XYPlot plot, SwingColorSchemeSupport support, LinesThickness linesThickness, RenderingStrategy strategy) {
        plot.setBackgroundPaint(support.getPlotColor());
        plot.setDomainGridlinePaint(support.getGridColor());
        plot.setRangeGridlinePaint(support.getGridColor());

        int mainSize = plot.getDataset().getSeriesCount();

        XYItemRenderer renderer = plot.getRenderer();
        for (int i = 0; i < mainSize; i++) {
            Paint paint = strategy.getColor(i, support);
            Stroke stroke = strategy.getStroke(i, linesThickness);
            boolean visibleInLegend = strategy.isVisibleInLegend(i);
            renderer.setSeriesPaint(i, paint);
            renderer.setSeriesStroke(i, stroke);
            renderer.setSeriesOutlineStroke(i, stroke);
            renderer.setSeriesVisibleInLegend(i, visibleInLegend);
            if (renderer instanceof HighlightedXYItemRenderer) {
                renderer.setSeriesItemLabelPaint(i, support.getPlotColor());
                HighlightedXYItemRenderer hr = (HighlightedXYItemRenderer) renderer;
                hr.setSeriesItemLabelBackgroundPaint(i, paint);
//                hr.setSeriesItemLabelOutlinePaint(i, support.getPlotColor());
//                hr.setSeriesItemLabelOutlineStroke(i, stroke);
            } else {
                renderer.setSeriesItemLabelPaint(i, paint);
            }
        }
        if (renderer instanceof XYLineAndShapeRenderer) {
            ((XYLineAndShapeRenderer) renderer).setBaseFillPaint(support.getPlotColor());
        }
    }

    @Deprecated
    public interface RenderingStrategy {

        Color getColor(int index, SwingColorSchemeSupport support);

        Stroke getStroke(int index, LinesThickness linesThickness);

        boolean isVisibleInLegend(int index);
    }

    @Deprecated
    public static class DefaultRenderingStrategy implements RenderingStrategy {

        @Override
        public Color getColor(int index, SwingColorSchemeSupport support) {
            return support.getLineColor(index);
        }

        @Override
        public Stroke getStroke(int index, LinesThickness linesThickness) {
            return TsCharts.getNormalStroke(linesThickness);
        }

        @Override
        public boolean isVisibleInLegend(int index) {
            return true;
        }
    }

    @Deprecated
    public static abstract class SelectionRenderingStrategy implements RenderingStrategy {

        abstract boolean isSelected(int index);

        @Override
        public Color getColor(int index, SwingColorSchemeSupport support) {
            Color color = support.getLineColor(index);
            return isSelected(index) ? color : SwingColorSchemeSupport.withAlpha(color, TsCharts.ALPHA);
        }

        @Override
        public Stroke getStroke(int index, LinesThickness linesThickness) {
            return isSelected(index) ? TsCharts.getStrongStroke(linesThickness) : TsCharts.getNormalStroke(linesThickness);
        }

        @Override
        public boolean isVisibleInLegend(int index) {
            return true;
        }
    }

    @Deprecated
    public static abstract class DndRenderingStrategy implements RenderingStrategy {

        abstract boolean isDnd(int index);

        @Override
        public Color getColor(int index, SwingColorSchemeSupport support) {
            Color color = support.getLineColor(index);
            return isDnd(index) ? color : SwingColorSchemeSupport.withAlpha(color, TsCharts.ALPHA);
        }

        @Override
        public Stroke getStroke(int index, LinesThickness linesThickness) {
            return isDnd(index) ? TsCharts.getStrongStroke(linesThickness) : TsCharts.getNormalStroke(linesThickness);
        }

        @Override
        public boolean isVisibleInLegend(int index) {
            return !isDnd(index);
        }
    }
}
