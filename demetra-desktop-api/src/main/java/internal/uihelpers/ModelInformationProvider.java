/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package internal.uihelpers;

import demetra.util.NamedObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.IntToDoubleFunction;
import jdplus.arima.AutoCovarianceFunction;
import jdplus.arima.IArimaModel;
import jdplus.arima.Spectrum;

/**
 *
 * @author Jean Palate
 */
public class ModelInformationProvider implements ContinuousInformationProvider, DiscreteInformationProvider {

    private final ArrayList<NamedObject<IArimaModel>> models;

    public ModelInformationProvider(ArrayList<NamedObject<IArimaModel>> models){
        this.models=models;
    }
    
    public ModelInformationProvider(Map<String, ? extends IArimaModel> models){
        this.models= new ArrayList<>();
        for (Entry<String, ? extends IArimaModel> o : models.entrySet()){
            this.models.add(new NamedObject<>(o.getKey(), o.getValue()));
        }
    }
    private void fillSpectrum(int cmp, double[] data, ContinuousDisplayDomain domain) {
        Spectrum spectrum = models.get(cmp).getObject().getSpectrum();
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
        if (currentInfo.equals(AUTOCORRELATIONS)) {
            return new DiscreteDisplayDomain(1, multiple(freq, npoints));
        }
        else {
            return null;
        }
    }

    private int multiple(int freq, int n) {
        if (freq == 0) {
            return n;
        }
        else if (n % freq == 0) {
            return n;
        }
        else {
            return (n / freq + 1) * freq;
        }
    }

    @Override
    public DiscreteDisplayDomain getDiscreteDisplayDomain(int lower, int upper) {
        if (currentInfo.equals(AUTOCORRELATIONS)) {
            return new DiscreteDisplayDomain(Math.max(1, lower), upper);
        }
        else {
            return null;
        }
    }

    @Override
    public double[] getDataArray(int cmp, DiscreteDisplayDomain domain) {
        double[] data = new double[domain.getLength()];
        if (currentInfo.equals(AUTOCORRELATIONS)) {
            fillAutocorrelations(cmp, data, domain);
        }
        else {
            return null;
        }
        return data;
    }

    @Override
    public double getData(int cmp, int x) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void fillAutocorrelations(int cmp, double[] data, DiscreteDisplayDomain domain) {
        IArimaModel cur = models.get(cmp).getObject();
        AutoCovarianceFunction acgf = cur.stationaryTransformation().getStationaryModel().getAutoCovarianceFunction();
        if (acgf == null) {
            return;
        }
        acgf.prepare(domain.beg + data.length);
        double var = acgf.get(0);
        for (int i = 0; i < data.length;
                ++i) {
            data[i] = acgf.get(i + domain.beg) / var;
        }
    }

    public static final String SPECTRUM = "Spectrum", AUTOCORRELATIONS = "Auto-correlations";
    private String currentInfo = SPECTRUM;
    private int freq = 0;

    /**
     * @return the freq
     */
    public int getFrequency() {
        return freq;


    }

    /**
     * @param freq_ the fFrequency to set
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

    @Override
    public double[] getDataArray(int cmp, ContinuousDisplayDomain domain) {
        double[] data = new double[domain.npoints];

        if (currentInfo.equals(SPECTRUM)) {
            fillSpectrum(cmp, data, domain);
       }
        else {
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
        if (currentInfo.equals(SPECTRUM)){
            return FrequencyInformationProvider.getDisplayDomain(freq, npoints);
       }
        else {
            return null;
        }
    }

    @Override
    public ContinuousDisplayDomain getContinuousDisplayDomain(double lower, double upper, int npoints) {
        if (currentInfo.equals(SPECTRUM)) {
            return FrequencyInformationProvider.getDisplayDomain(freq, lower, upper, npoints);
        }
         else {
            return null;
        }
    }

    @Override
    public boolean isDefined(int idx) {
        return models.get(idx).getObject() != null && !models.get(idx).getObject().isNull();
    }

    @Override
    public String[] getComponents() {
        String[] names=new String[models.size()];
        for (int i=0; i<names.length; ++i){
            names[i]=models.get(i).getName();
        }
        return names;
    }
}
