/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats.descriptors;

import ec.tstoolkit.modelling.DefaultTransformationType;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import ec.tstoolkit.modelling.arima.tramo.TransformSpec;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.ui.descriptors.TsPeriodSelectorUI;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Jean Palate
 */
public class TransformSpecUI extends BaseTramoSpecUI {

    @Override
    public String toString() {
        return "";
    }

    private TransformSpec inner() {
        return core.getTransform();
    }

    TransformSpecUI(TramoSpecification spec, boolean ro) {
        super(spec, ro);
    }

    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = fnDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = fctDesc();
        if (desc != null) {
            descs.add(desc);
        }
//        desc = unitDesc();
//        if (desc != null) {
//            descs.add(desc);
//        }
        return descs;
    }

    public String getDisplayName() {
        return "Transformation";
    }

    public DefaultTransformationType getFunction() {
        return inner().getFunction();

    }

    public void setFunction(DefaultTransformationType value) {
        inner().setFunction(value);
    }

    public TsPeriodSelectorUI getSpan() {
        return new TsPeriodSelectorUI(inner().getSpan(), ro_);
    }

    public double getFct() {
        return inner().getFct();
    }

    public void setFct(double value) {
        inner().setFct(value);
    }

    public boolean isUnits() {
        return inner().isUnits();
    }

    public void setUnits(boolean value) {
        inner().setUnits(true);
    }
    ///////////////////////////////////////////////////////////////////////////
    private static final int SPAN_ID = 0, FN_ID = 1, FCT_ID = 2, UNITS_ID = 3;

    @Messages({"transformSpecUI.fnDesc.name=Function",
        "transformSpecUI.fnDesc.desc=[lam]. None=no transformation of data; Log=takes logs of data; Auto:the program tests for the log-level specification."
    })
    private EnhancedPropertyDescriptor fnDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("function", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, FN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.transformSpecUI_fnDesc_name());
            desc.setShortDescription(Bundle.transformSpecUI_fnDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"transformSpecUI.fctDesc.name=Fct",
        "transformSpecUI.fctDesc.desc=[fct] Controls the bias in the log/level pretest: Fct > 1 favors levels, Fct < 1 favors logs."
    })
    private EnhancedPropertyDescriptor fctDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("fct", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, FCT_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro_ || getFunction() != DefaultTransformationType.Auto);
            desc.setDisplayName(Bundle.transformSpecUI_fctDesc_name());
            desc.setShortDescription(Bundle.transformSpecUI_fctDesc_desc());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor unitDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("units", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, UNITS_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro_ || getFunction() != DefaultTransformationType.Auto);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }


    }
}
