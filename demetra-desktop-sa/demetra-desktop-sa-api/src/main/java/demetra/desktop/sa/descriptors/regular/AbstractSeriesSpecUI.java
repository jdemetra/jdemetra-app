/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.descriptors.regular;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.DateSelectorUI;
import demetra.desktop.descriptors.IPropertyDescriptors;
import demetra.desktop.ui.properties.l2fprod.UserInterfaceContext;
import demetra.modelling.regular.SeriesSpec;
import demetra.timeseries.TimeSelector;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Jean Palate
 */
public abstract class AbstractSeriesSpecUI implements IPropertyDescriptors {
    
    protected abstract RegularSpecUI root();

    protected abstract SeriesSpec spec();
    


    public DateSelectorUI getSpan() {
        return new DateSelectorUI(spec().getSpan(), UserInterfaceContext.INSTANCE.getDomain(), root().isRo(), selector->updateSpan(selector));
    }
    
    public void updateSpan(TimeSelector span){
        root().update(spec().toBuilder().span(span).build());
    }

    public boolean isPreliminaryCheck() {
        return spec().isPreliminaryCheck();
    }

    public void setPreliminaryCheck(boolean value) {
        root().update(spec().toBuilder()
                .preliminaryCheck(value)
                .build());
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = spanDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = pcDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = automdlDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @Override
    @Messages("regular.seriesSpecUI.getDislayName=SERIES")
    public String getDisplayName() {
        return Bundle.regular_seriesSpecUI_getDislayName();
    }
    ///////////////////////////////////////////////////////////////////////////
    private static final int SPAN_ID = 1, AUTOMDL_ID = 2, PRELIMINARYCHECK_ID = 3;

    @Messages({
        "regular.seriesSpecUI.spanDesc.name=Series span",
        "regular.seriesSpecUI.spanDesc.desc=Time span used for the processing"
    })
    private EnhancedPropertyDescriptor spanDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("span", this.getClass(), "getSpan", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SPAN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(Bundle.regular_seriesSpecUI_spanDesc_desc());
            desc.setDisplayName(Bundle.regular_seriesSpecUI_spanDesc_name());
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regular.seriesSpecUI.automdlDesc.name=auto modelling",
        "regular.seriesSpecUI.automdlDesc.desc=Allows automatic model identification"
    })
    private EnhancedPropertyDescriptor automdlDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("autoMdl", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, AUTOMDL_ID);
            desc.setDisplayName(Bundle.regular_seriesSpecUI_automdlDesc_name());
            desc.setShortDescription(Bundle.regular_seriesSpecUI_automdlDesc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regular.seriesSpecUI.pcDesc.name=Preliminary Check",
        "regular.seriesSpecUI.pcDesc.desc="
    })
    private EnhancedPropertyDescriptor pcDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("preliminaryCheck", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, PRELIMINARYCHECK_ID);
            desc.setDisplayName(Bundle.regular_seriesSpecUI_pcDesc_name());
            desc.setShortDescription(Bundle.regular_seriesSpecUI_pcDesc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
}
