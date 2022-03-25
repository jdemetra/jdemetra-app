/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.highfreq.ui;

import demetra.desktop.descriptors.DateSelectorUI;
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.highfreq.EstimateSpec;
import demetra.highfreq.TransformSpec;
import demetra.modelling.TransformationType;
import demetra.timeseries.TimeSelector;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle;

/**
 *
 * @author PALATEJ
 */
public class TransformSpecUI extends BaseFractionalAirlineSpecUI {

    @Override
    public String toString() {
        return "";
    }

    private TransformSpec inner() {
        return core().getTransform();
    }

    public TransformSpecUI(FractionalAirlineSpecRoot root) {
        super(root);
    }

    public DateSelectorUI getSpan() {
        return new DateSelectorUI(inner().getSpan(), isRo(), selector -> updateSpan(selector));
    }

    public void updateSpan(TimeSelector span) {
        update(inner().toBuilder().span(span).build());
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = spanDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = logDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    private static final int SPAN_ID = 1, LOG_ID = 2;

    @NbBundle.Messages({
        "transformSpecUI.spanDesc.name=Model span",
        "transformSpecUI.spanDesc.desc=Span used for the estimation of the pre-processing model"
    })
    private EnhancedPropertyDescriptor spanDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("span", this.getClass(), "getSpan", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SPAN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(Bundle.transformSpecUI_spanDesc_desc());
            desc.setDisplayName(Bundle.transformSpecUI_spanDesc_name());
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public boolean isLog() {
        return inner().getFunction() == TransformationType.Log;
    }

    public void setLog(boolean log) {
        if (log != (inner().getFunction()== TransformationType.Log)) {
            update(inner().toBuilder()
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
            edesc.setReadOnly(isRo());
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
