/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramo.descriptors;

import demetra.data.Parameter;
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.tramo.EasterSpec;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Jean Palate
 */
public class EasterSpecUI extends BaseTramoSpecUI {

    @Override
    public String toString() {
        return getOption() == EasterSpec.Type.Unused ? "" : "in use";
    }
    
     private EasterSpec inner() {
        EasterSpec easter = core().getRegression().getCalendar().getEaster();
        return easter;
    }

    public EasterSpecUI(TramoSpecRoot root) {
        super(root);
    }

    public EasterSpec.Type getOption() {
        return inner().getType();
    }

    public void setOption(EasterSpec.Type value) {
        update(inner().toBuilder().type(value).build());
    }

    public int getDuration() {
        return inner().getDuration();
    }

    public void setDuration(int value) {
        update(inner().toBuilder().duration(value).build());
    }

    public boolean isTest() {
        EasterSpec spec = inner();
        if (!spec.isUsed()) {
            return false;
        } else {
            return spec.isTest();
        }
    }

    public void setTest(boolean value) {
        update(inner().toBuilder().test(value).build());
    }

    public boolean isJulian() {
        EasterSpec spec = inner();
        if (!spec.isUsed()) {
            return false;
        } else {
            return spec.isJulian();
        }
    }

    public void setJulian(boolean value) {
        update(inner().toBuilder().julian(value).build());
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
        EnhancedPropertyDescriptor desc = optionDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = julianDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = durationDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = testDesc();
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
    private static final int OPTION_ID = 1, DUR_ID = 2, TEST_ID = 3, JULIAN_ID = 4, COEFF_ID = 5, FIXED_COEFF_ID = 6;

    @Messages({
        "easterSpecUI.optionDesc.name=Option",
        "easterSpecUI.optionDesc.desc=Option"
    })
    private EnhancedPropertyDescriptor optionDesc() {
        if (isJulian()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("option", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, OPTION_ID);
            desc.setDisplayName(Bundle.easterSpecUI_optionDesc_name());
            desc.setShortDescription(Bundle.easterSpecUI_optionDesc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo() || isFixedCoefficient());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "easterSpecUI.durationDesc.name=Duration",
        "easterSpecUI.durationDesc.desc=Duration"
    })
    private EnhancedPropertyDescriptor durationDesc() {
        if (getOption() == EasterSpec.Type.Unused )
            return null;
        try {
            PropertyDescriptor desc = new PropertyDescriptor("duration", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DUR_ID);
            desc.setDisplayName(Bundle.easterSpecUI_durationDesc_name());
            desc.setShortDescription(Bundle.easterSpecUI_durationDesc_desc());
            if (isRo() || isFixedCoefficient()) {
                edesc.setReadOnly(true);
            }
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "easterSpecUI.testDesc.name=Test",
        "easterSpecUI.testDesc.desc=Test",})
    private EnhancedPropertyDescriptor testDesc() {
        if (getOption() == EasterSpec.Type.Unused )
            return null;
        try {
            PropertyDescriptor desc = new PropertyDescriptor("test", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TEST_ID);
            desc.setDisplayName(Bundle.easterSpecUI_testDesc_name());
            desc.setShortDescription(Bundle.easterSpecUI_testDesc_desc());
            if (isRo() || isFixedCoefficient()) {
                edesc.setReadOnly(true);
            }
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "easterSpecUI.julianDesc.desc=Use Julian Easter (expressed in Gregorian calendar)",
        "easterSpecUI.julianDesc.name=Julian",})
    private EnhancedPropertyDescriptor julianDesc() {
        if (getOption() == EasterSpec.Type.Unused )
            return null;
        try {
            PropertyDescriptor desc = new PropertyDescriptor("julian", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, JULIAN_ID);
            desc.setDisplayName(Bundle.easterSpecUI_julianDesc_name());
            desc.setShortDescription(Bundle.easterSpecUI_julianDesc_desc());
            if (isRo() || getOption() == EasterSpec.Type.Unused || isFixedCoefficient()) {
                edesc.setReadOnly(true);
            }
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
            if (isRo() || getOption() == EasterSpec.Type.Unused) {
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
        if (getOption() == EasterSpec.Type.Unused )
            return null;
        try {
            PropertyDescriptor desc = new PropertyDescriptor("fixedCoefficient", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, FIXED_COEFF_ID);
            desc.setDisplayName(Bundle.easterSpecUI_fixedCoeffDesc_name());
            desc.setShortDescription(Bundle.easterSpecUI_fixedCoeffDesc_desc());
            if (isRo() || getOption() == EasterSpec.Type.Unused || isTest() || ! isTransformationDefined()) {
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
