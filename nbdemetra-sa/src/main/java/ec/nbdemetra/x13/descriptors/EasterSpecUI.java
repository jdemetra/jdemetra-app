/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13.descriptors;

import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.modelling.RegressionTestSpec;
import ec.tstoolkit.modelling.arima.x13.MovingHolidaySpec;
import ec.tstoolkit.modelling.arima.x13.RegArimaSpecification;
import ec.tstoolkit.modelling.arima.x13.RegressionSpec;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

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
            easter = MovingHolidaySpec.easterSpec(true);
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

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        // regression
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = enabledDesc();
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
    private static final int ENABLED_ID = 1, TEST_ID = 2, DUR_ID = 3;

    private EnhancedPropertyDescriptor enabledDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("enabled", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ENABLED_ID);
            desc.setDisplayName(ENABLED_NAME);
            desc.setShortDescription(ENABLED_DESC);
            edesc.setReadOnly(ro_);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor durationDesc() {
        if (!isEnabled()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("duration", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DUR_ID);
            desc.setDisplayName(DURATION_NAME);
            desc.setShortDescription(DURATION_DESC);
            edesc.setReadOnly(ro_ || getTest() == RegressionTestSpec.Add);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor testDesc() {
        if (!isEnabled()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("test", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TEST_ID);
            desc.setDisplayName(TEST_NAME);
            desc.setShortDescription(TEST_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    public String getDisplayName() {
        return "Easter";
    }
    public static final String ENABLED_NAME = "Is enabled",
            TEST_NAME = "Pre-test",
            DURATION_NAME = "Easter duration";
    public static final String ENABLED_DESC = "",
            TEST_DESC = "",
            DURATION_DESC = "[w] Length of the easter regression effect";
}
