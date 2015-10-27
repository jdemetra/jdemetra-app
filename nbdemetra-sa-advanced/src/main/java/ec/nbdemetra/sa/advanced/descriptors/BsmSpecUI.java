/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.advanced.descriptors;

import ec.nbdemetra.ui.properties.l2fprod.CustomPropertyEditorRegistry;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IPropertyDescriptors;
import ec.tstoolkit.structural.BsmSpecification;
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
