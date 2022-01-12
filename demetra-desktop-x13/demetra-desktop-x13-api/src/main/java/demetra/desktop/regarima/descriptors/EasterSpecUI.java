/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.regarima.descriptors;

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
        return isEnabled()  ? "in use" : "";
    }

    private EasterSpec inner() {
        EasterSpec easter = core().getRegression().getEaster();
        return easter;
    }
    
    private boolean isEnabled(){
        return inner().getType() != EasterSpec.Type.Unused;
    }

    public EasterSpecUI(RegArimaSpecRoot root) {
        super(root);
    }
    
    @Override
    public boolean isRo(){
        return super.isRo() || inner().hasFixedCoefficient();
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
        return descs;
    }
    ///////////////////////////////////////////////////////////////////////////
    private static final int TYPE_ID = 1, AUTO_ID = 2, TEST_ID = 3, DUR_ID = 4;

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
            edesc.setReadOnly(isRo());
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
        if (! isEnabled() || inner().isAutomatic()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("duration", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DUR_ID);
            desc.setDisplayName(Bundle.easterSpecUI_durationDesc_name());
            desc.setShortDescription(Bundle.easterSpecUI_durationDesc_desc());
            edesc.setReadOnly(isRo() || getTest() == RegressionTestSpec.Add);
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
            edesc.setReadOnly(isRo());
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
