/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13.descriptors;

import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.modelling.RegressionTestSpec;
import ec.tstoolkit.modelling.arima.tramo.EasterSpec;
import ec.tstoolkit.modelling.arima.x13.MovingHolidaySpec;
import ec.tstoolkit.modelling.arima.x13.RegArimaSpecification;
import ec.tstoolkit.modelling.arima.x13.RegressionSpec;
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

    private MovingHolidaySpec getInner() {
        if (core.getRegression() == null) {
            return null;
        }
        return core.getRegression().getEaster();
    }

    public boolean isEnabled() {
        return getInner() != null;
    }

    private MovingHolidaySpec inner() {
        if (core.getRegression() == null) {
            core.setRegression(new RegressionSpec());
        }

        MovingHolidaySpec easter = core.getRegression().getEaster();

        if (easter == null) {
            easter = MovingHolidaySpec.easterSpec(true, isJulian());
            core.getRegression().add(easter);
        }
        return easter;
    }

    // should be changed in the future, with new moving holidays !!!
    public void setEnabled(boolean value) {
        if (!value) {
            core.getRegression().clearMovingHolidays();
        } else {
            inner();
        }
    }

    public EasterSpecUI(RegArimaSpecification spec, boolean ro) {
        super(spec, ro);
    }

    public RegressionTestSpec getTest() {
        MovingHolidaySpec spec = getInner();
        if (spec == null) {
            return RegressionTestSpec.None;
        } else {
            return spec.getTest();
        }
    }

    public void setTest(RegressionTestSpec value) {
        MovingHolidaySpec spec = inner();
        spec.setTest(value);
    }

    public int getDuration() {
        MovingHolidaySpec spec = getInner();
        if (spec == null) {
            return MovingHolidaySpec.DEF_EASTERDUR;
        } else {
            return spec.getW();
        }
    }

    public void setDuration(int value) {
        inner().setW(value);
    }

    public boolean isJulian() {
        MovingHolidaySpec spec = getInner();
        if (spec == null) {
            return false;
        } else {
            return spec.getType() == MovingHolidaySpec.Type.JulianEaster;
        }
    }

    public void setJulian(boolean value) {
        inner().setType(value ? MovingHolidaySpec.Type.JulianEaster : MovingHolidaySpec.Type.Easter);
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        // regression
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = enabledDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = julianDesc();
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
        return descs;
    }
    ///////////////////////////////////////////////////////////////////////////
    private static final int ENABLED_ID = 1, TEST_ID = 2, DUR_ID = 3, JULIAN_ID = 4;

    @Messages({
        "easterSpecUI.enabledDesc.name=Is enabled",
        "easterSpecUI.enabledDesc.desc="
    })
    private EnhancedPropertyDescriptor enabledDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("enabled", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ENABLED_ID);
            desc.setDisplayName(Bundle.easterSpecUI_enabledDesc_name());
            desc.setShortDescription(Bundle.easterSpecUI_enabledDesc_desc());
            edesc.setReadOnly(ro_);
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
        if (!isEnabled()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("duration", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DUR_ID);
            desc.setDisplayName(Bundle.easterSpecUI_durationDesc_name());
            desc.setShortDescription(Bundle.easterSpecUI_durationDesc_desc());
            edesc.setReadOnly(ro_ || getTest() == RegressionTestSpec.Add);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "easterSpecUI.julianDesc.desc=Use Julian Easter (expressed in Gregorian calendar)",
        "easterSpecUI.julianDesc.name=Julian",})
    private EnhancedPropertyDescriptor julianDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("julian", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, JULIAN_ID);
            desc.setDisplayName(Bundle.easterSpecUI_julianDesc_name());
            desc.setShortDescription(Bundle.easterSpecUI_julianDesc_desc());
            if (ro_ || !isEnabled()) {
                edesc.setReadOnly(true);
            }
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
            edesc.setReadOnly(ro_);
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
