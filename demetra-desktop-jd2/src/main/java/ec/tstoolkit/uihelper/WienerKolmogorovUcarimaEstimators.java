/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.tstoolkit.uihelper;

import ec.satoolkit.ComponentDescriptor;
import ec.tstoolkit.arima.AutoCovarianceFunction;
import ec.tstoolkit.arima.Spectrum;
import ec.tstoolkit.maths.Complex;
import ec.tstoolkit.maths.linearfilters.RationalFilter;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.ucarima.UcarimaModel;
import ec.tstoolkit.ucarima.WienerKolmogorovEstimator;
import ec.tstoolkit.ucarima.WienerKolmogorovEstimators;
import ec.tstoolkit.ucarima.WienerKolmogorovPreliminaryEstimatorProperties;

/**
 *
 * @author Jean Palate
 */
public class WienerKolmogorovUcarimaEstimators
        implements IContinuousInformationProvider, IDiscreteInformationProvider {

    private void fillGain(int cmp, double[] data, ContinuousDisplayDomain domain) {
        ComponentDescriptor desc = cmpDescs_[cmp];
        if (type_ == EstimatorType.Preliminary) {
            WienerKolmogorovPreliminaryEstimatorProperties wkp = new WienerKolmogorovPreliminaryEstimatorProperties(wk_);
            wkp.setLag(lag_);
            double freq = domain.beg;
            for (int j = 0; j < data.length; ++j, freq += domain.step) {
                data[j] = wkp.getFrequencyResponse(freq).absSquare();
            }
        }
        if (type_ == EstimatorType.Component) {
            return;
        }

        if (type_ == EstimatorType.Final) {
            WienerKolmogorovEstimator estimator = wk_.finalEstimator(desc.cmp, desc.signal);
            RationalFilter rf = estimator.getFilter();
            for (int i = 0; i < data.length; ++i) {
                data[i] = rf.frequencyResponse(domain.x(i)).absSquare();
            }
        }
    }

    private void fillPhase(int cmp, double[] data, ContinuousDisplayDomain domain) {
        ComponentDescriptor desc = cmpDescs_[cmp];
        if (type_ == EstimatorType.Preliminary) {
            WienerKolmogorovPreliminaryEstimatorProperties wkp = new WienerKolmogorovPreliminaryEstimatorProperties(wk_);
            wkp.setLag(lag_);
            double freq = domain.beg;
            for (int j = 0; j < data.length; ++j, freq += domain.step) {
                Complex rfw = wkp.getFrequencyResponse(freq);
                if (desc.lowFrequency && j < data.length && freq != 0) {
                    data[j] = -rfw.arg() / freq;
                }
            }
        }
    }

    private void fillSpectrum(int cmp, double[] data, ContinuousDisplayDomain domain) {
        ComponentDescriptor desc = cmpDescs_[cmp];
        if (type_ == EstimatorType.Preliminary) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
        Spectrum spectrum = null;
        if (type_ == EstimatorType.Final) {
            WienerKolmogorovEstimator estimator = wk_.finalEstimator(desc.cmp, desc.signal);
            spectrum = estimator.getModel().getSpectrum();
        } else if (type_ == EstimatorType.Component) {
            if (desc.signal) {
                spectrum = wk_.getUcarimaModel().getComponent(desc.cmp).getSpectrum();
            } else {
                spectrum = wk_.getUcarimaModel().getComplement(desc.cmp).getSpectrum();
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
        int ifreq = freq_.intValue();
        switch (currentInfo_) {
            case AUTOCORRELATIONS:
                return new DiscreteDisplayDomain(1, multiple(ifreq, npoints));
            case WKFILTER:
            case PSIEWEIGHTS:
                int np = multiple(ifreq, npoints / 2);
                return new DiscreteDisplayDomain(-np, np);
            default:
                return null;
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
        switch (currentInfo_) {
            case AUTOCORRELATIONS:
                return new DiscreteDisplayDomain(Math.max(1, lower), upper);
            case WKFILTER:
            case PSIEWEIGHTS:
                return new DiscreteDisplayDomain(lower, upper);
            default:
                return null;
        }
    }

    @Override
    public double[] getDataArray(int cmp, DiscreteDisplayDomain domain) {
        double[] data = new double[domain.getLength()];
        switch (currentInfo_) {
            case AUTOCORRELATIONS:
                fillAutocorrelations(cmp, data, domain);
                break;
            case WKFILTER:
                fillFilter(cmp, data, domain);
                break;
            case PSIEWEIGHTS:
                fillPsie(cmp, data, domain);
                break;
            default:
                return null;
        }
        return data;
    }

    @Override
    public double getData(int cmp, int x) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void fillAutocorrelations(int cmp, double[] data, DiscreteDisplayDomain domain) {
        ComponentDescriptor desc = cmpDescs_[cmp];
        if (type_ == EstimatorType.Preliminary) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
        AutoCovarianceFunction acgf = null;
        if (type_ == EstimatorType.Final) {
            WienerKolmogorovEstimator estimator = wk_.finalEstimator(desc.cmp, desc.signal);
            acgf = estimator.getModel().doStationary().getAutoCovarianceFunction();
        } else if (type_ == EstimatorType.Component) {
            if (desc.signal) {
                acgf = wk_.getUcarimaModel().getComponent(desc.cmp).stationaryTransformation().stationaryModel.getAutoCovarianceFunction();
            } else {
                acgf = wk_.getUcarimaModel().getComplement(desc.cmp).stationaryTransformation().stationaryModel.getAutoCovarianceFunction();
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
        ComponentDescriptor desc = cmpDescs_[cmp];
        if (type_ == EstimatorType.Preliminary) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
        if (type_ == EstimatorType.Final) {
            WienerKolmogorovEstimator estimator = wk_.finalEstimator(desc.cmp, desc.signal);
            RationalFilter rf = estimator.getFilter();
            for (int i = 0; i < data.length; ++i) {
                data[i] = rf.getWeight(domain.x(i));
            }
        } else {
            throw new UnsupportedOperationException();
        }

    }

    private void fillPsie(int cmp, double[] data, DiscreteDisplayDomain domain) {
        ComponentDescriptor desc = cmpDescs_[cmp];
        if (type_ != EstimatorType.Final) {
            throw new UnsupportedOperationException();
        }
        if (type_ == EstimatorType.Final) {
            WienerKolmogorovEstimator estimator = wk_.finalEstimator(desc.cmp, desc.signal);
            RationalFilter rf = estimator.getModel().getFilter();
            for (int i = 0; i < data.length; ++i) {
                data[i] = rf.getWeight(domain.x(i));
            }
        } else {
        }
    }

    public enum EstimatorType {

        Component, Final, Preliminary
    }
    public static final String SPECTRUM = "Spectrum", GAIN = "Square gain", PHASE = "Phase effect", WKFILTER = "Weights", PSIEWEIGHTS = "PsiE-weights", AUTOCORRELATIONS = "Auto-correlations";
    private String currentInfo_ = GAIN;
    private EstimatorType type_ = EstimatorType.Final;
    private int lag_ = 0;
    private final WienerKolmogorovEstimators wk_;
    private final ComponentDescriptor[] cmpDescs_;
    private TsFrequency freq_ = TsFrequency.Undefined;

    /**
     *
     * @param wk
     * @param cmps
     */
    public WienerKolmogorovUcarimaEstimators(WienerKolmogorovEstimators wk, ComponentDescriptor[] cmps) {
        wk_ = wk;
        cmpDescs_ = cmps;
    }

    /**
     *
     * @param ucm
     * @param cmps
     */
    public WienerKolmogorovUcarimaEstimators(UcarimaModel ucm, ComponentDescriptor[] cmps) {
        wk_ = new WienerKolmogorovEstimators(ucm);
        cmpDescs_ = cmps;
    }

    /**
     * @return the freq_
     */
    public TsFrequency getFrequency() {
        return freq_;
    }

    /**
     * @param freq_ the fFrequency to set
     */
    public void setFrequency(TsFrequency freq_) {
        this.freq_ = freq_;
    }

    public void setInformation(String info) {
        currentInfo_ = info;
    }

    /**
     *
     * @return
     */
    @Override
    public String getInformation() {
        return currentInfo_;
    }

    public EstimatorType getType() {
        return type_;
    }

    public void setType(EstimatorType type) {
        type_ = type;
    }

    public int getLag() {
        return type_ == EstimatorType.Preliminary ? lag_ : 0;
    }

    public void setLag(int lag) {
        if (type_ == EstimatorType.Preliminary) {
            lag_ = lag;
        }
    }

    @Override
    public String[] getComponents() {
        String[] cmps = new String[cmpDescs_.length];
        for (int i = 0; i < cmps.length; ++i) {
            cmps[i] = cmpDescs_[i].name;
        }
        return cmps;
    }

    @Override
    public double[] getDataArray(int cmp, ContinuousDisplayDomain domain) {
        double[] data = new double[domain.npoints];
        switch (currentInfo_) {
            case GAIN:
                fillGain(cmp, data, domain);
                break;
            case SPECTRUM:
                fillSpectrum(cmp, data, domain);
                break;
            case PHASE:
                fillPhase(cmp, data, domain);
                break;
            default:
                return null;
        }
        return data;
    }

    @Override
    public double getData(int cmp, double x) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ContinuousDisplayDomain getContinuousDisplayDomain(int npoints) {
        switch (currentInfo_) {
            case SPECTRUM:
            case GAIN:
                return FrequencyInformationProvider.getDisplayDomain(freq_, npoints);
            case PHASE:
                int ifreq = freq_.intValue() / 2;
                if (ifreq == 0) {
                    ifreq = 6;
                }
                return FrequencyInformationProvider.getDisplayDomain(freq_, 0, Math.PI / ifreq, npoints);
            default:
                return null;
        }
    }

    @Override
    public ContinuousDisplayDomain getContinuousDisplayDomain(double lower, double upper, int npoints) {
        switch (currentInfo_) {
            case SPECTRUM:
            case GAIN:
                return FrequencyInformationProvider.getDisplayDomain(freq_, lower, upper, npoints);
            case PHASE:
                int ifreq = freq_.intValue() / 2;
                double fmax = upper;
                if (ifreq != 0) {
                    fmax = Math.min(fmax, Math.PI / ifreq);
                } else {
                    fmax = Math.min(fmax, Math.PI / 6);
                }
                return FrequencyInformationProvider.getDisplayDomain(freq_, lower, upper, npoints);
            default:
                return null;
        }
    }

    @Override
    public boolean isDefined(int idx) {
        int cmp = this.cmpDescs_[idx].cmp;
        return !wk_.getUcarimaModel().getComponent(cmp).isNull();
    }
}
