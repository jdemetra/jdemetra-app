/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package internal.uihelpers;

import demetra.data.DoubleSeq;
import jdplus.arima.IArimaModel;
import jdplus.arima.Spectrum;

/**
 *
 * @author Jean Palate
 */
@lombok.experimental.UtilityClass
public class ArimaInformationProvider  {


    private DoubleSeq spectrum(IArimaModel model, ContinuousDisplayDomain domain) {
        Spectrum spectrum = model.getSpectrum();
        if (spectrum == null) {
            return DoubleSeq.empty();
        }

        double[] data=new double[domain.npoints];
        double x = domain.beg;
        for (int i = 0; i < data.length; ++i) {
            data[i] = spectrum.get(x);
            x += domain.step;
        }
        return DoubleSeq.of(data);
    }
    
//    private int multiple(int ifreq, int n) {
//        if (ifreq == 0) {
//            return n;
//        }
//        else if (n % ifreq == 0) {
//            return n;
//        }
//        else {
//            return (n / ifreq + 1) * ifreq;
//        }
//    }
//
//    @Override
//    public DiscreteDisplayDomain getDiscreteDisplayDomain(int lower, int upper) {
//        if (currentInfo_.equals(AUTOCORRELATIONS)) {
//            return new DiscreteDisplayDomain(Math.max(1, lower), upper);
//        }
//        else {
//            return null;
//        }
//    }
//
//    @Override
//    public double[] getDataArray(int cmp, DiscreteDisplayDomain domain) {
//        double[] data = new double[domain.getLength()];
//        if (currentInfo_.equals(AUTOCORRELATIONS)) {
//            fillAutocorrelations(cmp, data, domain);
//        }
//        else {
//            return null;
//        }
//        return data;
//    }
//
//    @Override
//    public double getData(int cmp, int x) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    private void fillAutocorrelations(int cmp, double[] data, DiscreteDisplayDomain domain) {
//        IArimaModel cur = models_.get(cmp).object;
//        AutoCovarianceFunction acgf = cur.stationaryTransformation().stationaryModel.getAutoCovarianceFunction();
//        if (acgf == null) {
//            return;
//        }
//        acgf.prepare(domain.beg + data.length);
//        double var = acgf.get(0);
//        for (int i = 0; i < data.length;
//                ++i) {
//            data[i] = acgf.get(i + domain.beg) / var;
//        }
//    }
//
//    public static final String SPECTRUM = "Spectrum", AUTOCORRELATIONS = "Auto-correlations";
//    private String currentInfo_ = SPECTRUM;
//    private TsFrequency freq_ = TsFrequency.Undefined;
//
//    /**
//     * @return the freq_
//     */
//    public TsFrequency getFrequency() {
//        return freq_;
//
//
//    }
//
//    /**
//     * @param freq_ the fFrequency to set
//     */
//    public void setFrequency(TsFrequency freq_) {
//        this.freq_ = freq_;
//
//
//    }
//
//    public void setInformation(String info) {
//        currentInfo_ = info;
//
//
//    }
//
//    /**
//     *
//     * @return
//     */
//    @Override
//    public String getInformation() {
//        return currentInfo_;
//
//
//    }
//
//    @Override
//    public double[] getDataArray(int cmp, ContinuousDisplayDomain domain) {
//        double[] data = new double[domain.npoints];
//
//        if (currentInfo_.equals(SPECTRUM)) {
//            fillSpectrum(cmp, data, domain);
//       }
//        else {
//            return null;
//
//
//        }
//        return data;
//    }
//
//    @Override
//    public double getData(int cmp, double x) {
//        throw new UnsupportedOperationException("Not supported yet.");
//
//
//    }
//
//    @Override
//    public ContinuousDisplayDomain getContinuousDisplayDomain(int npoints) {
//        if (currentInfo_.equals(SPECTRUM)){
//            return FrequencyInformationProvider.getDisplayDomain(freq_, npoints);
//       }
//        else {
//            return null;
//        }
//    }
//
//    @Override
//    public ContinuousDisplayDomain getContinuousDisplayDomain(double lower, double upper, int npoints) {
//        if (currentInfo_.equals(SPECTRUM)) {
//            return FrequencyInformationProvider.getDisplayDomain(freq_, lower, upper, npoints);
//        }
//         else {
//            return null;
//        }
//    }
//
//    @Override
//    public boolean isDefined(int idx) {
//        return models_.get(idx).object != null && !models_.get(idx).object.isNull();
//    }
//
//    @Override
//    public String[] getComponents() {
//        String[] names=new String[models_.size()];
//        for (int i=0; i<names.length; ++i){
//            names[i]=models_.get(i).name;
//        }
//        return names;
//    }
}
