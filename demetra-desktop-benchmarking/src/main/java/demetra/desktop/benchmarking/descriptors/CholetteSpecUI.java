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
package demetra.desktop.benchmarking.descriptors;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class CholetteSpecUI implements IObjectDescriptor<UniCholetteSpecification> {
    
    public static final String CHOLETTE = "Cholette";

    @Override
    public String toString() {
        return CHOLETTE;
    }

    final UniCholetteSpecification core;

    public CholetteSpecUI(UniCholetteSpecification spec) {
        core = spec;
    }

    @Override
    public UniCholetteSpecification getCore() {
        return core;
    }

    public BasicSpecUI.AggregationType getType() {
        return convert(core.getAggregationType());
    }

    public void setType(BasicSpecUI.AggregationType type) {
        core.setAggregationType(convert(type));
    }

    public TsFrequency getFrequency() {
        return core.getAggregationFrequency();
    }

    public void setFrequency(TsFrequency freq) {
        core.setAggregationFrequency(freq);
    }

    public double getRho() {
        return core.getRho();
    }

    public void setRho(double r) {
        core.setRho(r);
    }

    public double getLambda() {
        return core.getLambda();
    }

    public void setLambda(double r) {
        core.setLambda(r);
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        List<EnhancedPropertyDescriptor> props = new ArrayList<>();
        EnhancedPropertyDescriptor desc = typeDesc();
        if (desc != null) {
            props.add(desc);
        }
        desc = freqDesc();
        if (desc != null) {
            props.add(desc);
        }
        desc = rhoDesc();
        if (desc != null) {
            props.add(desc);
        }
        desc = lambdaDesc();
        if (desc != null) {
            props.add(desc);
        }
        return props;
    }

    private EnhancedPropertyDescriptor freqDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Frequency", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, FREQ_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(FREQ_NAME);
            desc.setShortDescription(FREQ_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor typeDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Type", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TYPE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(TYPE_NAME);
            desc.setShortDescription(TYPE_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor rhoDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Rho", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, RHO_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(RHO_NAME);
            desc.setShortDescription(RHO_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor lambdaDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Lambda", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LAMBDA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(LAMBDA_NAME);
            desc.setShortDescription(LAMBDA_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    public String getDisplayName() {
        return CHOLETTE; //To change body of generated methods, choose Tools | Templates.
    }

    private static final int TYPE_ID = 0, FREQ_ID = 10, RHO_ID = 20, LAMBDA_ID = 30;
    private static final String TYPE_NAME = "Type", FREQ_NAME = "Aggregation frequency",
            RHO_NAME = "Rho", LAMBDA_NAME = "Lambda",
            TYPE_DESC = "Type", FREQ_DESC = "Aggregation frequency",
            RHO_DESC = "Rho", LAMBDA_DESC = "Lambda";
}
