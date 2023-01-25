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
import demetra.modelling.highfreq.TransformSpec;
import demetra.modelling.TransformationType;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle;

/**
 *
 * @author PALATEJ
 */
public abstract class AbstractTransformSpecUI implements IPropertyDescriptors{

    @Override
    public String toString() {
        return "";
    }

    protected abstract HighFreqSpecUI root();

    protected abstract TransformSpec spec();

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = logDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    private static final int LOG_ID = 2;

     public boolean isLog() {
        return spec().getFunction() == TransformationType.Log;
    }

    public void setLog(boolean log) {
        if (log != (spec().getFunction()== TransformationType.Log)) {
            root().update(spec().toBuilder()
                    .function(log ? TransformationType.Log : TransformationType.None)
                    .build());
        }
    }

    private EnhancedPropertyDescriptor logDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Log", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LOG_ID);
            desc.setDisplayName("log");
            desc.setShortDescription("log transformation");
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    @NbBundle.Messages("transformSpecUI.getDisplayName=Series")
    public String getDisplayName() {
        return Bundle.transformSpecUI_getDisplayName();
    }

}
