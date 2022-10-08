/*
 * Copyright 2022 National Bank of Belgium
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
package demetra.desktop.sts.descriptors;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class OutlierSpecUI extends BasePreprocessingSpecUI {

    /**
     *
     * @param spec
     */
    public OutlierSpecUI(PreprocessingSpecification spec) {
        super(spec);
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = enabledDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = aoDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = lsDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = tcDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = soDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @Override
    public String getDisplayName() {
        return "Outliers";
    }

    public boolean isEnabled() {
        return core.ao || core.ls || core.tc || core.so;
    }

    public void setEnabled(boolean value) {
        if (!value) {
            core.ao = false;
            core.ls = false;
            core.tc = false;
            core.so = false;
        } else if (!isEnabled()) {
            core.ao = true;
            core.ls = true;
            core.tc = true;
        }
    }

    public boolean getAO() {
        return core.ao;
    }

    public void setAO(boolean value) {
        core.ao = value;
    }

    public boolean getLS() {
        return core.ls;
    }

    public void setLS(boolean value) {
        core.ls = value;
    }

    public boolean getTC() {
        return core.tc;
    }

    public void setTC(boolean value) {
        core.tc = value;
    }

    public boolean getSO() {
        return core.so;
    }

    public void setSO(boolean value) {
        core.so = value;
    }
    private static final int ENABLED_ID = 0, AO_ID = 1, LS_ID = 2, TC_ID = 3, SO_ID = 4;

    private EnhancedPropertyDescriptor enabledDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("enabled", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ENABLED_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(ENABLED_NAME);
            desc.setShortDescription(ENABLED_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor aoDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("AO", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, AO_ID);
            desc.setDisplayName(AO_NAME);
            desc.setShortDescription(AO_DESC);
            edesc.setReadOnly(!isEnabled());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor lsDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("LS", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LS_ID);
            desc.setDisplayName(LS_NAME);
            desc.setShortDescription(LS_DESC);
            edesc.setReadOnly(!isEnabled());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor tcDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("TC", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TC_ID);
            desc.setDisplayName(TC_NAME);
            desc.setShortDescription(TC_DESC);
            edesc.setReadOnly(!isEnabled());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor soDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("SO", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SO_ID);
            desc.setDisplayName(SO_NAME);
            desc.setShortDescription(SO_DESC);
            edesc.setReadOnly(!isEnabled());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
    private static final String ENABLED_NAME = "Is enabled",
            AO_NAME = "Additive",
            LS_NAME = "Level shift",
            TC_NAME = "Transitory",
            SO_NAME = "Seasonal";
    private static final String ENABLED_DESC = "Is automatic outliers detection enabled",
            AO_DESC = "[ao] Additive outlier",
            LS_DESC = "[ls] Level shift",
            TC_DESC = "[tc] Transitory change",
            SO_DESC = "[so] Seasonal outlier";
}
