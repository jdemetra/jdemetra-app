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
package demetra.desktop.sa.ui;

import demetra.data.DoubleSeq;
import demetra.desktop.components.tools.ARPView;
import demetra.stats.ProbabilityType;
import ec.util.chart.ColorScheme.KnownColor;
import java.awt.BasicStroke;
import java.awt.Stroke;
import javax.swing.JPopupMenu;
import jdplus.data.analysis.Periodogram;
import jdplus.data.analysis.WindowFunction;
import jdplus.dstats.Chi2;
import jdplus.sa.tests.CanovaHansen2;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Demortier Jeremy
 */
public class CanovaHansenView extends ARPView {

    // CONSTANTS
    private static final Stroke LIMIT_STROKE = new BasicStroke(2.0f);

    // PROPERTIES DEFINITIONS
    public static final String LIMIT_VISIBLE_PROPERTY = "limitVisible";
    public static final String LOG_PROPERTY = "log";
    public static final String TREND_PROPERTY = "trend";
    public static final String LAG1_PROPERTY = "lag1";
    public static final String TRUNCATIONLAG_PROPERTY = "truncationLag";
    public static final String WINDOWFUNCTION_PROPERTY = "windowFuction";
    public static final String MINPERIOD_PROPERTY = "minPeriod";
    public static final String MAXPERIOD_PROPERTY = "maxPeriod";
    public static final String RESOLUTION_PROPERTY = "resolution";
    public static final String INTRESOLUTION_PROPERTY = "intResolution";

    // DEFAULT PROPERTIES
    private static final boolean DEFAULT_LIMIT_VISIBLE = true;
    private static final boolean DEFAULT_LOG = false;
    private static final boolean DEFAULT_TREND = false;
    private static final boolean DEFAULT_LAG1 = true;
    private static final int DEFAULT_TRUNCATIONLAG = 36;
    private static final WindowFunction DEFAULT_WINDOWFUNCTION = WindowFunction.Bartlett;
    private static final double DEFAULT_MINPERIOD = 2;
    private static final double DEFAULT_MAXPERIOD = 0;
    private static final int DEFAULT_RESOLUTION = 200;
    private static final boolean DEFAULT_INTRESOLUTION = false;

    // PROPERTIES
    private boolean log;
    private boolean trend;
    private boolean lag1;
    private WindowFunction windowFunction;
    private int truncationLag;
    private double minPeriod;
    private double maxPeriod;
    private int resolution;
    private boolean intResolution;
    private boolean limitVisible;

    public CanovaHansenView() {
        this.limitVisible = DEFAULT_LIMIT_VISIBLE;
        this.log = DEFAULT_LOG;
        this.trend = DEFAULT_TREND;
        this.lag1 = DEFAULT_LAG1;
        this.windowFunction = DEFAULT_WINDOWFUNCTION;
        this.truncationLag = DEFAULT_TRUNCATIONLAG;
        this.minPeriod = DEFAULT_MINPERIOD;
        this.maxPeriod = DEFAULT_MAXPERIOD;
        this.resolution = DEFAULT_RESOLUTION;
        this.intResolution = DEFAULT_INTRESOLUTION;
        initComponents();
    }

    private void initComponents() {
        onColorSchemeChange();
        onComponentPopupMenuChange();
        enableProperties();
    }

    private void enableProperties() {
        addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
//                case LIMIT_VISIBLE_PROPERTY -> onLimitVisibleChange();
                case LOG_PROPERTY ->
                    onLogChange();
                case TREND_PROPERTY ->
                    onTrendChange();
                case LAG1_PROPERTY ->
                    onLag1Change();
                case WINDOWFUNCTION_PROPERTY ->
                    onWindowFunctionChange();
                case TRUNCATIONLAG_PROPERTY ->
                    onTruncationLagChange();
                case MINPERIOD_PROPERTY ->
                    onMinPeriodChange();
                case MAXPERIOD_PROPERTY ->
                    onMaxPeriodChange();
                case RESOLUTION_PROPERTY ->
                    onResolutionChange();
                case INTRESOLUTION_PROPERTY ->
                    onIntResolutionChange();
                case "componentPopupMenu" ->
                    onComponentPopupMenuChange();
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="Event handlers">
//    protected void onLimitVisibleChange() {
//        getPlot().clearRangeMarkers();
//        if (limitVisible) {
//            Chi2 chi = new Chi2(2);
//            double val = chi.getProbabilityInverse(.005, ProbabilityType.Upper);
//            if (db) {
//                val = Math.log(val);
//            }
//
//            ExtValueMarker limitMarker = new ExtValueMarker(val, KnownColor.GREEN);
//            limitMarker.setStroke(LIMIT_STROKE);
//            getPlot().addRangeMarker(limitMarker);
//        }
//    }
    protected void onLogChange() {
        onARPDataChange();
    }

    protected void onTrendChange() {
        onARPDataChange();
    }

    protected void onLag1Change() {
        onARPDataChange();
    }

    protected void onWindowFunctionChange() {
        onARPDataChange();
    }

    protected void onTruncationLagChange() {
        onARPDataChange();
    }

    protected void onMinPeriodChange() {
        onARPDataChange();
    }

    protected void onMaxPeriodChange() {
        onARPDataChange();
    }

    protected void onResolutionChange() {
        onARPDataChange();
    }

    protected void onIntResolutionChange() {
        onARPDataChange();
    }

    @Override
    protected void onARPDataChange() {
        XYPlot plot = getPlot();
        reset();
        if (data == null) {
            return;
        }
        XYSeries series = computeSeries();
        plot.setDataset(new XYSeriesCollection(series));
        chartPanel.getChart().getTitle().setText("Canova-Hansen");
    }

    private void onComponentPopupMenuChange() {
        JPopupMenu popupMenu = getComponentPopupMenu();
        chartPanel.setComponentPopupMenu(popupMenu != null ? popupMenu : buildMenu().getPopupMenu());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public boolean isLog() {
        return log;
    }

    public void setLog(final boolean log) {
        boolean old = this.log;
        this.log = log;
        firePropertyChange(LOG_PROPERTY, old, this.log);
    }

    public boolean isTrend() {
        return trend;
    }

    public void setTrend(final boolean trend) {
        boolean old = this.trend;
        this.trend = trend;
        firePropertyChange(TREND_PROPERTY, old, trend);
    }

    public boolean isLag1() {
        return lag1;
    }

    public void setLag1(final boolean lag1) {
        boolean old = this.lag1;
        this.lag1 = lag1;
        firePropertyChange(LAG1_PROPERTY, old, lag1);
    }

    public WindowFunction getWindowFunction() {
        return windowFunction;
    }

    public void setWindowFunction(WindowFunction f) {
        WindowFunction old = this.windowFunction;
        this.windowFunction = f;
        firePropertyChange(WINDOWFUNCTION_PROPERTY, old, f);
    }

    public int getTruncationLag() {
        return this.truncationLag;
    }

    public void setTruncationLag(final int lag) {
        if (lag <= 3) {
            throw new IllegalArgumentException("Lag order should be >3.");
        }
        int old = this.truncationLag;
        this.truncationLag = lag;
        firePropertyChange(TRUNCATIONLAG_PROPERTY, old, lag);
    }

    public double getMinPeriod() {
        return this.minPeriod;
    }

    public void setMinPeriod(double mp) {
        if ((maxPeriod != 0 && mp >= this.maxPeriod) || mp < 2) {
            throw new IllegalArgumentException("Min period should be smaller than max period and greater than 2");
        }
        double old = this.minPeriod;
        this.minPeriod = mp;
        firePropertyChange(MINPERIOD_PROPERTY, old, mp);
    }

    public double getMaxPeriod() {
        return this.maxPeriod;
    }

    public void setMaxPeriod(double mp) {
        if (mp != 0 && mp <= this.minPeriod) {
            throw new IllegalArgumentException("Max period should be larger than min period");
        }
        double old = this.maxPeriod;
        this.maxPeriod = mp;
        firePropertyChange(MAXPERIOD_PROPERTY, old, mp);
    }

    public boolean isLimitVisible() {
        return limitVisible;
    }

    public void setLimitVisible(boolean limitVisible) {
        boolean old = this.limitVisible;
        this.limitVisible = limitVisible;
        firePropertyChange(LIMIT_VISIBLE_PROPERTY, old, this.limitVisible);
    }

    public int getResolution() {
        return this.resolution;
    }

    public void setResolution(int res) {
        int old = this.resolution;
        this.resolution = res;
        firePropertyChange(RESOLUTION_PROPERTY, old, res);
    }

    public boolean isIntResolution() {
        return this.intResolution;
    }

    public void setIntResolution(boolean res) {
        boolean old = this.intResolution;
        this.intResolution = res;
        firePropertyChange(INTRESOLUTION_PROPERTY, old, res);
    }

    //</editor-fold>
    @Override
    protected XYSeries computeSeries() {
        XYSeries result = new XYSeries(data.getName());
        DoubleSeq val = data.getValues();
        if (log) {
            val = val.log();
        }

        double p = data.getFreq();

        CanovaHansen2 ch = CanovaHansen2.of(val)
                .trend(trend)
                .lag1(lag1)
                .windowFunction(windowFunction)
                .truncationLag(truncationLag);

        double min = minPeriod, max = maxPeriod == 0 ? p * 1.5 : maxPeriod;
        double step = (max - min) / (resolution - 1);

        for (double period = min; period <= max; period += step) {
            double pc = period;
            if (intResolution) {
                long lp = Math.round(pc);
                if (Math.abs(lp - pc) < step / 2) {
                    pc = lp;
                }
            }
            result.add(pc, ch.periodicity(pc).compute());
        }
        return result;
    }
    
}
