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
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IPropertyDescriptors;
import demetra.information.InformationSet;
import demetra.timeseries.TsDomain;
import demetra.timeseries.TsPeriod;
import demetra.timeseries.TsUnit;
import demetra.timeseries.regression.IOutlier;
import demetra.timeseries.regression.Variable;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mats Maggi
 */
public class OutlierDescriptor implements IPropertyDescriptors {

    public static enum OutlierType {
        AO, LS, TC, SO;
    }

    private static final int NAME_ID = 0, POSITION_ID = 1, TYPE_ID = 2, FIXEDPARAMETER_ID = 3, PARAMETER_ID = 4;
    private LocalDate position;
    private OutlierType type;
    private Parameter parameter;
    private String name;
    private boolean autoName;

    public OutlierDescriptor() {
        position = LocalDate.now();
        type = OutlierType.AO;
        parameter = Parameter.undefined();
        autoName = true;
        name = name();
    }

    public OutlierDescriptor(LocalDate pos, OutlierType type) {
        this.position = pos;
        this.type = type;
        parameter = Parameter.undefined();
        autoName = true;
        name = name();
    }

    public OutlierDescriptor(LocalDate pos, OutlierType type, Parameter parameter, String name) {
        this.position = pos;
        this.type = type;
        this.parameter = parameter;
        autoName = true;
        if (name != null && !name.isBlank()) {
            this.name = name;
            autoName = false;
        } else {
            autoName = true;
            this.name = name();
        }
    }

    public <O extends IOutlier> OutlierDescriptor(Variable<O> var) {
        O core = var.getCore();
        position = core.getPosition().toLocalDate();
        type = OutlierType.valueOf(core.getCode());
        parameter = var.getCoefficient(0);
        autoName = false;
        name = var.getName();
        if (name == null || name.isBlank())
            name=name();
    }

    public OutlierDescriptor(OutlierDescriptor desc) {
        position = desc.position;
        type = desc.type;
        parameter = desc.parameter;
        name = desc.name;
        autoName = desc.autoName;
    }

    public OutlierDescriptor duplicate() {
        return new OutlierDescriptor(this);
    }

    private String name() {
        TsDomain domain = UserInterfaceContext.INSTANCE.getDomain();
        return toString(domain == null ? 0 : domain.getAnnualFrequency());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!name.equals(this.name)) {
            this.name = name;
            this.autoName = name.isBlank();
        }
    }

    public void setName(String name, boolean auto) {
        this.name = name;
        this.autoName = auto;
    }

    public LocalDate getPosition() {
        return position;
    }

    public void setPosition(LocalDate position) {
        this.position = position;
        if (autoName) {
            name = name();
        }
    }

    public OutlierType getType() {
        return type;
    }

    public void setType(OutlierType type) {
        this.type = type;
        if (autoName) {
            name = name();
        }
    }

    public double getParameter() {
        return parameter.isDefined() ? parameter.getValue() : null;
    }

    public void setParameter(double p) {
        parameter = p == 0 ? Parameter.undefined() : Parameter.fixed(p);
    }

    public boolean isFixedParameter() {
        return parameter.isFixed();
    }

    public void setFixedParameter(boolean f) {
        if (f && !parameter.isFixed()) {
            parameter = Parameter.fixed(parameter.getValue());
        } else if (parameter.isFixed()) {
            if (parameter.getValue() == 0) {
                parameter = Parameter.undefined();
            } else {
                parameter = Parameter.initial(parameter.getValue());
            }
        }
    }

    public Parameter getCoefficient() {
        return parameter;
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc;
        desc = nameDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = positionDesc();
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

    EnhancedPropertyDescriptor nameDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("name", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, NAME_ID);
            desc.setDisplayName("Name");
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
        if (!parameter.isFixed()) {
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
        return getName();
    }

    public String toString(int freq) {
        if (!autoName) {
            return name;
        }
        if (freq == 0) {
            return toString();
        } else {
            StringBuilder builder = new StringBuilder();
            TsUnit unit = TsUnit.ofAnnualFrequency(freq);
            builder.append(type).append(InformationSet.SEP).append(TsPeriod.of(unit, position).display());
            if (parameter.isFixed()) {
                builder.append(" [").append(parameter.getValue()).append(']');
            }
            return builder.toString();
        }
    }

}
