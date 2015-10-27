/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.advanced.descriptors.mixedfrequencies;

import ec.nbdemetra.ui.properties.l2fprod.UserInterfaceContext;
import ec.tstoolkit.arima.special.mixedfrequencies.EstimateSpec;
import ec.tstoolkit.arima.special.mixedfrequencies.MixedFrequenciesSpecification;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.timeseries.DataType;
import ec.ui.descriptors.TsPeriodSelectorUI;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class BasicSpecUI extends BaseSpecUI {

    BasicSpecUI(MixedFrequenciesSpecification spec) {
        super(spec);
    }

    public TsPeriodSelectorUI getSpan() {
        return new TsPeriodSelectorUI(core.getBasic().getSpan(), UserInterfaceContext.INSTANCE.getDomain(), false);
    }

    public boolean isLog() {
        return core.getBasic().isLog();
    }

    public void setLog(boolean log) {
        core.getBasic().setLog(log);
    }

    public DataType getDataType() {
        return core.getBasic().getDataType();
    }

    public void setDataType(DataType type) {
        core.getBasic().setDataType(type);
    }

    private static final int SPAN_ID = 0, DATATYPE_ID = 1, LOG_ID = 2;

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = spanDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = dtDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = logDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @Override
    public String getDisplayName() {
        return "Basic";
    }
    ///////////////////////////////////////////////////////////////////////////

    private EnhancedPropertyDescriptor spanDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("span", this.getClass(), "getSpan", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SPAN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(SPAN_DESC);
            desc.setDisplayName(SPAN_NAME);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor logDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("log", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LOG_ID);
            desc.setDisplayName(LOG_NAME);
            desc.setShortDescription(LOG_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor dtDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("dataType", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DATATYPE_ID);
            desc.setDisplayName(DATATYPE_NAME);
            desc.setShortDescription(DATATYPE_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private static final String SPAN_NAME = "Series span", DATATYPE_NAME = "Data type", LOG_NAME = "Log";
    private static final String SPAN_DESC = "Time span used for the processing",
            DATATYPE_DESC = "Type of the series", LOG_DESC = "Log transformation of the series";
}
