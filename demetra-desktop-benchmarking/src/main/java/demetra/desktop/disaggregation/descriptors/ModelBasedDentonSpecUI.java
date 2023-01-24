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
package demetra.desktop.disaggregation.descriptors;

import demetra.desktop.benchmarking.descriptors.*;
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.tempdisagg.univariate.ModelBasedDentonSpec;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle;

/**
 *
 * @author Jean Palate
 */
public class ModelBasedDentonSpecUI implements IObjectDescriptor<ModelBasedDentonSpec> {

    public static final String MODELBASEDDENTON = "Model-based Denton";

    @Override
    public String toString() {
        return MODELBASEDDENTON;
    }

    private ModelBasedDentonSpec core;

    public ModelBasedDentonSpecUI(ModelBasedDentonSpec spec) {
        core = spec;
    }

    @Override
    public ModelBasedDentonSpec getCore() {
        return core;
    }

    public Utility.AggregationType getType() {
        return Utility.convert(core.getAggregationType());
    }

    public void setType(Utility.AggregationType type) {
        core = core.toBuilder().aggregationType(Utility.convert(type)).build();
    }

//    public int getDifferencing() {
//        return core.getDifferencing();
//    }
//
//    public void setDifferencing(int diff) {
//        core = core.toBuilder().differencing(diff).build();
//    }
//
    public ShockDescriptor[] getShocks() {
        return core.getShockVariances()
                .entrySet()
                .stream()
                .map(var -> new ShockDescriptor(var.getKey(), var.getValue()))
                .toArray(ShockDescriptor[]::new);
    }

    public void setShocks(ShockDescriptor[] value) {
        ModelBasedDentonSpec.Builder builder = core.toBuilder().clearShockVariances();
        for (ShockDescriptor val : value)
            builder.shockVariance(val.getPosition(),val.getVariance());
        
        core=builder.build();
    }
    
    public BiRatioDescriptor[] getFixedBiRatios() {
        return core.getFixedBiRatios()
                .entrySet()
                .stream()
                .map(var -> new BiRatioDescriptor(var.getKey(), var.getValue()))
                .toArray(BiRatioDescriptor[]::new);
    }

    public void setFixedBiRatios(BiRatioDescriptor[] value) {
        ModelBasedDentonSpec.Builder builder = core.toBuilder().clearFixedBiRatios();
        for (BiRatioDescriptor val : value)
            builder.fixedBiRatio(val.getPosition(),val.getValue());
        
        core=builder.build();
    }
    
    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        List<EnhancedPropertyDescriptor> props = new ArrayList<>();
        EnhancedPropertyDescriptor desc = typeDesc();
        if (desc != null) {
            props.add(desc);
        }
//        desc = diffDesc();
//        if (desc != null) {
//            props.add(desc);
//        }
        desc = shocksDesc();
        if (desc != null) {
            props.add(desc);
        }
        desc = biratiosDesc();
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

//    private EnhancedPropertyDescriptor diffDesc() {
//        try {
//            PropertyDescriptor desc = new PropertyDescriptor("Differencing", this.getClass());
//            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DIFF_ID);
//            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
//            desc.setDisplayName(DIFF_NAME);
//            desc.setShortDescription(DIFF_DESC);
//            return edesc;
//        } catch (IntrospectionException ex) {
//            return null;
//        }
//    }

    @NbBundle.Messages({
        "modelBasedDentonSpecUI.shocksDesc.name=Shocks",
        "modelBasedDentonSpecUI.shocksDesc.desc=Shocks"
    })
    private EnhancedPropertyDescriptor shocksDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Shocks", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SHOCKS_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.modelBasedDentonSpecUI_shocksDesc_name());
            desc.setShortDescription(Bundle.modelBasedDentonSpecUI_shocksDesc_desc());
//            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

   @NbBundle.Messages({
        "modelBasedDentonSpecUI.biratiosDesc.name=Fixed bi-ratios",
        "modelBasedDentonSpecUI.biratiosDesc.desc=Fixed bi-ratios"
    })
    private EnhancedPropertyDescriptor biratiosDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("FixedBiRatios", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BIRATIOS_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.modelBasedDentonSpecUI_biratiosDesc_name());
            desc.setShortDescription(Bundle.modelBasedDentonSpecUI_biratiosDesc_desc());
//            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    public String getDisplayName() {
        return MODELBASEDDENTON; //To change body of generated methods, choose Tools | Templates.
    }

    private static final int TYPE_ID = 10, DIFF_ID = 40, SHOCKS_ID=50, BIRATIOS_ID=60;
    private static final String TYPE_NAME = "Type", DIFF_NAME = "Differencing",
            TYPE_DESC = "Type", DIFF_DESC = "Differencing order in the objective function";
}
