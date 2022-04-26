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
package demetra.desktop.stl.ui;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IPropertyDescriptors;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.openide.util.NbBundle;

/**
 *
 * @author PALATEJ
 */
public class PeriodUI implements IPropertyDescriptors {

    @Override
    public String toString() {
        return "";
    }

    private int period;
    private final Consumer<Integer> callback;
    private final boolean ro;

    public PeriodUI(int period, boolean ro, Consumer<Integer> callback) {
        this.period=period;
        this.ro = ro;
        this.callback = callback;
    }

    public int getPeriod() {
        return period;
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        // regression
        EnhancedPropertyDescriptor desc = pDesc();
        return Collections.singletonList(desc);
    }
    public void setPeriod(int period) {
        if (period <= 1) {
            throw new IllegalArgumentException("Should be gt 1");
        }
        this.period=period;
        callback.accept(period);
    }


    @NbBundle.Messages({
        "periodUI.pDesc.name=period",
        "periodUI.pDesc.desc=Period."
    })
    private EnhancedPropertyDescriptor pDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Period", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, 1);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.periodUI_pDesc_name());
            desc.setShortDescription(Bundle.periodUI_pDesc_desc());
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

 
    @Override
    @NbBundle.Messages("periodUI.getDisplayName=period")
    public String getDisplayName() {
        return Bundle.periodUI_getDisplayName();
    }

}

