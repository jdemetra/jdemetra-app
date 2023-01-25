/*
 * Copyright 2023 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.sa.descriptors.highfreq;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IPropertyDescriptors;
import demetra.modelling.highfreq.EasterSpec;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle;

/**
 *
 * @author PALATEJ
 */
public abstract class AbstractEasterSpecUI implements IPropertyDescriptors  {

    @Override
    public String toString() {
        return getOption() == EasterSpec.Type.Unused ? "" : "in use";
    }

    protected abstract HighFreqSpecUI root();
    
    protected abstract EasterSpec spec();
 
    public boolean isro(){
        return root().isRo() || spec().hasFixedCoefficient();
    }

    public EasterSpec.Type getOption() {
        return spec().getType();
    }

    public void setOption(EasterSpec.Type value) {
        root().update(spec().toBuilder().type(value).build());
    }

    public int getDuration() {
        return spec().getDuration();
    }

    public void setDuration(int value) {
        root().update(spec().toBuilder().duration(value).build());
    }

    public boolean isTest() {
        EasterSpec spec = spec();
        if (!spec.isUsed()) {
            return false;
        } else {
            return spec.isTest();
        }
    }

    public void setTest(boolean value) {
        root().update(spec().toBuilder().test(value).build());
    }

    public boolean isJulian() {
        EasterSpec spec = spec();
        if (!spec.isUsed()) {
            return false;
        } else {
            return spec.isJulian();
        }
    }

    public void setJulian(boolean value) {
        root().update(spec().toBuilder().julian(value).build());
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

    @NbBundle.Messages({
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
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "easterSpecUI.durationDesc.name=Duration",
        "easterSpecUI.durationDesc.desc=Duration"
    })
    private EnhancedPropertyDescriptor durationDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("duration", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DUR_ID);
            desc.setDisplayName(Bundle.easterSpecUI_durationDesc_name());
            desc.setShortDescription(Bundle.easterSpecUI_durationDesc_desc());
            if (root().isRo() || getOption() == EasterSpec.Type.Unused) {
                edesc.setReadOnly(true);
            }
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "easterSpecUI.testDesc.name=Test",
        "easterSpecUI.testDesc.desc=Test",
    })
    private EnhancedPropertyDescriptor testDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("test", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TEST_ID);
            desc.setDisplayName(Bundle.easterSpecUI_testDesc_name());
            desc.setShortDescription(Bundle.easterSpecUI_testDesc_desc());
            if (root().isRo() || getOption() == EasterSpec.Type.Unused) {
                edesc.setReadOnly(true);
            }
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "easterSpecUI.julianDesc.desc=Use Julian Easter (expressed in Gregorian calendar)",
        "easterSpecUI.julianDesc.name=Julian",
    })
    private EnhancedPropertyDescriptor julianDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("julian", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, JULIAN_ID);
            desc.setDisplayName(Bundle.easterSpecUI_julianDesc_name());
            desc.setShortDescription(Bundle.easterSpecUI_julianDesc_desc());
            if (root().isRo() || getOption() == EasterSpec.Type.Unused) {
                edesc.setReadOnly(true);
            }
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    @NbBundle.Messages("easterSpecUI.getDisplayName=Easter")
    public String getDisplayName() {
        return Bundle.easterSpecUI_getDisplayName();
    }
}
