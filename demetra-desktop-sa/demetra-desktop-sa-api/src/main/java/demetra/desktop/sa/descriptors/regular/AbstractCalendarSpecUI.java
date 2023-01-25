/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package demetra.desktop.sa.descriptors.regular;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IPropertyDescriptors;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Kristof Bayens
 */
public abstract class AbstractCalendarSpecUI implements IPropertyDescriptors {

    @Override
    public String toString(){
        return "";
    }

    public abstract IPropertyDescriptors getTradingDays();

    public abstract IPropertyDescriptors getEaster();

    @Override
     public List<EnhancedPropertyDescriptor> getProperties() {
        // regression
        ArrayList<EnhancedPropertyDescriptor> descs=new ArrayList<>();
        EnhancedPropertyDescriptor desc=tdDesc();
        if (desc != null)
            descs.add(desc);
        desc=easterDesc();
        if (desc != null)
            descs.add(desc);
        return descs;
    }


    ///////////////////////////////////////////////////////////////////////////

    private static final int TD_ID=1, EASTER_ID=2;

    @Messages({
        "regular.calendarSpecUI.tdDesc.name=tradingDays",
        "regular.calendarSpecUI.tdDesc.desc=tradingDays"
    })
    private EnhancedPropertyDescriptor tdDesc(){
        try {
            PropertyDescriptor desc = new PropertyDescriptor("tradingDays", this.getClass(),"getTradingDays", null);
            EnhancedPropertyDescriptor edesc=new EnhancedPropertyDescriptor(desc, TD_ID);
            desc.setDisplayName(Bundle.regular_calendarSpecUI_tdDesc_name());
            desc.setShortDescription(Bundle.regular_calendarSpecUI_tdDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regular.calendarSpecUI.easterDesc.name=easter",
        "regular.calendarSpecUI.easterDesc.desc=easter"
    })
    private EnhancedPropertyDescriptor easterDesc(){
        try {
            PropertyDescriptor desc = new PropertyDescriptor("easter", this.getClass(), "getEaster", null);
            EnhancedPropertyDescriptor edesc=new EnhancedPropertyDescriptor(desc, EASTER_ID);
            desc.setDisplayName(Bundle.regular_calendarSpecUI_easterDesc_name());
            desc.setShortDescription(Bundle.regular_calendarSpecUI_easterDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages("regular.calendarSpecUI.getDisplayName=Calendar")
    @Override
    public String getDisplayName() {
        return Bundle.regular_calendarSpecUI_getDisplayName();
    }
}
