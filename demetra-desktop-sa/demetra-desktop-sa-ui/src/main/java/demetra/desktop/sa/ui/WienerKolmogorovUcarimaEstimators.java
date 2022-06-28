/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.ui;

import demetra.math.Complex;
import demetra.sa.ComponentDescriptor;
import internal.uihelpers.ContinuousDisplayDomain;
import internal.uihelpers.ContinuousInformationProvider;
import internal.uihelpers.DiscreteDisplayDomain;
import internal.uihelpers.DiscreteInformationProvider;
import internal.uihelpers.FrequencyInformationProvider;
import jdplus.arima.AutoCovarianceFunction;
import jdplus.arima.Spectrum;
import jdplus.math.linearfilters.RationalFilter;
import jdplus.ucarima.UcarimaModel;
import jdplus.ucarima.WienerKolmogorovEstimator;
import jdplus.ucarima.WienerKolmogorovEstimators;
import jdplus.ucarima.WienerKolmogorovPreliminaryEstimatorProperties;


/**
 *
 * @author Jean Palate
 */
public class WienerKolmogorovUcarimaEstimators
        implements ContinuousInformationProvider, DiscreteInformationProvider {

    private void fillGain(int cmp, double[] data, ContinuousDisplayDomain domain) {
        ComponentDescriptor desc = cmpDescs[cmp];
        if (type == EstimatorType.Preliminary) {
            WienerKolmogorovPreliminaryEstimatorProperties wkp = new WienerKolmogorovPreliminaryEstimatorProperties(wk);
            wkp.setLag(lag);
            double freq = domain.beg;
            for (int j = 0; j < data.length; ++j, freq += domain.step) {
                data[j] = wkp.getFrequencyResponse(freq).absSquare();
            }
        }
        if (type == EstimatorType.Component) {
            return;
        }

        if (type == EstimatorType.Final) {
            WienerKolmogorovEstimator estimator = wk.finalEstimator(desc.getComponent(), desc.isSignal());
            RationalFilter rf = estimator.getWienerKolmogorovFilter();
            for (int i = 0; i < data.length; ++i) {
                data[i] = rf.frequencyResponse(domain.x(i)).absSquare();
            }
        }
    }

    private void fillPhase(int cmp, double[] data, ContinuousDisplayDomain domain) {
        ComponentDescriptor desc = cmpDescs[cmp];
        if (type == EstimatorType.Preliminary) {
            WienerKolmogorovPreliminaryEstimatorProperties wkp = new WienerKolmogorovPreliminaryEstimatorProperties(wk);
            wkp.setLag(lag);
            double freq = domain.beg;
            for (int j = 0; j < data.length; ++j, freq += domain.step) {
                Complex rfw = wkp.getFrequencyResponse(freq);
                if (desc.isLowFrequency() && j < data.length && freq != 0) {
                    data[j] = -rfw.arg() / freq;
                }
            }
        }
    }

    private void fillSpectrum(int cmp, double[] data, ContinuousDisplayDomain domain) {
        ComponentDescriptor desc = cmpDescs[cmp];
        if (type == EstimatorType.Preliminary) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
        Spectrum spectrum = null;
        if (type == EstimatorType.Final) {
            WienerKolmogorovEstimator estimator = wk.finalEstimator(desc.getComponent(), desc.isSignal());
            spectrum = estimator.getEstimatorModel().getSpectrum();
        } else if (type == EstimatorType.Component) {
            if (desc.isSignal()) {
                spectrum = wk.getUcarimaModel().getComponent(desc.getComponent()).getSpectrum();
            } else {
                spectrum = wk.getUcarimaModel().getComplement(desc.getComponent()).getSpectrum();
            }
        }
        if (spectrum == null) {
            return;
        }

        double x = domain.beg;
        for (int i = 0; i < data.length; ++i) {
            data[i] = spectrum.get(x);
            x += domain.step;
        }
    }

    @Override
    public DiscreteDisplayDomain getDiscreteDisplayDomain(int npoints) {
        switch (currentInfo) {
            case AUTOCORRELATIONS -> {
                return new DiscreteDisplayDomain(1, multiple(freq, npoints));
            }
            case WKFILTER, PSIEWEIGHTS -> {
                int np = multiple(freq, npoints / 2);
                return new DiscreteDisplayDomain(-np, np);
            }
            default -> {
                    return null;
            }
        }
    }

    private int multiple(int ifreq, int n) {
        if (ifreq == 0) {
            return n;
        } else if (n % ifreq == 0) {
            return n;
        } else {
            return (n / ifreq + 1) * ifreq;
        }
    }

    @Override
    public DiscreteDisplayDomain getDiscreteDisplayDomain(int lower, int upper) {
        return switch (currentInfo) {
            case AUTOCORRELATIONS -> new DiscreteDisplayDomain(Math.max(1, lower), upper);
            case WKFILTER, PSIEWEIGHTS -> new DiscreteDisplayDomain(lower, upper);
            default -> null;
        };
    }

    @Override
    public double[] getDataArray(int cmp, DiscreteDisplayDomain domain) {
        double[] data = new double[domain.getLength()];
        switch (currentInfo) {
            case AUTOCORRELATIONS -> fillAutocorrelations(cmp, data, domain);
            case WKFILTER -> fillFilter(cmp, data, domain);
            case PSIEWEIGHTS -> fillPsie(cmp, data, domain);
            default -> {
                    return null;
            }
        }
        return data;
    }

    @Override
    public double getData(int cmp, int x) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void fillAutocorrelations(int cmp, double[] data, DiscreteDisplayDomain domain) {
        ComponentDescriptor desc = cmpDescs[cmp];
        if (type == EstimatorType.Preliminary) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
        AutoCovarianceFunction acgf = null;
        if (type == EstimatorType.Final) {
            WienerKolmogorovEstimator estimator = wk.finalEstimator(desc.getComponent(), desc.isSignal());
            acgf = estimator.getEstimatorModel().doStationary().getAutoCovarianceFunction();
        } else if (type == EstimatorType.Component) {
            if (desc.isSignal()) {
                acgf = wk.getUcarimaModel().getComponent(desc.getComponent()).stationaryTransformation().getStationaryModel().getAutoCovarianceFunction();
            } else {
                acgf = wk.getUcarimaModel().getComplement(desc.getComponent()).stationaryTransformation().getStationaryModel().getAutoCovarianceFunction();
            }
        }
        if (acgf == null) {
            return;
        }

        acgf.prepare(domain.beg + data.length);
        double var = acgf.get(0);
        for (int i = 0; i < data.length; ++i) {
            data[i] = acgf.get(i + domain.beg) / var;
        }
    }

    private void fillFilter(int cmp, double[] data, DiscreteDisplayDomain domain) {
        ComponentDescriptor desc = cmpDescs[cmp];
        if (type == EstimatorType.Preliminary) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
        if (type == EstimatorType.Final) {
            WienerKolmogorovEstimator estimator = wk.finalEstimator(desc.getComponent(), desc.isSignal());
            RationalFilter rf = estimator.getWienerKolmogorovFilter();
            for (int i = 0; i < data.length; ++i) {
                data[i] = rf.weight(domain.x(i));
            }
        } else {
            throw new UnsupportedOperationException();
        }

    }

    private void fillPsie(int cmp, double[] data, DiscreteDisplayDomain domain) {
        ComponentDescriptor desc = cmpDescs[cmp];
        if (type != EstimatorType.Final) {
            throw new UnsupportedOperationException();
        }
        if (type == EstimatorType.Final) {
            WienerKolmogorovEstimator estimator = wk.finalEstimator(desc.getComponent(), desc.isSignal());
            RationalFilter rf = estimator.getEstimatorModel().getFilter();
            for (int i = 0; i < data.length; ++i) {
                data[i] = rf.weight(domain.x(i));
            }
        } else {
        }
    }

    public enum EstimatorType {

        Component, Final, Preliminary
    }
    public static final String SPECTRUM = "Spectrum", GAIN = "Square gain", PHASE = "Phase effect", WKFILTER = "Weights", PSIEWEIGHTS = "PsiE-weights", AUTOCORRELATIONS = "Auto-correlations";
    private String currentInfo = GAIN;
    private EstimatorType type = EstimatorType.Final;
    private int lag = 0;
    private final WienerKolmogorovEstimators wk;
    private final ComponentDescriptor[] cmpDescs;
    private int freq = 0;

    /**
     *
     * @param wk
     * @param cmps
     */
    public WienerKolmogorovUcarimaEstimators(WienerKolmogorovEstimators wk, ComponentDescriptor[] cmps) {
        this.wk = wk;
        cmpDescs = cmps;
    }

    /**
     *
     * @param ucm
     * @param cmps
     */
    public WienerKolmogorovUcarimaEstimators(UcarimaModel ucm, ComponentDescriptor[] cmps) {
        wk = new WienerKolmogorovEstimators(ucm);
        cmpDescs = cmps;
    }

    /**
     * @return the freq_
     */
    public int getFrequency() {
        return freq;
    }

    /**
     * @param freq the fFrequency to set
     */
    public void setFrequency(int freq) {
        this.freq = freq;
    }

    public void setInformation(String info) {
        currentInfo = info;
    }

    /**
     *
     * @return
     */
    @Override
    public String getInformation() {
        return currentInfo;
    }

    public EstimatorType getType() {
        return type;
    }

    public void setType(EstimatorType type) {
        this.type = type;
    }

    public int getLag() {
        return type == EstimatorType.Preliminary ? lag : 0;
    }

    public void setLag(int lag) {
        if (type == EstimatorType.Preliminary) {
            this.lag = lag;
        }
    }

    @Override
    public String[] getComponents() {
        String[] cmps = new String[cmpDescs.length];
        for (int i = 0; i < cmps.length; ++i) {
            cmps[i] = cmpDescs[i].getName();
        }
        return cmps;
    }

    @Override
    public double[] getDataArray(int cmp, ContinuousDisplayDomain domain) {
        double[] data = new double[domain.npoints];
        switch (currentInfo) {
            case GAIN -> fillGain(cmp, data, domain);
            case SPECTRUM -> fillSpectrum(cmp, data, domain);
            case PHASE -> fillPhase(cmp, data, domain);
            default -> {
                    return null;
            }
        }
        return data;
    }

    @Override
    public double getData(int cmp, double x) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ContinuousDisplayDomain getContinuousDisplayDomain(int npoints) {
        switch (currentInfo) {
            case SPECTRUM:
            case GAIN:
                return FrequencyInformationProvider.getDisplayDomain(freq, npoints);
            case PHASE:
                int ifreq = freq / 2;
                if (ifreq == 0) {
                    ifreq = 6;
                }
                return FrequencyInformationProvider.getDisplayDomain(freq, 0, Math.PI / ifreq, npoints);
            default:
                return null;
        }
    }

    @Override
    public ContinuousDisplayDomain getContinuousDisplayDomain(double lower, double upper, int npoints) {
        switch (currentInfo) {
            case SPECTRUM, GAIN -> {
                return FrequencyInformationProvider.getDisplayDomain(freq, lower, upper, npoints);
            }
            case PHASE -> {
                int ifreq = freq / 2;
                double fmax = upper;
                if (ifreq != 0) {
                    fmax = Math.min(fmax, Math.PI / ifreq);
                } else {
                    fmax = Math.min(fmax, Math.PI / 6);
                }
                return FrequencyInformationProvider.getDisplayDomain(freq, lower, fmax, npoints);
            }
            default -> {
                    return null;
            }
        }
    }

    @Override
    public boolean isDefined(int idx) {
        int cmp = this.cmpDescs[idx].getComponent();
        return !wk.getUcarimaModel().getComponent(cmp).isNull();
    }
}
