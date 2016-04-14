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
package ec.ui.view;

import ec.nbdemetra.ui.DemetraUI;
import ec.tstoolkit.data.BlackmanTukeySpectrum;
import ec.tstoolkit.data.DataBlock;
import ec.tstoolkit.data.TukeyHanningTaper;
import ec.tstoolkit.data.Values;
import ec.tstoolkit.data.WindowType;
import javax.swing.JPopupMenu;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author Demortier Jeremy
 */
public class TukeySpectrumView extends ARPView {

    // PROPERTIES DEFINITIONS
    public static final String WINDOW_LENGTH_PROPERTY = "windowLength";
    public static final String WINDOW_TYPE_PROPERTY = "windowType";
    public static final String TAPER_PART_PROPERTY = "tapingPart";
    public static final String LOG_PROPERTY = "logTransformation";
    public static final String DIFF_PROPERTY = "differencing";
    public static final String DIFF_LAG_PROPERTY = "differencingLag";
    public static final String LASTYEARS_PROPERTY = "lastYears";

    // DEFAULT PROPERTIES
    private static final int DEFAULT_WINDOW_LENGTH = 0; // use default
    private static final WindowType DEFAULT_WINDOW_TYPE = WindowType.Tukey;
    private static final double DEFAULT_TAPER_PART = 0; // no taper
    private static final boolean DEFAULT_LOG = false; // no log
    private static final int DEFAULT_DIFF = 1; // no log
    private static final int DEFAULT_DIFF_LAG = 1; // no log

    // PROPERTIES
    protected int windowLength;
    protected WindowType windowType;
    protected double taperPart;
    private int del;
    private int lag;
    private boolean log;
    private int lastYears;

    public TukeySpectrumView() {
        this.windowLength = DEFAULT_WINDOW_LENGTH;
        this.windowType = DEFAULT_WINDOW_TYPE;
        this.taperPart = DEFAULT_TAPER_PART;
        this.del = DEFAULT_DIFF;
        this.lag = DEFAULT_DIFF_LAG;
        this.log = DEFAULT_LOG;
        this.lastYears = DemetraUI.getDefault().getSpectralLastYears();

        onComponentPopupMenuChange();
        enableProperties();
    }

    private void enableProperties() {
        addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case WINDOW_LENGTH_PROPERTY:
                    onWindowLengthChange();
                    break;
                case WINDOW_TYPE_PROPERTY:
                    onWindowTypeChange();
                    break;
                case TAPER_PART_PROPERTY:
                    onTaperChange();
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
                case "componentPopupMenu":
                    onComponentPopupMenuChange();
                    break;
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="Event handlers">
    protected void onWindowLengthChange() {
        onARPDataChange();
    }

    protected void onWindowTypeChange() {
        onARPDataChange();
    }

    protected void onTaperChange() {
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

    @Override
    protected void onARPDataChange() {
        super.onARPDataChange();
        if (data == null) {
            return;
        }
        onColorSchemeChange();
    }

    private void onComponentPopupMenuChange() {
        JPopupMenu popupMenu = getComponentPopupMenu();
        chartPanel.setComponentPopupMenu(popupMenu != null ? popupMenu : buildMenu().getPopupMenu());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public int getWindowLength() {
        return windowLength;
    }

    public void setWindowLength(int windowLength) {
        int old = this.windowLength;
        this.windowLength = windowLength > 0 ? windowLength : DEFAULT_WINDOW_LENGTH;
        firePropertyChange(WINDOW_LENGTH_PROPERTY, old, this.windowLength);
    }

    public WindowType getWindowType() {
        return windowType;
    }

    public void setWindowType(WindowType windowType) {
        if (windowType != this.windowType) {
            WindowType old = this.windowType;
            this.windowType = windowType;
            firePropertyChange(WINDOW_TYPE_PROPERTY, old, this.windowType);
        }
    }

    public double getTaperPart() {
        return taperPart;
    }

    public void setTaperPart(double part) {
        if (part != this.taperPart) {
            double old = this.taperPart;
            this.taperPart = part;
            firePropertyChange(TAPER_PART_PROPERTY, old, this.taperPart);
        }
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
    //</editor-fold>

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
            val = new Values(new DataBlock(s, del * lag, s.length, 1));
        }
        if (lastYears > 0 && data.freq > 0) {
            int nmax = lastYears * data.freq;
            int nbeg = val.getLength() - nmax;
            if (nbeg > 0) {
                val = val.drop(nbeg, 0);
            }
        }

        BlackmanTukeySpectrum tukey = new BlackmanTukeySpectrum();
        if (taperPart != 0) {
            tukey.setTaper(new TukeyHanningTaper(taperPart));
        }
        tukey.setWindowType(windowType);
        if (this.windowLength != 0 && this.windowLength < val.getLength() - 1) {
            tukey.setWindowLength(this.windowLength);
        } else {
            int len = defWindowLength(val.getLength(), data.freq);
            if (len > 0) {
                tukey.setWindowLength(len);
            } else {
                return result;
            }
        }
        tukey.setData(val.internalStorage());
        double[] yp = tukey.getSpectrum();
        for (int i = 0; i < yp.length; ++i) {
            result.add(i * TWO_PI / tukey.getWindowLength(), yp[i]);
        }

        return result;
    }

    private int defWindowLength(int ndata, int freq) {
        if (freq != 12 && ndata >= 45) {
            return 44;
        } else if (freq == 12 && ndata >= 120) {
            return 112;
        } else if (freq == 12 && ndata >= 80) {
            return 79;
        } else {
            return -1;
        }
    }

}
