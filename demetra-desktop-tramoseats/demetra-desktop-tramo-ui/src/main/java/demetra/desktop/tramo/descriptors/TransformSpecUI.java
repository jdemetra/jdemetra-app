/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramo.descriptors;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.DateSelectorUI;
import demetra.modelling.TransformationType;
import demetra.timeseries.TimeSelector;
import demetra.tramo.TramoSpec;
import demetra.tramo.TransformSpec;
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
        return core().getTransform();
    }

    TransformSpecUI(TramoSpecRoot root) {
        super(root);
    }
    
    @Override
    public boolean isRo(){
        return super.isRo() 
                || core().getRegression().hasFixedCoefficients();
    }

    @Override
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

    @Override
    public String getDisplayName() {
        return "Transformation";
    }

    public TransformationType getFunction() {
        return inner().getFunction();

    }

    public void setFunction(TransformationType value) {
        update(inner().toBuilder().function(value).build());
    }

    public DateSelectorUI getSpan() {
        return new DateSelectorUI(inner().getSpan(), isRo(), span->updateSpan(span));
    }

    public void updateSpan(TimeSelector span){
        update(inner().toBuilder().span(span).build());
    }

    public double getFct() {
        return inner().getFct();
    }

    public void setFct(double value) {
        update(inner().toBuilder().fct(value).build());
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
            edesc.setReadOnly(isRo());
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
            edesc.setReadOnly(isRo() || getFunction() != TransformationType.Auto);
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
            edesc.setReadOnly(isRo() || getFunction() != TransformationType.Auto);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }


    }
}
