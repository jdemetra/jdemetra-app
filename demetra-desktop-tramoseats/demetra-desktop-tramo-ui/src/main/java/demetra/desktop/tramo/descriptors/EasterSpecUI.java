/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramo.descriptors;

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

    EasterSpecUI(TramoSpecRoot root) {
        super(root);
    }
    
    public boolean isro(){
        return super.isRo() || inner().hasFixedCoefficient();
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
            edesc.setReadOnly(isRo());
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
            if (isRo() || getOption() == EasterSpec.Type.Unused) {
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
            if (isRo() || getOption() == EasterSpec.Type.Unused) {
                edesc.setReadOnly(true);
            }
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "easterSpecUI.julianDesc.desc=Use Julian Easter (expressed in Gregorian calendar)",
        "easterSpecUI.julianDesc.name=Julian",
    })
    private EnhancedPropertyDescriptor julianDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("julian", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, JULIAN_ID);
            desc.setDisplayName(Bundle.easterSpecUI_julianDesc_name());
            desc.setShortDescription(Bundle.easterSpecUI_julianDesc_desc());
            if (isRo() || getOption() == EasterSpec.Type.Unused) {
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
