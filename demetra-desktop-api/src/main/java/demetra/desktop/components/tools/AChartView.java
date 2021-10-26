/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.components.tools;

import demetra.desktop.components.parts.HasColorScheme;
import demetra.desktop.components.parts.HasColorSchemeResolver;
import demetra.desktop.components.parts.HasColorSchemeSupport;
import ec.util.chart.swing.SwingColorSchemeSupport;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import javax.swing.JComponent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Kristof Bayens
 */
public abstract class AChartView extends JComponent implements HasColorScheme {

    // PROPERTIES
    protected PlotOrientation orientation_;
    protected int points_;
    protected double customXmin, customXmax, customYmin, customYmax;
    protected double baseminx_, basemaxx_, baseminy_, basemaxy_;
    protected double minx_, maxx_, miny_, maxy_;
    protected NumberTickUnit basetickunitx_, basetickunity_;
    protected NumberTickUnit tickunitx_, tickunity_;
    protected DecimalFormat basedecimalformat_;
    protected DecimalFormat decimalformat_;
    protected boolean zoomable_ = true;
    protected Comparable focus = "";
    // OTHER
    protected final JChartPanel chartPanel;
    protected final XYSeriesCollection seriesCollection;

    @lombok.experimental.Delegate
    private final HasColorScheme colorScheme = HasColorSchemeSupport.of(this::firePropertyChange);

    protected final HasColorSchemeResolver colorSchemeResolver = new HasColorSchemeResolver(colorScheme, this::onColorSchemeChange);

    public AChartView(int points, PlotOrientation orientation, double baseminx, double basemaxx, double baseminy, double basemaxy, NumberTickUnit basetickunitx, NumberTickUnit basetickunity, DecimalFormat baseformat) {
        setLayout(new BorderLayout());

        this.points_ = points;
        this.baseminx_ = baseminx;
        this.basemaxx_ = basemaxx;
        this.baseminy_ = baseminy;
        this.basemaxy_ = basemaxy;
        this.basetickunitx_ = basetickunitx;
        this.basetickunity_ = basetickunity;
        this.basedecimalformat_ = baseformat;
        this.orientation_ = orientation;

        SwingColorSchemeSupport themeSupport = colorSchemeResolver.resolve();

        this.seriesCollection = new XYSeriesCollection();
        this.chartPanel = new JChartPanel(ChartFactory.createLineChart(null, null, null, null, orientation_, false, false, false)); //, getDefaultMinX(), getDefaultMaxX(), getDefaultMinY(), getDefaultMaxY());

        chartPanel.addChartMouseListener(new ChartMouseListener() {

            @Override
            public void chartMouseClicked(ChartMouseEvent cme) {
                ChartEntity entity = cme.getEntity();
                if (entity != null && entity instanceof LegendItemEntity) {
                    LegendItemEntity ent = (LegendItemEntity) entity;
                    setFocus(ent.getSeriesKey());
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent cme) {
                //Nothing.
            }
        });
        chartPanel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    customXmin = e.getX();
                    customYmin = e.getY();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    customXmax = e.getX();
                    customYmax = e.getY();

                    //Pan
                    if (e.isControlDown()) {
                        double panDistance = Math.abs(Math.abs(chartPanel.getChartX(customXmax)) - Math.abs(chartPanel.getChartX(customXmin)));
                        if (customXmin > customXmax) {
                            panRight(panDistance);
                        } else {
                            panLeft(panDistance);
                        }
                    } //Zoom
                    else {
                        if (customXmax > customXmin && isZoomable()) {
                            boolean zoomTrigger1 = Math.abs(customXmax) - customXmin >= chartPanel.getZoomTriggerDistance();
                            boolean zoomTrigger2 = Math.abs(customYmax) - customYmin >= chartPanel.getZoomTriggerDistance();
                            if (zoomTrigger1 && zoomTrigger2) {
                                zoom(chartPanel.getChartX(customXmin), chartPanel.getChartX(customXmax), chartPanel.getChartY(customYmax), chartPanel.getChartY(customYmin));
                            }
                        }
                    }
                } else {
                    restoreBaseValues();
                }
                onDomainChange();
            }
        });

        restoreBaseValues();
        this.add(chartPanel, BorderLayout.CENTER);
    }

    // EVENT HANDLERS >
    abstract protected void onDomainChange();

    abstract protected void onColorSchemeChange();

    abstract protected void onFocusChange();
    // < EVENT HANDLERS

    // GETTERS/SETTERS >
    public double getBaseMinX() {
        return baseminx_;
    }

    public double getBaseMaxX() {
        return basemaxx_;
    }

    public double getBaseMinY() {
        return baseminy_;
    }

    public double getBaseMaxY() {
        return basemaxy_;
    }

    public NumberTickUnit getBaseTickUnitX() {
        return basetickunitx_;
    }

    public NumberTickUnit getBaseTickUnitY() {
        return basetickunity_;
    }

    public DecimalFormat getBaseDecimalFormat() {
        return basedecimalformat_;
    }

    public NumberTickUnit getTickUnitX() {
        return tickunitx_;
    }

    public void setTickUnitX(NumberTickUnit tickunit) {
        tickunitx_ = tickunit;
    }

    public NumberTickUnit getTickUnitY() {
        return tickunity_;
    }

    public void setTickUnitY(NumberTickUnit tickunit) {
        tickunity_ = tickunit;
    }

    public DecimalFormat getDecimalFormat() {
        return decimalformat_;
    }

    public void setDecimalFormat(DecimalFormat format) {
        decimalformat_ = format;
    }

    public int getPoints() {
        return points_;
    }

    public double getMinX() {
        return minx_;
    }

    public void setMinX(double value) {
        minx_ = value;
    }

    public double getMaxX() {
        return maxx_;
    }

    public void setMaxX(double value) {
        maxx_ = value;
    }

    public double getMinY() {
        return miny_;
    }

    public void setMinY(double value) {
        miny_ = value;
    }

    public double getMaxY() {
        return maxy_;
    }

    public void setMaxY(double value) {
        maxy_ = value;
    }

    public boolean isZoomable() {
        return zoomable_;
    }

    public void setZoomable(boolean value) {
        zoomable_ = value;
    }

    public void setFocus(Comparable key) {
        focus = focus.equals(key) ? "" : key;
        onFocusChange();
    }

    public Comparable getFocus() {
        return focus;
    }
    // < GETTERS/SETTERS

    protected int getFactorX() {
        double totaldefaultlength = Math.abs(getBaseMaxX()) - getBaseMinX();
        double totalcustomlength = Math.abs(getMaxX()) - getMinX();
        return (int) (totaldefaultlength / totalcustomlength);
    }

    protected int getFactorY() {
        double totaldefaultlength = Math.abs(getBaseMaxY()) - getBaseMinY();
        double totalcustomlength = Math.abs(getMaxY()) - getMinY();
        return (int) (totaldefaultlength / totalcustomlength);
    }

    protected boolean isBaseValues() {
        return getMinX() == getBaseMinX() && getMaxX() == getBaseMaxX();
    }

    protected void restoreBaseValues() {
        minx_ = getBaseMinX();
        maxx_ = getBaseMaxX();
        miny_ = getBaseMinY();
        maxy_ = getBaseMaxY();
        tickunitx_ = getBaseTickUnitX();
        tickunity_ = getBaseTickUnitY();
        decimalformat_ = getBaseDecimalFormat();
    }

    protected void setValues(double minx, double maxx, double miny, double maxy) {
        setMinX(minx);
        setMaxX(maxx);
        setMinY(miny);
        setMaxY(maxy);

        setTickUnitX(new PiNumberTickUnit(getBaseTickUnitX().getSize() / getFactorX()));
        setTickUnitY(new NumberTickUnit(getBaseTickUnitY().getSize() / getFactorY()));

        onDomainChange();
    }

    protected void zoom(double minx, double maxx, double miny, double maxy) {
        setValues(minx, maxx, miny, maxy);
    }

    protected void panLeft(double value) {
        setValues(getMinX() - value, getMaxX() - value, getMinY(), getMaxY());
    }

    protected void panRight(double value) {
        setValues(getMinX() + value, getMaxX() + value, getMinY(), getMaxY());
    }

    protected int getFocusIndex() {
        return seriesCollection.getSeriesIndex(focus);
    }

    protected void configureAxis() {
        XYPlot plot = chartPanel.getChart().getXYPlot();

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(getMinY(), getMaxY());
        //rangeAxis.setTickUnit(getTickUnitY());
        rangeAxis.setNumberFormatOverride(getDecimalFormat());

        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setRange(getMinX(), getMaxX());
        domainAxis.setTickUnit(getTickUnitX());
    }
}
