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

import demetra.benchmarking.univariate.DentonSpec;
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IObjectDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class DentonSpecUI implements IObjectDescriptor<DentonSpec> {

    public static final String DENTON = "Denton";

    @Override
    public String toString() {
        return DENTON;
    }

    private DentonSpec core;
 
    public DentonSpecUI(DentonSpec spec) {
        core = spec;
    }

    @Override
    public DentonSpec getCore() {
        return core;
    }

    public Utility.AggregationType getType() {
        return Utility.convert(core.getAggregationType());
    }

    public void setType(Utility.AggregationType type) {
        core = core.toBuilder().aggregationType(Utility.convert(type)).build();
    }

    public int getDifferencing() {
        return core.getDifferencing();
    }

    public void setDifferencing(int diff) {
        core = core.toBuilder().differencing(diff).build();
    }

    public boolean isMultiplicative() {
        return core.isMultiplicative();
    }

    public void setMultiplicative(boolean mul) {
        core = core.toBuilder().multiplicative(mul).build();
    }

    public boolean isModified() {
        return core.isModified();
    }

    public void setModified(boolean mod) {
        core = core.toBuilder().modified(mod).build();
    }

    public int getFrequency() {
        return core.getDefaultPeriod();
    }

    public void setFrequency(int period) {
        core=core.toBuilder().defaultPeriod(period).build();
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        List<EnhancedPropertyDescriptor> props = new ArrayList<>();
        EnhancedPropertyDescriptor desc = typeDesc();
        if (desc != null) {
            props.add(desc);
        }
        desc = mulDesc();
        if (desc != null) {
            props.add(desc);
        }
        desc = modDesc();
        if (desc != null) {
            props.add(desc);
        }
        desc = diffDesc();
        if (desc != null) {
            props.add(desc);
        }
        desc = freqDesc();
        if (desc != null) {
            props.add(desc);
        }
        return props;
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

    private EnhancedPropertyDescriptor diffDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Differencing", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DIFF_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(DIFF_NAME);
            desc.setShortDescription(DIFF_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor mulDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Multiplicative", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, MUL_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(MUL_NAME);
            desc.setShortDescription(MUL_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor modDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Modified", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, MOD_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(MOD_NAME);
            desc.setShortDescription(MOD_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor freqDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Frequency", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DEFFREQ_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(DEFFREQ_NAME);
            desc.setShortDescription(DEFFREQ_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    public String getDisplayName() {
        return DENTON; //To change body of generated methods, choose Tools | Templates.
    }

    private static final int TYPE_ID = 10, DIFF_ID = 40, MUL_ID = 20, MOD_ID = 30, DEFFREQ_ID = 40;
    private static final String TYPE_NAME = "Type", DIFF_NAME = "Differencing",
            MUL_NAME = "Multiplicative", MOD_NAME = "Modified Denton", DEFFREQ_NAME = "Default frequency",
            TYPE_DESC = "Type", DIFF_DESC = "Differencing order in the objective function",
            MUL_DESC = "Multiplicative", MOD_DESC = "Modified Denton", DEFFREQ_DESC = "Default frequency";
}
