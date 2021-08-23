package demetra.ui.jfreechart;

import demetra.timeseries.TsData;
import demetra.ui.components.parts.HasChart.LinesThickness;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
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

    public static XYDataset newSparklineDataset(TsData data) {
        return new TsDataAsXYDataset(data);
    }

    @lombok.AllArgsConstructor
    private static final class TsDataAsXYDataset extends AbstractXYDataset {

        final demetra.timeseries.TsData data;

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
            return data.length();
        }

        @Override
        public Number getX(int i, int i1) {
            return i1;
        }

        @Override
        public Number getY(int i, int i1) {
            return data.getValue(i1);
        }
    }
}
