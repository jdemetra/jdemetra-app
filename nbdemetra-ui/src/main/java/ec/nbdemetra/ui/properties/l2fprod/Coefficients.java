/*
 * Copyright 2017 National Bank of Belgium
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
package ec.nbdemetra.ui.properties.l2fprod;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Mats Maggi
 */
public class Coefficients {

    private Map<String, double[]> fixedCoefficients;
    private String[] allNames;

    public Coefficients() {
        fixedCoefficients = new LinkedHashMap<>();
    }

    public Coefficients(Map<String, double[]> fixed) {
        this.fixedCoefficients = new LinkedHashMap<>(fixed);
    }
    
    public Coefficients(Coefficients copy) {
        if (copy.allNames != null) {
            allNames = Arrays.copyOf(copy.allNames, copy.allNames.length);
        }
        fixedCoefficients = new LinkedHashMap<>(copy.fixedCoefficients);    
    }

    public void setAllNames(String[] allNames) {
        this.allNames = allNames;
    }

    public String[] getAllNames() {
        return allNames;
    }

    public Map<String, double[]> getFixedCoefficients() {
        return fixedCoefficients;
    }

    public void setFixedCoefficients(Map<String, double[]> fixedCoefficients) {
        this.fixedCoefficients = new LinkedHashMap<>(fixedCoefficients);
    }

    @Override
    protected Coefficients clone() {
        Coefficients clone = new Coefficients();
        if (allNames != null) {
            clone.setAllNames(Arrays.copyOf(allNames, allNames.length));
        }
        clone.setFixedCoefficients(new LinkedHashMap<>(fixedCoefficients));
        return clone;
    }
}
