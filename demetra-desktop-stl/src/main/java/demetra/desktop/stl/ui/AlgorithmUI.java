/*
 * Copyright 2022 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.stl.ui;

import demetra.data.WeightFunction;
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle;

/**
 *
 * @author PALATEJ
 */
public class AlgorithmUI extends BaseStlPlusSpecUI {
    
    public AlgorithmUI(StlPlusSpecRoot root){
        super(root);
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = mulDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = niDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = noDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = wfnDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = wtDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    public WeightFunction getRobustWeightFunction() {
        return root.core.getRobustWeightFunction();
    }

    public void setRobustWeightFunction(WeightFunction wfn) {
        root.core = root.core.toBuilder().robustWeightFunction(wfn).build();
    }

    public double getRobustWeightThreshold() {
        return root.core.getRobustWeightThreshold();
    }

    public void setRobustWeightThreshold(double wt) {
        root.core = root.core.toBuilder().robustWeightThreshold(wt).build();
    }
    
   public boolean isMultiplicative() {
        return root.core.isMultiplicative();
    }

    public void setMultiplicative(boolean mul) {
        root.core = root.core.toBuilder()
                .multiplicative(mul)
                .build();
    }

    public int getInnerLoopCount() {
        return root.core.getInnerLoopsCount();
    }

    public void setInnerLoopCount(int ni) {
        if (ni < 1) {
            throw new IllegalArgumentException("Number of inner loops should be ge 1");
        }

        root.core = root.core.toBuilder()
                .innerLoopsCount(ni)
                .build();
    }

    public int getOuterLoopCount() {
        return root.core.getOuterLoopsCount();
    }

    public void setOuterLoopCount(int no) {
        if (no < 0) {
            throw new IllegalArgumentException("Number of outer loops should be ge 0");
        }
        root.core = root.core.toBuilder()
                .outerLoopsCount(no)
                .build();
    }

    @NbBundle.Messages({
        "algorithmUI.mulDesc.name=mul",
        "algorithmUI.mulDesc.desc=Multiplicative decomposition."
    })
    private EnhancedPropertyDescriptor mulDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Multiplicative", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, MUL_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.algorithmUI_mulDesc_name());
            desc.setShortDescription(Bundle.algorithmUI_mulDesc_desc());
            edesc.setReadOnly(root.ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "algorithmUI.niDesc.name=inner loops",
        "algorithmUI.niDesc.desc=Number of inner loops."
    })
    private EnhancedPropertyDescriptor niDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("InnerLoopCount", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, NI_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.algorithmUI_niDesc_name());
            desc.setShortDescription(Bundle.algorithmUI_niDesc_desc());
            edesc.setReadOnly(root.ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "algorithmUI.noDesc.name=outer loops",
        "algorithmUI.noDesc.desc=Number of outer loops."
    })
    private EnhancedPropertyDescriptor noDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("OuterLoopCount", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, NO_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.algorithmUI_noDesc_name());
            desc.setShortDescription(Bundle.algorithmUI_noDesc_desc());
            edesc.setReadOnly(root.ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "algorithmUI.wfnDesc.name=robust weights",
        "algorithmUI.wfnDesc.desc=Robust weights function (outliers)."
    })
    private EnhancedPropertyDescriptor wfnDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("RobustWeightFunction", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, WFN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.algorithmUI_wfnDesc_name());
            desc.setShortDescription(Bundle.algorithmUI_wfnDesc_desc());
            edesc.setReadOnly(root.ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "algorithmUI.wtDesc.name=robust weights threshold",
        "algorithmUI.wtDesc.desc=Robust weights threshold (outliers)."
    })
    private EnhancedPropertyDescriptor wtDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("RobustWeightThreshold", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, WT_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.algorithmUI_wtDesc_name());
            desc.setShortDescription(Bundle.algorithmUI_wtDesc_desc());
            edesc.setReadOnly(root.ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
    private static final int MUL_ID = 1, NI_ID = 10, NO_ID = 11, WFN_ID = 12, WT_ID = 13;

    @Override
    @NbBundle.Messages("algorithmUI.getDisplayName=Algorithm")
    public String getDisplayName() {
        return Bundle.algorithmUI_getDisplayName();
    }

}
