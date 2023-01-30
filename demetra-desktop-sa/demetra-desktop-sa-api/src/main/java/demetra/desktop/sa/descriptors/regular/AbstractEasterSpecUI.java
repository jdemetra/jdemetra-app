/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.descriptors.regular;

import demetra.data.Parameter;
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IPropertyDescriptors;
import demetra.modelling.TransformationType;
import demetra.modelling.regular.EasterSpec;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Jean Palate
 */
public abstract class AbstractEasterSpecUI implements IPropertyDescriptors {

    protected abstract EasterSpec spec();

    protected abstract RegularSpecUI root();

    @Override
    public String toString() {
        return isEnabled() ? "in use" : "";
    }

    private boolean isEnabled() {
        return spec().getType() != EasterSpec.Type.UNUSED;
    }

    public EasterSpec.Type getType() {
        return spec().getType();
    }

    // should be changed in the future, with new moving holidays !!!
    public void setType(EasterSpec.Type value) {
        root().update(spec().toBuilder().type(value).build());
    }

    public boolean isTest() {
        return spec().isTest();
    }

    public void setTest(boolean value) {
        root().update(spec().toBuilder().test(value).build());
    }

    public int getDuration() {
        return spec().getDuration();
    }

    public void setDuration(int value) {
        root().update(spec().toBuilder().duration(value).build());
    }

    public double getCoefficient() {
        Parameter coefficient = spec().getCoefficient();
        return coefficient != null && coefficient.isDefined() ? coefficient.getValue() : 0;
    }

    public void setCoefficient(double p) {
        Parameter coefficient = p == 0 ? Parameter.undefined() : Parameter.fixed(p);
        root().update(spec().toBuilder().coefficient(coefficient).build());
    }

    public boolean isFixedCoefficient() {
        Parameter coefficient = spec().getCoefficient();
        return coefficient != null && coefficient.isFixed();
    }

    public void setFixedCoefficient(boolean f) {
        Parameter coefficient = spec().getCoefficient();
        if (coefficient == null) {
            coefficient = Parameter.undefined();
        }
        if (f && !coefficient.isFixed()) {
            coefficient = Parameter.fixed(coefficient.getValue());
            root().update(spec().toBuilder().coefficient(coefficient).build());
        } else if (coefficient.isFixed()) {
            if (coefficient.getValue() == 0) {
                coefficient = Parameter.undefined();
            } else {
                coefficient = Parameter.initial(coefficient.getValue());
            }
            root().update(spec().toBuilder().coefficient(coefficient).build());
        }
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        // regression
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = typeDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = testDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = durationDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = fixedCoeffDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = coeffDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    ///////////////////////////////////////////////////////////////////////////
    private static final int TYPE_ID = 1, AUTO_ID = 2, TEST_ID = 3, DUR_ID = 4, COEFF_ID = 5, FIXED_COEFF_ID = 6;

    @Messages({
        "regular.easterSpecUI.typeDesc.name=Type",
        "regular.easterSpecUI.typeDesc.desc="
    })
    private EnhancedPropertyDescriptor typeDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Type", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TYPE_ID);
            desc.setDisplayName(Bundle.regular_easterSpecUI_typeDesc_name());
            desc.setShortDescription(Bundle.regular_easterSpecUI_typeDesc_desc());
            edesc.setReadOnly(root().isRo() || isFixedCoefficient());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regular.easterSpecUI.durationDesc.name=Easter duration",
        "regular.easterSpecUI.durationDesc.desc=[w] Length of the easter regression effect"
    })
    private EnhancedPropertyDescriptor durationDesc() {
        if (!isEnabled()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("duration", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DUR_ID);
            desc.setDisplayName(Bundle.regular_easterSpecUI_durationDesc_name());
            desc.setShortDescription(Bundle.regular_easterSpecUI_durationDesc_desc());
            edesc.setReadOnly(root().isRo() || spec().isTest() || isFixedCoefficient());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regular.easterSpecUI.testDesc.name=Pre-test",
        "regular.easterSpecUI.testDesc.desc="
    })
    private EnhancedPropertyDescriptor testDesc() {
        if (!isEnabled()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("test", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TEST_ID);
            desc.setDisplayName(Bundle.regular_easterSpecUI_testDesc_name());
            desc.setShortDescription(Bundle.regular_easterSpecUI_testDesc_desc());
            edesc.setReadOnly(root().isRo() || isFixedCoefficient());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regular.easterSpecUI.coeffDesc.name=Coefficient",
        "regular.easterSpecUI.coeffDesc.desc=Coefficient"
    })

    private EnhancedPropertyDescriptor coeffDesc() {
        if (!isFixedCoefficient()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("coefficient", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, COEFF_ID);
            desc.setDisplayName(Bundle.regular_easterSpecUI_coeffDesc_name());
            desc.setShortDescription(Bundle.regular_easterSpecUI_coeffDesc_desc());
            if (root().isRo()) {
                edesc.setReadOnly(true);
            }
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regular.easterSpecUI.fixedCoeffDesc.name=Fixed coeff.",
        "regular.easterSpecUI.fixedCoeffDesc.desc=Fixed coeff."
    })
    private EnhancedPropertyDescriptor fixedCoeffDesc() {
        if (!isEnabled()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("fixedCoefficient", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, FIXED_COEFF_ID);
            desc.setDisplayName(Bundle.regular_easterSpecUI_fixedCoeffDesc_name());
            desc.setShortDescription(Bundle.regular_easterSpecUI_fixedCoeffDesc_desc());
            if (root().isRo() || !isEnabled() || spec().isTest() || root().transform().getFunction() == TransformationType.Auto) {
                edesc.setReadOnly(true);
            }
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    @Messages("regular.easterSpecUI.getDisplayName=Easter")
    public String getDisplayName() {
        return Bundle.regular_easterSpecUI_getDisplayName();
    }
}
