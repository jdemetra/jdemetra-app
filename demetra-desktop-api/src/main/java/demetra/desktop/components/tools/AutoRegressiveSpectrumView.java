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
import javax.swing.JPopupMenu;
import jdplus.data.analysis.AutoRegressiveSpectrum;
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

    // DEFAULT PROPERTIES
    private static final int DEFAULT_AR_COUNT = 0;
    private static final int DEFAULT_RESOLUTION = 5;
    private static final boolean DEFAULT_LOG = false;
    private static final int DEFAULT_DIFF = 1;
    private static final int DEFAULT_DIFF_LAG = 1;
    public static final int DEFAULT_LAST = 0;

    // PROPERTIES
    private int del;
    private int lag;
    private boolean log;
    private int lastYears;
    protected int arcount;
    protected int resolution;

    public AutoRegressiveSpectrumView() {
        this.del = DEFAULT_DIFF;
        this.lag = DEFAULT_DIFF_LAG;
        this.log = DEFAULT_LOG;
        // TODO
        this.lastYears = 12; //DemetraUI.getDefault().getSpectralLastYears();
        this.arcount = DEFAULT_AR_COUNT;
        this.resolution = DEFAULT_RESOLUTION;
        initComponents();
    }

    private void initComponents() {
        onComponentPopupMenuChange();
        enableProperties();
    }

    private void enableProperties() {
        this.addPropertyChangeListener(evt -> {
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
                case "componentPopupMenu":
                    onComponentPopupMenuChange();
                    break;
            }
        });
    }

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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Event handlers">
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

    private void onComponentPopupMenuChange() {
        JPopupMenu popupMenu = getComponentPopupMenu();
        chartPanel.setComponentPopupMenu(popupMenu != null ? popupMenu : buildMenu().getPopupMenu());
    }
    //</editor-fold>

    @Override
    protected XYSeries computeSeries() {
        DoubleSeq val = data.getValues();
        if (log) {
            val=val.log();
        }
         if (del > 0) {
            val=val.delta(lag, del);
        }
        if (lastYears > 0 && data.getFreq() != 0) {
            int nmax = (int)(lastYears * data.getFreq());
            int nbeg = val.length() - nmax;
            if (nbeg > 0) {
                val = val.drop(nbeg, 0);
            }
        } 

        int nar = arcount;
        if (nar <= 0) {
            nar = (int) Math.min(val.length() - 1, 30 * data.getFreq() / 12);
        }
        AutoRegressiveSpectrum ar = new AutoRegressiveSpectrum(AutoRegressiveSpectrum.Method.Ols);
        XYSeries result = new XYSeries(data.getName());
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
