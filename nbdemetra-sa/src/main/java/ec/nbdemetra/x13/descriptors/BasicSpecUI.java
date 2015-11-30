/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13.descriptors;

import ec.tstoolkit.modelling.arima.x13.RegArimaSpecification;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.ui.descriptors.TsPeriodSelectorUI;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Kristof Bayens
 */
public class BasicSpecUI extends BaseRegArimaSpecUI {

    BasicSpecUI(RegArimaSpecification spec, boolean ro) {
        super(spec, ro);
    }

    public TsPeriodSelectorUI getSpan() {
        return new TsPeriodSelectorUI(core.getBasic().getSpan(), ro_);
    }

    public boolean isPreprocessing() {
        return core.getBasic().isPreprocessing();
    }

    public void setPreprocessing(boolean value) {
        core.getBasic().setPreprocessing(value);
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = spanDesc();
        if (desc != null) {
            descs.add(desc);
        }
//        desc = ppDesc();
//        if (desc != null) {
//            descs.add(desc);
//        }
        return descs;
    }

    @Messages("basicSpecUI.getDisplayName=Basic")
    @Override
    public String getDisplayName() {
        return Bundle.basicSpecUI_getDisplayName();
    }
    ///////////////////////////////////////////////////////////////////////////
    private static final int SPAN_ID = 1, PREPROCESSING_ID = 2;

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
        "basicSpecUI.ppDesc.name=Use preprocessing",
        "basicSpecUI.ppDesc.desc=Do pre-processing"
    })
    private EnhancedPropertyDescriptor ppDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("preprocessing", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, PREPROCESSING_ID);
            desc.setDisplayName(Bundle.basicSpecUI_ppDesc_name());
            desc.setShortDescription(Bundle.basicSpecUI_ppDesc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

}
