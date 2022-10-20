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

import demetra.data.Parameter;
import demetra.desktop.benchmarking.descriptors.Utility;
import demetra.desktop.descriptors.DateSelectorUI;
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.tempdisagg.univariate.TemporalDisaggregationSpec;
import demetra.timeseries.TimeSelector;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class BasicSpecUI extends BaseTemporalDisaggregationSpecUI {

    public static final String DISPLAYNAME = "Basic";
    public static final String ERROR_NAME = "Error", PARAM_NAME = "Parameter", CONSTANT_NAME = "Constant", TREND_NAME = "Trend", TYPE_NAME = "Type", SPAN_NAME = "Estimation span", DEFFREQ_NAME = "Default frequency";
    public static final String ERROR_DESC = "Model of the regression error", PARAM_DESC = "Parameter", CONSTANT_DESC = "Constant", TREND_DESC = "Trend", TYPE_DESC = "Type", SPAN_DESC = "Estimation span", DEFFREQ_DESC = "Default frequency";
    public static final int SPAN_ID = 0, ERROR_ID = 5, PARAM_ID = 10, CONSTANT_ID = 15, TREND_ID = 20, TYPE_ID = 30, DEFFREQ_ID = 40;

    @Override
    public String toString() {
        return "";
    }

    public BasicSpecUI(TemporalDisaggregationSpecRoot root) {
        super(root);
    }

    public TemporalDisaggregationSpec.Model getErrorModel() {
        return core().getResidualsModel();
    }

    public void setErrorModel(TemporalDisaggregationSpec.Model model) {
        TemporalDisaggregationSpec.Builder builder = core().toBuilder().residualsModel(model);
        if (model.getDifferencingOrder() == 1 && !core().isZeroInitialization()) {
            builder.constant(false);
        }
        if (model.getDifferencingOrder() > 1) {
            builder.zeroInitialization(false)
                    .constant(false)
                    .trend(false);
        }
        update(builder.build());
    }

    public Parameter[] getParameter() {
        return new Parameter[]{core().getParameter()};
    }

    public void setParameter(Parameter[] p) {
        update(core().toBuilder()
                .parameter(p[0])
                .build());
    }

    public boolean isConstant() {
        return core().isConstant();
    }

    public void setConstant(boolean cnt) {
        update(core().toBuilder()
                .constant(cnt)
                .build());
    }

    public boolean isTrend() {
        return core().isTrend();
    }

    public void setTrend(boolean t) {
        update(core().toBuilder()
                .trend(t)
                .build());
    }

    public Utility.AggregationType getType() {
        return Utility.convert(core().getAggregationType());
    }

    public void setType(Utility.AggregationType type) {
        update(core().toBuilder()
                .aggregationType(Utility.convert(type))
                .build());
    }

    public int getFrequency() {
        return core().getDefaultPeriod();
    }

    public void setFrequency(int freq) {
        update(core().toBuilder().defaultPeriod(freq).build());
    }

    public DateSelectorUI getSpan() {
        return new DateSelectorUI(core().getEstimationSpan(), isRo(), span->updateSpan(span));
    }

    public void updateSpan(TimeSelector span){
         update(core().toBuilder().estimationSpan(span).build());
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
            edesc.setReadOnly(isRo());
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
            edesc.setReadOnly(isRo());
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
            edesc.setReadOnly(isRo() || (core().getResidualsModel().getDifferencingOrder() == 1 && !core().isZeroInitialization())
                    || core().getResidualsModel().getDifferencingOrder() > 1);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor parameterDesc() {
        if (!core().getResidualsModel().hasParameter()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Parameter", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, PARAM_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(PARAM_NAME);
            desc.setShortDescription(PARAM_DESC);
            edesc.setReadOnly(isRo());
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
            edesc.setReadOnly(isRo() || core().getResidualsModel().getDifferencingOrder() > 1);
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
            edesc.setReadOnly(isRo());
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
            edesc.setReadOnly(isRo());
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
