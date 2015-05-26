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
package ec.nbdemetra.sa.advanced.descriptors;

import ec.tstoolkit.Parameter;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IPropertyDescriptors;
import ec.tstoolkit.structural.ComponentUse;
import ec.tstoolkit.structural.ModelSpecification;
import ec.tstoolkit.structural.SeasonalModel;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class BsmModelSpecUI implements IPropertyDescriptors {

    final ModelSpecification core;

    public BsmModelSpecUI(ModelSpecification spec) {
        core = spec;
    }

    public ComponentUse getLevel() {
        return core.getLevelUse();
    }

    public void setLevel(ComponentUse use) {
        core.useLevel(use);
    }

    public ComponentUse getSlope() {
        return core.getSlopeUse();
    }

    public void setSlope(ComponentUse use) {
        core.useSlope(use);
    }

    public ComponentUse getNoise() {
        return core.getNoiseUse();
    }

    public void setNoise(ComponentUse use) {
        core.useNoise(use);
    }

    public ComponentUse getCycle() {
        return core.getCycleUse();
    }

    public void setCycle(ComponentUse use) {
        core.useCycle(use);
    }

    public SeasonalModel getModel() {
        return core.getSeasonalModel();
    }

    public void setModel(SeasonalModel model) {
        core.setSeasonalModel(model);
    }
    
    public Parameter[] getCycleDumpingFactor(){
        Parameter p=core.getCyclicalDumpingFactor();
        if (p == null)
            p=new Parameter();
        return new Parameter[]{p};
    }

    public Parameter[] getCycleLength(){
        Parameter p=core.getCyclicalPeriod();
        if (p == null)
            p=new Parameter();
        return new Parameter[]{p};
    }
    
    public void setCycleDumpingFactor(Parameter[] p){
        if (p != null && p.length == 1)
            core.setCyclicalDumpingFactor(p[0]);
    }

    public void setCycleLength(Parameter[] p){
        if (p != null && p.length == 1)
            core.setCyclicalPeriod(p[0]);
    }

    private EnhancedPropertyDescriptor lDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("level", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, L_ID);
            desc.setDisplayName(L_NAME);
            desc.setShortDescription(L_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor sDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("slope", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, S_ID);
            desc.setDisplayName(S_NAME);
            desc.setShortDescription(S_DESC);
            edesc.setReadOnly(getLevel() == ComponentUse.Unused);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor smDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("model", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SM_ID);
            desc.setDisplayName(SM_NAME);
            desc.setShortDescription(SM_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor nDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("noise", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, N_ID);
            desc.setDisplayName(N_NAME);
            desc.setShortDescription(N_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor cDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("cycle", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, C_ID);
            desc.setDisplayName(C_NAME);
            desc.setShortDescription(C_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor cdDesc() {
        try {
            if (core.getCycleUse() == ComponentUse.Unused) {
                return null;
            }
            PropertyDescriptor desc = new PropertyDescriptor("cycleDumpingFactor", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, CDUMP_ID);
            desc.setDisplayName(CDUMP_NAME);
            desc.setShortDescription(CDUMP_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor clDesc() {
        try {
            if (core.getCycleUse() == ComponentUse.Unused) {
                return null;
            }
            PropertyDescriptor desc = new PropertyDescriptor("cycleLength", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, CLEN_ID);
            desc.setDisplayName(CLEN_NAME);
            desc.setShortDescription(CLEN_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = lDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = sDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = cDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = cdDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = clDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = nDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = smDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    public static final int L_ID = 0, S_ID = 1, C_ID = 2, CDUMP_ID = 3, CLEN_ID = 4, N_ID = 6, SM_ID = 10;
    public static final String L_NAME = "Level",
            S_NAME = "Slope",
            C_NAME = "Cycle",
            CLEN_NAME = "Cycle length",
            CDUMP_NAME = "Cycle dumping factor",
            N_NAME = "Noise",
            SM_NAME = "Seasonal model";
    public static final String L_DESC = "Level",
            S_DESC = "Slope",
            C_DESC = "Cycle",
            CLEN_DESC = "Cycle length",
            CDUMP_DESC = "Cycle dumping factor",
            N_DESC = "Noise",
            SM_DESC = "Seasonal model";

    @Override
    public String getDisplayName() {
        return "Basic structural model";
    }

    @Override
    public String toString() {
        return "";
    }
}
