/*
 * Copyright 2013-2014 National Bank of Belgium
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
package ec.nbdemetra.sa.advanced.descriptors.mixedfrequencies;

import ec.nbdemetra.ui.properties.l2fprod.CustomPropertyEditorRegistry;
import ec.tstoolkit.arima.special.mixedfrequencies.EstimateSpec;
import ec.tstoolkit.arima.special.mixedfrequencies.MixedFrequenciesSpecification;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IObjectDescriptor;
import ec.tstoolkit.timeseries.DataType;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class MixedFrequenciesArimaSpecUI implements IObjectDescriptor<MixedFrequenciesSpecification> {

    static {
        CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(EstimateSpec.Method.class);
        CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(DataType.class);
    }
    final MixedFrequenciesSpecification core;

    public MixedFrequenciesArimaSpecUI(MixedFrequenciesSpecification spec) {
        core = spec;
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor ed = basicDesc();
        if (ed != null) {
            descs.add(ed);
        }
        ed = calendarDesc();
        if (ed != null) {
            descs.add(ed);
        }
        ed = arimaDesc();
        if (ed != null) {
            descs.add(ed);
        }
        ed = estimateDesc();
        if (ed != null) {
            descs.add(ed);
        }
        return descs;
    }

    @Override
    public String getDisplayName() {
        return "Mixed frequencies Arima model";
    }

    public BasicSpecUI getBasic() {
        return new BasicSpecUI(core);
    }

    public CalendarSpecUI getCalendar() {
        return new CalendarSpecUI(core);
    }

    public ArimaSpecUI getArima() {
        return new ArimaSpecUI(core);
    }

    public EstimateSpecUI getEstimate() {
        return new EstimateSpecUI(core);
    }

    private static final int BASIC_ID = 0, ARIMA_ID = 4, CALENDAR_ID = 3, ESTIMATE_ID = 4;

    private EnhancedPropertyDescriptor basicDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("basic", this.getClass(), "getBasic", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BASIC_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(BASIC_NAME);
            desc.setShortDescription(BASIC_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor calendarDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("calendar", this.getClass(), "getCalendar", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, CALENDAR_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(CALENDAR_NAME);
            desc.setShortDescription(CALENDAR_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor arimaDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("arima", this.getClass(), "getArima", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ARIMA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(ARIMA_NAME);
            desc.setShortDescription(ARIMA_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor estimateDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("estimate", this.getClass(), "getEstimate", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ESTIMATE_ID);
            desc.setDisplayName(ESTIMATE_NAME);
            desc.setShortDescription(ESTIMATE_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private static final String BASIC_NAME = "BASIC", CALENDAR_NAME = "CALENDAR",
            ARIMA_NAME = "ARIMA", ESTIMATE_NAME = "ESTIMATE";
    private static final String BASIC_DESC = "Basic options",
            CALENDAR_DESC = "Calendar effects (trading days + Easter)",
            ARIMA_DESC = "Arima model (high-frequency)",
            ESTIMATE_DESC = "Estimation procedure";

    @Override
    public MixedFrequenciesSpecification getCore() {
        return core;
    }
}
