/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.properties.l2fprod;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.ui.properties.l2fprod.UserInterfaceContext;
import demetra.desktop.ui.properties.l2fprod.UserVariable;
import demetra.sa.ComponentType;
import demetra.sa.SaVariable;
import demetra.timeseries.regression.TsContextVariable;
import demetra.timeseries.regression.Variable;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class SaTsVariableDescriptor extends SaVariableDescriptor<TsContextVariable> {

    public static final String DISPLAYNAME = "Ts variable";
    private TsContextVariable core;
    
    @Override
    public String toString(){
        return getName();
    }

    public SaTsVariableDescriptor() {
        core = new TsContextVariable("");
    }

    public SaTsVariableDescriptor(Variable<TsContextVariable> var) {
        super(var);
        core = var.getCore();
        setName(var.getName());
        setRegressionEffect(SaVariable.regressionEffect(var));
    }
    
    public SaTsVariableDescriptor(SaTsVariableDescriptor desc) {
        super(desc);
        core = desc.core;
    }

    public SaTsVariableDescriptor duplicate(){
        return new SaTsVariableDescriptor(this);
    }

    public UserVariable getVariableName() {
        return new UserVariable(core.getId());
    }
    
    public void setVariableName(UserVariable name){
        core=core.withId(name.getName());
    }
    
    public int getLag(){
        return core.getLag();
    }

    public void setLag(int lag){
        core=core.withLag(lag);
    }

    @Override
    public TsContextVariable getCore() {
        return core;
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc;
        desc = nameDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = variableNameDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = lagDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = regDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = parameterDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    private static final int USERNAME_ID = 10, LAG_ID = 11;
    private static final String USERNAME = "Variable", LAG = "Lag";
    private static final String USERNAME_DESC = "Name", LAG_DESC = "Lag";

    private EnhancedPropertyDescriptor lagDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("lag", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LAG_ID);
            desc.setDisplayName(LAG);
            desc.setShortDescription(LAG_DESC);
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor variableNameDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("variableName", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, USERNAME_ID);
            desc.setDisplayName(USERNAME);
            desc.setShortDescription(USERNAME_DESC);
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

    @Override
    protected ComponentType regressionEffect() {
        return ComponentType.Undefined;
    }

    @Override
    public String name() {
        return core.description(UserInterfaceContext.INSTANCE.getDomain());
    }
}
