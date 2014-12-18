package ec.ui.view;

import ec.nbdemetra.ui.DemetraUI;
import ec.tstoolkit.data.AutoRegressiveSpectrum;
import ec.tstoolkit.data.DataBlock;
import ec.tstoolkit.data.Values;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author Demortier Jeremy
 */
public class AutoRegressiveSpectrumView extends ARPView {

    // PROPERTIES DEFINITIONS
    public static final String LOG_PROPERTY = "logTransformation";
    public static final String DIFF_PROPERTY = "differencing";
    public static final String DIFF_LAG_PROPERTY = "differencingLag";
    public static final String AR_COUNT_PROPERTY = "arCount";
    public static final String RESOLUTION_PROPERTY = "resolution";
    public static final String LASTYEARS_PROPERTY = "lastYears";
    //
    private int del = DEFAULT_DIFF, lag = DEFAULT_DIFF_LAG;
    private boolean log = DEFAULT_LOG;
    private int lastYears;
    // DEFAULT PROPERTIES
    private static final int DEFAULT_AR_COUNT = 0;
    private static final int DEFAULT_RESOLUTION = 5;
    private static final boolean DEFAULT_LOG = false;
    private static final int DEFAULT_DIFF = 1;
    private static final int DEFAULT_DIFF_LAG = 1;
    public static final int DEFAULT_LAST = 0;
    // PROPERTIES
    protected int arcount;
    protected int resolution;

    public AutoRegressiveSpectrumView() {
        DemetraUI demetraUI = DemetraUI.getDefault();
        lastYears = demetraUI.getSpectralLastYears();

        this.arcount = DEFAULT_AR_COUNT;
        this.resolution = DEFAULT_RESOLUTION;
        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case AR_COUNT_PROPERTY:
                        onArCountChange();
                        break;
                    case RESOLUTION_PROPERTY:
                        onFreqCountChange();
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
                }
            }
        });
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

    public int getArCount() {
        return arcount;
    }

    public void setArCount(final int count) {
        if (count < 0) {
            throw new IllegalArgumentException("AR count should be >0.");
        }
        int old = arcount;
        arcount = count;
        firePropertyChange(AR_COUNT_PROPERTY, old, this.arcount);
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(final int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Resolution should be strictly positive.");
        }
        int old = resolution;
        resolution = count;
        firePropertyChange(RESOLUTION_PROPERTY, old, this.resolution);
    }

    protected void onArCountChange() {
        onARPDataChange();
    }

    protected void onFreqCountChange() {
        onARPDataChange();
    }

    protected void onLogChange() {
        onARPDataChange();
    }

    protected void onDiffChange() {
        onARPDataChange();
    }

    protected void onDiffLagChange() {
        onARPDataChange();
    }

    protected void onLastYearsChange() {
        onARPDataChange();
    }

    @Override
    protected void onARPDataChange() {
        super.onARPDataChange();
        if (data == null) {
            return;
        }
        onColorSchemeChange();
    }

    @Override
    protected XYSeries computeSeries() {
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
            val = new Values(new DataBlock(s, del * lag, s.length, 1));
        }
        if (lastYears > 0 && data.freq > 0) {
            int nmax = lastYears * data.freq;
            int nbeg = val.getLength() - nmax;
            if (nbeg > 0) {
                val = val.drop(nbeg, 0);
            }
        }

        int nar = arcount;
        if (nar <= 0) {
            nar = Math.min(val.getLength() - 1, 30 * data.freq / 12);
        }
        AutoRegressiveSpectrum ar = new AutoRegressiveSpectrum(AutoRegressiveSpectrum.Method.Ols);
        XYSeries result = new XYSeries(data.name);
        if (ar.process(val, nar)) {

            int nf = resolution * 60;
            for (int i = 0; i <= nf; ++i) {
                double f = Math.PI * i / nf;
                result.add(f, ar.value(f));
            }
        }

        return result;
    }
}
