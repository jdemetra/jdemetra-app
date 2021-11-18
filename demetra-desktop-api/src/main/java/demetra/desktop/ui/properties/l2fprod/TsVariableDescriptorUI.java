/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.properties.l2fprod;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.timeseries.regression.TsContextVariable;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class TsVariableDescriptorUI implements IObjectDescriptor<TsContextVariable> {

    public static final String DISPLAYNAME = "Ts variable";
    private TsContextVariable core;
    
    @Override
    public String toString(){
        return core.toString();
    }

    public TsVariableDescriptorUI() {
        core = new TsContextVariable("");
    }

    public TsVariableDescriptorUI(TsContextVariable desc) {
        core = desc;
    }

    public UserVariable getName() {
        return new UserVariable(core.getId());
    }
    
    public void setName(UserVariable name){
        core=core.withId(name.getName());
    }
    
    public int getFirstLag(){
        return core.getFirstLag();
    }

    public void setFirstLag(int lag){
        core=core.withLags(lag, core.getLastLag());
    }

    public int getLastLag(){
        return core.getLastLag();
    }

    public void setLastLag(int lag){
        core=core.withLags(core.getFirstLag(), lag);
    }

//    public UserComponentType getComponent(){
//        return core.getEffect();
//    }
//
//    public void setComponent(UserComponentType cmp){
//        core.setEffect(cmp);
//    }

    @Override
    public TsContextVariable getCore() {
        return core;
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = nameDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = flagDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = llagDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = typeDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    private static final int NAME_ID = 0, START_ID = 1, END_ID = 2, TYPE_ID = 3;
    private static final String NAME = "Name", START = "First lag", END = "Last lag", TYPE = "Component type";
    private static final String NAME_DESC = "Name", START_DESC = "First lag",
            END_DESC = "Last lag", TYPE_DESC = "Component type";

    private EnhancedPropertyDescriptor flagDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("firstLag", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, START_ID);
            desc.setDisplayName(START);
            desc.setShortDescription(START_DESC);
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor llagDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("lastLag", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, END_ID);
            desc.setDisplayName(END);
            desc.setShortDescription(END_DESC);
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor typeDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("component", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TYPE_ID);
            desc.setDisplayName(TYPE);
            desc.setShortDescription(TYPE_DESC);
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor nameDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("name", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, NAME_ID);
            desc.setDisplayName(NAME);
            desc.setShortDescription(NAME_DESC);
            //edesc.setReadOnly(true);
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
