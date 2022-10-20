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
package demetra.desktop.disaggregation.descriptors;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.tempdisagg.univariate.TemporalDisaggregationSpec;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean
 */
public class TemporalDisaggregationSpecUI implements IObjectDescriptor<TemporalDisaggregationSpec> {

    public static final String DISPLAYNAME = "Disaggregation";
    public static final String BASIC_NAME = "Basic options", ADVANCED_NAME = "Advanced options";
    public static final String BASIC_DESC = "Basic options", ADVANCED_DESC = "Advanced options";
    public static final int BASIC_ID = 0, ADVANCED_ID = 10;

    private final TemporalDisaggregationSpecRoot root;
    
    @Override
    public TemporalDisaggregationSpec getCore(){
        return root.getCore();
    }

    public TemporalDisaggregationSpecUI(TemporalDisaggregationSpec spec, boolean ro) {
        root=new TemporalDisaggregationSpecRoot(spec, ro);
    }

    public TemporalDisaggregationSpecUI(TemporalDisaggregationSpecRoot root) {
        this.root=root;
    }


    public BasicSpecUI getBasic() {
        return new BasicSpecUI(root);
    }

    public AdvancedSpecUI getAdvanced() {
        return new AdvancedSpecUI(root);
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> props = new ArrayList<>();
        EnhancedPropertyDescriptor desc = basicDesc();
        if (desc != null) {
            props.add(desc);
        }
        desc = advancedDesc();
        if (desc != null) {
            props.add(desc);
        }
        return props;
    }

    private EnhancedPropertyDescriptor basicDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Basic", this.getClass(), "getBasic", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BASIC_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(BASIC_NAME);
            desc.setShortDescription(BASIC_DESC);
            edesc.setReadOnly(root.isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor advancedDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Advanced", this.getClass(), "getAdvanced", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ADVANCED_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(ADVANCED_NAME);
            desc.setShortDescription(ADVANCED_DESC);
            edesc.setReadOnly(root.isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    public String getDisplayName() {
        return DISPLAYNAME;
    }
}
