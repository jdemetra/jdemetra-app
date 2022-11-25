/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.regarima.descriptors;

import demetra.data.Parameter;
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.regarima.EasterSpec;
import demetra.regarima.RegressionTestSpec;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Jean Palate
 */
public class EasterSpecUI extends BaseRegArimaSpecUI {

    @Override
    public String toString() {
        return isEnabled() ? "in use" : "";
    }

    private EasterSpec inner() {
        EasterSpec easter = core().getRegression().getEaster();
        return easter;
    }

    private boolean isEnabled() {
        return inner().getType() != EasterSpec.Type.Unused;
    }

    public EasterSpecUI(RegArimaSpecRoot root) {
        super(root);
    }

    public EasterSpec.Type getType() {
        return inner().getType();
    }

    // should be changed in the future, with new moving holidays !!!
    public void setType(EasterSpec.Type value) {
        update(inner().toBuilder().type(value).build());
    }

    public RegressionTestSpec getTest() {
        return inner().getTest();
    }

    public void setTest(RegressionTestSpec value) {
        update(inner().toBuilder().test(value).build());
    }

    public int getDuration() {
        return inner().getDuration();
    }

    public void setDuration(int value) {
        update(inner().toBuilder().duration(value).build());
    }

    public double getCoefficient() {
        Parameter coefficient = inner().getCoefficient();
        return coefficient != null && coefficient.isDefined() ? coefficient.getValue() : 0;
    }

    public void setCoefficient(double p) {
        Parameter coefficient = p == 0 ? Parameter.undefined() : Parameter.fixed(p);
        update(inner().toBuilder().coefficient(coefficient).build());
    }

    public boolean isFixedCoefficient() {
        Parameter coefficient = inner().getCoefficient();
        return coefficient != null && coefficient.isFixed();
    }

    public void setFixedCoefficient(boolean f) {
        Parameter coefficient = inner().getCoefficient();
        if (coefficient == null) {
            coefficient = Parameter.undefined();
        }
        if (f && !coefficient.isFixed()) {
            coefficient = Parameter.fixed(coefficient.getValue());
            update(inner().toBuilder().coefficient(coefficient).build());
        } else if (coefficient.isFixed()) {
            if (coefficient.getValue() == 0) {
                coefficient = Parameter.undefined();
            } else {
                coefficient = Parameter.initial(coefficient.getValue());
            }
            update(inner().toBuilder().coefficient(coefficient).build());
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
        "easterSpecUI.typeDesc.name=Type",
        "easterSpecUI.typeDesc.desc="
    })
    private EnhancedPropertyDescriptor typeDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Type", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TYPE_ID);
            desc.setDisplayName(Bundle.easterSpecUI_typeDesc_name());
            desc.setShortDescription(Bundle.easterSpecUI_typeDesc_desc());
            edesc.setReadOnly(isRo() || isFixedCoefficient());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "easterSpecUI.durationDesc.name=Easter duration",
        "easterSpecUI.durationDesc.desc=[w] Length of the easter regression effect"
    })
    private EnhancedPropertyDescriptor durationDesc() {
        if (!isEnabled() || inner().isAutomatic()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("duration", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DUR_ID);
            desc.setDisplayName(Bundle.easterSpecUI_durationDesc_name());
            desc.setShortDescription(Bundle.easterSpecUI_durationDesc_desc());
            edesc.setReadOnly(isRo() || getTest() == RegressionTestSpec.Add || isFixedCoefficient());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "easterSpecUI.testDesc.name=Pre-test",
        "easterSpecUI.testDesc.desc="
    })
    private EnhancedPropertyDescriptor testDesc() {
        if (!isEnabled()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("test", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TEST_ID);
            desc.setDisplayName(Bundle.easterSpecUI_testDesc_name());
            desc.setShortDescription(Bundle.easterSpecUI_testDesc_desc());
            edesc.setReadOnly(isRo() || isFixedCoefficient());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "easterSpecUI.coeffDesc.name=Coefficient",
        "easterSpecUI.coeffDesc.desc=Coefficient"
    })

    private EnhancedPropertyDescriptor coeffDesc() {
        if (!isFixedCoefficient()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("coefficient", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, COEFF_ID);
            desc.setDisplayName(Bundle.easterSpecUI_coeffDesc_name());
            desc.setShortDescription(Bundle.easterSpecUI_coeffDesc_desc());
            if (isRo()) {
                edesc.setReadOnly(true);
            }
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "easterSpecUI.fixedCoeffDesc.name=Fixed coeff.",
        "easterSpecUI.fixedCoeffDesc.desc=Fixed coeff."
    })
    private EnhancedPropertyDescriptor fixedCoeffDesc() {
        if (!isEnabled()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("fixedCoefficient", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, FIXED_COEFF_ID);
            desc.setDisplayName(Bundle.easterSpecUI_fixedCoeffDesc_name());
            desc.setShortDescription(Bundle.easterSpecUI_fixedCoeffDesc_desc());
            if (isRo() || !isEnabled() || this.getTest() != RegressionTestSpec.None || !isTransformationDefined()) {
                edesc.setReadOnly(true);
            }
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    @Messages("easterSpecUI.getDisplayName=Easter")
    public String getDisplayName() {
        return Bundle.easterSpecUI_getDisplayName();
    }
}
