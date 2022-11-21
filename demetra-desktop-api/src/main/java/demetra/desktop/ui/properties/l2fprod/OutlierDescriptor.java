/*
 * Copyright 2014 National Bank of Belgium
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
package demetra.desktop.ui.properties.l2fprod;

import demetra.data.Parameter;
import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.ui.properties.l2fprod.OutlierDefinition.OutlierType;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mats Maggi
 */
public class OutlierDescriptor implements IObjectDescriptor<OutlierDefinition> {
    
    private static final int POSITION_ID = 1, TYPE_ID = 2, FIXEDPARAMETER_ID = 3, PARAMETER_ID = 4;
    private OutlierDefinition core;
    
    public OutlierDescriptor() {
        core=new OutlierDefinition(LocalDate.now(),OutlierType.AO, Parameter.undefined());
    }
    
    public OutlierDescriptor(OutlierDefinition outlier) {
        core=outlier;
    }
    
    public OutlierDescriptor duplicate(){
        return new OutlierDescriptor(core);
    }
    
    @Override
    public OutlierDefinition getCore() {
        return core;
    }
    
    public LocalDate getPosition() {
        return core.getPosition();
    }
    
    public void setPosition(LocalDate position) {
        core=core.withPosition(position);
    }
    
    public OutlierType getType() {
        return core.getType();
    }
    
    public void setType(OutlierType type) {
        core=core.withType(type);
    }
    
    public double getParameter() {
        return core.getParameter().isDefined() ? core.getParameter().getValue() : null;
    }
    
    public void setParameter(double p) {
        core=core.withParameter(p == 0 ? Parameter.undefined() : Parameter.fixed(p));
    }
    
    public boolean isFixedParameter() {
        return core.getParameter().isFixed();
    }
    
    public void setFixedParameter(boolean f) {
        Parameter p = core.getParameter();
        if (f && !p.isFixed()) {
            core=core.withParameter(Parameter.fixed(p.getValue()));
        } else if (p.isFixed()) {
            if (p.getValue() == 0) {
                core=core.withParameter(Parameter.undefined());
            } else {
                core=core.withParameter(Parameter.initial(p.getValue()));
            }
        }
    }
    
    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = positionDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = typeDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = fixedParameterDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = parameterDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    
    @Override
    public String getDisplayName() {
        return "Outlier";
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
    
    private EnhancedPropertyDescriptor typeDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("type", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TYPE_ID);
            desc.setDisplayName("Outlier Type");
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
    
    private EnhancedPropertyDescriptor fixedParameterDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("fixedParameter", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, FIXEDPARAMETER_ID);
            desc.setDisplayName("Fixed Parameter");
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
    
    private EnhancedPropertyDescriptor parameterDesc() {
        if (!core.getParameter().isFixed()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("parameter", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, PARAMETER_ID);
            desc.setDisplayName("Parameter value");
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
    
    @Override
    public String toString() {
        return getCore().toString();
    }
}
