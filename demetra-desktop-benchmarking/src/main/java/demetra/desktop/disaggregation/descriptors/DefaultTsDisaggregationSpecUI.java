/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.disaggregation.descriptors;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean
 */
public class DefaultTsDisaggregationSpecUI extends BaseTsDisaggregationSpecUI implements IObjectDescriptor<DisaggregationSpecification> {

    static {
        CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(DisaggregationSpecification.Model.class);
        CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(TsDisaggregation.SsfOption.class);
    }
    public static final String DISPLAYNAME = "Disaggregation";
    public static final String BASIC_NAME = "Basic options", ADVANCED_NAME = "Advanced options";
    public static final String BASIC_DESC = "Basic options", ADVANCED_DESC = "Advanced options";
    public static final int BASIC_ID = 0, ADVANCED_ID = 10;

    public DefaultTsDisaggregationSpecUI(DisaggregationSpecification spec, boolean ro) {
        super(spec, null, ro);
    }

    public DefaultTsDisaggregationSpecUI(DisaggregationSpecification spec, TsDomain domain, boolean ro) {
        super(spec, domain, ro);
    }

    public BasicSpecUI getBasic() {
        return new BasicSpecUI(core, domain_, ro_);
    }

    public AdvancedSpecUI getAdvanced() {
        return new AdvancedSpecUI(core, domain_, ro_);
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
            edesc.setReadOnly(ro_);
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
            edesc.setReadOnly(ro_);
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
