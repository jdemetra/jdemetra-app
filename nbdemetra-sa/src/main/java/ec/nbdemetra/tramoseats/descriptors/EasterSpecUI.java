/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats.descriptors;

import ec.tstoolkit.modelling.arima.tramo.EasterSpec;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pcuser
 */
public class EasterSpecUI extends BaseTramoSpecUI {

    @Override
    public String toString() {
        return getOption() == EasterSpec.Type.Unused ? "" : "in use";
    }

    private EasterSpec inner() {
        EasterSpec easter = core.getRegression().getCalendar().getEaster();
        return easter;
    }

    private void disable() {
        core.getRegression().getCalendar().getEaster().setOption(EasterSpec.Type.Unused);
    }

    public EasterSpecUI(TramoSpecification spec, boolean ro) {
        super(spec, ro);
    }

    public EasterSpec.Type getOption() {
        return inner().getOption();
    }

    public void setOption(EasterSpec.Type value) {
        inner().setOption(value);
    }

    public int getDuration() {
        return inner().getDuration();
    }

    public void setDuration(int value) {
        inner().setDuration(value);
    }

    public boolean getTest() {
        EasterSpec spec = inner();
        if (!spec.isUsed()) {
            return false;
        } else {
            return spec.isTest();
        }
    }

    public void setTest(boolean value) {
        inner().setTest(value);
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        // regression
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = optionDesc();
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
        return descs;
    }
    ///////////////////////////////////////////////////////////////////////////
    private static final int OPTION_ID = 1, DUR_ID = 2, TEST_ID = 3;

    private EnhancedPropertyDescriptor optionDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("option", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, OPTION_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor durationDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("duration", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DUR_ID);
            if (ro_ || getOption() == EasterSpec.Type.Unused) {
                edesc.setReadOnly(true);
            }
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor testDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("test", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TEST_ID);
            if (ro_ || getOption() == EasterSpec.Type.Unused) {
                edesc.setReadOnly(true);
            }
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    public String getDisplayName() {
        return "Easter";
    }
}
