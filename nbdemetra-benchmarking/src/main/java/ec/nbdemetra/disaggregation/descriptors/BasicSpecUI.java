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
package ec.nbdemetra.disaggregation.descriptors;

import ec.tss.disaggregation.documents.DisaggregationSpecification;
import ec.tstoolkit.Parameter;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IObjectDescriptor;
import ec.tstoolkit.timeseries.TsAggregationType;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.ui.descriptors.TsPeriodSelectorUI;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class BasicSpecUI extends BaseTsDisaggregationSpecUI implements IObjectDescriptor<DisaggregationSpecification> {

    public static enum AggregationType {

        Sum, Average, Last, First
    }

    public static AggregationType convert(TsAggregationType a) {
        switch (a) {
            case Average:
                return AggregationType.Average;
            case Last:
                return AggregationType.Last;
            case First:
                return AggregationType.First;
            default:
                return AggregationType.Sum;
        }
    }
    
    public static TsAggregationType convert(AggregationType a) {
        switch (a) {
            case Average:
                return TsAggregationType.Average;
            case Last:
                return TsAggregationType.Last;
            case First:
                return TsAggregationType.First;
            default:
                return TsAggregationType.Sum;
        }
    }
    
    public static final String DISPLAYNAME = "Basic";
    public static final String ERROR_NAME = "Error", PARAM_NAME = "Parameter", CONSTANT_NAME = "Constant", TREND_NAME = "Trend", TYPE_NAME = "Type", SPAN_NAME="Estimation span", DEFFREQ_NAME="Default frequency";
    public static final String ERROR_DESC = "Model of the regression error", PARAM_DESC = "Parameter", CONSTANT_DESC = "Constant", TREND_DESC = "Trend", TYPE_DESC = "Type", SPAN_DESC="Estimation span", DEFFREQ_DESC="Default frequency";
    public static final int SPAN_ID=0, ERROR_ID = 5, PARAM_ID = 10, CONSTANT_ID = 15, TREND_ID = 20, TYPE_ID = 30, DEFFREQ_ID=40;
 
    public BasicSpecUI(DisaggregationSpecification spec, TsDomain domain, boolean ro) {
        super(spec, domain, ro);
    }

    public DisaggregationSpecification.Model getErrorModel() {
        return core.getModel();
    }

    public void setErrorModel(DisaggregationSpecification.Model model) {
        core.setModel(model);
        if (! model.isStationary() && ! core.isZeroInitialization())
            core.setConstant(false);
    }

    public Parameter[] getParameter() {
        return new Parameter[]{core.getParameter()};
    }

    public void setParameter(Parameter[] p) {
        core.setParameter(p[0]);
    }

    public boolean isConstant() {
        return core.isConstant();
    }

    public void setConstant(boolean cnt) {
        core.setConstant(cnt);
    }

    public boolean isTrend() {
        return core.isTrend();
    }

    public void setTrend(boolean t) {
        core.setTrend(t);
    }

    public AggregationType getType() {
        return convert(core.getType());
    }

    public void setType(AggregationType type) {
        core.setType(convert(type));
    }

    public TsFrequency getFrequency() {
        return core.getDefaultFrequency();
    }

    public void setFrequency(TsFrequency freq) {
        core.setDefaultFrequency(freq);
    }
        public TsPeriodSelectorUI getSpan() {
        return new TsPeriodSelectorUI(core.getSpan(), domain_, ro_);
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> props = new ArrayList<>();
        EnhancedPropertyDescriptor desc = spanDesc();
        if (desc != null) {
            props.add(desc);
        }
        desc = errorDesc();
        if (desc != null) {
            props.add(desc);
        }
        desc = parameterDesc();
        if (desc != null) {
            props.add(desc);
        }
        desc = cntDesc();
        if (desc != null) {
            props.add(desc);
        }
        desc = trendDesc();
        if (desc != null) {
            props.add(desc);
        }
        desc = typeDesc();
        if (desc != null) {
            props.add(desc);
        }
        desc = freqDesc();
        if (desc != null) {
            props.add(desc);
        }
        return props;
    }

        private EnhancedPropertyDescriptor spanDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("span", this.getClass(), "getSpan", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SPAN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(SPAN_DESC);
            desc.setDisplayName(SPAN_NAME);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor errorDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("ErrorModel", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ERROR_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(ERROR_NAME);
            desc.setShortDescription(ERROR_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor cntDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Constant", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, CONSTANT_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(CONSTANT_NAME);
            desc.setShortDescription(CONSTANT_DESC);
            edesc.setReadOnly(ro_|| (!core.getModel().isStationary() && !core.isZeroInitialization()) );
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor parameterDesc() {
        if (!core.getModel().hasParameter()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Parameter", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, PARAM_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(PARAM_NAME);
            desc.setShortDescription(PARAM_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor trendDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Trend", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TREND_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(TREND_NAME);
            desc.setShortDescription(TREND_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor typeDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Type", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TYPE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(TYPE_NAME);
            desc.setShortDescription(TYPE_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor freqDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Frequency", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DEFFREQ_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(DEFFREQ_NAME);
            desc.setShortDescription(DEFFREQ_DESC);
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
