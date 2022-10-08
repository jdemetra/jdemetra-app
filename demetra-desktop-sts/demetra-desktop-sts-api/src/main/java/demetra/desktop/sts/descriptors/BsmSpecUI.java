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
package demetra.desktop.sts.descriptors;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class BsmSpecUI implements IPropertyDescriptors {
    static{
        CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(BsmSpecification.Optimizer.class);
    }

    final BsmSpecification core;

    public BsmSpecUI(BsmSpecification spec) {
        core = spec;
    }

    public BsmModelSpecUI getModel() {
        return new BsmModelSpecUI(core.getModelSpecification());
    }

    public BsmSpecification.Optimizer getOptimizer() {
        return core.getOptimizer();
    }

    public void setOptimizer(BsmSpecification.Optimizer opt) {
        core.setOptimizer(opt);
    }

    public double getPrecision() {
        return core.getPrecision();
    }

    public void setPrecision(double tol) {
        core.setPrecision(tol);
    }

    public boolean isDiffuseRegs() {
        return core.isDiffuseRegressors();
    }

    public void setDiffuseRegs(boolean dregs) {
        core.setDiffuseRegressors(dregs);
    }


    private EnhancedPropertyDescriptor mDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("model", this.getClass(), "getModel", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, M_ID);
            desc.setDisplayName(M_NAME);
            desc.setShortDescription(M_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor pDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("precision", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, P_ID);
            desc.setDisplayName(P_NAME);
            desc.setShortDescription(P_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor oDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("optimizer", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, O_ID);
            desc.setDisplayName(O_NAME);
            desc.setShortDescription(O_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor dDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("diffuseRegs", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, D_ID);
            desc.setDisplayName(D_NAME);
            desc.setShortDescription(D_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = mDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = oDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = pDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = dDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    public static final int M_ID = 0, O_ID = 1, P_ID = 2, D_ID = 3;
    public static final String M_NAME = "Model",
            O_NAME = "Optimizer",
            P_NAME = "Precision",
            D_NAME = "Diffuse reg. coefficients";
    public static final String M_DESC = "Model",
            O_DESC = "Optimization method used in the computatuion of the structural model",
            P_DESC = "Precision used in the computatuion of the structural model",
            D_DESC = "Indicates if the regression coefficients are considered as diffuse (arbitrary large variance) "
            + "or fixed (unkonwn)";


    public String getDisplayName() {
        return "Basic structural model";
    }
}
