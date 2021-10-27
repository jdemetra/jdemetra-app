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
package demetra.desktop.anomalydetection.report;

import demetra.timeseries.TsPeriod;
import demetra.util.MultiLineNameUtil;
import java.text.DecimalFormat;

/**
 *
 * @author Mats Maggi
 */
public class AnomalyPojo {
    
    private static final DecimalFormat df = new DecimalFormat("0.00");
//    private static int NAME_LEN=60;

    public enum Status {

        Empty("Empty"),
        Valid("Valid"),
        Invalid("Invalid");

        Status(String name) {
        }
    }
    
    private String tsName;
    private TsPeriod period;
    private Double relativeError;
    private Double absoluteError;
    private Status validity;

    public AnomalyPojo() {
    }

    public AnomalyPojo(String name, TsPeriod p, double rel, double abs, Status v) {
        tsName = name;
        period = p;
        relativeError = rel;
        absoluteError = abs;
        validity = v;
    }

    public String getTsName() {
        return tsName;
    }

    public void setTsName(String tsName) {
        this.tsName = tsName;
    }

    public TsPeriod getPeriod() {
        return period;
    }

    public void setPeriod(TsPeriod period) {
        this.period = period;
    }

    public Double getRelativeError() {
        return relativeError;
    }

    public void setRelativeError(Double relativeError) {
        this.relativeError = relativeError;
    }

    public Double getAbsoluteError() {
        return absoluteError;
    }

    public void setAbsoluteError(Double absoluteError) {
        this.absoluteError = absoluteError;
    }

    public Status getValidity() {
        return validity;
    }

    public void setValidity(Status valid) {
        this.validity = valid;
    }
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
//        if(tsName.length() > NAME_LEN) {
//            result += tsName.substring(0, NAME_LEN) + "... \t ";
//        } else {
//            result += String.format("%1$-" + (NAME_LEN+3)+"s \t ", tsName);
//        }
        result.append(MultiLineNameUtil.join(tsName)).append('\t');
        result.append(period).append('\t');
        result.append(df.format(absoluteError)).append('\t');
        result.append(df.format(relativeError));
        
        return result.toString();
    }
}
