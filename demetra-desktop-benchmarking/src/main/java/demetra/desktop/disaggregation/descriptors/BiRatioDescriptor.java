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
import demetra.desktop.descriptors.IPropertyDescriptors;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author palatej
 */
@lombok.Data
@lombok.AllArgsConstructor
public class BiRatioDescriptor implements IPropertyDescriptors{

    private static final int POSITION_ID = 1, VALUE_ID = 2;

    LocalDate position;
    double value;
    
    public BiRatioDescriptor() {
        position = LocalDate.now();
        value = 1;
    }

    public BiRatioDescriptor duplicate() {
        return new BiRatioDescriptor(this);
    }

    public BiRatioDescriptor(BiRatioDescriptor desc) {
        position = desc.position;
        value = desc.value;
    }

        public LocalDate getPosition() {
        return position;
    }
    
    public void setPosition(LocalDate position) {
        this.position = position;
    }
    
    public double getValue() {
        return value;
    }
    
    public void setValue(double val) {
        this.value = val;
    }
    
    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc;
        desc = positionDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = valueDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    
    @Override
    public String getDisplayName() {
        return "Bi-Ratio";
    }
    
    EnhancedPropertyDescriptor valueDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("value", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, VALUE_ID);
            desc.setDisplayName("Value");
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
    
    private EnhancedPropertyDescriptor positionDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("position", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, POSITION_ID);
            desc.setDisplayName("Position");
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

}
