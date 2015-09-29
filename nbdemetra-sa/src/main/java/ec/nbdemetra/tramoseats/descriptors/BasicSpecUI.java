/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats.descriptors;

import ec.nbdemetra.ui.properties.l2fprod.UserInterfaceContext;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.ui.descriptors.TsPeriodSelectorUI;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author pcuser
 */
public class BasicSpecUI extends BaseTramoSpecUI {

    BasicSpecUI(TramoSpecification spec, boolean ro) {
        super(spec, ro);
    }

    public TsPeriodSelectorUI getSpan() {
        return new TsPeriodSelectorUI(core.getTransform().getSpan(), UserInterfaceContext.INSTANCE.getDomain(), ro_);
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = spanDesc();
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
    private static final int SPAN_ID = 1, AUTOMDL_ID = 2;

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
            edesc.setReadOnly(ro_);
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
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
}
