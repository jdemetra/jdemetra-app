/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.util.chart.swing;

import ec.util.chart.ColorSchemeSupport;
import ec.util.chart.ObsFunction;
import ec.util.chart.ObsPredicate;
import ec.util.chart.SeriesFunction;
import ec.util.chart.SeriesPredicate;
import ec.util.chart.TimeSeriesChart;
import ec.util.chart.impl.SmartColorScheme;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import javax.swing.JComponent;
import org.jfree.data.xy.IntervalXYDataset;

/**
 *
 * @author Philippe Charles
 */
abstract class ATimeSeriesChart extends JComponent implements TimeSeriesChart<IntervalXYDataset, ColorSchemeSupport<? extends Color>> {

    // PROPERTIES DEFINITION
    public static final String COLOR_SCHEME_SUPPORT_PROPERTY = "colorSchemeSupport";
    public static final String LINE_THICKNESS_PROPERTY = "lineThickness";
    public static final String PERIOD_FORMAT_PROPERTY = "periodFormat";
    public static final String VALUE_FORMAT_PROPERTY = "valueFormat";
    public static final String SERIES_RENDERER_PROPERTY = "seriesRenderer";
    public static final String SERIES_FORMATTER_PROPERTY = "seriesFormatter";
    public static final String OBS_FORMATTER_PROPERTY = "obsFormatter";
    public static final String DASH_PREDICATE_PROPERTY = "dashPredicate";
    public static final String LEGEND_VISIBILITY_PREDICATE_PROPERTY = "legendVisibilityPredicate";
    public static final String PLOT_DISPATCHER_PROPERTY = "plotDispatcher";
    public static final String DATASET_PROPERTY = "dataset";
    public static final String TITLE_PROPERTY = "title";
    public static final String NO_DATA_MESSAGE_PROPERTY = "noDataMessage";
    public static final String PLOT_WEIGHTS_PROPERTY = "plotWeights";
    public static final String ELEMENT_VISIBLE_PROPERTY = "elementVisible";
    public static final String CROSSHAIR_TYPE_PROPERTY = "crosshairType";
    // DEFAULT PROPERTIES
    private static final ColorSchemeSupport<? extends Color> DEFAULT_COLOR_SCHEME_SUPPORT = SwingColorSchemeSupport.from(new SmartColorScheme());
    private static final LineStrokes DEFAULT_LINE_STROKES = new LineStrokes(1f);
    private static final String DEFAULT_PERIOD_FORMAT = "yyyy-MM";
    private static final String DEFAULT_VALUE_FORMAT = "0";
    private static final SeriesFunction<RendererType> DEFAULT_SERIES_RENDERER = SeriesFunction.always(RendererType.LINE);
    private static final SeriesFunction<String> DEFAULT_SERIES_FORMATTER = SeriesFunction.format("Series %d");
    private static final ObsFunction<String> DEFAULT_OBS_FORMATTER = ObsFunction.format("Series %d, Obs %d");
    private static final ObsPredicate DEFAULT_DASH_PREDICATE = ObsPredicate.alwaysFalse();
    private static final SeriesPredicate DEFAULT_LEGEND_VISIBILITY_PREDICATE = SeriesPredicate.alwaysTrue();
    private static final SeriesFunction<Integer> DEFAULT_PLOT_DISPATCHER = SeriesFunction.always(0);
    private static final IntervalXYDataset DEFAULT_DATASET = Charts.emptyXYDataset();
    private static final String DEFAULT_TITLE = "";
    private static final String DEFAULT_NO_DATA_MESSAGE = "No data";
    private static final int[] DEFAULT_PLOT_WEIGHTS = new int[]{1};
    private static final CrosshairType DEFAULT_CROSSHAIR_TYPE = CrosshairType.NONE;
    // PROPERTIES
    protected ColorSchemeSupport<? extends Color> colorSchemeSupport;
    protected LineStrokes lineStrokes;
    protected DateFormat periodFormat;
    protected NumberFormat valueFormat;
    protected SeriesFunction<RendererType> seriesRenderer;
    protected SeriesFunction<String> seriesFormatter;
    protected ObsFunction<String> obsFormatter;
    protected ObsPredicate dashPredicate;
    protected SeriesPredicate legendVisibilityPredicate;
    protected SeriesFunction<Integer> plotDispatcher;
    protected IntervalXYDataset dataset;
    protected String title;
    protected String noDataMessage;
    protected int[] plotWeights;
    protected final boolean[] elementVisible;
    protected final List<RendererType> supportedRendererTypes;
    protected CrosshairType crosshairType;

    public ATimeSeriesChart(List<RendererType> supportedRendererTypes) {
        this.colorSchemeSupport = DEFAULT_COLOR_SCHEME_SUPPORT;
        this.lineStrokes = DEFAULT_LINE_STROKES;
        this.periodFormat = new SimpleDateFormat(DEFAULT_PERIOD_FORMAT);
        this.valueFormat = new DecimalFormat(DEFAULT_VALUE_FORMAT);
        this.seriesRenderer = DEFAULT_SERIES_RENDERER;
        this.seriesFormatter = DEFAULT_SERIES_FORMATTER;
        this.obsFormatter = DEFAULT_OBS_FORMATTER;
        this.dashPredicate = DEFAULT_DASH_PREDICATE;
        this.legendVisibilityPredicate = DEFAULT_LEGEND_VISIBILITY_PREDICATE;
        this.plotDispatcher = DEFAULT_PLOT_DISPATCHER;
        this.dataset = DEFAULT_DATASET;
        this.title = DEFAULT_TITLE;
        this.noDataMessage = DEFAULT_NO_DATA_MESSAGE;
        this.plotWeights = DEFAULT_PLOT_WEIGHTS.clone();
        this.elementVisible = new boolean[Element.values().length];
        this.supportedRendererTypes = supportedRendererTypes;
        this.crosshairType = DEFAULT_CROSSHAIR_TYPE;
        Arrays.fill(elementVisible, true);
    }

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    @Override
    public IntervalXYDataset getDataset() {
        return dataset;
    }

    @Override
    public void setDataset(IntervalXYDataset dataset) {
        IntervalXYDataset old = this.dataset;
        this.dataset = dataset != null ? dataset : DEFAULT_DATASET;
        firePropertyChange(DATASET_PROPERTY, old, this.dataset);
    }

    @Override
    public ColorSchemeSupport<? extends Color> getColorSchemeSupport() {
        return colorSchemeSupport;
    }

    @Override
    public void setColorSchemeSupport(ColorSchemeSupport<? extends Color> colorSchemeSupport) {
        ColorSchemeSupport<? extends Color> old = this.colorSchemeSupport;
        this.colorSchemeSupport = colorSchemeSupport != null ? colorSchemeSupport : DEFAULT_COLOR_SCHEME_SUPPORT;
        firePropertyChange(COLOR_SCHEME_SUPPORT_PROPERTY, old, this.colorSchemeSupport);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        String old = this.title;
        this.title = title != null ? title : DEFAULT_TITLE;
        firePropertyChange(TITLE_PROPERTY, old, this.title);
    }

    @Override
    public String getNoDataMessage() {
        return noDataMessage;
    }

    @Override
    public void setNoDataMessage(String noDataMessage) {
        String old = this.noDataMessage;
        this.noDataMessage = noDataMessage != null ? noDataMessage : DEFAULT_NO_DATA_MESSAGE;
        firePropertyChange(NO_DATA_MESSAGE_PROPERTY, old, this.noDataMessage);
    }

    @Override
    public int[] getPlotWeights() {
        return plotWeights.clone();
    }

    @Override
    public void setPlotWeights(int[] weights) {
        int[] old = this.plotWeights;
        this.plotWeights = weights != null ? weights : DEFAULT_PLOT_WEIGHTS.clone();
        if (!Arrays.equals(old, this.plotWeights)) {
            firePropertyChange(PLOT_WEIGHTS_PROPERTY, old, this.plotWeights);
        }
    }

    @Override
    public SeriesFunction<Integer> getPlotDispatcher() {
        return plotDispatcher;
    }

    @Override
    public void setPlotDispatcher(SeriesFunction<Integer> plotDispatcher) {
        SeriesFunction<Integer> old = this.plotDispatcher;
        this.plotDispatcher = plotDispatcher != null ? plotDispatcher : DEFAULT_PLOT_DISPATCHER;
        firePropertyChange(PLOT_DISPATCHER_PROPERTY, old, this.plotDispatcher);
    }

    @Override
    public float getLineThickness() {
        return lineStrokes.getLineThickness();
    }

    @Override
    public void setLineThickness(float lineThickness) {
        LineStrokes old = this.lineStrokes;
        this.lineStrokes = lineThickness > 0 ? new LineStrokes(lineThickness) : DEFAULT_LINE_STROKES;
        firePropertyChange(LINE_THICKNESS_PROPERTY, old.lineThickness, this.lineStrokes.lineThickness);
    }

    @Override
    public DateFormat getPeriodFormat() {
        return periodFormat;
    }

    @Override
    public void setPeriodFormat(DateFormat periodFormat) {
        DateFormat old = this.periodFormat;
        this.periodFormat = periodFormat != null ? periodFormat : new SimpleDateFormat(DEFAULT_PERIOD_FORMAT);
        firePropertyChange(PERIOD_FORMAT_PROPERTY, old, this.periodFormat);
    }

    @Override
    public NumberFormat getValueFormat() {
        return valueFormat;
    }

    @Override
    public void setValueFormat(NumberFormat valueFormat) {
        NumberFormat old = this.valueFormat;
        this.valueFormat = valueFormat != null ? valueFormat : new DecimalFormat(DEFAULT_VALUE_FORMAT);
        firePropertyChange(VALUE_FORMAT_PROPERTY, old, this.valueFormat);
    }

    @Override
    public SeriesFunction<RendererType> getSeriesRenderer() {
        return seriesRenderer;
    }

    @Override
    public void setSeriesRenderer(SeriesFunction<RendererType> renderer) {
        SeriesFunction<RendererType> old = this.seriesRenderer;
        this.seriesRenderer = renderer != null ? renderer : DEFAULT_SERIES_RENDERER;
        firePropertyChange(SERIES_RENDERER_PROPERTY, old, this.seriesRenderer);
    }

    @Override
    public SeriesFunction<String> getSeriesFormatter() {
        return seriesFormatter;
    }

    @Override
    public void setSeriesFormatter(SeriesFunction<String> seriesFormatter) {
        SeriesFunction<String> old = this.seriesFormatter;
        this.seriesFormatter = seriesFormatter != null ? seriesFormatter : DEFAULT_SERIES_FORMATTER;
        firePropertyChange(SERIES_FORMATTER_PROPERTY, old, this.seriesFormatter);
    }

    @Override
    public ObsFunction<String> getObsFormatter() {
        return obsFormatter;
    }

    @Override
    public void setObsFormatter(ObsFunction<String> obsFormatter) {
        ObsFunction<String> old = this.obsFormatter;
        this.obsFormatter = obsFormatter != null ? obsFormatter : DEFAULT_OBS_FORMATTER;
        firePropertyChange(OBS_FORMATTER_PROPERTY, old, this.obsFormatter);
    }

    @Override
    public ObsPredicate getDashPredicate() {
        return dashPredicate;
    }

    @Override
    public void setDashPredicate(ObsPredicate predicate) {
        ObsPredicate old = this.dashPredicate;
        this.dashPredicate = predicate != null ? predicate : DEFAULT_DASH_PREDICATE;
        firePropertyChange(DASH_PREDICATE_PROPERTY, old, this.dashPredicate);
    }

    @Override
    public SeriesPredicate getLegendVisibilityPredicate() {
        return legendVisibilityPredicate;
    }

    @Override
    public void setLegendVisibilityPredicate(SeriesPredicate predicate) {
        SeriesPredicate old = this.legendVisibilityPredicate;
        this.legendVisibilityPredicate = predicate != null ? predicate : DEFAULT_LEGEND_VISIBILITY_PREDICATE;
        firePropertyChange(LEGEND_VISIBILITY_PREDICATE_PROPERTY, old, this.legendVisibilityPredicate);
    }

    @Override
    public boolean isElementVisible(Element element) {
        return elementVisible[element.ordinal()];
    }

    @Override
    public void setElementVisible(Element element, boolean visible) {
        boolean old = elementVisible[element.ordinal()];
        elementVisible[element.ordinal()] = visible;
        firePropertyChange(ELEMENT_VISIBLE_PROPERTY, old, visible);
    }

    @Override
    public CrosshairType getCrosshairType() {
        return crosshairType;
    }

    @Override
    public void setCrosshairType(CrosshairType crosshairType) {
        CrosshairType old = this.crosshairType;
        this.crosshairType = crosshairType != null ? crosshairType : DEFAULT_CROSSHAIR_TYPE;
        firePropertyChange(CROSSHAIR_TYPE_PROPERTY, old, this.crosshairType);
    }
    //</editor-fold>

    @Override
    public EnumSet<RendererType> getSupportedRendererTypes() {
        return EnumSet.copyOf(supportedRendererTypes);
    }

    static final class LineStrokes {

        private final float lineThickness;
        private final Stroke[] strokes;

        public LineStrokes(float lineThickness) {
            this.lineThickness = lineThickness;
            this.strokes = new Stroke[4];
            strokes[0] = new BasicStroke(lineThickness);
            strokes[1] = new BasicStroke(lineThickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{6.0f, 6.0f}, 0.0f);
            strokes[2] = new BasicStroke(lineThickness + 1);
            strokes[3] = new BasicStroke(lineThickness + 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{6.0f, 6.0f}, 0.0f);
        }

        public float getLineThickness() {
            return lineThickness;
        }

        public Stroke getStroke(boolean strong, boolean dash) {
            return strokes[(strong ? 1 : 0) * 2 + (dash ? 1 : 0)];
        }
    }
}
