package ec.ui.view;

import ec.nbdemetra.ui.DemetraUI;
import ec.tstoolkit.data.DataBlock;
import ec.tstoolkit.data.Periodogram;
import ec.tstoolkit.data.Values;
import ec.tstoolkit.dstats.Chi2;
import ec.tstoolkit.dstats.ProbabilityType;
import ec.util.chart.ColorScheme.KnownColor;
import java.awt.BasicStroke;
import java.awt.Stroke;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
    // DEFAULT PROPERTIES
    private static final boolean DEFAULT_LIMIT_VISIBLE = true;
    private static final int DEFAULT_WINDOW_LENGTH = 1;
    private static final boolean DEFAULT_LOG = false;
    private static final int DEFAULT_DIFF = 1;
    private static final int DEFAULT_DIFF_LAG = 1;
    private static final boolean DEFAULT_FULL = true;
    // PROPERTIES
    protected boolean limitVisible;
    protected int windowLength;
    private int del = DEFAULT_DIFF, lag = DEFAULT_DIFF_LAG;
    private boolean log = DEFAULT_LOG;
    private int lastYears;
    private boolean full = DEFAULT_FULL;

    public PeriodogramView() {
        DemetraUI demetraUI = DemetraUI.getDefault();
        lastYears = demetraUI.getSpectralLastYears();

        this.limitVisible = DEFAULT_LIMIT_VISIBLE;
        this.windowLength = DEFAULT_WINDOW_LENGTH;

        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
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
                }
            }
        });

        onColorSchemeChange();
    }

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

    public boolean isFullYears() {
        return full;
    }

    public void setFullYears(boolean f) {
        boolean old = full;
        full = f;
        firePropertyChange(FULL_PROPERTY, old, this.full);
    }

    // EVENT HANDLERS > 
    protected void onLimitVisibleChange() {
        getPlot().clearRangeMarkers();
        if (limitVisible) {
            Chi2 chi = new Chi2();
            chi.setDegreesofFreedom(2);
            double val = chi.getProbabilityInverse(.005, ProbabilityType.Upper);

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

    @Override
    protected void onARPDataChange() {
        super.onARPDataChange();
        if (data == null) {
            return;
        }
        onColorSchemeChange();
        onLimitVisibleChange();
    }
    // < EVENT HANDLERS 

    // GETTERS/SETTERS >
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
    // < GETTERS/SETTERS

    @Override
    protected XYSeries computeSeries() {
        XYSeries result = new XYSeries(data.name);
        Values val = data.values.clone();
        if (log) {
            val.log();
        }
        if (del > 0) {
            double[] s = new double[val.getLength()];
            val.copyTo(s, 0);
            for (int i = 0; i < del; ++i) {
                for (int j = s.length - 1; j >= (i + 1) * lag; --j) {
                    s[j] -= s[j - lag];
                }
            }
            val = new Values(s.length - del * lag);
            val.copyFrom(s, del * lag);
        }
        if (lastYears > 0 && data.freq > 0) {
            int nmax = lastYears * data.freq;
            int nbeg = val.getLength() - nmax;
            if (nbeg > 0) {
                val = val.drop(nbeg, 0);
            }
        } else if (full && data.freq > 0) {
            // Keep full years
            int nvals = val.getLength();
            int nbeg = nvals % data.freq;
            if (nbeg > 0) {
                val = val.drop(nbeg, 0);
            }
        }

        Periodogram periodogram = new Periodogram(val);
        periodogram.setWindowLength(windowLength);
        double[] yp = periodogram.getS();
        for (int i = 0; i < yp.length; ++i) {
            result.add(i * TWO_PI / val.getLength(), yp[i]);
        }

        return result;
    }
}
