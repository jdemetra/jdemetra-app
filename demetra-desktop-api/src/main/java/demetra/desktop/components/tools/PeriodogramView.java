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
package demetra.desktop.components.tools;

import demetra.data.DoubleSeq;
import demetra.stats.ProbabilityType;
import ec.util.chart.ColorScheme.KnownColor;
import java.awt.BasicStroke;
import java.awt.Stroke;
import javax.swing.JPopupMenu;
import jdplus.data.analysis.Periodogram;
import jdplus.dstats.Chi2;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author Demortier Jeremy
 */
public class PeriodogramView extends ARPView {

    // CONSTANTS
    private static final Stroke LIMIT_STROKE = new BasicStroke(2.0f);

    // PROPERTIES DEFINITIONS
    public static final String LIMIT_VISIBLE_PROPERTY = "limitVisible";
    public static final String WINDOW_LENGTH_PROPERTY = "windowLength";
    public static final String LOG_PROPERTY = "logTransformation";
    public static final String DIFF_PROPERTY = "differencing";
    public static final String DIFF_LAG_PROPERTY = "differencingLag";
    public static final String LASTYEARS_PROPERTY = "lastYears";
    public static final String FULL_PROPERTY = "fullYears";
    public static final String DB_PROPERTY = "decibels";

    // DEFAULT PROPERTIES
    private static final boolean DEFAULT_LIMIT_VISIBLE = true;
    private static final int DEFAULT_WINDOW_LENGTH = 1;
    private static final boolean DEFAULT_LOG = false;
    private static final int DEFAULT_DIFF = 1;
    private static final int DEFAULT_DIFF_LAG = 1;
    private static final boolean DEFAULT_FULL = true;
    private static final boolean DEFAULT_DB = false;

    // PROPERTIES
    protected boolean limitVisible;
    protected int windowLength;
    private int del;
    private int lag;
    private boolean log;
    private int lastYears;
    private boolean full;
    private boolean db;

    public PeriodogramView() {
        this.limitVisible = DEFAULT_LIMIT_VISIBLE;
        this.windowLength = DEFAULT_WINDOW_LENGTH;
        this.del = DEFAULT_DIFF;
        this.lag = DEFAULT_DIFF_LAG;
        this.log = DEFAULT_LOG;
        this.lastYears = 0;
        this.full = DEFAULT_FULL;
        this.db = DEFAULT_DB;
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
                case LIMIT_VISIBLE_PROPERTY:
                    onLimitVisibleChange();
                    break;
                case WINDOW_LENGTH_PROPERTY:
                    onWindowLengthChange();
                    break;
                case LOG_PROPERTY:
                    onLogChange();
                    break;
                case DIFF_PROPERTY:
                    onDiffChange();
                    break;
                case DIFF_LAG_PROPERTY:
                    onDiffChange();
                    break;
                case LASTYEARS_PROPERTY:
                    onLastYearsChange();
                    break;
                case FULL_PROPERTY:
                    onFullChange();
                    break;
                case DB_PROPERTY:
                    onDbChange();
                    break;
                case "componentPopupMenu":
                    onComponentPopupMenuChange();
                    break;
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="Event handlers">
    protected void onLimitVisibleChange() {
        getPlot().clearRangeMarkers();
        if (limitVisible) {
            Chi2 chi = new Chi2(2);
            double val = chi.getProbabilityInverse(.005, ProbabilityType.Upper);
            if (db) {
                val = Math.log(val);
            }

            ExtValueMarker limitMarker = new ExtValueMarker(val, KnownColor.GREEN);
            limitMarker.setStroke(LIMIT_STROKE);
            getPlot().addRangeMarker(limitMarker);
        }
    }

    protected void onWindowLengthChange() {
        onARPDataChange();
    }

    protected void onLogChange() {
        onARPDataChange();
    }

    protected void onDiffChange() {
        onARPDataChange();
    }

    protected void onLastYearsChange() {
        onARPDataChange();
    }

    protected void onFullChange() {
        onARPDataChange();
    }

    protected void onDbChange() {
        onARPDataChange();
    }

    @Override
    protected void onARPDataChange() {
        super.onARPDataChange();
        if (data == null) {
            return;
        }
        onColorSchemeChange();
        onLimitVisibleChange();
    }

    private void onComponentPopupMenuChange() {
        JPopupMenu popupMenu = getComponentPopupMenu();
        chartPanel.setComponentPopupMenu(popupMenu != null ? popupMenu : buildMenu().getPopupMenu());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public boolean isLogTransformation() {
        return log;
    }

    public void setLogTransformation(final boolean log) {
        boolean old = this.log;
        this.log = log;
        firePropertyChange(LOG_PROPERTY, old, this.log);
    }

    public int getDifferencingOrder() {
        return del;
    }

    public void setDifferencingOrder(int order) {
        if (order < 0) {
            throw new IllegalArgumentException("Differencing order should be >=0.");
        }
        int old = del;
        del = order;
        firePropertyChange(DIFF_PROPERTY, old, this.del);
    }

    public boolean isDb() {
        return db;
    }

    public void setDb(boolean f) {
        boolean old = db;
        db = f;
        firePropertyChange(DB_PROPERTY, old, this.db);
    }

    public boolean isFullYears() {
        return full;
    }

    public void setFullYears(boolean f) {
        boolean old = full;
        full = f;
        firePropertyChange(FULL_PROPERTY, old, this.full);
    }

    public int getDifferencingLag() {
        return lag;
    }

    public void setDifferencingLag(final int lag) {
        if (lag <= 0) {
            throw new IllegalArgumentException("Lag order should be >0.");
        }
        int old = this.lag;
        this.lag = lag;
        firePropertyChange(DIFF_LAG_PROPERTY, old, this.lag);
    }

    public int getLastYears() {
        return lastYears;
    }

    public void setLastYears(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Last years should be >=0.");
        }
        int old = lastYears;
        lastYears = length;
        firePropertyChange(DIFF_PROPERTY, old, lastYears);
    }

    public boolean isLimitVisible() {
        return limitVisible;
    }

    public void setLimitVisible(boolean limitVisible) {
        boolean old = this.limitVisible;
        this.limitVisible = limitVisible;
        firePropertyChange(LIMIT_VISIBLE_PROPERTY, old, this.limitVisible);
    }

    public int getWindowLength() {
        return windowLength;
    }

    public void setWindowLength(int windowLength) {
        int old = this.windowLength;
        this.windowLength = windowLength > 0 ? windowLength : DEFAULT_WINDOW_LENGTH;
        firePropertyChange(WINDOW_LENGTH_PROPERTY, old, this.windowLength);
    }
    //</editor-fold>

    @Override
    protected XYSeries computeSeries() {
        XYSeries result = new XYSeries(data.getName());
        DoubleSeq val = data.getValues();
        if (log) {
            val = val.log();
        }
        if (del > 0) {
            val = val.delta(lag, del);
        }
        if (lastYears > 0 && data.getFreq() != 0) {
            int nmax = (int) (lastYears * data.getFreq());
            int nbeg = val.length() - nmax;
            if (nbeg > 0) {
                val = val.drop(nbeg, 0);
            }
        } else if (full && data.getFreq() > 0) {
            // Keep full years
            int nvals = val.length();
            int np = (int) (nvals / data.getFreq());
            int nbeg = nvals - (int) (np * data.getFreq());
            if (nbeg > 0) {
                val = val.drop(nbeg, 0);
            }
        }

        Periodogram periodogram = Periodogram.of(val);
        double[] yp = periodogram.getP();
        for (int i = 0; i < yp.length; ++i) {
            result.add(i * TWO_PI / val.length(), db ? Math.log(yp[i]) : yp[i]);
        }

        return result;
    }
}
