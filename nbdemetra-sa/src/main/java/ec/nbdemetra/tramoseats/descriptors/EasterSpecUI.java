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

    public boolean isTest() {
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

    public boolean isJulian() {
        EasterSpec spec = inner();
        if (!spec.isUsed()) {
            return false;
        } else {
            return spec.isJulian();
        }
    }

    public void setJulian(boolean value) {
        inner().setJulian(value);
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
        return descs;
    }
    ///////////////////////////////////////////////////////////////////////////
    private static final int OPTION_ID = 1, DUR_ID = 2, TEST_ID = 3, JULIAN_ID=4;

    @Messages({
        "easterSpecUI.optionDesc.name=Option",
        "easterSpecUI.optionDesc.desc=Option"
    })
    private EnhancedPropertyDescriptor optionDesc() {
        if (isJulian())
            return null;
        try {
            PropertyDescriptor desc = new PropertyDescriptor("option", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, OPTION_ID);
            desc.setDisplayName(Bundle.easterSpecUI_optionDesc_name());
            desc.setShortDescription(Bundle.easterSpecUI_optionDesc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro_);
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
        try {
            PropertyDescriptor desc = new PropertyDescriptor("duration", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DUR_ID);
            desc.setDisplayName(Bundle.easterSpecUI_durationDesc_name());
            desc.setShortDescription(Bundle.easterSpecUI_durationDesc_desc());
            if (ro_ || getOption() == EasterSpec.Type.Unused) {
                edesc.setReadOnly(true);
            }
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "easterSpecUI.testDesc.name=Test",
        "easterSpecUI.testDesc.desc=Test",
    })
    private EnhancedPropertyDescriptor testDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("test", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TEST_ID);
            desc.setDisplayName(Bundle.easterSpecUI_testDesc_name());
            desc.setShortDescription(Bundle.easterSpecUI_testDesc_desc());
            if (ro_ || getOption() == EasterSpec.Type.Unused) {
                edesc.setReadOnly(true);
            }
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "easterSpecUI.julianDesc.name=Use Julian Easter (expressed in Gregorian calendar)",
        "easterSpecUI.julianDesc.desc=Julian",
    })
    private EnhancedPropertyDescriptor julianDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("julian", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, JULIAN_ID);
            desc.setDisplayName(Bundle.easterSpecUI_julianDesc_name());
            desc.setShortDescription(Bundle.easterSpecUI_julianDesc_desc());
            if (ro_ || getOption() == EasterSpec.Type.Unused) {
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
