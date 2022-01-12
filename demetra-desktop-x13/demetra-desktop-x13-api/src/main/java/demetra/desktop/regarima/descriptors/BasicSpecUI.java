/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.regarima.descriptors;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.DateSelectorUI;
import demetra.desktop.ui.properties.l2fprod.UserInterfaceContext;
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
public class BasicSpecUI extends BaseRegArimaSpecUI {

    public BasicSpecUI(RegArimaSpecRoot root) {
        super(root);
    }

    public DateSelectorUI getSpan() {
        return new DateSelectorUI(core().getBasic().getSpan(), UserInterfaceContext.INSTANCE.getDomain(), isRo(), selector->updateSpan(selector));
    }
    
    public void updateSpan(TimeSelector span){
        update(core().getBasic().toBuilder().span(span).build());
    }

    public boolean isPreliminaryCheck() {
        return core().getBasic().isPreliminaryCheck();
    }

    public void setPreliminaryCheck(boolean value) {
        update(core().getBasic().toBuilder()
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
    @Messages("basicSpecUI.getDislayName=Basic")
    public String getDisplayName() {
        return Bundle.basicSpecUI_getDislayName();
    }
    ///////////////////////////////////////////////////////////////////////////
    private static final int SPAN_ID = 1, AUTOMDL_ID = 2, PRELIMINARYCHECK_ID = 3;

    @Messages({
        "basicSpecUI.spanDesc.name=Series span",
        "basicSpecUI.spanDesc.desc=Time span used for the processing"
    })
    private EnhancedPropertyDescriptor spanDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("span", this.getClass(), "getSpan", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SPAN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(Bundle.basicSpecUI_spanDesc_desc());
            desc.setDisplayName(Bundle.basicSpecUI_spanDesc_name());
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "basicSpecUI.automdlDesc.name=auto modelling",
        "basicSpecUI.automdlDesc.desc=Allows automatic model identification"
    })
    private EnhancedPropertyDescriptor automdlDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("autoMdl", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, AUTOMDL_ID);
            desc.setDisplayName(Bundle.basicSpecUI_automdlDesc_name());
            desc.setShortDescription(Bundle.basicSpecUI_automdlDesc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "basicSpecUI.pcDesc.name=Preliminary Check",
        "basicSpecUI.pcDesc.desc="
    })
    private EnhancedPropertyDescriptor pcDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("preliminaryCheck", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, PRELIMINARYCHECK_ID);
            desc.setDisplayName(Bundle.basicSpecUI_pcDesc_name());
            desc.setShortDescription(Bundle.basicSpecUI_pcDesc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
}
