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
public class PreprocessingSpecUI extends BasePreprocessingSpecUI {

    public PreprocessingSpecUI(PreprocessingSpecification spec) {
        super(spec);
    }

    public Method getMethod() {
        return core.method;
    }

    public void setMethod(Method value) {
        core.method = value;
    }

    public DefaultTransformationType getFunction() {
        return core.transform;
    }

    public void setFunction(DefaultTransformationType value) {
        core.transform = value;
    }

    public OutlierSpecUI getOutliers() {
        return new OutlierSpecUI(core);
    }

    public CalendarSpecUI getCalendar() {
        return new CalendarSpecUI(core);
    }

    public String getDisplayName() {
        return "Preprocessing";
    }

    private EnhancedPropertyDescriptor mDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("method", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, M_ID);
            desc.setDisplayName(M_NAME);
            desc.setShortDescription(M_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor oDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("outliers", this.getClass(), "getOutliers", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, O_ID);
            desc.setDisplayName(O_NAME);
            desc.setShortDescription(O_DESC);
            edesc.setReadOnly(core.method == Method.None);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor cDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("calendar", this.getClass(), "getCalendar", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, C_ID);
            desc.setDisplayName(C_NAME);
            desc.setShortDescription(C_DESC);
            edesc.setReadOnly(core.method == Method.None);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor tDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("transform", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, T_ID);
            desc.setDisplayName(T_NAME);
            desc.setShortDescription(T_DESC);
            edesc.setReadOnly(core.method == Method.None);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = mDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = tDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = cDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = oDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    public static final int M_ID = 0, T_ID = 1, C_ID = 2, O_ID = 3;
    public static final String M_NAME = "Method",
            T_NAME = "Transformation",
            C_NAME = "Calendar",
            O_NAME = "Outliers";
    public static final String M_DESC = "Pre-processing method",
            T_DESC = "Transformation of the series",
            C_DESC = "Calendar effects",
            O_DESC = "Outliers detection";
}
